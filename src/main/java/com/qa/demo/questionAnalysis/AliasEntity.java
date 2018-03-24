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
 * 返回别名对应的实体列表
 **/
public class AliasEntity {
    public static ArrayList<Entity> getAliasEntities(String questionString) {
        ArrayList<Entity> entityList = new ArrayList<>();

        ArrayList<String[]> alisaDict = new ArrayList<>();
        String dictpath = ALIAS_DICTIONARY;
        alisaDict = readAliasDict(dictpath);


        for (String[] tuple : alisaDict){
            String alias = tuple[1].replace("\r\n", "").replace("\n","").replace("\r","");
            String entityURI = tuple[0];
            String entity = entityURI.split("/")[entityURI.split("/").length-1].split(">")[0];

            if (questionString.indexOf(alias) != -1) {
                Entity e = new Entity();
                e.setEntityURI(entityURI);
                e.setKgEntityName(entity);
                entityList.add(e);
            }
        }

        return entityList;
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
