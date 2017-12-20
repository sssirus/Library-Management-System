package com.qa.demo.questionAnalysis;

import com.qa.demo.conf.Configuration;
import com.qa.demo.dataStructure.Entity;
import com.qa.demo.dataStructure.QueryTuple;
import com.qa.demo.dataStructure.Question;
import com.qa.demo.dataStructure.QuestionTemplate;
import com.qa.demo.templateTraining.TemplateFromTripletsClient;

import java.util.ArrayList;
import java.util.HashSet;

/**
 *  Created time: 2017_09_08
 *  Author: Devin Hua
 *  Function description:
 *  The class to match template or pattern.
 */

public class PatternMatch {

    //从模板库中匹配到模板，以及模板对应的谓词；
    public static ArrayList<QueryTuple> patternmatch(Question q){

        //取得模板库；
        HashSet<QuestionTemplate> qTemplates =
                TemplateFromTripletsClient.getInstance().getTemplateRepository();

        ArrayList<QueryTuple> tuples = new ArrayList<>();

        if(q.getQuestionEntity().isEmpty()||q.getQuestionEntity()==null)
            return tuples;
        //从问题中将实体删掉后，去匹配模板;
        for(Entity e : q.getQuestionEntity())
        {
            String sentence =
                    q.getQuestionString().replace(e.getKgEntityName(), Configuration.SPLITSTRING);
            for(String punctuation : Configuration.PUNCTUATION_SET)
            {
                sentence = sentence.replace(punctuation,"");
            }
            sentence = sentence.trim();
            for(QuestionTemplate qTemplate : qTemplates) {
                if (qTemplate.getTemplateString().equalsIgnoreCase(sentence)) {
                    QueryTuple tuple = new QueryTuple();
                    tuple.setTemplate(qTemplate);
                    tuple.setSubjectEntity(e);
                    tuple.setPredicate(qTemplate.getPredicate());
                    tuples.add(tuple);
                }
            }
        }
        return tuples;
    }

}
