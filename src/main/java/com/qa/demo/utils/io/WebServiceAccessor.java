package com.qa.demo.utils.io;

import com.qa.demo.conf.FileConfig;
import com.qa.demo.dataStructure.Triplet;

import java.io.*;
import javax.validation.constraints.NotNull;

import java.net.URI;
import java.net.URLEncoder;
import java.net.URLDecoder;
import java.net.URISyntaxException;

import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import com.qa.demo.utils.kgprocess.KGTripletsClient;
import com.qa.demo.utils.qgeneration.KBTripletBasedQuestionGeneration;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import static com.qa.demo.conf.FileConfig.NT_TRIPLETS;
import static java.lang.Thread.sleep;

// import com.google.gson.Gson;

public class WebServiceAccessor {
    private static final WebServiceAccessor webServiceAccessor = new WebServiceAccessor();

    // 查询所属仓库
    private enum Repository{
        zhishi_201801, agriculture
    }

    private WebServiceAccessor() {
        // System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.SimpleLog");
        // System.setProperty("org.apache.commons.logging.simplelog.showdatetime", "true");
        // System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient", "stdout");
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
    public static List<Triplet> query(Triplet triplet) {

        WebServiceAccessor accessor = WebServiceAccessor._getWebServiceAccessor();
        // 根据三元组生成对应的 sparql 查询语句
        String sparql = accessor._createSparql(triplet);
        sparql = _encode_chinese(sparql);
        Repository repository = accessor._judgeRepository(triplet);
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
        if(cnt == 0){
            System.err.println("传输次数达到限制，传输失败！");
        }
        // System.out.println(repository);
        // System.out.println(sparql);
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
        // ArrayList<Triplet> tripletList = KGTripletsClient.getInstance().getKgTriplets();
        // NT_TRIPLETS;
        // List<Triplet> tripletList = KBTripletBasedQuestionGeneration.generateTriplets("src/main/resources/data/kbfile/NT_triplets.nt");
        List<Triplet> tripletList = null;
        try {
            tripletList = getTripletsFromFile(FileConfig.NT_TRIPLETS);
            // tripletList = getTripletsFromFile("src/main/resources/data/kbfile/NT_triplets.nt");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("读取文件失败！");
        }
        // String abc = tripletList.get(0).getSubjectURI();
        String subject = triplet.getSubjectURI();
        if(subject != null){
            for(Triplet t : tripletList){
                if(t.getSubjectURI().contains(subject) || t.getObjectURI().contains(subject))
                    return Repository.agriculture;
            }
        }
        String object = triplet.getObjectURI();
        if(object != null){
            for(Triplet t : tripletList){
                if(t.getSubjectURI().contains(object) || t.getObjectURI().contains(object))
                    return Repository.agriculture;
            }
        }
        return Repository.zhishi_201801;
    }


    private List<Triplet> getTripletsFromFile(String filepath) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                new FileInputStream(new File(filepath))));
        String str;

        List<Triplet> tripletList = new LinkedList<>();
        int index;
        while((str = bufferedReader.readLine()) != null){
            Triplet triplet = new Triplet();
            index = str.indexOf(' ');
            triplet.setSubjectURI(str.substring(0, index));
            str = str.substring(index + 1);
            index = str.indexOf(' ');
            triplet.setPredicateURI(str.substring(0, index));
            str = str.substring(index + 1);
            triplet.setObjectURI(str.substring(0, str.length() - 2));
            tripletList.add(triplet);
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
     * @param triplet 查询三元组
     * @param sparql sparql 查询语句
     * @param repository 查询所处仓库
     * @return 查询得到的三元组链表
     * @throws IOException
     * @throws URISyntaxException
     */
    private List<Triplet> _queryServer(Triplet triplet, String sparql, Repository repository) throws IOException, URISyntaxException {
        sparql = URLEncoder.encode(sparql, "UTF-8").replaceAll("\\+", "%20");
        HttpClient httpClient = new DefaultHttpClient();
        _login(httpClient);
        String json = _request(httpClient, sparql, repository);
        return _analysisJson(triplet, json);
    }


    /**
     * 根据 triplet 生成对应的 sparql 语句
     *
     * @param triplet 三元组
     * @return 对应的 sparql 查询语句
     */
    private String _createSparql(Triplet triplet) {

        String sparql;

        String subject_uri = triplet.getSubjectURI();
        String predict_uri = triplet.getPredicateURI();
        String object_uri = triplet.getObjectURI();

        subject_uri = subject_uri == null ? "?s" :
                "<" + subject_uri + ">";
        predict_uri = predict_uri == null ? "?p" :
                "<" + predict_uri + ">";
        object_uri = object_uri == null ? "?o" :
                "\"" + object_uri + "\"";


        sparql = "select ?s ?p ?o " + "{" + subject_uri + " " + predict_uri + " " + object_uri + "}";
        // System.out.println(sparql);
        return sparql;
    }


    /**
     * 将返回的 json 数据解析为 List<Triplet>
     *
     * @param triplet 查询三元组
     * @param json json 数据
     * @return 解析后的三元组链表
     */
    private List<Triplet> _analysisJson(Triplet triplet, String json) throws UnsupportedEncodingException {

        List<Triplet> tripletList = new ArrayList<>();

        // 用于匹配外部的 json
        // 匹配格式为：["subject","predict","object"]
        Pattern external = Pattern.compile("\\[(.*?)\\]");

        // 用于匹配内部的 json
        // 格式为："subject/predict/object" 
        Pattern internal = Pattern.compile("(\"<.*?>\")|(null)|(\"\\\\\".*?\\\\\"\")");

        Matcher external_matcher = external.matcher(json);
        Matcher internal_matcher;

        String subject = null, predict = null, object = null;
        // System.out.println(json);
        
        // 过滤第一个匹配 ["s", "p", "o"]
        external_matcher.find();
        // 此时没有返回结果
        if(json.substring(json.indexOf(external_matcher.group()) + external_matcher.group().length()).length() < 30)
            return tripletList;
        while (external_matcher.find()) {
            String internal_string = external_matcher.group();
            // System.out.println(internal_string);
            internal_matcher = internal.matcher(internal_string);
            while(internal_matcher.find()){
                subject = internal_matcher.group().replaceAll("\\\\\"", "\"");
                if(subject.length() == 4){
                    subject = triplet.getSubjectURI();
                }else{
                    subject = URLDecoder.decode(subject.substring(2, subject.length() - 2), "UTF-8");
                }

                internal_matcher.find();
                predict = internal_matcher.group().replaceAll("\\\\\"", "\"");
                if(predict.length() == 4){
                    predict = triplet.getPredicateURI();
                }else {
                    predict = URLDecoder.decode(predict.substring(2, predict.length() - 2), "UTF-8");
                }

                internal_matcher.find();
                object = internal_matcher.group().replaceAll("\\\\\"", "\"")
                        .replaceAll("\\\\\\\\", "\\\\");
                        // .replaceAll("\\\\u", "\\u");
                if(object.length() == 4){
                    object = triplet.getObjectURI();
                }else {
                    object = URLDecoder.decode(object.substring(2, object.length() - 2), "UTF-8");
                }
            }
            Triplet t = new Triplet();
            t.setSubjectURI(URLDecoder.decode(subject, "UTF-8"));
            t.setPredicateURI(URLDecoder.decode(predict, "UTF-8"));
            if(object.substring(0, 2).equals("\\u"))
                t.setObjectURI(_decode_unicode(object));
            else
                t.setObjectURI(URLDecoder.decode(object, "UTF-8"));
            // System.out.println(subject);
            // System.out.println(predict);
            // System.out.println(object);
            tripletList.add(t);

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

    public static List<String> getChinese(String paramValue) {
        String regex = "([\u4e00-\u9fa5]+)";
        List<String> stringList = new LinkedList<>();
        if(paramValue == null)
            return stringList;
        Matcher matcher = Pattern.compile(regex).matcher(paramValue);
        while (matcher.find()) {
            stringList.add(matcher.group(0));
        }
        return stringList;
    }

    // 现有的系统，主语谓语如果是中文，则需要编码两次（一次为中文独立编码，另一次为url编码）
    // 如果宾语是中文，则只需要编码一次（url编码）
    private static String _encode_chinese(String sparql){
        List<String> chinese = getChinese(sparql);
        int i = 0;
        for(String s : chinese){
            // System.out.println(s);
            if(i == chinese.size() - 1 && !sparql.contains("?o}")){
                break;
            }
            try {
                sparql = sparql.replace(s, URLEncoder.encode(s, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            ++i;
        }
        return sparql;
    }

    private static String _decode_unicode(String unicodeStr) {
        if (unicodeStr == null) {
            return null;
        }
        StringBuffer retBuf = new StringBuffer();
        int maxLoop = unicodeStr.length();
        for (int i = 0; i < maxLoop; i++) {
            if (unicodeStr.charAt(i) == '\\') {
                if ((i < maxLoop - 5)
                        && ((unicodeStr.charAt(i + 1) == 'u') || (unicodeStr
                        .charAt(i + 1) == 'U')))
                    try {
                        retBuf.append((char) Integer.parseInt(
                                unicodeStr.substring(i + 2, i + 6), 16));
                        i += 5;
                    } catch (NumberFormatException localNumberFormatException) {
                        retBuf.append(unicodeStr.charAt(i));
                    }
                else
                    retBuf.append(unicodeStr.charAt(i));
            } else {
                retBuf.append(unicodeStr.charAt(i));
            }
        }
        return retBuf.toString();
    }
}
