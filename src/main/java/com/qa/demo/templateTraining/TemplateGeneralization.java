package com.qa.demo.templateTraining;
/**
 *  Created time: 2017_10_05
 *  Author: Devin Hua
 *  Function description:
 *  To get thesaurus for predicates from templates.
 */

import com.qa.demo.dataStructure.QuestionTemplate;
import com.qa.demo.questionAnalysis.Segmentation;

import java.io.*;
import java.util.*;

import static com.qa.demo.conf.FileConfig.*;

public class TemplateGeneralization {

    //单例模式，全局访问模板的同义词组；
    private static TemplateGeneralization uniqueInstance;
    //将一个谓词映射的不同问法，作为模板，加到集合中；
    private static HashMap<String, ArrayList<String>> predicateTemplateMap;
    ///将一个谓词映射的不同问法，分词之后作为模板，加到集合中；
    private static HashMap<String, HashSet<String>> predicateTemplateSegmentationMap;
    //将一个谓词映射的不同问法，进行分词、去除停用词，得到一组同义词，加到集合中；
    private static HashMap<String, HashSet<String>> predicateSynonymsMap;



    public HashMap<String, ArrayList<String>> getPredicateTemplateMap() {
        return predicateTemplateMap;
    }

    public HashMap<String, HashSet<String>> getPredicateTemplateSegmentationMap() {
        return predicateTemplateSegmentationMap;
    }

    public HashMap<String, HashSet<String>> getPredicateSynonymsMap() {
        return predicateSynonymsMap;
    }



    private TemplateGeneralization(){

       this.predicateTemplateMap = getPredicateTemplateMappings();
       this.predicateSynonymsMap = getPredicateSynonymsMappings();
       this.predicateTemplateSegmentationMap = getPredicateTemplateSegmentationMappings();
    }

    public static synchronized TemplateGeneralization getInstance()
    {
        if(uniqueInstance==null)
        {
            uniqueInstance = new TemplateGeneralization();
        }
        return uniqueInstance;
    }

