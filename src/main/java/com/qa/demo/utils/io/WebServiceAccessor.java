package com.qa.demo.utils.io;

import com.qa.demo.conf.FileConfig;
import com.qa.demo.dataStructure.Triplet;

import java.io.*;
import javax.validation.constraints.NotNull;

import java.net.URI;
import java.net.URLEncoder;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import static com.qa.demo.utils.io.TripletReader.getTripletsFromNT_Triplets;
import static com.qa.demo.utils.io.WebServiceTool.*;
import static java.lang.Thread.sleep;

/**
 * 说明：
 * 在通过WebServiceAccessor访问服务器端的数据时
 * 默认为主语和谓语uri添加<>
 * 为宾语uri添加""
 * 见 _createSparql 函数
 */
public class WebServiceAccessor {
    private static final WebServiceAccessor webServiceAccessor = new WebServiceAccessor();
    private List<Triplet> tripletList = null;


    // 查询所属仓库
    private enum Repository {
        zhishi_201801, agriculture
    }

    private WebServiceAccessor() {
        try {
            tripletList = getTripletsFromNT_Triplets(FileConfig.NT_TRIPLETS);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("读取文件失败！");
        }
    }

    private static WebServiceAccessor _getWebServiceAccessor() {
        return webServiceAccessor;
    }


    /**
     * 得到主语对应的所有三元组
     *
     * @param subject 主语
     * @return 主语对应的所有三元组
     */
    @NotNull
    public static List<Triplet> queryBySubject(String subject) {
        List<String> subjects = new LinkedList<>();
        subjects.add(subject);
        return queryByMultiSubjects(subjects);
    }

    /**
     * 得到谓语对应的所有三元组
     *
     * @param predict 谓语
     * @return 谓语对应的所有三元组
     */
    @NotNull
    public static List<Triplet> queryByPredict(String predict) {
        List<String> predicts = new LinkedList<>();
        predicts.add(predict);
        return queryByMultiSubjects(predicts);
    }

    /**
     * 得到宾语对应的所有三元组
     *
     * @param object 宾语
     * @return 宾语对应的所有三元组
     */
    @NotNull
    public static List<Triplet> queryByObject(String object) {
        List<String> objects = new LinkedList<>();
        objects.add(object);
        return queryByMultiSubjects(objects);
    }

    /**
     * 得到链表中候选主语对应的三元组
     *
     * @param subjects 候选主语
     * @return 对应的所有三元组
     */
    @NotNull
    public static List<Triplet> queryByMultiSubjects(List<String> subjects) {

        WebServiceAccessor accessor = WebServiceAccessor._getWebServiceAccessor();
        String filter = accessor._createFilterString("s", subjects);
        String sparql = accessor._createSparql(new Triplet(), filter);
        List<Triplet> ret = new ArrayList<>();
        for (String subject : subjects) {
            for (Triplet triplet : accessor.tripletList) {
                if (triplet.getSubjectURI().contains(subject)) {
                    ret.addAll(queryServer(new Triplet(), sparql, Repository.agriculture));
                    break;
                }
            }
        }

        // 访问 zhishi_201801，需要考虑重定向与多义词
        ret.addAll(queryServer(new Triplet(), sparql, Repository.zhishi_201801));
        subjects = new LinkedList<>();
        String predict;
        for (Triplet t : ret) {
            predict = t.getPredicateURI();
            if (predict.contains("pageRedirects") || predict.contains("pageDisambiguates"))
                subjects.add(t.getObjectURI());
        }
        if (subjects.size() != 0) {
            filter = accessor._createFilterString("s", subjects);
            sparql = accessor._createSparql(new Triplet(), filter);
            ret.addAll(queryServer(new Triplet(), sparql, Repository.zhishi_201801));
        }
        return ret;
    }

    /**
     * 得到链表中候选谓语对应的三元组
     *
     * @param predicts 候选谓语
     * @return 对应的所有三元组
     */
    public static List<Triplet> queryByMultiPredicts(List<String> predicts) {
        WebServiceAccessor accessor = WebServiceAccessor._getWebServiceAccessor();
        String filter = accessor._createFilterString("p", predicts);
        String sparql = accessor._createSparql(new Triplet(), filter);
        for (String predict : predicts) {
            for (Triplet triplet : accessor.tripletList) {
                if (triplet.getPredicateURI().contains(predict)) {
                    return queryServer(new Triplet(), sparql, Repository.agriculture);
                }
            }
        }
        return queryServer(new Triplet(), sparql, Repository.zhishi_201801);
    }

