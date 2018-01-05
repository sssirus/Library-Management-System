package com.qa.demo.query;

import com.qa.demo.conf.Configuration;
import com.qa.demo.dataStructure.*;
import com.qa.demo.questionAnalysis.QuestionAnalysisDriver;
import com.qa.demo.questionAnalysis.QuestionAnalysisDriverImpl;
import com.qa.demo.templateTraining.TemplateGeneralization;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;

import java.net.UnknownHostException;
import java.util.*;

/**
 *  Created time: 2017_10_12
 *  Author: Devin Hua
 *  Function description:
 *  To match template-based-synonyms which are generated from triplets
 *  and to return the candidate answers by using self-defined algorithm.
 */

public class ALGQuerySynonymKBQA  implements KbqaQueryDriver {

    //对问题进行基于KB的查询，返回候选答案集等相关信息，放在question数据结构中；
    //该查询中，将模板库放在ES索引中，通过ES查询得到谓词；
    @Override
    public Question kbQueryAnswers(Question q) {

        //取得问题分析器驱动；
        QuestionAnalysisDriver qAnalysisDriver = new QuestionAnalysisDriverImpl();
        q = qAnalysisDriver.nerQuestion(q);
        q = qAnalysisDriver.segmentationQuestion(q);
        q = this.patternExtractQuestion(q);
        q = GetCandidateAnswers.getCandidateAnswers(q, DataSource.SYNONYM);
        return q;
    }

    //模板到谓词的映射;
    private Question patternExtractQuestion(Question q){

        ArrayList<QueryTuple> tuples = this.patternMatch(q);
        q.setQueryTuples(tuples);
        return q;
    }

    private ArrayList<QueryTuple> patternMatch(Question q) {

        ArrayList<QueryTuple> tuples = new ArrayList<>();

        if (q.getQuestionEntity().isEmpty() || q.getQuestionEntity() == null
                || q.getQuestionToken().size()==0 || q.getQuestionToken().isEmpty())
            return tuples;
        //从问题中将实体删掉后，去匹配模板;

        Iterator iterator = q.getQuestionToken().entrySet().iterator();

        //对应问题中的每一个实体，挖去实体后形成的模板tokens各不相同;
        while (iterator.hasNext()) {

            Map.Entry<Entity, ArrayList<String>> entry =
                    (Map.Entry) iterator.next();
            ArrayList<String> tokens = entry.getValue();
            Entity subject_entity = entry.getKey();

            //问句如果为_____是什么，那么映射到谓词为“描述”和“简介”；
            if(tokens==null||tokens.isEmpty()||tokens.size()==0)
            {
                Predicate p = new Predicate();
                p.setKgPredicateName("描述");
                QueryTuple tuple = new QueryTuple();
                QuestionTemplate qTemplate = new QuestionTemplate();
                qTemplate.setPredicate(p);
                qTemplate.setTemplateString("描述");
                tuple.setTemplate(qTemplate);
                tuple.setSubjectEntity(subject_entity);
                tuple.setPredicate(qTemplate.getPredicate());
                tuple.setTupleScore(0.5);
                tuples.add(tuple);

                p = new Predicate();
                p.setKgPredicateName("简介");
                tuple = new QueryTuple();
                qTemplate = new QuestionTemplate();
                qTemplate.setPredicate(p);
                qTemplate.setTemplateString("简介");
                tuple.setTemplate(qTemplate);
                tuple.setSubjectEntity(subject_entity);
                tuple.setPredicate(qTemplate.getPredicate());
                tuple.setTupleScore(0.5);
                tuples.add(tuple);
            }

            else
            {
                List<QueryTuple> ts = _searchTemplate(subject_entity, tokens);
                for(QueryTuple t : ts)
                {
                    tuples.add(t);
                }
            }
        }

        if(tuples.size()>0)
            tuples = RerankQueryTuple.rankTuples(tuples);
        return tuples;
    }

    //对于问题分词之后的tokens，进行查询；
    private List<QueryTuple> _searchTemplate(Entity subject_entity, ArrayList<String> tokens)
    {
        //取得模板库;使用单例模式，以防每次循环都get mappings，导致计算时间太长；
        HashMap<String, HashSet<String>> predicateSynonymsMap =
                TemplateGeneralization.getInstance().getPredicateSynonymsMap();
        List<QueryTuple> tuples = new ArrayList<>();

        //遍历谓词-同义词集合来找到可能匹配的谓词；
        Iterator it = predicateSynonymsMap.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<String, HashSet<String>> entry = (Map.Entry) (it.next());
            String predicatename = entry.getKey();
            HashSet<String> synonyms = entry.getValue();
            double coOccurrenceScore = _coOccurrence(tokens, synonyms);
            //double coOccurrenceScore = _coOccurrenceNew(tokens,predicatename,synonyms);
            if(coOccurrenceScore>0)
            {
                String templateString = "";
                for(String synonym : synonyms)
                {
                    templateString += synonym + " ";
                }
                Predicate p = new Predicate();
                p.setKgPredicateName(predicatename);
                QueryTuple tuple = new QueryTuple();
                QuestionTemplate qTemplate = new QuestionTemplate();
                qTemplate.setPredicate(p);
                qTemplate.setTemplateString(templateString);
                tuple.setTemplate(qTemplate);
                tuple.setSubjectEntity(subject_entity);
                tuple.setPredicate(qTemplate.getPredicate());
                tuple.setTupleScore(coOccurrenceScore);
                tuples.add(tuple);
            }
        }
        return tuples;
    }

    //找到tokens和synonyms中有多少共现词并计算相似度；
    private double _coOccurrence(ArrayList<String> tokens, HashSet<String> synonyms)
    {
        if(tokens.isEmpty()||tokens.size()==0)
            return 0;
        else if(synonyms.isEmpty()||synonyms.size()==0)
            return 0;
        double co_occurrence_count = 0;
        for(String temp : tokens)
        {
            if(synonyms.contains(temp))
                co_occurrence_count++;
        }
        return (co_occurrence_count/(double)(tokens.size()));
    }

    private double _coOccurrenceNew(ArrayList<String> tokens, String predicatename, HashSet<String> synonyms)
    {
        if(tokens.isEmpty()||tokens.size()==0)
            return 0;
        else if(synonyms.isEmpty()||synonyms.size()==0)
            return 0;
        else if(tokens.size()==1&&tokens.get(0).equalsIgnoreCase(predicatename))  //直接与原谓词匹配
            return 1.0;
        double co_occurrence_count = 0;
        for(String temp : tokens)
        {
            if(synonyms.contains(temp))
                co_occurrence_count++;
        }
        return (co_occurrence_count/(double)(tokens.size()))-0.01;  //一般来说，近似的值，相似度不应该为1
    }

}
