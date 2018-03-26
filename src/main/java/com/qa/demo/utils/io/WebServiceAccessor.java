package com.qa.demo.utils.io;

import com.qa.demo.dataStructure.Triplet;

import java.io.*;
import javax.validation.constraints.NotNull;

import java.net.URI;
import java.net.URLEncoder;
import java.net.URLDecoder;
import java.net.URISyntaxException;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

public class WebServiceAccessor {
    private static final WebServiceAccessor webServiceAccessor = new WebServiceAccessor();

    private WebServiceAccessor() {

    }

    private static WebServiceAccessor _getWebServiceAccessor() {
        return webServiceAccessor;
    }

    /**
     * 根据需要将 uri 填入 triplet，进行 Web Service 访问
     * 填充主谓宾：得到该主语谓语宾语对应的三元组
     * 填充主谓 ：得到该主语谓语对应的所有三元组
     * 填充主语 ：得到该主语对应的所有三元组
     *
     * @param triplet uri 载体
     * @return 三元组链表
     */
    @NotNull
    public static List<Triplet> query(Triplet triplet) throws IOException {

        WebServiceAccessor accessor = WebServiceAccessor._getWebServiceAccessor();
        String sparql = accessor._createSparql(triplet);

        List<Triplet> tripletList = null;

        try {
            tripletList = accessor._queryServer(sparql);

        } catch (IOException e) {
            System.err.println("传输出错，请重试");
            throw new IOException();

        } catch (URISyntaxException e) {
            System.err.println("网页 URI 错误");
            assert false;
        }

        return tripletList;
    }

    /**
     * 请求查询服务器
     * 主语 uri 固定，谓语 uri 未定时，返回主语对应的所有三元组
     * 主语谓语 uri 固定，宾语 uri 未定时，返回主语谓语对应的所有三元组
     * 主语谓语宾语 uri 固定时，返回对应的三元组
     * <p>
     * 注：如果有密集查询，可以尝试不登录直接查询
     *
     * @param sparql sparql 查询语句
     * @return 查询得到的三元组链表
     * @throws IOException
     * @throws URISyntaxException
     */
    private List<Triplet> _queryServer(String sparql) throws IOException, URISyntaxException {
        sparql = URLEncoder.encode(sparql, "UTF-8").replaceAll("\\+", "%20");
        HttpClient httpClient = new DefaultHttpClient();
        _login(httpClient);
        String json = _request(httpClient, sparql);
        return _analysisJson(json);
    }


    /**
     * 根据 triplet 生成对应的 sparql 语句
     *
     * @param triplet 三元组
     * @return 对应的 sparql 查询语句
     */
    private String _createSparql(Triplet triplet) throws UnsupportedEncodingException {

        String sparql;

        String subject_uri = triplet.getSubjectURI();
        String predict_uri = triplet.getPredicateURI();
        String object_uri = triplet.getObjectURI();

        subject_uri = subject_uri == null ? "?s" :
                "<" + subject_uri + ">";
        predict_uri = predict_uri == null ? "?p" :
                "<" + predict_uri + ">";
        object_uri = object_uri == null ? "?o" :
                "<" + object_uri + ">";


        sparql = "select ?s ?p ?o " + "{" + subject_uri + " " + predict_uri + " " + object_uri + "}";

        return sparql;
    }


    /**
     * 将返回的 json 数据解析为 List<Triplet>
     *
     * @param json json 数据
     * @return 解析后的三元组链表
     */
    private List<Triplet> _analysisJson(String json) throws UnsupportedEncodingException {

        List<Triplet> tripletList = new ArrayList<>();
        Pattern pattern = Pattern.compile("(<.*?>)|(null)");
        Matcher matcher = pattern.matcher(json);
        String subject, predict, object;

        while (matcher.find()) {

            subject = matcher.group();
            if (!subject.equals("null"))
                subject = URLDecoder.decode(subject.substring(1, subject.length() - 1), "UTF-8");

            matcher.find();
            predict = matcher.group();
            if (!predict.equals("null"))
                predict = URLDecoder.decode(predict.substring(1, predict.length() - 1), "UTF-8");

            matcher.find();
            object = matcher.group();
            if (!object.equals("null"))
                object = URLDecoder.decode(object.substring(1, object.length() - 1), "UTF-8");

            Triplet triplet = new Triplet();
            triplet.setSubjectURI(subject);
            triplet.setPredicateURI(predict);
            triplet.setObjectURI(object);

            tripletList.add(triplet);

        }

        return tripletList;
    }


