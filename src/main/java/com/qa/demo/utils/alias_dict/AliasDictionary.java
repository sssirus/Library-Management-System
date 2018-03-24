package com.qa.demo.utils.alias_dict;


import com.qa.demo.utils.io.IOTool;

import java.io.*;
import java.util.*;

import static com.qa.demo.conf.FileConfig.ALIAS_DICTIONARY;
import static com.qa.demo.conf.FileConfig.NT_TRIPLETS;

/**
 * @author J.Y.Zhang
 * @create 2018-03-21
 * Function description:
 * 构建别名词典BuildAliasDict(String filepath, String outpath)
 * 查找别名词典
 * 利用NT_TRIPLETS训练 得到32343个别名的别名词典：data/templateRepository/aliasDict.txt
 **/
public class AliasDictionary {

    // 创建别名词典
    public static void BuildAliasDict(String filepath, String outpath) {
        ArrayList<String> aliasDict = new ArrayList<>();
        Map<String, Integer> dict_statistics = new HashMap<String, Integer>();
        dict_statistics.put("括号", 0);
        dict_statistics.put("书名号", 0);
        try {
            // read file content from file
            FileReader reader = new FileReader(filepath);
            BufferedReader br = new BufferedReader(reader);
            String line = null;

            while ((line = br.readLine()) != null) {
                // 得到实体名
                String entityURI = line.split(" ")[0];
                String entity = entityURI.split("/")[entityURI.split("/").length-1].split(">")[0];
                // 得到属性名
                String predicateURI = line.split(" ")[1];
                String predicate = predicateURI.split("/")[predicateURI.split("/").length-1].split(">")[0];
                // 得到属性值
                String valueURI = line.split(" ")[2];
                String value = valueURI.split("/")[valueURI.split("/").length-1].split(">")[0];
                if(value.length()>1) value = value.substring(1, value.length()-1); // 去掉引号

                // 过滤创建别名词典
                // 以“名”结尾: 别名、中文名、英文名、原名等。( 第 X 名、排名等除外) 。
                // 以“称”结尾: 别称、全称、简称、旧称等。( XX 职称等除外)
                // 以“名称”结尾: 中文名称、其它名称等。( 专辑名称、粉丝名称等除外)
                if(predicate.endsWith("名") || predicate.endsWith("称") || predicate.endsWith("名称")) {
                    if(  !predicate.equals("专辑名称") && !predicate.equals("粉丝名称") && !predicate.endsWith("排名")) {
                        // <http://zhishi.me/hudongbaike/resource/云猫> 石猫、石斑猫、草豹、小云豹、小云猫、豹皮
                        // <http://zhishi.me/hudongbaike/resource/麻梨> 麻梨子，黄皮梨
                        if (value.contains("、") || value.contains(",")) {
                            String[] valueList = value.split("、|,");
                            for (String val : valueList) {
                                if (val.length() > 0) aliasDict.add(entityURI + " " + val + "\r\n");
                            }
                        } else {
                            if (value.length() > 0) aliasDict.add(entityURI + " " + value + "\r\n");
                        }

                        if (!dict_statistics.containsKey(predicate)) {
                            dict_statistics.put(predicate, 1);
                        } else {
                            dict_statistics.put(predicate, dict_statistics.get(predicate) + 1);
                        }
                    }
                }
                // 实体名中有括号 括号外最为实体的别名
                if(entity.contains("(") && entity.contains(")")){
                    String bracket = entity.substring(entity.indexOf("("),entity.indexOf(")")+1);
                    String alias = entity.replace(bracket, "");
                    if(alias.length() > 0) aliasDict.add(entityURI + " " + alias + "\r\n");
                    dict_statistics.put("括号", dict_statistics.get("括号")+1);
                }
                // 书名号的别名是书名号内部的
                if(entity.contains("《") && entity.contains("》")){
                    String alias = entity.substring(entity.indexOf("《") + 1,entity.indexOf("》"));
                    if(alias.length() > 0) aliasDict.add(entityURI + " " + alias + "\r\n");
                    dict_statistics.put("书名号", dict_statistics.get("书名号")+1);
                }
            }
            System.out.println(dict_statistics);
            br.close();
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            IOTool.writeToFile(aliasDict, outpath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 在别名词典中查找实体对应的别名 返回别名列表
    public static ArrayList<String> SearchAliasDict(String entity, String dictpath) throws IOException {
        ArrayList<String> aliasList = new ArrayList<String>();
        // read file content from file
        FileReader reader = new FileReader(dictpath);
        BufferedReader br = new BufferedReader(reader);
        String line = null;

        while ((line = br.readLine()) != null) {
            String entityUrl = line.split(" ")[0];
            String aliasname = line.split(" ")[1];

            if (entityUrl.indexOf(entity) != -1){
                aliasList.add(aliasname);
            }
        }
        // 去除重复
        HashSet h  =   new HashSet(aliasList);
        aliasList.clear();
        aliasList.addAll(h);
        return  aliasList;
    }

//    public static void main(String[] args) throws IOException {
//        String dictpath = ALIAS_DICTIONARY;
//        String filepath = NT_TRIPLETS;
//        BuildAliasDict(filepath, dictpath);
//        ArrayList<String> aliases = SearchAliasDict("可蒙犬", dictpath);
//        System.out.println(aliases);
//    }
}