    private static HashMap<String, HashSet<String>> getPredicateTemplateSegmentationMappings(){
        //取得模板库，形式为每一个谓词对应了几种问法的分词组合;
        HashMap<String, HashSet<String>> predicateTemplateSegmentationMap
                = new HashMap<>();
        HashMap<String, ArrayList<String>> map = predicateTemplateMap;
        Iterator it = map.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<String, ArrayList<String>> entry = (Map.Entry) (it.next());
            String predicate_name = entry.getKey();
            ArrayList<String> templates = entry.getValue();
            HashSet<String> set = new HashSet<>();
            for (String temp : templates) {
                String tokenOutput = "";
                Segmentation.segmentation(temp);
                for (String token : Segmentation.getTokens())
                {
                    tokenOutput += token;
                    tokenOutput += " ";
                }
                if(set.isEmpty()||!set.contains(tokenOutput))
                {
                    set.add(tokenOutput);
                }
            }
            predicateTemplateSegmentationMap.put(predicate_name, set);
        }
        return predicateTemplateSegmentationMap;
    }


    private static HashMap<String, ArrayList<String>> getPredicateTemplateMappings(){
        //取得模板库，形式为每一个谓词对应了几种问法;
        HashSet<QuestionTemplate> qTemplates =
                TemplateFromTripletsClient.getInstance().getTemplateRepository();
        HashMap<String, ArrayList<String>> map = new HashMap<>();
        for(QuestionTemplate qtemplate : qTemplates)
        {
            String predicateName = qtemplate.getPredicate().getKgPredicateName();
            String templateString = qtemplate.getTemplateString();
            if(map.isEmpty()||!map.containsKey(predicateName))
            {
                ArrayList<String> list = new ArrayList<String>();
                list.add(qtemplate.getTemplateString());
                map.put(predicateName, list);
            }
            else if(map.containsKey(predicateName))
            {
                ArrayList<String> list = map.get(predicateName);
                list.add(templateString);
                map.put(predicateName, list);
            }
        }
        return map;
    }

    private static HashSet<String> addSynonym(HashSet<String> s)
    {
        HashSet<String> synonyms = new HashSet<>();
        for(String temp : s)
        {
            synonyms.add(temp);
            if(temp.equalsIgnoreCase("哪里")||temp.equalsIgnoreCase("哪儿")
                    ||temp.equalsIgnoreCase("哪边"))
            {
                synonyms.add("哪里");
                synonyms.add("哪");
                synonyms.add("哪儿");
                synonyms.add("哪边");
            }
            else if(temp.equalsIgnoreCase("时代")||temp.equalsIgnoreCase("年代")
                    ||temp.equalsIgnoreCase("哪年"))
            {
                synonyms.add("时代");
                synonyms.add("年代");
                synonyms.add("哪年");
            }
            else if(temp.equalsIgnoreCase("名字")||temp.equalsIgnoreCase("名称")
                    ||temp.equalsIgnoreCase("名")||temp.equalsIgnoreCase("姓名"))
            {
                synonyms.add("名字");
                synonyms.add("名称");
                synonyms.add("名");
                synonyms.add("姓名");
            }
            else if(temp.equalsIgnoreCase("使用")||temp.equalsIgnoreCase("利用"))
            {
                synonyms.add("使用");
                synonyms.add("利用");
            }
            else if(temp.equalsIgnoreCase("功效")||temp.equalsIgnoreCase("效果")
                    ||temp.equalsIgnoreCase("作用")||temp.equalsIgnoreCase("功用"))
            {
                synonyms.add("功效");
                synonyms.add("效果");
                synonyms.add("作用");
                synonyms.add("功用");
            }
        }
        return synonyms;
    }

    //将一个谓词映射的不同问法，进行分词、去除停用词，得到一组同义词，加到集合中；
    private static HashMap<String, HashSet<String>> getPredicateSynonymsMappings(){

        HashMap<String, ArrayList<String>> map = predicateTemplateMap;
        HashMap<String, HashSet<String>> predicateSynonymsMap = new HashMap<>();
        Iterator it = map.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<String, ArrayList<String>> entry = (Map.Entry) (it.next());
            String key = entry.getKey();
            ArrayList<String> list = entry.getValue();
            HashSet<String> synonyms = new HashSet<String>();
            for (String temp : list) {
                Segmentation.segmentation(temp);
                for (String token : Segmentation.getTokens()) {
                    synonyms.add(token);
                }
            }
            synonyms = addSynonym(synonyms);
            predicateSynonymsMap.put(key, synonyms);
        }
        return predicateSynonymsMap;
    }

    public void printPredicateSynonymsMappings() throws IOException {

        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(TEMPLATE_SYNONYM_REPOSITORY,false), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        HashMap<String, HashSet<String>> predicateSynonymsMap = this.getPredicateSynonymsMap();
        Iterator it = predicateSynonymsMap.entrySet().iterator();
        while(it.hasNext())
        {
            Map.Entry<String, HashSet<String>> entry = (Map.Entry)(it.next());
            String key = entry.getKey();
            HashSet<String> synonyms = entry.getValue();
            System.out.print("PREDICATE IS: " + key + "\r\n");
            out.write("PREDICATE IS: " + key + "\r\n");
            String tokenOutput = "";
            for(String temp : synonyms)
            {
                tokenOutput += temp;
                tokenOutput += " ";
            }
            tokenOutput += "\r\n";
            System.out.print(tokenOutput);
            out.write(tokenOutput);
        }
        out.close();
    }


    public void printPredicateTemplateMappings() throws IOException {

        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(TEMPLATE_SEGMENTATION_REPOSITORY,false), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        HashMap<String, ArrayList<String>> map = this.getPredicateTemplateMap();
        Iterator it = map.entrySet().iterator();
        while(it.hasNext())
        {
            Map.Entry<String, ArrayList<String>> entry = (Map.Entry)(it.next());
            String key = entry.getKey();
            ArrayList<String> list = entry.getValue();
            System.out.print("PREDICATE IS: " + key + "\r\n");
            out.write("PREDICATE IS: " + key + "\r\n");
            for(String temp : list)
            {
                System.out.print(temp + "\r\n");
                out.write(temp + "\r\n");
                String tokenOutput = "";
                Segmentation.segmentation(temp);
                for(String token : Segmentation.getTokens())
                {
                    tokenOutput += token;
                    tokenOutput += " ";
                }
                tokenOutput += "\r\n";
                System.out.print(tokenOutput);
                out.write(tokenOutput);
            }
            System.out.print("\r\n");
            out.write("\r\n");
        }
        out.close();
    }

    public void printPredicateTemplateSegmentationMappings() throws IOException {

        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(TEMPLATE_SEGMENTATION_KEYWORDS_REPOSITORY,false), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        HashMap<String, HashSet<String>> map = this.getPredicateTemplateSegmentationMap();
        Iterator it = map.entrySet().iterator();
        while(it.hasNext())
        {
            Map.Entry<String, HashSet<String>> entry = (Map.Entry)(it.next());
            String key = entry.getKey();
            HashSet<String> set = entry.getValue();
            System.out.print("PREDICATE IS: " + key + "\r\n");
            out.write("PREDICATE IS: " + key + "\r\n");
            for(String temp : set)
            {
                System.out.print(temp + "\r\n");
                out.write(temp + "\r\n");
            }
            System.out.print("\r\n");
            out.write("\r\n");
        }
        out.close();
    }
}