    /**
     * 登录服务器
     *
     * @param httpClient httpClient
     * @throws IOException
     * @throws URISyntaxException
     */
    private void _login(HttpClient httpClient) throws IOException, URISyntaxException {
        String url = "http://120.77.215.39:10035/users/test/effectivePermissions";
        HttpGet httpGet = new HttpGet(url);

        httpGet.addHeader("Accept", "application/json");
        httpGet.addHeader("Accept-Encoding", "gzip, deflate");
        httpGet.addHeader("Accept-Language", "zh-CN,zh;q=0.9");
        httpGet.addHeader("Cache-Control", "no-cache");
        httpGet.addHeader("Cookie", "auth4=test: af8d48a9be089c87d92ab58bfec9fe5c1ea97caa");
        httpGet.addHeader("Host", "120.77.215.39:10035");
        httpGet.addHeader("Pragma", "no-cache");
        httpGet.addHeader("Proxy-Connection", "keep-alive");
        httpGet.addHeader("Referer", "http://120.77.215.39:10035/");
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
        httpGet.addHeader("X-Cookie-Auth", "yes");

        HttpResponse httpResponse = httpClient.execute(httpGet);
        // String ret = _getStringFromResponse(httpResponse);
        // System.out.println(ret);

        httpGet.releaseConnection();
        url = "http://120.77.215.39:10035/users/test/effectiveAccess";
        httpGet.setURI(new URI(url));
        httpResponse = httpClient.execute(httpGet);

        // ret = _getStringFromResponse(httpResponse);
        // System.out.println(ret);
        httpGet.releaseConnection();
    }

    /**
     * 根据 sparql 查询语句在服务器上进行查询
     *
     * @param httpClient httpClient
     * @param sparql     sparql 查询语句
     * @return 查询结果 (json形式)
     * @throws URISyntaxException
     * @throws IOException
     */
    private String _request(HttpClient httpClient, String sparql) throws URISyntaxException, IOException {
        String url = "http://120.77.215.39:10035/catalogs/zhishime/repositories/zhishi_201801";
        url += "?query=" + sparql + "&queryLn=SPARQL&limit=100&infer=false";
        HttpPost httpPost = new HttpPost();
        httpPost.setURI(new URI(url));

        httpPost.addHeader("Accept", "application/json");
        httpPost.addHeader("Accept-Encoding", "gzip, deflate");
        httpPost.addHeader("Accept-Language", "zh-CN,zh;q=0.9");
        httpPost.addHeader("Cache-Control", "no-cache");
        httpPost.addHeader("Connection", "keep-alive");
        // httpPost.addHeader("Content-Length", "0");
        httpPost.addHeader("Cookie", "auth4=test: af8d48a9be089c87d92ab58bfec9fe5c1ea97caa");
        httpPost.addHeader("Host", "120.77.215.39:10035");
        httpPost.addHeader("Origin", "http://120.77.215.39:10035");
        httpPost.addHeader("Pragma", "no-cache");
        httpPost.addHeader("Referer", "http://120.77.215.39:10035/catalogs/zhishime/repositories/zhishi_201801");
        httpPost.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
        httpPost.addHeader("X-Cookie-Auth", "yes");

        HttpResponse httpResponse = httpClient.execute(httpPost);
        String ret = _getStringFromResponse(httpResponse);
        httpPost.releaseConnection();
        // System.out.println(ret);
        return ret;
    }

    /**
     * 从 gzip 格式的 response 中提取服务器返回的信息
     *
     * @param response response
     * @return 服务器返回的信息
     */
    private String _getStringFromResponse(HttpResponse response) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(new GZIPInputStream(response.getEntity().getContent()), "UTF-8"));
        String str;
        StringBuilder stringBuilder = new StringBuilder();
        String lineSeparator = System.getProperty("line.separator");
        while ((str = bufferedReader.readLine()) != null) {
            stringBuilder.append(str).append(lineSeparator);
        }
        bufferedReader.close();
        return stringBuilder.toString();
    }
}
