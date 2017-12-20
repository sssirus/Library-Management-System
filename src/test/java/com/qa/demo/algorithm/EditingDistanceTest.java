package com.qa.demo.algorithm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EditingDistanceTest {
    @Test
    public void cutChineseCharacterTest(){
        String test = "中华人民共和国";
        for(int i = 0; i < test.length()-1; i++){
            System.out.println(test.substring(i,i+1));
        }
        String s1 = "中";
        String s2 = "锅";
        System.out.println(s1.equals(s2));
    }

    @Test
    public void editDisTest(){
        String test1 = "中国惊奇先生";
        String test2 = "中国惊奇先生";
        int score = EditingDistance.editDis(test1, test2);
        System.out.println(score);
    }
}