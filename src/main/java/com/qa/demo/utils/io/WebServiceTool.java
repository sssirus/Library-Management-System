package com.qa.demo.utils.io;

import com.qa.demo.dataStructure.Triplet;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebServiceTool {
    /**
     * 从Unicode中解码
     * @param unicodeStr Unicode编码后的字符串
     * @return 解码后的字符串
     */
    public static String _decode_unicode(String unicodeStr) {
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

    /**
     * 从指定字符串中获得中文
     * @param paramValue 需要提取中文的字符串
     * @return 字符串中的中文List
     */
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

    /**
     * 将返回的 json 数据解析为 List<Triplet>
     *
     * @param triplet 查询三元组
     * @param json json 数据
     * @return 解析后的三元组链表
     */
    static List<Triplet> _analysisJson(Triplet triplet, String json) throws UnsupportedEncodingException {

        List<Triplet> tripletList = new ArrayList<>();

        // 用于匹配外部的 json
        // 匹配格式为：["subject","predict","object"]
        Pattern external = Pattern.compile("\\[(.*?)\\]");

        // 用于匹配内部的 json
        // 格式为："subject/predict/object"
        // Pattern internal = Pattern.compile("(\"<.*?>\")|(null)|(\"\\\\\".*?\\\\\"\")");
        Matcher external_matcher = external.matcher(json);

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
            // 将 \"@zh" 替换为 \""
            internal_string = internal_string.replaceAll("\\\\\"@.*?\"", "\\\\\"\"");

            // System.out.println(internal_string);
            Triplet t = getTripletFromEncodedJson(triplet, internal_string);
            tripletList.add(t);
        }

        return tripletList;
    }

    static Triplet getTripletFromEncodedJson(Triplet triplet, String internal_string) throws UnsupportedEncodingException {
        // Pattern internal = Pattern.compile("(<.*?>)|(null)|(\\\\\".*?\\\\\")");
        Pattern internal = Pattern.compile("(\"<.*?>\")|(null)|(\"\\\\\".*?\\\\\"\")");
        Matcher internal_matcher;
        internal_matcher = internal.matcher(internal_string);

        internal_matcher.find();
        String subject = internal_matcher.group().replaceAll("\\\\\"", "\"");
        if(subject.length() == 4){
            subject = triplet.getSubjectURI();
        }else{
            subject = URLDecoder.decode(subject.substring(2, subject.length() - 2), "UTF-8");
        }

        internal_matcher.find();
        String predict = internal_matcher.group().replaceAll("\\\\\"", "\"");
        if(predict.length() == 4){
            predict = triplet.getPredicateURI();
        }else {
            predict = URLDecoder.decode(predict.substring(2, predict.length() - 2), "UTF-8");
        }

        internal_matcher.find();
        String object = internal_matcher.group().replaceAll("\\\\\"", "\"")
                .replaceAll("\\\\\\\\", "\\\\");

        if(object.length() == 4){
            object = triplet.getObjectURI();
        }else {
            try{
                object = object.substring(2, object.length() - 2);
                if(object.charAt(0) != '\\')
                    object = URLDecoder.decode(object, "UTF-8");
                // ""\u88AB\u5B50\u690D\u7269\u95E8""
                // ""\u82F9\u679C\u516C\u53F8\uFF08Apple Inc.\uFF0CNASDAQ\uFF1AAAPL\uFF0CLSE\uFF1AACP\uFF09\uFF0C\u539F\u79F0\u82F9\u679C\u7535\u8111\u516C\u53F8\uFF08Apple Computer, Inc.\uFF09\u603B\u90E8\u4F4D\u4E8E\u7F8E\u56FD\u52A0\u5229\u798F\u5C3C\u4E9A\u7684\u5E93\u6BD4\u63D0\u8BFA\uFF0C\u6838\u5FC3\u4E1A\u52A1\u662F\u7535\u5B50\u79D1\u6280\u4EA7\u54C1\uFF0C\u76EE\u524D\u5168\u7403\u7535\u8111\u5E02\u573A\u5360\u6709\u7387\u4E3A3.8%\u3002\u82F9\u679C\u7684Apple II\u4E8E1970\u5E74\u4EE3\u52A9\u957F\u4E86\u4E2A\u4EBA\u7535\u8111\u9769\u547D\uFF0C\u5176\u540E\u7684Macintosh\u63A5\u529B\u4E8E1980\u5E74\u4EE3\u6301\u7EED\u53D1\u5C55\u3002\u6700\u77E5\u540D\u7684\u4EA7\u54C1\u662F\u5176\u51FA\u54C1\u7684Apple II\u3001Macintosh\u7535\u8111\u3001iPod\u6570\u4F4D\u97F3\u4E50\u64AD\u653E\u5668\u3001iTunes\u97F3\u4E50\u5546\u5E97\u548CiPhone\u667A\u80FD\u624B\u673A\uFF0C\u5B83\u5728\u9AD8\u79D1\u6280\u4F01\u4E1A\u4E2D\u4EE5\u521B\u65B0\u800C\u95FB\u540D\u3002\u82F9\u679C\u516C\u53F8\u4E8E2007\u5E741\u67089\u65E5\u65E7\u91D1\u5C71\u7684Macworld Expo\u4E0A\u5BA3\u5E03\u6539\u540D\u3002""
            }
            catch (Exception e){
                System.err.println("URL 解码出现异常");
            }
        }

        Triplet t = new Triplet();
        t.setSubjectURI(URLDecoder.decode(subject, "UTF-8"));
        t.setPredicateURI(URLDecoder.decode(predict, "UTF-8"));
        t.setObjectURI(_decode_unicode(object));
        return t;
    }
}
