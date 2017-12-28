package com.qa.demo.query;

import com.qa.demo.dataStructure.*;
import com.qa.demo.questionAnalysis.QuestionAnalysisDriver;
import com.qa.demo.questionAnalysis.QuestionAnalysisDriverImpl;
import com.qa.demo.templateTraining.TemplateGeneralization;

import java.util.*;

/**
 *  Created time: 2017_12_27
 *  Author: Weizhuo Li
 *  Function description:
 *  To match POS-based-synonyms which are generated from triplets
 *  and to return the candidate answers by using self-defined algorithm.
 */

public class QueryPOSKBQA implements KbqaQueryDriver {
    //对问题进行基于KB的查询，返回候选答案集等相关信息，放在question数据结构中；
    //该查询中，将模板库放在ES索引中，通过ES查询得到谓词；
    @Override
    public Question kbQueryAnswers(Question q)
    {
        //取得问题分析器驱动；
        QuestionAnalysisDriver qAnalysisDriver = new QuestionAnalysisDriverImpl();
        q = qAnalysisDriver.nerQuestion(q);
        q = qAnalysisDriver.segmentationQuestionPOS(q); //会识别出用户意图以及解析出词性
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

   /* private ArrayList<QueryTuple> patternMatch(Question q) {

        ArrayList<QueryTuple> tuples = new ArrayList<>();
        //没有实体的情况，元组返回为空
        if (q.getQuestionEntity().isEmpty() || q.getQuestionEntity() == null
                || q.getQuestionToken().size()==0 || q.getQuestionToken().isEmpty())
            return tuples;

        List<Entity> questionEntity =q.getQuestionEntity();

        int tokenSize=q.getQuestionTokenPOS().size();
        String intetion=q.getQuestionIntention();
        if(tokenSize==0&&intetion.equalsIgnoreCase("what")) //没有tokens的情况
        {
            for(Entity subject_entity:questionEntity)
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
        }
        else {

            for(Entity subject_entity:questionEntity) {
                List<QueryTuple> ts = _searchTemplate(subject_entity, q.getQuestionTokenPOS(), intetion);
                for(QueryTuple t : ts)
                {
                    tuples.add(t);
                }
            }
        }

        if(tuples.size()>0)
            tuples = RerankQueryTuple.rankTuples(tuples);
        return tuples;
    }*/

    private ArrayList<QueryTuple> patternMatch(Question q) {

        ArrayList<QueryTuple> tuples = new ArrayList<>();
        //没有实体的情况，元组返回为空
        if (q.getQuestionEntity().isEmpty() || q.getQuestionEntity() == null
                || q.getQuestionToken().size()==0 || q.getQuestionToken().isEmpty())
            return tuples;

        //List<Entity> questionEntity =q.getQuestionEntity();


        for(Entity subject_entity:q.getQuestionToken().keySet())
        {
            List<String> tokens = q.getQuestionToken().get(subject_entity);
            List<Map<String,String>> EntityPOS=q.getQuestionEntityPOS().get(subject_entity);
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
                String intention=q.getQuestionIntention();
                List<QueryTuple> ts = _searchTemplate(subject_entity,EntityPOS, intention);
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

    private List<QueryTuple> _searchTemplate(Entity subject_entity, List<Map<String, String>> questionTokenPOS, String intention)
    {

        List<String>  nounSet=new ArrayList<String>();
        List<String>  predicateSet=new ArrayList<String>();
        List<String>  adjectiveSet=new ArrayList<String>();

        //根据词性来进行划分tokens的集合
        for(Map<String,String> pair:questionTokenPOS)
        {
            for(String token:pair.keySet())
            {
                String POS = pair.get(token);
                if(POS.equalsIgnoreCase("n")||POS.equalsIgnoreCase("nr")) {
                    nounSet.add(token);
                }
                else if(POS.equalsIgnoreCase("v")||POS.equalsIgnoreCase("vg")||POS.equalsIgnoreCase("vn")) {
                    predicateSet.add(token);
                }
                else if(POS.equalsIgnoreCase("adj")) {
                    adjectiveSet.add(token);
                }
                //可以把它们专门作为名词来看待
            }
        }

        ArrayList<String> predicatetokens=new ArrayList<String>();
        switch (intention) {
            case "where":
                for(String predicate:predicateSet)
                {
                    predicatetokens.add(predicate);
                    //考虑谓词的同义词，等等
                }
                //这里的名词一般起修饰作用
                boolean flag=false;
                for(String noun:nounSet)
                {
                    predicatetokens.add(noun);
                    flag=true;
                };
                if(!flag) {//修饰的名词如果没有，加入缺省的名词
                    predicatetokens.add("地方");
                    predicatetokens.add("哪里");
                }
                break;
            case "when":
                for(String predicate:predicateSet)
                {
                    predicatetokens.add(predicate);
                    //考虑谓词的同义词，等等
                };
                flag=false;
                for(String noun:nounSet)
                {
                    predicatetokens.add(noun);
                    flag=true;
                };
                if(!flag) {//修饰的名词如果没有，加入缺省的名词
                    predicatetokens.add("时间");
                    predicatetokens.add("日期");
                    predicatetokens.add("时候");
                    predicatetokens.add("时代");
                }
                break;
            case "who":
                for(String predicate:predicateSet)
                {
                    predicatetokens.add(predicate);
                    predicatetokens.add(predicate+"人");
                    predicatetokens.add(predicate+"者");
                    //考虑谓词的同义词，等等
                };
                //缺省的情况
                predicatetokens.add("谁");
                break;
            case "how":
                for(String predicate:predicateSet)
                {
                    predicatetokens.add(predicate);
                    //考虑谓词的同义词，等等
                };
                //缺省的情况
                flag=false;
                for(String noun:nounSet)
                {
                    predicatetokens.add(noun);
                    flag=true;
                };
                if(!flag) {//修饰的名词如果没有，加入缺省的名词
                    predicatetokens.add("方法");
                    predicatetokens.add("方式");
                    predicatetokens.add("方案");
                }
                break;
            case "enumerate":  //这需要名词来修饰  可能缺失东西，必须用名词来填补
                for(String predicate:predicateSet)
                {
                    predicatetokens.add(predicate);
                };
                for(String noun:nounSet)
                {
                    predicatetokens.add(noun);
                };
                break;
            case "num":
                for(String predicate:predicateSet)
                {
                    predicatetokens.add(predicate);
                };
                //缺省的情况
                predicatetokens.add("多少");
                break;
            case "why":
                for(String predicate:predicateSet)
                {
                    predicatetokens.add(predicate);
                };
                //缺省的情况
                predicatetokens.add("原因");
                predicatetokens.add("理由");
                break;
            case "IsIt":
                for(String predicate:predicateSet)
                {
                    predicatetokens.add(predicate);
                };
                break;
            case "what":  //what 的情况谓词很少，一般名词较多（动词可能修饰名词）
                for(String noun:nounSet)
                {
                    predicatetokens.add(noun);
                };
                for(String predicate:predicateSet)
                {
                    predicatetokens.add(predicate);
                };
                //缺省的情况
                if(predicatetokens.isEmpty()) {
                    predicatetokens.add("简介");
                    predicatetokens.add("描述");
                }
                break;
            default://不做任何操作
        }

        HashMap<String, HashSet<String>> predicateSynonymsMap =
                TemplateGeneralization.getInstance().getPredicateSynonymsMap();
        List<QueryTuple> tuples = new ArrayList<>();

        //遍历谓词-同义词集合来找到可能匹配的谓词；
        Iterator it = predicateSynonymsMap.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<String, HashSet<String>> entry = (Map.Entry) (it.next());
            String predicatename = entry.getKey();
            HashSet<String> synonyms = entry.getValue();
            double coOccurrenceScore = _coOccurrence(predicatetokens, synonyms);
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



}
