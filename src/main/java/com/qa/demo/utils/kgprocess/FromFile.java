/*
Extract entity and property from file
Last updated by QM Ren on 2017/9/7
 */
package com.qa.demo.utils.kgprocess;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FromFile {

    public List<String> EntityFromFile(String source, String to)throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(source),"UTF-8"));
        OutputStreamWriter bw = new OutputStreamWriter(new FileOutputStream(to, false), "UTF-8");
        String line = "";
        List<String> list = new ArrayList<String>();
        Pattern PatternBaidu = Pattern.compile("<http://zhishi.me/baidubaike/resource/(.*?)> ");
        Pattern PatternHudong = Pattern.compile("<http://zhishi.me/hudongbaike/resource/(.*?)> ");
        Pattern PatternWiki= Pattern.compile("<http://zhishi.me/zhwiki/resource/(.*?)> ");
        while ((line=br.readLine())!=null){
            Matcher MatcherBaidu = PatternBaidu.matcher(line);
            Matcher MatcherHudong = PatternHudong.matcher(line);
            Matcher MatcherWiki = PatternWiki.matcher(line);
            if(MatcherBaidu.find()){
                list.add(MatcherBaidu.group(1));
            }
            if(MatcherHudong.find()){
                list.add(MatcherHudong.group(1));
            }
            if(MatcherWiki.find()){
                list.add(MatcherWiki.group(1));
            }
        }
        list = FromFile.dahash(list);
        for(String element:list){
            bw.write(element+"\r\n");
        }
        br.close();
        bw.close();
        return list;
    }

    public List<String> PropertyFromFile(String source,String to)throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(source),"UTF-8"));
        OutputStreamWriter bw = new OutputStreamWriter(new FileOutputStream(to,false),"UTF-8");
        String line = " ";
        List<String> list = new ArrayList<String>();
        Pattern PatternBaidu = Pattern.compile("<http://zhishi.me/baidubaike/property/(.*?)> ");
        Pattern PatternHudong = Pattern.compile("<http://zhishi.me/hudongbaike/property/(.*?)> ");
        Pattern PatternWiki= Pattern.compile("<http://zhishi.me/zhwiki/property/(.*?)> ");
        while ((line=br.readLine())!=null){
            Matcher MatcherBaidu = PatternBaidu.matcher(line);
            Matcher MatcherHudong = PatternHudong.matcher(line);
            Matcher MatcherWiki = PatternWiki.matcher(line);
            if(MatcherBaidu.find()){
                list.add(MatcherBaidu.group(1));;
            }
            if(MatcherHudong.find()){
                list.add(MatcherHudong.group(1));
            }
            if(MatcherWiki.find()){
                list.add(MatcherWiki.group(1));
            }
        }
        list = FromFile.dahash(list);
        for(String element:list){
            bw.write(element+"\r\n");
        }
        br.close();
        bw.close();
        return list;
    }

    public void AddFile(String To,String ...Source){
        try {
            OutputStreamWriter bw = new OutputStreamWriter(new FileOutputStream(To, false), "UTF-8");
            String source;
            String line;
            for (int i = 0; i < Source.length; i++) {
                source = Source[i];
                BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(source),"UTF-8"));
                while ((line=br.readLine())!=null){
                    bw.write(line+"\r\n");
                }
                br.close();
            }
            bw.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static List<String> dahash(List<String> list){
        Set set=new HashSet();
        List<String>  HashList = new ArrayList<String>();
        for(String element:list){
            set.add(element);
        }
        HashList.addAll(set);
        return HashList;
    }
}