    /**
     * 得到链表中候选宾语对应的三元组
     *
     * @param objects 候选宾语
     * @return 对应的所有三元组
     */
    public static List<Triplet> queryByMultiObjects(List<String> objects) {
        WebServiceAccessor accessor = WebServiceAccessor._getWebServiceAccessor();
        String filter = accessor._createFilterString("o", objects);
        String sparql = accessor._createSparql(new Triplet(), filter);
        List<Triplet> ret = new ArrayList<>();
        /*for (String object : objects) {
            for (Triplet triplet : accessor.tripletList) {
                if (triplet.getObjectURI().contains(object)) {
                    return queryServer(new Triplet(), sparql, Repository.agriculture);
                }
            }
        }*/
        ret.addAll(queryServer(new Triplet(), sparql, Repository.agriculture));

        // 访问 zhishi_201801，需要考虑重定向与多义词
        ret.addAll(queryServer(new Triplet(), sparql, Repository.zhishi_201801));
        objects = new LinkedList<>();
        String predict;
        for (Triplet t : ret) {
            predict = t.getPredicateURI();
            if (predict.contains("pageRedirects") || predict.contains("pageDisambiguates"))
                objects.add(t.getObjectURI());
        }
        if (objects.size() != 0) {
            filter = accessor._createFilterString("o", objects);
            sparql = accessor._createSparql(new Triplet(), filter);
            ret.addAll(queryServer(new Triplet(), sparql, Repository.zhishi_201801));
        }
        return ret;
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
    public static List<Triplet> query(Triplet triplet) {
        WebServiceAccessor accessor = WebServiceAccessor._getWebServiceAccessor();
        String filter = "";
        if(triplet.getObjectURI() != null){
            List<String> objectList = new ArrayList<>();
            objectList.add(triplet.getObjectURI());
            filter = accessor._createFilterString("o", objectList);
            triplet.setObjectURI(null);
        }
        String sparql = accessor._createSparql(triplet, filter);
        Repository repository = accessor._judgeRepository(triplet);
        return queryServer(triplet, sparql, repository);
    }

    private static List<Triplet> queryServer(Triplet triplet, String sparql, Repository repository) {

        WebServiceAccessor accessor = WebServiceAccessor._getWebServiceAccessor();

        // System.out.println(sparql);
        // System.out.println(repository);

        // 存放结果
        List<Triplet> tripletList = null;

        // 进行请求，出错重传
        boolean endFlag = false;
        // 失败时重试5次
        int cnt = 5;
        do {
            try {
                tripletList = accessor._queryServer(triplet, sparql, repository);
                endFlag = true;
            } catch (IOException e) {
                --cnt;
                System.err.println("传输出错，正在重试");

                try {
                    sleep(500);
                } catch (InterruptedException ignored) {
                }

            } catch (URISyntaxException e) {
                System.err.println("网页 URI 错误");
                assert false;
            }
        } while (!endFlag && cnt != 0);
        if (cnt == 0) {
            System.err.println("重试次数达到限制，传输失败！");
        }
        return tripletList;
    }


    /**
     * 判断三元组中主语或者宾语 uri 是否属于农业体系
     * 主语或宾语 uri 存在时对其进行判断
     * 如果两者都存在，那两者都将进行判断
     *
     * @param triplet 查询三元组
     * @return 所属仓库
     */
    private Repository _judgeRepository(Triplet triplet) {
        String subject = triplet.getSubjectURI();
        if (subject != null) {
            for (Triplet t : tripletList) {
                if (t.getSubjectURI().contains(subject) || t.getObjectURI().contains(subject))
                    return Repository.agriculture;
            }
        }
        return Repository.zhishi_201801;
    }


    /**
     * 请求查询服务器
     * 主语 uri 固定，谓语 uri 未定时，返回主语对应的所有三元组
     * 主语谓语 uri 固定，宾语 uri 未定时，返回主语谓语对应的所有三元组
     * 主语谓语宾语 uri 固定时，返回对应的三元组
     * <p>
     * 注：如果有密集查询，可以尝试不登录直接查询
     *
     * @param triplet    查询三元组
     * @param sparql     sparql 查询语句
     * @param repository 查询所处仓库
     * @return 查询得到的三元组链表
     * @throws IOException
     * @throws URISyntaxException
     */
    private List<Triplet> _queryServer(Triplet triplet, String sparql, Repository repository) throws IOException, URISyntaxException {
        sparql = URLEncoder.encode(sparql, "UTF-8");
        // System.out.println(sparql);
        HttpClient httpClient = new DefaultHttpClient();
        _login(httpClient);
        String json = _request(httpClient, sparql, repository);
        return _analysisJson(triplet, json);
    }


    /**
     * 根据 triplet 生成对应的 sparql 语句
     *
     * @param triplet 三元组
     * @param filter  过滤条件
     * @return 对应的 sparql 查询语句
     */
    private String _createSparql(Triplet triplet, String filter) {

        String sparql;

        String subject_uri = triplet.getSubjectURI();
        String predict_uri = triplet.getPredicateURI();
        String object_uri = triplet.getObjectURI();
        try {
            if (subject_uri == null)
                subject_uri = "?s";
            else if(!subject_uri.contains("/resource/")){
                subject_uri = subject_uri.replace(subject_uri, URLEncoder.encode(subject_uri, "UTF-8"));
                subject_uri = "<" + subject_uri + ">";
            }
            else {
                int index = subject_uri.lastIndexOf("/resource/");
                String name = subject_uri.substring(index + "/resource/".length());
                subject_uri = subject_uri.replace(name, URLEncoder.encode(name, "UTF-8"));
                subject_uri = "<" + subject_uri + ">";
            }

            if (predict_uri == null)
                predict_uri = "?p";
            else if(!predict_uri.contains("/property/")){
                predict_uri = predict_uri.replace(predict_uri, URLEncoder.encode(predict_uri, "UTF-8"));
                predict_uri = "<" + predict_uri + ">";
            }
            else {
                int index = predict_uri.lastIndexOf("/property/");
                String name = predict_uri.substring(index + "/property/".length());
                predict_uri = predict_uri.replace(name, URLEncoder.encode(name, "UTF-8"));
                predict_uri = "<" + predict_uri + ">";
            }

            object_uri = object_uri == null ? "?o" :
                    "\"" + object_uri + "\"";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


        sparql = "select ?s ?p ?o " + "{" + subject_uri + " " + predict_uri + " " + object_uri
                + " " + filter + "}";

        System.out.println(sparql);

        return sparql;
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
    private String _request(HttpClient httpClient, String sparql, Repository repository) throws URISyntaxException, IOException {
        String url = "http://120.77.215.39:10035/catalogs/zhishime/repositories/" + repository;
        url += "?query=" + sparql + "&queryLn=SPARQL&limit=100&infer=false";
        // System.out.println(url);
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
        httpPost.addHeader("Referer", "http://120.77.215.39:10035/catalogs/zhishime/repositories/" + repository);
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

    private String _createFilterString(String spo, List<String> stringList) {

        StringBuilder ret = new StringBuilder("FILTER (?" + spo + " IN ( ");
        try {
            int index;
            String match;
            String left, right;
            String[] templates = new String[]{"resource/", "property/", "category/"};
            for (String s : stringList) {

                match = "";
                for(String template : templates){
                    if(s.contains(template))
                        match = template;
                }
                match = match.equals("") ? s : match;

                if(match.equals(s)){
                    index = 0;
                    left = "\"";
                    right = "\"@zh";
                }else{
                    index = s.lastIndexOf(match);
                    left = "<";
                    right = ">";
                }

                String name = s.substring(index + match.length());
                s = s.replace(name, URLEncoder.encode(name, "UTF-8"));
                ret.append(left).append(s).append(right).append(", ");
                if(right.equals("\"@zh"))
                    // 访问 agriculture 时不需要 @zh
                    ret.append(left).append(s).append("\"").append(", ");
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        ret.delete(ret.length() - 2, ret.length());
        ret.append(" ))");
        return ret.toString();
    }


}
