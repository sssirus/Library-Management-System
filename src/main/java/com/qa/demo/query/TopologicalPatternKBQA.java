package com.qa.demo.query;

import com.qa.demo.algorithm.EditingDistance;
import com.qa.demo.conf.Configuration;
import com.qa.demo.dataStructure.*;
import com.qa.demo.questionAnalysis.QuestionAnalysisDriver;
import com.qa.demo.questionAnalysis.QuestionAnalysisDriverImpl;
import com.qa.demo.questionAnalysis.TopologicalPatternMatch;
import com.qa.demo.templateTraining.TemplateGeneralization;
import com.qa.demo.utils.w2v.Word2VecGensimModel;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 *  Created time: 2018_01_31
 *  Author: Devin Hua
 *  Function description:
 *  To match topological pattern which are generated from question
 *  and to map the pre-defined predicate mention.
 */

public class TopologicalPatternKBQA implements KbqaQueryDriver {

    //对问题进行基于KB的查询，返回候选答案集等相关信息，放在question数据结构中；
    //该查询中，从文件读取拓扑结构模板，并将问题解析为拓扑结构，进行匹配；
    @Override
    public Question kbQueryAnswers(Question q) {

        //取得问题分析器驱动；
        QuestionAnalysisDriver qAnalysisDriver = new QuestionAnalysisDriverImpl();
        q = qAnalysisDriver.nerQuestion(q);
        q = qAnalysisDriver.posQuestion(q);
        q = this.patternExtractQuestion(q);
        q = GetCandidateAnswers.getCandidateAnswers(q, DataSource.SYNONYM);
        return q;
    }

    //拓扑结构模板到谓词的映射;
    private Question patternExtractQuestion(Question q){

        ArrayList<QueryTuple> tuples = this.patternMatch(q);
        q.setQueryTuples(tuples);
        return q;
    }

    private ArrayList<QueryTuple> patternMatch(Question q) {
        ArrayList<QueryTuple> tuples = new ArrayList<>();

        if (q.getQuestionEntity().isEmpty() || q.getQuestionEntity() == null
                || q.getQuestionToken().size() == 0 || q.getQuestionToken().isEmpty())
            return tuples;

        //取得每个实体对应的词性标注序列；
        HashMap<Entity,List<Map<String,String>>> questionEntityPOS = q.getQuestionEntityPOS();
        Iterator it = questionEntityPOS.entrySet().iterator();
        while(it.hasNext())
        {
            Map.Entry<Entity,List<Map<String,String>>> entry = (Map.Entry) it.next();
            Entity subjectEntity = entry.getKey();

            String posSequence = "";
            for(Map<String,String> map : entry.getValue())
            {
                Iterator it2 = map.entrySet().iterator();
                String POS = "";
                while(it2.hasNext())
                {
                    Map.Entry<String,String> entry2 = (Map.Entry) it2.next();
                    POS = entry2.getValue();
                }
                posSequence += POS + " ";
            }
//            System.out.println(posSequence.trim());

            ArrayList<String> tokens = q.getQuestionToken().get(entry.getKey());
            String[] questionTokens = tokens.toArray(new String[tokens.size()]);

            ArrayList<String> predicateMentionWords = TopologicalPatternMatch.getInstance()
                    .getPredicateMention(posSequence, questionTokens);

            //如果没有找到谓词指称;
            if(predicateMentionWords.size()==0)
            {
                for(Map<String,String> map : entry.getValue())
                {
                    Iterator it2 = map.entrySet().iterator();
                    String POS = "";
                    while(it2.hasNext())
                    {
                        Map.Entry<String,String> entry2 = (Map.Entry) it2.next();
                        POS = entry2.getValue();
                        String verb = entry2.getKey();
                        if(POS.equalsIgnoreCase("v"))
                            predicateMentionWords.add(verb);
                    }
                }
            }

            if(predicateMentionWords.size()==0)
            {
                Predicate p = new Predicate();
                p.setKgPredicateName("描述");
                QueryTuple tuple = new QueryTuple();
                QuestionTemplate qTemplate = new QuestionTemplate();
                qTemplate.setPredicate(p);
                qTemplate.setTemplateString("描述");
                tuple.setTemplate(qTemplate);
                tuple.setSubjectEntity(subjectEntity);
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
                tuple.setSubjectEntity(subjectEntity);
                tuple.setPredicate(qTemplate.getPredicate());
                tuple.setTupleScore(0.5);
                tuples.add(tuple);
            }

            else
            {
                List<QueryTuple> ts = _searchTemplate(subjectEntity, predicateMentionWords);
                for(QueryTuple t : ts)
                {
                    tuples.add(t);
                }
            }
        }
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
            //double coOccurrenceScore = _coOccurrence(tokens, synonyms);
//            double coOccurrenceScore = _coOccurrenceNew(tokens,predicatename,synonyms);
            double coOccurrenceScore = _SoftSimilairty(tokens,predicatename,synonyms);
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

    //建议使用，但不强求，可以节省大量计算的时间
    private double _SoftSimilairty(ArrayList<String> tokens, String predicatename, HashSet<String> synonyms) {
        if (tokens.isEmpty() || tokens.size() == 0)
            return 0;
        else if (synonyms.isEmpty() || synonyms.size() == 0)
            return 0;
        else if (tokens.size() == 1 && tokens.get(0).equalsIgnoreCase(predicatename))  //直接与原谓词匹配
            return 1.0;

        //定义词向量模型
        Word2VecGensimModel w2vModel = null;
        try {
            w2vModel = Word2VecGensimModel.getInstance();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        double co_occurrence_count = 0;
        for (String temp : tokens) {
            double score = 0.0;
            for (String synonym : synonyms) {
                double temp_score = 0.0;
                if (temp.equalsIgnoreCase(synonym)) {
                    temp_score = 1.0;
                } else { //其他则计算词向量的相似度；
                    temp_score = w2vModel.calcWordSimilarity(temp, synonym);
                    temp_score = temp_score >= 0.5 ? temp_score : 0.0; //对应可以在 Configuration.W2V_THRESHOLD 中设置阈值大小
                    if (temp.contains(synonym) || synonym.contains(temp)) //若有包含关系，计算两者的编辑距离；
                    {
                        double ed1 = EditingDistance.getRepetitiveRate(temp, synonym);
                        double ed2 = EditingDistance.getRepetitiveRate(synonym, temp);
                        double ed = ed1 >= ed2 ? ed1 : ed2;
                        temp_score = ed >= temp_score ? ed : temp_score;
                    }
                }
                score = score >= temp_score ? score : temp_score;
            }
            co_occurrence_count += score; //取两者的最大值

        }
        return (co_occurrence_count / (double) (tokens.size())) - 0.01;  //一般来说，近似的值，相似度不应该为1
    }

    }
