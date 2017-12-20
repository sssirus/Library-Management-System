package com.qa.demo.utils.trainingcorpus;

import com.qa.demo.conf.Configuration;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *  Created time: 2017_08_18
 *  Author: Devin Hua
 *  Function description:
 *  Read questions and corresponding answers from txt.
 */
public class ExtractQuestionsFromText {

    public static ArrayList<String> readLinesFromFile(String filepath) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filepath),"utf-8"));
        String line = "";
        ArrayList<String> list = new ArrayList<String>();
        while ((line=br.readLine())!=null){
            list.add(line);
        }
        br.close();
        return list;
    }

    public static HashMap<Integer,HashMap<String,String>> getQuestionsFromFile(String filepath) throws IOException {

        ArrayList<String> list = readLinesFromFile(filepath);
        HashMap<Integer,HashMap<String,String>> question_contents_map = new HashMap<Integer,HashMap<String,String>>();
        int number = 0;
        for(String temp:list)
        {
            String[] temps = temp.split(Configuration.SPLITSTRING);
            if(temps.length<3)
                continue;
            else{
                HashMap<String, String> map = new HashMap<String, String>();
                int id = number++;
                String category = temps[0];
                String question = temps[1];
                String answer = temps[2];
                map.put(Configuration.CATEGORY,category);
                map.put(Configuration.QUESTION,question);
                map.put(Configuration.ANSWER,answer);
                question_contents_map.put(id,map);
            }
        }
        return question_contents_map;
    }

    public static void printQuestions(HashMap<Integer,HashMap<String,String>> map) {

        ArrayList<String> list = new ArrayList<String>();
        Iterator<Map.Entry<Integer,HashMap<String,String>>> iterator = map.entrySet().iterator();
        while(iterator.hasNext()){
            String temp = "";
            Map.Entry<Integer,HashMap<String,String>> entry = iterator.next();
            Integer key = entry.getKey();
            temp += "ID: "+key+"\r\n";
            HashMap<String,String> key_value_map = new HashMap<String,String>(entry.getValue());
            temp += "Question: "+key_value_map.get("QUESTION")+"\r\n";
            temp += "Answer: "+key_value_map.get("ANSWER")+"\r\n";
            temp += "Category: "+key_value_map.get("CATEGORY")+"\r\n";
            list.add(temp);
        }
        for(String temp:list){
            System.out.print(temp);
        }
    }
}
