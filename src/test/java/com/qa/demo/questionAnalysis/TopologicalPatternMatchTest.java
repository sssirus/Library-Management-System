package com.qa.demo.questionAnalysis;

import com.qa.demo.dataStructure.TopologicalPattern;
import com.qa.demo.dataStructure.TopologicalStructure;
import com.qa.demo.utils.topologicalpattern.TopologicalPatternClient;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class TopologicalPatternMatchTest {

    @Test
    void getParseTreeString() {

//        String[] sentence = {"ENTITY", "的" ,"颜色", "是", "什么"};
//        String posSequence = "en uj n v r";

        String[] sentence = {"ENTITY", "是", "什么"};
        String posSequence = "en v r";

//        String[] sentence = {"ENTITY", "的", "规范", "汉字", "编号", "是", "什么"};
//        String posSequence = "eng uj n nz n v r";

//        String[] sentence = {"ENTITY","的","主要", "支流", "是", "什么"};
//        String posSequence = "eng uj b n v r";


        System.out.println(TopologicalPatternMatch.getInstance()
                .getPredicateMention(posSequence, sentence));

        System.out.println(TopologicalPatternMatch.getInstance().getParseTreeString(sentence));
        String subtreeString = TopologicalPatternMatch.getInstance().extractSubTree(posSequence, sentence);
        System.out.println(subtreeString);
        ArrayList<String> topologicalPatternString = TopologicalPatternMatch.getInstance().buildTopologicalPattern(subtreeString);
        System.out.println(topologicalPatternString);
    }

}