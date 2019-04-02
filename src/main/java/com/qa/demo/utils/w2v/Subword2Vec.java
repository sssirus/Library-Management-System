package com.qa.demo.utils.w2v;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import com.qa.demo.utils.w2v.Word2VEC;

import static com.qa.demo.conf.FileConfig.*;

public class Subword2Vec {

    //计算词对应字的向量
    public static Result calVec(Result result) throws IOException {
        int label = result.label;
        String word = result.word;
        if (label == 0) {
            Word2VEC w1 = new Word2VEC();
            w1.loadJavaModel(normalwordsVector);
            result.vec = w1.getWordVector(word);
        } else if (label == 1) {
            Word2VEC w1 = new Word2VEC();
            w1.loadJavaModel(subwordsVector);
            result.vec = w1.getWordVector(word);
        } else if (label == 2) {
            if (useList(result.subwords_for_check, "None@@")) {
                if (useList(result.subwords_for_check, "None")) {
                    double[] vsum = new double[200];
                    Word2VEC w1 = new Word2VEC();
                    w1.loadJavaModel(subwordsVector);
                    for (int i = 0; i < result.subwords_for_check.size(); i++) {
                        result.vec = w1.getWordVector(result.subwords_for_check.get(i));
                        result.vec = sum(vsum, result.vec);
                    }
                    int r;
//                    for (r = 0; r < 200; r++) {
//                        System.out.print(vsum[r]);
//                    }

                } else {
                    System.out.print("This word not in subword list!");
                }

            } else {
                System.out.print("This word not in subword list!");
            }
        }
        return result;
    }

    public static Result check_words(String word) throws IOException {
        List<String> words = new ArrayList<String>();
        List<String> subwords = new ArrayList<String>();
        List<String> subwordsplus = new ArrayList<String>();

        String[] subword_units = null;

        String lines = null;
        FileInputStream in = null;
        BufferedReader br = null;

        in = new FileInputStream(wiki5000seg);
        br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
        lines = br.readLine();
        while(lines != null && !lines.equals("")){
            if(lines.contains(":")){
                lines = prepare(lines);
                words.add(lines);
            }
            lines = br.readLine();
        }
        br.close();

        in = new FileInputStream(wiki5000segbpe);
        br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
        lines = br.readLine();
        while(lines != null && !lines.equals("")){
            if(lines.contains(":")){
                lines = prepare(lines);
                subwords.add(lines);
                subwordsplus.add(lines);
            }
            lines = br.readLine();
        }
        br.close();

        Result res = new Result();
        res.word = word;
        if(words.contains(word)) {
            res.subwords_for_check.add(word);
            res.label = 0;
        } else {
            if(subwords.contains(word)) {
                res.subwords_for_check.add(word);
                res.label = 1;
            } else {
                subword_units = check_subwords(word).split("[ ]");
                boolean flag = false;
                for(String s : subword_units){
                    if(s.equals("None")){
                        flag = true;
                        break;
                    }
                }
                if(flag == true) {
                    System.out.println("Not find this words in subword units");
                    res.subwords_for_check.add("None");
                } else {
                    for(String s : subword_units) {
                        res.subwords_for_check.add(s);
                    }
                }
                res.label = 2;
            }
        }
        return res;
    }

    public static String check_subwords(String word) throws IOException{//递归 每次都要读文件0.0
        List<String> subwords = new ArrayList<String>();
        List<String> subwordsplus = new ArrayList<String>();

        String lines = null;
        String subwordresult = null;
        String subwordresultcut = null;

        FileInputStream in = null;
        BufferedReader br = null;

        in = new FileInputStream(wiki5000segbpe);
        br = new BufferedReader(new InputStreamReader(in,"UTF-8"));

        lines = br.readLine();
        while(lines != null && !lines.equals("")){
            if(lines.contains(":")){
                lines = prepare(lines);
                subwords.add(lines);
                subwordsplus.add(lines);
            }
            lines = br.readLine();
        }
        br.close();

        int length = word.length();

        for(int i = 0; i < length; i++){
            subwordresult = "";
            if(subwordsplus.contains(word.substring(0, length - i)+"@@") && subwordsplus.contains(word.substring(length - i, length))){
                subwordresult = subwordresult + word.substring(0,length - i) + "@@ ";
                subwordresult = subwordresult + word.substring(length - i, length);
//                System.out.println(subwordresult + " :1");
                return subwordresult;
            }
            else if(subwordsplus.contains(word.substring(0, length - i)+"@@") && !subwordsplus.contains(word.substring(length - i, length))) {
                subwordresult = subwordresult + word.substring(0, length - i) + "@@ ";
                subwordresultcut = check_subwords(word.substring(length - i, length));
                subwordresult = subwordresult + subwordresultcut;
//                System.out.println(subwordresult + " :2");
                return subwordresult;
            }
            else if(!subwordsplus.contains(word.substring(0,length - i)+"@@") && subwordsplus.contains(word.substring(length - i, length))){
                subwordresultcut = check_subwords(word.substring(0,length - i));
                subwordresult = subwordresult + subwordresultcut + "@@ ";
                subwordresult = subwordresult + word.substring(length - i, length);
//                System.out.println(subwordresult + " :3");
                return subwordresult.replaceAll("  ", " ");
            } else {
//                System.out.println(word.substring(0,length - i) + "@@ and " + word.substring(length - i, length) + " not in subword list");
                continue;
            }
        }

        return "None";
    }

    //预处理
    public static String prepare(String lines){
        String line = null;
        line = lines.split(":")[0];
        line = line.replaceAll("\"", "");
        line = line.replaceAll("[ ]", "");
        line = line.replaceAll("\n", "");
        return line;
    }

    // 向量求和
    public static double[] sum(double[] v1, double[] v2){
        int length = v1.length;
        for(int i = 0; i < length; i++){
            v1[i] = v1[i] + v2[i];
        }
        return v1;
    }


    public static boolean useList(List<String> subwords_for_check,String targetValue){
        int check = 0;
        for(int i = 0; i < subwords_for_check.size(); i++){
            String str = subwords_for_check.get(i);
            String str1 = targetValue;
            if(str.equals(str1)){
                check = 1;
            }
        }
        if(check == 1){
            return false;
        }else{
            return true;
        }
    }

    // 返回测试谓词链指和谓词的相似度 输入是谓词链指列表 和 谓词
    public static void calSim(ArrayList<ArrayList<String>> preList, String pre){
        for(ArrayList<String> predicateMentionWords : preList)
        {
            for(String predicateMentionWord: predicateMentionWords){
                try {
                    Result res = check_words(predicateMentionWord);
                    res = calVec(res);
                    Result presult = check_words(pre);
                    presult = calVec(presult);
                    double sim = Word2VecGensimModel.calcVecSimilarity(res.vec, presult.vec);
                    System.out.println("谓词指称"+predicateMentionWord+ "谓词"+ pre +"相似度：" +sim);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
