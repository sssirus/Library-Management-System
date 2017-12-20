package com.qa.demo.utils;

import com.qa.demo.conf.FileConfig;
import com.qa.demo.utils.trainingcorpus.ExtractInfo;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashSet;

class ExtractInfoTest {

    private ExtractInfo extractInfo = new ExtractInfo(FileConfig.DIC_CATEGORY_PROPERTIES);
    private HashSet<String> propertySet = new HashSet<String>();

    @Test
    void readInfo() {
    }

    @Test
    void getList() {
        try {
            propertySet = extractInfo.getList();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(String temp:propertySet)
        {
            System.out.println(temp);
        }
    }

    @Test
    void regxChinese() {
    }

    @Test
    void writeTxtFile() {
    }

    @Test
    void writeTxtAttributeFile() {
    }

}