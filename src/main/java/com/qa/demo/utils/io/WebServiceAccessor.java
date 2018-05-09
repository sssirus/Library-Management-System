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
    private enum Repository{
        zhishi_201801, agriculture
    }

    private WebServiceAccessor() {
        try {
            tripletList = getTripletsFromFile(FileConfig.NT_TRIPLETS);
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
     * @param subject 主语
     * @return 主语对应的所有三元组
     */
    @NotNull
    public static List<Triplet> queryBySubject(String subject) {
        Triplet triplet = new Triplet();
        triplet.setSubjectURI(subject);
        return query(triplet);
    }
    /**
     * 得到谓语对应的所有三元组
     * @param predict 谓语
     * @return 谓语对应的所有三元组
     */
    @NotNull
    public static List<Triplet> queryByPredict(String predict) {
        Triplet triplet = new Triplet();
        triplet.setPredicateURI(predict);
        return query(triplet);
    }
    /**
     * 得到宾语对应的所有三元组
     * @param object 宾语
     * @return 宾语对应的所有三元组
     */
    @NotNull
    public static List<Triplet> queryByObject(String object) {
        Triplet triplet = new Triplet();
        triplet.setObjectURI(object);
        return query(triplet);
    }

    /**
     * 得到链表中候选主语对应的三元组
     * @param subjects 候选主语
     * @return 对应的所有三元组
     */
    @NotNull
    public static List<Triplet> queryByMultiSubjects(List<String> subjects) {
        WebServiceAccessor accessor = WebServiceAccessor._getWebServiceAccessor();
        String filter = _createFilterString("s", subjects);
        String sparql = accessor._createSparql(new Triplet(), filter);
        for(String subject : subjects){
            for(Triplet triplet : accessor.tripletList){
                if(triplet.getSubjectURI().contains(subject)){
                    return queryServer(new Triplet(), sparql, Repository.agriculture);
                }
            }
        }
        return queryServer(new Triplet(), sparql, Repository.zhishi_201801);
    }
    /**
     * 得到链表中候选谓语对应的三元组
     * @param predicts 候选谓语
     * @return 对应的所有三元组
     */
    public static List<Triplet> queryByMultiPredicts(List<String> predicts){
        WebServiceAccessor accessor = WebServiceAccessor._getWebServiceAccessor();
        String filter = _createFilterString("p", predicts);
        String sparql = accessor._createSparql(new Triplet(), filter);
        for(String predict : predicts){
            for(Triplet triplet : accessor.tripletList){
                if(triplet.getPredicateURI().contains(predict)){
                    return queryServer(new Triplet(), sparql, Repository.agriculture);
                }
            }
        }
        return queryServer(new Triplet(), sparql, Repository.zhishi_201801);
    }
    /**
     * 得到链表中候选宾语对应的三元组
     * @param objects 候选宾语
     * @return 对应的所有三元组
     */
    public static List<Triplet> queryByMultiObjects(List<String> objects){
        WebServiceAccessor accessor = WebServiceAccessor._getWebServiceAccessor();
        String filter = _createFilterString("o", objects);
        String sparql = accessor._createSparql(new Triplet(), filter);
        for(String object : objects){
            for(Triplet triplet : accessor.tripletList){
                if(triplet.getObjectURI().contains(object)){
                    return queryServer(new Triplet(), sparql, Repository.agriculture);
                }
            }
        }
        return queryServer(new Triplet(), sparql, Repository.zhishi_201801);
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
        String sparql = accessor._createSparql(triplet, "");
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
        if(cnt == 0){
            System.err.println("传输次数达到限制，传输失败！");
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
        if(subject != null){
            for(Triplet t : tripletList){
                if(t.getSubjectURI().contains(subject) || t.getObjectURI().contains(subject))
                    return Repository.agriculture;
            }
        }
        String predict = triplet.getPredicateURI();
        if(predict != null){
            for(Triplet t : tripletList){
                if(t.getPredicateURI().contains(predict))
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


    /**
     * 从文件中获取三元组列表，以判断三元组属于哪个目录
     * @param filepath 文件目录
     * @return 文件中的所有三元祖
     * @throws IOException 文件打开失败，或者读取失败
     */
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
     * @param filter 过滤条件
     * @return 对应的 sparql 查询语句
     */
    private String _createSparql(Triplet triplet, String filter) {

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

        try{
            List<String> chineseList = getChinese(subject_uri);
            for(String chinese : chineseList){
                subject_uri = subject_uri.replace(chinese, URLEncoder.encode(chinese, "UTF-8"));
            }
            chineseList = getChinese(predict_uri);
            for(String chinese : chineseList){
                predict_uri = predict_uri.replace(chinese, URLEncoder.encode(chinese, "UTF-8"));
            }
            if(!filter.equals("") && !filter.contains("?o")){
                chineseList = getChinese(filter);
                for(String chinese : chineseList){
                    filter = filter.replace(chinese, URLEncoder.encode(chinese, "UTF-8"));
                }
            }
        }catch(UnsupportedEncodingException e){
            e.printStackTrace();
        }

        sparql = "select ?s ?p ?o " + "{" + subject_uri + " " + predict_uri + " " + object_uri
                + " " + filter + "}";
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
        // json = json.replaceAll("@zh", "");
        // System.out.println(json);
        if(json.contains("#<end-of-file"))
            return null;

        // 过滤第一个匹配 ["s", "p", "o"]
        external_matcher.find();
        // 此时没有返回结果
        if(json.substring(json.indexOf(external_matcher.group()) + external_matcher.group().length()).length() < 30)
            return tripletList;
        while (external_matcher.find()) {
            String internal_string = external_matcher.group();
            internal_string = internal_string.replaceAll("\\\\\"@.*?\"", "\\\\\"\"");
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
                if(object.length() == 4){
                    object = triplet.getObjectURI();
                }else {
                    // .replaceAll("%(?![0-9a-fA-F]{2})", "%25") add by yaoleo to fix bug: java.lang.IllegalArgumentException: URLDecoder: Illegal hex characters in escape (%)
                    object = URLDecoder.decode(object.substring(2, object.length() - 2).replaceAll("%(?![0-9a-fA-F]{2})", "%25"), "UTF-8");
                }
            }
            Triplet t = new Triplet();
            t.setSubjectURI(URLDecoder.decode(subject, "UTF-8"));
            t.setPredicateURI(URLDecoder.decode(predict, "UTF-8"));
            t.setObjectURI(_decode_unicode(object));
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

    private static List<String> getChinese(String paramValue) {
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
    // 要考虑的问题：1 主语谓语filter是如何编码的，宾语filter是如何编码的
    //              2 如何确定宾语不被url编码替换
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

    private static String _createFilterString(String spo, List<String> stringList){
        String left = "", right = "";
        if(spo.equals("o")){
            left = "\"";
            right = "\"";
        }else{
            left = "<";
            right = ">";
        }
        StringBuilder ret = new StringBuilder("FILTER (?" + spo + " IN ( ");
        for(String s : stringList){
            ret.append(left).append(s).append(right).append(", ");
        }
        ret.delete(ret.length() - 2, ret.length());
        // ret = new StringBuilder(ret.substring(0, ret.length() - 2));
        ret.append(" ))");
        return ret.toString();
    }

    /**
     * 从Unicode中解码
     * @param unicodeStr Unicode编码后的字符串
     * @return 解码后的字符串
     */
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