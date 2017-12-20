package com.qa.demo.questionAnalysis;

import com.qa.demo.conf.Configuration;
import com.qa.demo.conf.FileConfig;
import com.qa.demo.dataStructure.Entity;
import com.qa.demo.dataStructure.QueryTuple;
import com.qa.demo.dataStructure.Question;
import org.ansj.domain.Result;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.nlpcn.commons.lang.tire.domain.Forest;
import org.nlpcn.commons.lang.tire.library.Library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QuestionAnalysisDriverImpl implements QuestionAnalysisDriver {

    //TODO 仓促写之，需要检查;
    //输入一个Question类型的数据结构，对其进行分词后输出；
    public Question segmentationQuestion(Question q) {

        HashMap<Entity, ArrayList<String>> map = new HashMap<>();
        for (Entity e : q.getQuestionEntity()) {
            String sentence =
                    q.getQuestionString().replace(e.getKgEntityName(), Configuration.SPLITSTRING);
            for (String punctuation : Configuration.PUNCTUATION_SET) {
                sentence = sentence.replace(punctuation, "");
            }
            sentence = sentence.trim();
            //System.out.println("待分词的句子为：" + sentence);
            Segmentation.segmentation(sentence);
            ArrayList<String> tokens = (ArrayList<String>) Segmentation.getTokens();
            map.put(e, tokens);
        }
        q.setQuestionToken(map);
        return q;
    }

    //输入一个Question类型的数据结构，对其进行POS分析后输出；
    public Question posQuestion(Question q) {
        return null;
    }

    //输入一个Question类型的数据结构，对其进行NER分析后输出；
    public Question nerQuestion(Question q) {
        q.setQuestionEntity(NER.getEntities(q.getQuestionString()));
        return q;
    }

    //输入一个Question类型的数据结构，对其进行模板匹配后输出；
    public Question patternExtractQuestion(Question q) {
        ArrayList<QueryTuple> tuples = PatternMatch.patternmatch(q);
        q.setQueryTuples(tuples);
        return q;
    }

    //输入一个Question类型的数据结构，对其进行问题意图、答案类型分析后输出；
    public Question latQuestion(Question q) {
        return null;
    }

    //输入一个Question类型的数据结构，对其进行关系抽取后输出；
    public Question relationExtractQuestion(Question q) {
        return null;
    }
}
