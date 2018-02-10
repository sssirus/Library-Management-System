package com.qa.demo.query;

import com.qa.demo.algorithm.EditingDistance;
import com.qa.demo.dataStructure.*;
import com.qa.demo.questionAnalysis.MoveStopwords;
import com.qa.demo.questionAnalysis.QuestionAnalysisDriver;
import com.qa.demo.questionAnalysis.QuestionAnalysisDriverImpl;
import com.qa.demo.questionAnalysis.TopologicalPatternMatch;
import com.qa.demo.templateTraining.TemplateGeneralization;
import com.qa.demo.utils.w2v.Word2VecGensimModel;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 *  Created time: 2018_02_08
 *  Author: Weizhuo Li
 *  Function description:
 *  Match topological pattern which are generated from question, and
 *  Use user intention to make up for missing tokens. Finally,
 *  return the candidate answers by using self-defined algorithm.
 */

public class TopologicalPatternKBQAWithIntention implements KbqaQueryDriver {

    //对问题进行基于KB的查询，返回候选答案集等相关信息，放在question数据结构中；
    //该查询中，从文件读取拓扑结构模板，并将问题解析为拓扑结构，进行匹配；
    @Override
    public Question kbQueryAnswers(Question q) {

        //取得问题分析器驱动；
        QuestionAnalysisDriver qAnalysisDriver = new QuestionAnalysisDriverImpl();
        q = qAnalysisDriver.nerQuestion(q);
        q = qAnalysisDriver.posQuestion(q);
        q = qAnalysisDriver.intentionQuestion(q);  //获取用户的意图
        //q = qAnalysisDriver.segmentationQuestionPOS(q); //会识别出用户意图以及解析出词性
        q = this.patternExtractQuestion(q);
        //q = GetCandidateAnswers.getCandidateAnswers(q, DataSource.SYNONYM);
        q = GetCandidateAnswers.getCandidateAnswersWithIntention(q, DataSource.SYNONYM); //由于又要对答案进行一些词性判别，时间开销较高
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

            ArrayList<ArrayList<String>> predicateMentionWordList = TopologicalPatternMatch.getInstance()
                    .getPredicateMention(posSequence, questionTokens);

            //如果通过模板解析没有词汇或者词汇都被过滤（XX是什么）
            ArrayList<ArrayList<String>> refinePredicateMentionWordList=new ArrayList<ArrayList<String>>();
            for(ArrayList<String> predicateMention:predicateMentionWordList)
            {
                if(!predicateMention.isEmpty())
                    refinePredicateMentionWordList.add(predicateMention);
            }

            ArrayList<String> leftTokens = new ArrayList<String>();
            HashSet<String> stopwords = MoveStopwords.getInstance().getStopwordSet();
            if(refinePredicateMentionWordList.size()==0)
            {
                ArrayList<String> predicateMentionWord = new ArrayList<>();
                for (Map<String, String> map : entry.getValue()) {
                    Iterator it2 = map.entrySet().iterator();
                    String POS = "";
                    while (it2.hasNext()) {
                        Map.Entry<String, String> entry2 = (Map.Entry) it2.next();
                        POS = entry2.getValue();
                        String verb = entry2.getKey();
                        if(verb.equalsIgnoreCase("entity")||stopwords.contains(verb))
                            continue;
                        //不能只考虑动词，会有问题
                        if (POS.equalsIgnoreCase("n") || POS.equalsIgnoreCase("nr")) {
                            predicateMentionWord.add(verb);
                        } else if (POS.equalsIgnoreCase("v") || POS.equalsIgnoreCase("vg") || POS.equalsIgnoreCase("vn")) {
                            predicateMentionWord.add(verb);
                        } else if (POS.equalsIgnoreCase("en")) {  //考虑英文问句的情况
                            predicateMentionWord.add(verb);
                        }
                        else { //剩余的情况
                            leftTokens.add(verb);
                        }
                    }
                }
                if(!predicateMentionWord.isEmpty())
                     refinePredicateMentionWordList.add(predicateMentionWord);
            }
            //有可能词性解析错误
            if(refinePredicateMentionWordList.size()==0) {
                refinePredicateMentionWordList.add(leftTokens);
            }


            //仍然是空集的情况
            if(refinePredicateMentionWordList.size()==0)
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
            else {
                String intention = q.getQuestionIntention();
                for (ArrayList<String> predicateMentionWords : refinePredicateMentionWordList) {
                    List<QueryTuple> ts = _searchTemplate(subjectEntity, predicateMentionWords, intention);
                    for (QueryTuple t : ts) {
                        tuples.add(t);
                    }
                }
            }
        }
        return tuples;
    }

    //对于问题分词之后的tokens，进行查询；
    private List<QueryTuple> _searchTemplate(Entity subject_entity, ArrayList<String> tokens, String intention)
    {
        ArrayList<String> predicatetokens = new ArrayList<String>();
        switch (intention) {
            case "where":
                predicatetokens.add("地方");
                predicatetokens.add("哪里");
                break;
            case "when":
                predicatetokens.add("时间");
                predicatetokens.add("日期");
                predicatetokens.add("时代");
                break;
            case "who":
                for (String predicate : tokens) {
                    predicatetokens.add(predicate + "人");
                    predicatetokens.add(predicate + "者");
                    //考虑谓词的同义词，等等
                }
                ;
                //缺省的情况
                predicatetokens.add("谁");
                break;
            case "how":
                predicatetokens.add("方法");
                predicatetokens.add("方式");
                predicatetokens.add("方案");
                break;
            case "enumerate":  //这需要名词来修饰  可能缺失东西，必须用名词来填补
                break;
            case "num":
                predicatetokens.add("多少");
                break;
            case "why":
                predicatetokens.add("原因");
                predicatetokens.add("理由");
                break;
            case "IsIt":
                break;
            case "what":  //what 的情况谓词很少，一般名词较多（动词可能修饰名词）
                if(tokens.isEmpty()) {
                    predicatetokens.add("简介");
                    predicatetokens.add("描述");
                }
                break;
            default://不做任何操作
        }
        //将原始的词进行添加
        for(String token: tokens)
        {
            if(!predicatetokens.contains(token))
                predicatetokens.add(token);
        }


        //取得模板库;使用单例模式，以防每次循环都get mappings，导致计算时间太长；
        HashMap<String, HashSet<String>> predicateSynonymsMap =TemplateGeneralization.getInstance().getPredicateSynonymsMap();
        List<QueryTuple> tuples = new ArrayList<>();

        //遍历谓词-同义词集合来找到可能匹配的谓词；
        Iterator it = predicateSynonymsMap.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<String, HashSet<String>> entry = (Map.Entry) (it.next());
            String predicatename = entry.getKey();
            HashSet<String> synonyms = entry.getValue();
            double coOccurrenceScore = _coOccurrence(predicatetokens,predicatename,synonyms);
            //double coOccurrenceScore = _coOccurrenceNew(predicatetokens,predicatename,synonyms);
//            double coOccurrenceScore =_SoftSimilairty(predicatetokens,predicatename,synonyms);
            if(coOccurrenceScore>0) {
                String templateString = "";
                for(String synonym : synonyms){
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

    //时间开销小，且能保证精度
    private double _coOccurrence(ArrayList<String> tokens, String predicatename, HashSet<String> synonyms) {
        if (tokens.isEmpty() || tokens.size() == 0)
            return 0;
        else if (synonyms.isEmpty() || synonyms.size() == 0)
            return 0;
        else if (tokens.size() == 1 && tokens.get(0).equalsIgnoreCase(predicatename))  //直接与原谓词匹配
            return 1.0;
        double co_occurrence_count = 0;
        for (String temp : tokens) {
            if (synonyms.contains(temp))
                co_occurrence_count++;
        }
        return (co_occurrence_count / (double) (tokens.size())) - 0.01;  //一般来说，近似的值，相似度不应该为1
    }

    //时间开销过大，并不推荐
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
                    score=temp_score;
                    break;
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
