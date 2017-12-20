package com.qa.demo.questionAnalysis;

import org.junit.jupiter.api.Test;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SegmentationTest {
    @Test
    void segmentation() {

        String sentence = "如何养植_____最好";
        System.out.println("待分词的句子为：" + sentence);
        Segmentation.segmentation(sentence);
        List<String> tokens = Segmentation.getTokens();
        List<Map<String, String>> tokenPOS = Segmentation.getTokenPOSList();
        String tokenOutput = "";
        String tokenPOSOutput = "";

        for(String token : tokens)
        {
            tokenOutput += token;
            tokenOutput += " ";
        }
        tokenOutput += "\r\n";

        for(Map<?,?> map : tokenPOS)
        {
            Iterator it = map.entrySet().iterator();
            while(it.hasNext())
            {
                Map.Entry entry = (Map.Entry<?,?>) it.next();
                tokenPOSOutput += entry.getKey();
                tokenPOSOutput += ":";
                tokenPOSOutput += entry.getValue();
                tokenPOSOutput += "\r\n";
            }
        }
        System.out.print(tokenOutput);
        System.out.print(tokenPOSOutput);
    }

}