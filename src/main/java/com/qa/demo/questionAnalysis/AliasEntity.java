package com.qa.demo.questionAnalysis;

import com.qa.demo.dataStructure.Entity;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static com.qa.demo.conf.FileConfig.ALIAS_DICTIONARY;

/**
 * @author J.Y.Zhang
 * @create 2018-03-24
 * Function description:
 * 实体识别那部分 好像是把问题拆成单个字
 * 只要数据库里的实体名包括问题里的任何字 那么这个实体就是候选实体了 然后打分 筛选
 * 类似的方法 找别名 别名库里的别名只要和问题有交集 那么就把他对应的实体返回 已完成
 * 但是这会返回超多的实体 而且没分数
 * 我觉得接下来 或者是用问题分词的结果找对应的实体 或者学习一下打分过滤的方法 就可以用了
 **/
public class AliasEntity {
    public static ArrayList<Entity> getAliasEntities(String questionString) {
        ArrayList<Entity> entityList = new ArrayList<>();

        ArrayList<String[]> alisaDict = new ArrayList<>();
        String dictpath = ALIAS_DICTIONARY;
        alisaDict = readAliasDict(dictpath);
        char[] questionCharArray = questionString.toCharArray();

        for (String[] tuple : alisaDict){
            String alias = tuple[1];
            String entityURI = tuple[0];
            String entity = entityURI.split("/")[entityURI.split("/").length-1].split(">")[0];
            if (_isCandidate(questionCharArray, alias)) {
                Entity e = new Entity();
                e.setEntityURI(alias);
                e.setKgEntityName(alias);
                entityList.add(e);
            }
        }

        return entityList;
    }
    /**
     * 判断实体名字和问题是否有交集
     *
     * @param charArray question
     * @param entity entity
     * @return 是否有交集
     */
    private static boolean _isCandidate(char[] charArray, String entity){
        for (char ch : charArray){
            if (entity.indexOf(ch) != -1)
                return true;
        }
        return false;
    }

    private static ArrayList<String[]> readAliasDict(String dictpath) {

        ArrayList<String[]> aliasList = new ArrayList<>();
        // read file content from file

        FileReader reader = null;
        try {
            reader = new FileReader(dictpath);
            BufferedReader br = new BufferedReader(reader);
            String line = null;

            while ((line = br.readLine()) != null) {
                String entityname = line.split(" ")[0];
                String aliasname = line.split(" ")[1];
                String[] entity_alias = new String[]{entityname, aliasname};

                aliasList.add(entity_alias);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return aliasList;
    }


}
