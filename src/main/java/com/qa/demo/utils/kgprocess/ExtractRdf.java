/*
Extract RDF
Last updated by QM Ren on 2017/9/7
 */
package com.qa.demo.utils.kgprocess;

import com.qa.demo.conf.Configuration;
import com.qa.demo.conf.FileConfig;
import com.qa.demo.dataStructure.PredicateType;
import com.qa.demo.dataStructure.Triplet;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractRdf {

    public static void main(String[] args){
        ExtractRdf E = new ExtractRdf();
        List<Triplet> ResultList = new ArrayList<>();
        String Source = FileConfig.PROPERTY_SOURCE_TOTAL;
        String To = FileConfig.RDFOBJ_RESULT;
        try {
            ResultList = E.ObjectRdf(Source);
            E.ObjTripletToFile(ResultList,To);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<Triplet> ObjectRdf(String Source)throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(Source),"UTF-8"));
        Triplet T = new Triplet();
        Pattern pattern_s_wiki = Pattern.compile("<http://zhishi.me/zhwiki/resource/(.*?)>");
        Pattern pattern_s_baidu = Pattern.compile("<http://zhishi.me/baidubaike/resource/(.*?)>");
        Pattern pattern_s_hudong = Pattern.compile("<http://zhishi.me/hudongbaike/resource/(.*?)>");
        Pattern pattern_p_wiki = Pattern.compile("<http://zhishi.me/zhwiki/property/(.*?)>");
        Pattern pattern_p_baidu = Pattern.compile("<http://zhishi.me/baidubaike/property/(.*?)>");
        Pattern pattern_p_hudong = Pattern.compile("<http://zhishi.me/hudongbaike/property/(.*?)>");
        Pattern pattern_o1 = Pattern.compile("<http://zhishi.me/zhwiki/resource/(.*?)>");
        Pattern pattern_o2 = Pattern.compile("<http://zhishi.me/baidubaike/resource/(.*?)>");
        Pattern pattern_o3 = Pattern.compile("<http://zhishi.me/hudongbaike/resource/(.*?)>");
        List<Triplet> ResultList = new ArrayList<>();
        String s;
        String string;
        int obj_flag = 1;
        while(( s = br.readLine())!=null){
            obj_flag = 1;
            s = ExtractRdf.unicode2string(s);
            string = s;
            Matcher matcher_s_baidu = pattern_s_baidu.matcher(s);
            Matcher matcher_s_hudong = pattern_s_hudong.matcher(s);
            Matcher matcher_s_wiki = pattern_s_wiki.matcher(s);
            Matcher matcher_p_baidu = pattern_p_baidu.matcher(s);
            Matcher matcher_p_hudong = pattern_p_hudong.matcher(s);
            Matcher matcher_p_wiki = pattern_p_wiki.matcher(s);
            Matcher matcher_o1 = pattern_o1.matcher(s);
            Matcher matcher_o2 = pattern_o2.matcher(s);
            Matcher matcher_o3 = pattern_o1.matcher(s);
            if(matcher_s_baidu.find()){
                s = s.replace(matcher_s_baidu.group(),"");
                matcher_s_baidu = pattern_s_baidu.matcher(s);
                matcher_s_hudong = pattern_s_hudong.matcher(s);
                matcher_s_wiki = pattern_s_wiki.matcher(s);
                if(matcher_s_baidu.find() == false && matcher_s_hudong.find() == false && matcher_s_wiki.find() == false){
                    obj_flag = 0;
                }
            }
            if(matcher_s_hudong.find()){
                s = s.replace(matcher_s_hudong.group(),"");
                matcher_s_baidu = pattern_s_baidu.matcher(s);
                matcher_s_hudong = pattern_s_hudong.matcher(s);
                matcher_s_wiki = pattern_s_wiki.matcher(s);
                if(matcher_s_baidu.find() == false && matcher_s_hudong.find() == false && matcher_s_wiki.find() == false){
                    obj_flag = 0;
                }
            }
            if(matcher_s_wiki.find()){
                s = s.replace(matcher_s_wiki.group(),"");
                matcher_s_baidu = pattern_s_baidu.matcher(s);
                matcher_s_hudong = pattern_s_hudong.matcher(s);
                matcher_s_wiki = pattern_s_wiki.matcher(s);
                if(matcher_s_baidu.find() == false && matcher_s_hudong.find() == false && matcher_s_wiki.find() == false){
                    obj_flag = 0;
                }
            }
            if(obj_flag == 1){
                matcher_s_baidu = pattern_s_baidu.matcher(string);
                matcher_s_hudong = pattern_s_hudong.matcher(string);
                matcher_s_wiki = pattern_s_wiki.matcher(string);
                T.setTripletString(string);
                if(matcher_s_baidu.find()){
                    T.setSubjectURI(matcher_s_baidu.group());
                    T.setSubjectName(matcher_s_baidu.group(1));
                    string = string.replace(matcher_s_baidu.group(),"");
                    matcher_p_baidu = pattern_p_baidu.matcher(string);
                    matcher_p_hudong = pattern_p_hudong.matcher(string);
                    matcher_p_wiki = pattern_p_wiki.matcher(string);
                    if(matcher_p_baidu.find()){
                        T.setPredicateURI(matcher_p_baidu.group());
                        T.setPredicateName(matcher_p_baidu.group(1));
                        T.setPredicateType(PredicateType.OBJECTPROPERTY);
                        string = string.replace(matcher_p_baidu.group(),"");
                        matcher_o1 = pattern_o1.matcher(string);
                        matcher_o2 = pattern_o2.matcher(string);
                        matcher_o3 = pattern_o3.matcher(string);
                        if(matcher_o1.find()){
                            T.setObjectName(matcher_o1.group(1));
                            T.setObjectURI(matcher_o1.group());
                        }
                        if(matcher_o2.find()){
                            T.setObjectName(matcher_o2.group(1));
                            T.setObjectURI(matcher_o2.group());
                        }
                        if(matcher_o3.find()){
                            T.setObjectName(matcher_o3.group(1));
                            T.setObjectURI(matcher_o3.group());
                        }
                    }
                    if(matcher_p_hudong.find()){
                        T.setPredicateURI(matcher_p_hudong.group());
                        T.setPredicateName(matcher_p_hudong.group(1));
                        T.setPredicateType(PredicateType.OBJECTPROPERTY);
                        string = string.replace(matcher_p_hudong.group(),"");
                        matcher_o1 = pattern_o1.matcher(string);
                        matcher_o2 = pattern_o2.matcher(string);
                        matcher_o3 = pattern_o3.matcher(string);
                        if(matcher_o1.find()){
                            T.setObjectName(matcher_o1.group(1));
                            T.setObjectURI(matcher_o1.group());
                        }
                        if(matcher_o2.find()){
                            T.setObjectName(matcher_o2.group(1));
                            T.setObjectURI(matcher_o2.group());
                        }
                        if(matcher_o3.find()){
                            T.setObjectName(matcher_o3.group(1));
                            T.setObjectURI(matcher_o3.group());
                        }
                    }
                    if(matcher_p_wiki.find()){
                        T.setPredicateURI(matcher_p_wiki.group());
                        T.setPredicateName(matcher_p_wiki.group(1));
                        T.setPredicateType(PredicateType.OBJECTPROPERTY);
                        string = string.replace(matcher_p_wiki.group(),"");
                        matcher_o1 = pattern_o1.matcher(string);
                        matcher_o2 = pattern_o2.matcher(string);
                        matcher_o3 = pattern_o3.matcher(string);
                        if(matcher_o1.find()){
                            T.setObjectName(matcher_o1.group(1));
                            T.setObjectURI(matcher_o1.group());
                        }
                        if(matcher_o2.find()){
                            T.setObjectName(matcher_o2.group(1));
                            T.setObjectURI(matcher_o2.group());
                        }
                        if(matcher_o3.find()){
                            T.setObjectName(matcher_o3.group(1));
                            T.setObjectURI(matcher_o3.group());
                        }
                    }
                }

                if(matcher_s_hudong.find()){
                    T.setSubjectURI(matcher_s_hudong.group());
                    T.setSubjectName(matcher_s_hudong.group(1));
                    string = string.replace(matcher_s_hudong.group(),"");
                    matcher_p_baidu = pattern_p_baidu.matcher(string);
                    matcher_p_hudong = pattern_p_hudong.matcher(string);
                    matcher_p_wiki = pattern_p_wiki.matcher(string);
                    if(matcher_p_baidu.find()){
                        T.setPredicateURI(matcher_p_baidu.group());
                        T.setPredicateName(matcher_p_baidu.group(1));
                        T.setPredicateType(PredicateType.OBJECTPROPERTY);
                        string = string.replace(matcher_p_baidu.group(),"");
                        matcher_o1 = pattern_o1.matcher(string);
                        matcher_o2 = pattern_o2.matcher(string);
                        matcher_o3 = pattern_o3.matcher(string);
                        if(matcher_o1.find()){
                            T.setObjectName(matcher_o1.group(1));
                            T.setObjectURI(matcher_o1.group());
                        }
                        if(matcher_o2.find()){
                            T.setObjectName(matcher_o2.group(1));
                            T.setObjectURI(matcher_o2.group());
                        }
                        if(matcher_o3.find()){
                            T.setObjectName(matcher_o3.group(1));
                            T.setObjectURI(matcher_o3.group());
                        }
                    }
                    if(matcher_p_hudong.find()){
                        T.setPredicateURI(matcher_p_hudong.group());
                        T.setPredicateName(matcher_p_hudong.group(1));
                        T.setPredicateType(PredicateType.OBJECTPROPERTY);
                        string = string.replace(matcher_p_hudong.group(),"");
                        matcher_o1 = pattern_o1.matcher(string);
                        matcher_o2 = pattern_o2.matcher(string);
                        matcher_o3 = pattern_o3.matcher(string);
                        if(matcher_o1.find()){
                            T.setObjectName(matcher_o1.group(1));
                            T.setObjectURI(matcher_o1.group());
                        }
                        if(matcher_o2.find()){
                            T.setObjectName(matcher_o2.group(1));
                            T.setObjectURI(matcher_o2.group());
                        }
                        if(matcher_o3.find()){
                            T.setObjectName(matcher_o3.group(1));
                            T.setObjectURI(matcher_o3.group());
                        }
                    }
                    if(matcher_p_wiki.find()){
                        T.setPredicateURI(matcher_p_wiki.group());
                        T.setPredicateName(matcher_p_wiki.group(1));
                        T.setPredicateType(PredicateType.OBJECTPROPERTY);
                        string = string.replace(matcher_p_wiki.group(),"");
                        matcher_o1 = pattern_o1.matcher(string);
                        matcher_o2 = pattern_o2.matcher(string);
                        matcher_o3 = pattern_o3.matcher(string);
                        if(matcher_o1.find()){
                            T.setObjectName(matcher_o1.group(1));
                            T.setObjectURI(matcher_o1.group());
                        }
                        if(matcher_o2.find()){
                            T.setObjectName(matcher_o2.group(1));
                            T.setObjectURI(matcher_o2.group());
                        }
                        if(matcher_o3.find()){
                            T.setObjectName(matcher_o3.group(1));
                            T.setObjectURI(matcher_o3.group());
                        }
                    }
                }

                if(matcher_s_wiki.find()){
                    T.setSubjectURI(matcher_s_wiki.group());
                    T.setSubjectName(matcher_s_wiki.group(1));
                    string = string.replace(matcher_s_wiki.group(),"");
                    matcher_p_baidu = pattern_p_baidu.matcher(string);
                    matcher_p_hudong = pattern_p_hudong.matcher(string);
                    matcher_p_wiki = pattern_p_wiki.matcher(string);
                    if(matcher_p_baidu.find()){
                        T.setPredicateURI(matcher_p_baidu.group());
                        T.setPredicateName(matcher_p_baidu.group(1));
                        T.setPredicateType(PredicateType.OBJECTPROPERTY);
                        string = string.replace(matcher_p_baidu.group(),"");
                        matcher_o1 = pattern_o1.matcher(string);
                        matcher_o2 = pattern_o2.matcher(string);
                        matcher_o3 = pattern_o3.matcher(string);
                        if(matcher_o1.find()){
                            T.setObjectName(matcher_o1.group(1));
                            T.setObjectURI(matcher_o1.group());
                        }
                        if(matcher_o2.find()){
                            T.setObjectName(matcher_o2.group(1));
                            T.setObjectURI(matcher_o2.group());
                        }
                        if(matcher_o3.find()){
                            T.setObjectName(matcher_o3.group(1));
                            T.setObjectURI(matcher_o3.group());
                        }
                    }
                    if(matcher_p_hudong.find()){
                        T.setPredicateURI(matcher_p_hudong.group());
                        T.setPredicateName(matcher_p_hudong.group(1));
                        T.setPredicateType(PredicateType.OBJECTPROPERTY);
                        string = string.replace(matcher_p_hudong.group(),"");
                        matcher_o1 = pattern_o1.matcher(string);
                        matcher_o2 = pattern_o2.matcher(string);
                        matcher_o3 = pattern_o3.matcher(string);
                        if(matcher_o1.find()){
                            T.setObjectName(matcher_o1.group(1));
                            T.setObjectURI(matcher_o1.group());
                        }
                        if(matcher_o2.find()){
                            T.setObjectName(matcher_o2.group(1));
                            T.setObjectURI(matcher_o2.group());
                        }
                        if(matcher_o3.find()){
                            T.setObjectName(matcher_o3.group(1));
                            T.setObjectURI(matcher_o3.group());
                        }
                    }
                    if(matcher_p_wiki.find()){
                        T.setPredicateURI(matcher_p_wiki.group());
                        T.setPredicateName(matcher_p_wiki.group(1));
                        T.setPredicateType(PredicateType.OBJECTPROPERTY);
                        string = string.replace(matcher_p_wiki.group(),"");
                        matcher_o1 = pattern_o1.matcher(string);
                        matcher_o2 = pattern_o2.matcher(string);
                        matcher_o3 = pattern_o3.matcher(string);
                        if(matcher_o1.find()){
                            T.setObjectName(matcher_o1.group(1));
                            T.setObjectURI(matcher_o1.group());
                        }
                        if(matcher_o2.find()){
                            T.setObjectName(matcher_o2.group(1));
                            T.setObjectURI(matcher_o2.group());
                        }
                        if(matcher_o3.find()){
                            T.setObjectName(matcher_o3.group(1));
                            T.setObjectURI(matcher_o3.group());
                        }
                    }
                }
            }
            ResultList.add(T);
        }
        return ResultList;
    }

    public List<Triplet> DataRdf(String Source)throws IOException{
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(Source),"UTF-8"));
        Triplet T = new Triplet() ;
        Pattern pattern_s_wiki = Pattern.compile("<http://zhishi.me/zhwiki/resource/(.*?)>");
        Pattern pattern_s_baidu = Pattern.compile("<http://zhishi.me/baidubaike/resource/(.*?)>");
        Pattern pattern_s_hudong = Pattern.compile("<http://zhishi.me/hudongbaike/resource/(.*?)>");
        Pattern pattern_p_wiki = Pattern.compile("<http://zhishi.me/zhwiki/property/(.*?)>");
        Pattern pattern_p_baidu = Pattern.compile("<http://zhishi.me/baidubaike/property/(.*?)>");
        Pattern pattern_p_hudong = Pattern.compile("<http://zhishi.me/hudongbaike/property/(.*?)>");
        Pattern pattern_o1 = Pattern.compile("<(.*?)>");
        Pattern pattern_o2 = Pattern.compile("\"(.*?)\"");
        List<Triplet> ResultList = new ArrayList<>();
        String s;
        String string;
        int data_flag = 0;
        while(( s = br.readLine())!=null){
            data_flag = 0;
            s = ExtractRdf.unicode2string(s);
            string = s;
            Matcher matcher_s_baidu = pattern_s_baidu.matcher(s);
            Matcher matcher_s_hudong = pattern_s_hudong.matcher(s);
            Matcher matcher_s_wiki = pattern_s_wiki.matcher(s);
            Matcher matcher_p_baidu = pattern_p_baidu.matcher(s);
            Matcher matcher_p_hudong = pattern_p_hudong.matcher(s);
            Matcher matcher_p_wiki = pattern_p_wiki.matcher(s);
            Matcher matcher_o1 = pattern_o1.matcher(s);
            Matcher matcher_o2 = pattern_o2.matcher(s);
            if(matcher_s_baidu.find()){
                s = s.replace(matcher_s_baidu.group(),"");
                matcher_s_baidu = pattern_s_baidu.matcher(s);
                matcher_s_hudong = pattern_s_hudong.matcher(s);
                matcher_s_wiki = pattern_s_wiki.matcher(s);
                if(matcher_s_baidu.find() == false && matcher_s_hudong.find() == false && matcher_s_wiki.find() == false){
                    data_flag = 1;
                }
            }
            if(matcher_s_hudong.find()){
                s = s.replace(matcher_s_hudong.group(),"");
                matcher_s_baidu = pattern_s_baidu.matcher(s);
                matcher_s_hudong = pattern_s_hudong.matcher(s);
                matcher_s_wiki = pattern_s_wiki.matcher(s);
                if(matcher_s_baidu.find() == false && matcher_s_hudong.find() == false && matcher_s_wiki.find() == false){
                    data_flag = 1;
                }
            }
            if(matcher_s_wiki.find()){
                s = s.replace(matcher_s_wiki.group(),"");
                matcher_s_baidu = pattern_s_baidu.matcher(s);
                matcher_s_hudong = pattern_s_hudong.matcher(s);
                matcher_s_wiki = pattern_s_wiki.matcher(s);
                if(matcher_s_baidu.find() == false && matcher_s_hudong.find() == false && matcher_s_wiki.find() == false){
                    data_flag = 1;
                }
            }
            if(data_flag ==1){
                matcher_s_baidu = pattern_s_baidu.matcher(string);
                matcher_s_hudong = pattern_s_hudong.matcher(string);
                matcher_s_wiki = pattern_s_wiki.matcher(string);
                T.setTripletString(string);
                if(matcher_s_baidu.find()){
                    T.setSubjectURI(matcher_s_baidu.group());
                    T.setSubjectName(matcher_s_baidu.group(1));
                    string = string.replace(matcher_s_baidu.group(),"");
                    matcher_p_baidu = pattern_p_baidu.matcher(string);
                    matcher_p_hudong = pattern_p_hudong.matcher(string);
                    matcher_p_wiki = pattern_p_wiki.matcher(string);
                    if(matcher_p_baidu.find()){
                        T.setPredicateURI(matcher_p_baidu.group());
                        T.setPredicateName(matcher_p_baidu.group(1));
                        T.setPredicateType(PredicateType.DATATYPEPROPERTY);
                        string = string.replace(matcher_p_baidu.group(),"");
                        matcher_o1 = pattern_o1.matcher(string);
                        matcher_o2 = pattern_o2.matcher(string);
                        if(matcher_o1.find()){
                            T.setObjectName(matcher_o1.group(1));
                        }
                        if(matcher_o2.find()){
                            T.setObjectName(matcher_o2.group(1));
                        }
                    }
                    if(matcher_p_hudong.find()){
                        T.setPredicateURI(matcher_p_hudong.group());
                        T.setPredicateName(matcher_p_hudong.group(1));
                        T.setPredicateType(PredicateType.DATATYPEPROPERTY);
                        string = string.replace(matcher_p_hudong.group(),"");
                        matcher_o1 = pattern_o1.matcher(string);
                        matcher_o2 = pattern_o2.matcher(string);
                        if(matcher_o1.find()){
                            T.setObjectName(matcher_o1.group(1));
                        }
                        if(matcher_o2.find()){
                            T.setObjectName(matcher_o2.group(1));
                        }
                    }
                    if(matcher_p_wiki.find()){
                        T.setPredicateURI(matcher_p_wiki.group());
                        T.setPredicateName(matcher_p_wiki.group(1));
                        T.setPredicateType(PredicateType.DATATYPEPROPERTY);
                        string = string.replace(matcher_p_wiki.group(),"");
                        matcher_o1 = pattern_o1.matcher(string);
                        matcher_o2 = pattern_o2.matcher(string);
                        if(matcher_o1.find()){
                            T.setObjectName(matcher_o1.group(1));
                        }
                        if(matcher_o2.find()){
                            T.setObjectName(matcher_o2.group(1));
                        }
                    }
                }

                if(matcher_s_hudong.find()){
                    T.setSubjectURI(matcher_s_hudong.group());
                    T.setSubjectName(matcher_s_hudong.group(1));
                    string = string.replace(matcher_s_hudong.group(),"");
                    matcher_p_baidu = pattern_p_baidu.matcher(string);
                    matcher_p_hudong = pattern_p_hudong.matcher(string);
                    matcher_p_wiki = pattern_p_wiki.matcher(string);
                    if(matcher_p_baidu.find()){
                        T.setPredicateURI(matcher_p_baidu.group());
                        T.setPredicateName(matcher_p_baidu.group(1));
                        T.setPredicateType(PredicateType.DATATYPEPROPERTY);
                        string = string.replace(matcher_p_baidu.group(),"");
                        matcher_o1 = pattern_o1.matcher(string);
                        matcher_o2 = pattern_o2.matcher(string);
                        if(matcher_o1.find()){
                            T.setObjectName(matcher_o1.group(1));
                        }
                        if(matcher_o2.find()){
                            T.setObjectName(matcher_o2.group(1));
                        }
                    }
                    if(matcher_p_hudong.find()){
                        T.setPredicateURI(matcher_p_hudong.group());
                        T.setPredicateName(matcher_p_hudong.group(1));
                        T.setPredicateType(PredicateType.DATATYPEPROPERTY);
                        string = string.replace(matcher_p_hudong.group(),"");
                        matcher_o1 = pattern_o1.matcher(string);
                        matcher_o2 = pattern_o2.matcher(string);
                        if(matcher_o1.find()){
                            T.setObjectName(matcher_o1.group(1));
                        }
                        if(matcher_o2.find()){
                            T.setObjectName(matcher_o2.group(1));
                        }
                    }
                    if(matcher_p_wiki.find()){
                        T.setPredicateURI(matcher_p_wiki.group());
                        T.setPredicateName(matcher_p_wiki.group(1));
                        T.setPredicateType(PredicateType.DATATYPEPROPERTY);
                        string = string.replace(matcher_p_wiki.group(),"");
                        matcher_o1 = pattern_o1.matcher(string);
                        matcher_o2 = pattern_o2.matcher(string);
                        if(matcher_o1.find()){
                            T.setObjectName(matcher_o1.group(1));
                        }
                        if(matcher_o2.find()){
                            T.setObjectName(matcher_o2.group(1));
                        }
                    }
                }

                if(matcher_s_wiki.find()){
                    T.setSubjectURI(matcher_s_wiki.group());
                    T.setSubjectName(matcher_s_wiki.group(1));
                    string = string.replace(matcher_s_wiki.group(),"");
                    matcher_p_baidu = pattern_p_baidu.matcher(string);
                    matcher_p_hudong = pattern_p_hudong.matcher(string);
                    matcher_p_wiki = pattern_p_wiki.matcher(string);
                    if(matcher_p_baidu.find()){
                        T.setPredicateURI(matcher_p_baidu.group());
                        T.setPredicateName(matcher_p_baidu.group(1));
                        T.setPredicateType(PredicateType.DATATYPEPROPERTY);
                        string = string.replace(matcher_p_baidu.group(),"");
                        matcher_o1 = pattern_o1.matcher(string);
                        matcher_o2 = pattern_o2.matcher(string);
                        if(matcher_o1.find()){
                            T.setObjectName(matcher_o1.group(1));
                        }
                        if(matcher_o2.find()){
                            T.setObjectName(matcher_o2.group(1));
                        }
                    }
                    if(matcher_p_hudong.find()){
                        T.setPredicateURI(matcher_p_hudong.group());
                        T.setPredicateName(matcher_p_hudong.group(1));
                        T.setPredicateType(PredicateType.DATATYPEPROPERTY);
                        string = string.replace(matcher_p_hudong.group(),"");
                        matcher_o1 = pattern_o1.matcher(string);
                        matcher_o2 = pattern_o2.matcher(string);
                        if(matcher_o1.find()){
                            T.setObjectName(matcher_o1.group(1));
                        }
                        if(matcher_o2.find()){
                            T.setObjectName(matcher_o2.group(1));
                        }
                    }
                    if(matcher_p_wiki.find()){
                        T.setPredicateURI(matcher_p_wiki.group());
                        T.setPredicateName(matcher_p_wiki.group(1));
                        T.setPredicateType(PredicateType.DATATYPEPROPERTY);
                        string = string.replace(matcher_p_wiki.group(),"");
                        matcher_o1 = pattern_o1.matcher(string);
                        matcher_o2 = pattern_o2.matcher(string);
                        if(matcher_o1.find()){
                            T.setObjectName(matcher_o1.group(1));
                        }
                        if(matcher_o2.find()){
                            T.setObjectName(matcher_o2.group(1));
                        }
                    }
                }
            }
            ResultList.add(T);
        }
        return ResultList;
    }

    public void ObjTripletToFile(List<Triplet> list, String To)throws IOException{
        OutputStreamWriter bw = new OutputStreamWriter(new FileOutputStream(To,false),"UTF-8");
        Triplet T = new Triplet();
        for(Triplet element:list){
            if(element.getPredicateType() == PredicateType.OBJECTPROPERTY){
                bw.write("<"+T.getSubjectURI()+ Configuration.SPLITSTRING+T.getPredicateURI()+ Configuration.SPLITSTRING+T.getObjectURI()+ Configuration.SPLITSTRING+T.getSubjectName()+ Configuration.SPLITSTRING+T.getPredicateName()+ Configuration.SPLITSTRING+T.getObjectName()+">\r\n");
            }
        }
        bw.close();
    }

    public void DataTripletToFile(List<Triplet> list, String To)throws IOException{
        OutputStreamWriter bw = new OutputStreamWriter(new FileOutputStream(To,false),"UTF-8");
        Triplet T =  new Triplet();
        for(Triplet element:list){
            if(element.getPredicateType() == PredicateType.DATATYPEPROPERTY){
                bw.write("<"+T.getSubjectURI()+ Configuration.SPLITSTRING+T.getPredicateURI()+ Configuration.SPLITSTRING+T.getObjectName()+ Configuration.SPLITSTRING+T.getSubjectName()+ Configuration.SPLITSTRING+T.getPredicateName()+">\r\n");
            }
        }
        bw.close();
    }

    public static String unicode2string(String str) {
        Charset set = Charset.forName("UTF-16");
        Pattern p = Pattern.compile("\\\\u([0-9a-fA-F]{4})");
        Matcher m = p.matcher( str );
        int start = 0 ;
        int start2 = 0 ;
        StringBuffer sb = new StringBuffer();
        while( m.find( start ) ) {
            start2 = m.start() ;
            if( start2 > start ){
                String seg = str.substring(start, start2) ;
                sb.append( seg );
            }
            String code = m.group( 1 );
            int i = Integer.valueOf( code , 16 );
            byte[] bb = new byte[ 4 ] ;
            bb[ 0 ] = (byte) ((i >> 8) & 0xFF );
            bb[ 1 ] = (byte) ( i & 0xFF ) ;
            ByteBuffer b = ByteBuffer.wrap(bb);
            sb.append( String.valueOf( set.decode(b) ).trim() );
            start = m.end() ;
        }
        start2 = str.length() ;
        if( start2 > start ){
            String seg = str.substring(start, start2) ;
            sb.append( seg );
        }
        return sb.toString() ;
    }
}
