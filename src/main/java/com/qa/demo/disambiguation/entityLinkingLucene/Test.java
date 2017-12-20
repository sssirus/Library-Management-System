package com.qa.demo.disambiguation.entityLinkingLucene;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xht on 2017/8/30.
 */

public class Test {
    public static void main(String[] args) throws Exception{

        GenerateIndexForQA generateIndexForQA = new GenerateIndexForQA();
        generateIndexForQA.createIndex();

        List<String> mentionList= new ArrayList<String>();
        mentionList.add("西瓜");
        mentionList.add("冬瓜");
        mentionList.add("韭菜");

        List<String> ans = new ArrayList<String>();
        GenerateCandidates entitylinking  = new GenerateCandidates();
        ans = entitylinking.EntityLinkingQA(mentionList);
        System.out.println(ans);
    }
}
