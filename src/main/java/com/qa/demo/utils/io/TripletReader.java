package com.qa.demo.utils.io;

import com.qa.demo.dataStructure.Triplet;

import java.io.*;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.qa.demo.utils.io.WebServiceTool._decode_unicode;

public class TripletReader {
    /**
     * 从 NT_triplets.nt 文件中获取三元组列表
     *
     * @param filepath  文件目录
     * @return 文件中的所有三元祖
     * @throws IOException 文件打开失败，或者读取失败
     */
    static List<Triplet> getTripletsFromNT_Triplets(String filepath) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                new FileInputStream(new File(filepath))));
        String str;

        List<Triplet> tripletList = new ArrayList<>();
        String subject, predict, object;
        while ((str = bufferedReader.readLine()) != null) {
            str = str.replaceAll("\n", "");
            try{
                char[] chars = str.toCharArray();
                int left = 0, right;

                while(chars[left] != '<') ++left;
                right = left + 1;
                while(chars[right] != '>') ++right;
                subject = str.substring(left, right + 1);

                left = right + 1;
                while(chars[left] != '<') ++left;
                right = left + 1;
                while(chars[right] != '>') ++right;
                predict = str.substring(left, right + 1);

                left = right + 1;
                while(chars[left] != '\"') ++left;
                right = left + 1;
                while(chars[right] != '\"') ++right;
                object = str.substring(left, right + 1);

                Triplet triplet = new Triplet();
                triplet.setSubjectURI(subject);
                triplet.setPredicateURI(predict);
                triplet.setObjectURI(object);
                tripletList.add(triplet);
            }
            catch(Exception e){
                // System.out.println(123);
            }

        }
        return tripletList;
    }

    /**
     * 从 zhwiki_abstracts.nt 文件中获取解码后的三元组列表
     *
     * @param filepath  文件目录
     * @return 文件中的所有三元祖
     * @throws IOException 文件打开失败，或者读取失败
     */
    public static List<Triplet> analysys_zhwiki_abstracts(String filepath) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                new FileInputStream(new File(filepath))));
        String str;

        List<Triplet> tripletList = new LinkedList<>();
        String subject = "", predict = "", object = "";

        while ((str = bufferedReader.readLine()) != null) {
            try{
                char[] chars = str.toCharArray();
                int left = 0, right;

                while(chars[left] != '<') ++left;
                right = left + 1;
                while(chars[right] != '>') ++right;
                subject = str.substring(left, right + 1);

                left = right + 1;
                while(chars[left] != '<') ++left;
                right = left + 1;
                while(chars[right] != '>') ++right;
                predict = str.substring(left, right + 1);

                left = right + 1;
                while(chars[left] != '\"') ++left;
                right = left + 1;
                while(chars[right] != '\"') ++right;
                object = str.substring(left, right + 1);


                subject = URLDecoder.decode(subject, "UTF-8").replaceAll("\n", "");
                predict = URLDecoder.decode(predict, "UTF-8").replaceAll("\n", "");
                object = _decode_unicode(object).replaceAll("\n", "");

                Triplet triplet = new Triplet();
                triplet.setSubjectURI(subject);
                triplet.setPredicateURI(predict);
                triplet.setObjectURI(object);
                tripletList.add(triplet);
            }catch(Exception e){
                // System.out.println(1123);
            }


        }
        return tripletList;
    }


    public static void main(String[] args) throws IOException {
        File file = new File("src/main/resources/data/kbfile/abc.txt");
        File out = new File("src/main/resources/data/kbfile/out1.txt");
        if(out.exists() && out.isFile()){
            out.delete();
            out.createNewFile();
        }
        if(!out.exists())
            out.createNewFile();
        // 是什么_____刺桐花有什么其他别称？_____象牙红、木本象牙红_____http://zhishi.me/baidubaike/resource/刺桐花_____http://zhishi.me/baidubaike/property/别称_____象牙红、木本象牙红
        //-
        //主语uri
        //谓语uri
        //宾语uri
        //问题
        //-
        String subject = "", predict = "", object = "", answer = "";
        List<String> questions = new LinkedList<>();
        String str;
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(out));
        String line;
        String split = "_____";
        while((str = bufferedReader.readLine()) != null){
            if(str.equals("-")){
                str = bufferedReader.readLine();
                subject = str.substring(1, str.length() - 1);
                str = bufferedReader.readLine();
                predict = str.substring(1, str.length() - 1);
                str = bufferedReader.readLine();
                object = str.substring(1, str.length() - 1);
                answer = object;
                str = bufferedReader.readLine();
                while(!str.equals("-")){
                    questions.add(str);
                    str = bufferedReader.readLine();
                }
            }

            for(String s : questions){
                line = "是什么" + split + s + split + answer + split
                        + subject + split + predict + split + object + "\n";
                bufferedWriter.write(line);
                System.out.println(line = "是什么" + split + s + split + answer + split
                        + subject + split + predict + split + object);
            }
            questions.clear();
        }
    }
}
