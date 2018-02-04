package com.qa.demo.query;

import com.qa.demo.conf.Configuration;
import com.qa.demo.dataStructure.*;
import com.qa.demo.questionAnalysis.Segmentation;
import com.qa.demo.utils.nt_triple.AG;
import com.qa.demo.utils.kgprocess.KGTripletsClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *  Created time: 2017_09_08
 *  Author: Devin Hua
 *  Function description: 通过分析的实体和模板等，从KB中得到候选答案。
 *  The main driver interface for get candidate answers from KB.
 */

public class GetCandidateAnswers {

    private static ArrayList<Answer> _getCandidateAnswers(Question q, DataSource p)
    {
        ArrayList<Answer> answers = new ArrayList<>();
        ArrayList<Triplet> triplets = KGTripletsClient.getInstance().getKgTriplets();
        ArrayList<QueryTuple> tuples = q.getQueryTuples();
        if(tuples.isEmpty()||tuples==null)
            return answers;
        for(QueryTuple tuple : tuples)
        {
            for(Triplet triplet : triplets)
            {
                if(triplet.getSubjectURI().equalsIgnoreCase(tuple.getSubjectEntity().getEntityURI())
                        &&triplet.getPredicateName().equalsIgnoreCase(tuple.getPredicate().getKgPredicateName()))
                {
                    Answer answer = new Answer();
                    answer.setAnswerString(triplet.getObjectName());
                    ArrayList<Triplet> answertriplets = new ArrayList<>();
                    answertriplets.add(triplet);
                    answer.setAnswerTriplet(answertriplets);
                    answer.setAnswerScore(tuple.getTupleScore());
                    answer.setAnswerSource(p.toString());
                    answers.add(answer);
                }
            }
        }
        return answers;
    }

    private static ArrayList<Answer> _getNTCandidateAnswers(Question q, DataSource p) {
        ArrayList<Answer> answers = new ArrayList<>();
        ArrayList<QueryTuple> tuples = q.getQueryTuples();
        if (tuples.isEmpty() || tuples == null)
            return answers;
        for (QueryTuple tuple : tuples)
        {
            String subject_uri = tuple.getSubjectEntity().getEntityURI();
            String predicate_uri = "";
            if(subject_uri.contains("zhwiki"))
                predicate_uri = Configuration.PREDICATE_PREFIX_WIKI + tuple.getPredicate().getKgPredicateName();
            else if(subject_uri.contains("hudongbaike"))
                predicate_uri = Configuration.PREDICATE_PREFIX_HUDONG + tuple.getPredicate().getKgPredicateName();
            else if(subject_uri.contains("baidubaike"))
                predicate_uri = Configuration.PREDICATE_PREFIX_BAIDU + tuple.getPredicate().getKgPredicateName();
            else
                continue;
            ArrayList<String> objects = SparqlQuery.getObject(subject_uri, predicate_uri);
            for (String object : objects)
            {
                Answer answer = new Answer();
                answer.setAnswerString(object);
                ArrayList<Triplet> answertriplets = new ArrayList<>();
                Triplet triplet = new Triplet();
                triplet.setSubjectURI(tuple.getSubjectEntity().getEntityURI());
                triplet.setSubjectName(tuple.getSubjectEntity().getKgEntityName());
                triplet.setPredicateURI(predicate_uri);
                triplet.setPredicateName(tuple.getPredicate().getKgPredicateName());
                triplet.setObjectName(object);
                answertriplets.add(triplet);
                answer.setAnswerTriplet(answertriplets);
                answer.setAnswerScore(tuple.getTupleScore());
                answer.setAnswerSource(p.toString());
                answers.add(answer);
            }
        }
        return answers;
    }

    private static ArrayList<Answer> _getTDBCandidateAnswers(Question q, DataSource p) {
        //System.out.println("_getTDBCandidateAnswers");
        ArrayList<Answer> answers = new ArrayList<>();
        ArrayList<QueryTuple> tuples = q.getQueryTuples();
        if (tuples == null || tuples.isEmpty())
            return answers;
        for (QueryTuple tuple : tuples)
        {
            String subject_uri = tuple.getSubjectEntity().getEntityURI();
            String predicate_uri = "";
            //System.out.println(tuple.getSubjectEntity().getEntityURI());
            if(subject_uri.contains("zhwiki"))
                predicate_uri = Configuration.PREDICATE_PREFIX_WIKI + tuple.getPredicate().getKgPredicateName();
            else if(subject_uri.contains("hudongbaike"))
                predicate_uri = Configuration.PREDICATE_PREFIX_HUDONG + tuple.getPredicate().getKgPredicateName();
            else if(subject_uri.contains("baidubaike"))
                predicate_uri = Configuration.PREDICATE_PREFIX_BAIDU + tuple.getPredicate().getKgPredicateName();
            else if(subject_uri.contains("caas"))
                predicate_uri = Configuration.PREDICATE_PREFIX_CAAS + tuple.getPredicate().getKgPredicateName();
            else
                continue;
            ArrayList<String> objects = TDBQuery.getObject(subject_uri, predicate_uri);
            for (String object : objects)
            {
                Answer answer = new Answer();
                answer.setAnswerString(object);
                ArrayList<Triplet> answertriplets = new ArrayList<>();
                Triplet triplet = new Triplet();
                triplet.setSubjectURI(tuple.getSubjectEntity().getEntityURI());
                triplet.setSubjectName(tuple.getSubjectEntity().getKgEntityName());
                triplet.setPredicateURI(predicate_uri);
                triplet.setPredicateName(tuple.getPredicate().getKgPredicateName());
                triplet.setObjectName(object);
                answertriplets.add(triplet);
                answer.setAnswerTriplet(answertriplets);
                answer.setAnswerScore(tuple.getTupleScore());
                answer.setAnswerSource(p.toString());
                answers.add(answer);
            }
        }
        return answers;
    }

    /**
     * create by j.y.zhang
     * getCandidateAnswers from allgregrograph
     * @param q
     * @param p
     * @return
     */
    private static ArrayList<Answer> _getAllgregraphCandidateAnswers(Question q, DataSource p){
        //System.out.println("_getAllgregraphCandidateAnswers");
        ArrayList<Answer> answers = new ArrayList<>();
        ArrayList<QueryTuple> tuples = q.getQueryTuples();
        if (tuples.isEmpty() || tuples == null)
            return answers;
        for (QueryTuple tuple : tuples)
        {
            String subject_uri = tuple.getSubjectEntity().getEntityURI();
            String predicate_uri = "";
            if(subject_uri.contains("zhwiki"))
                predicate_uri = Configuration.PREDICATE_PREFIX_WIKI + tuple.getPredicate().getKgPredicateName();
            else if(subject_uri.contains("hudongbaike"))
                predicate_uri = Configuration.PREDICATE_PREFIX_HUDONG + tuple.getPredicate().getKgPredicateName();
            else if(subject_uri.contains("baidubaike"))
                predicate_uri = Configuration.PREDICATE_PREFIX_BAIDU + tuple.getPredicate().getKgPredicateName();
            else if(subject_uri.contains("caas"))
                predicate_uri = Configuration.PREDICATE_PREFIX_CAAS + tuple.getPredicate().getKgPredicateName();
            else
                continue;
            ArrayList<String> objects = null;
            try {
                objects = AG.queryObject(subject_uri, predicate_uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
            for (String object : objects)
            {
                Answer answer = new Answer();
                answer.setAnswerString(object);
                ArrayList<Triplet> answertriplets = new ArrayList<>();
                Triplet triplet = new Triplet();
                triplet.setSubjectURI(tuple.getSubjectEntity().getEntityURI());
                triplet.setSubjectName(tuple.getSubjectEntity().getKgEntityName());
                triplet.setPredicateURI(predicate_uri);
                triplet.setPredicateName(tuple.getPredicate().getKgPredicateName());
                triplet.setObjectName(object);
                answertriplets.add(triplet);
                answer.setAnswerTriplet(answertriplets);
                answer.setAnswerScore(tuple.getTupleScore());
                answer.setAnswerSource(p.toString());
                answers.add(answer);
            }
        }
        return answers;

    }

    public static Question getCandidateAnswers(Question q, DataSource p)
    {
        ArrayList<Answer> answers = _getTDBCandidateAnswers(q, p);
        ArrayList<Answer> results = (ArrayList<Answer>)q.getCandidateAnswer();
        //如果没有候选答案则返回一个默认答案同时将分数置为0；
        if(!answers.isEmpty()||answers!=null)
        {
            results.addAll(answers);
        }
        q.setCandidateAnswer(results);
        return q;
    }

    //基于答案的词性以及特殊的tokens对答案进行筛选
    public static Question getCandidateAnswersWithIntention(Question q, DataSource p)
    {
        ArrayList<Answer> answers = _getTDBCandidateAnswers(q, p);
        ArrayList<Answer> results = (ArrayList<Answer>)q.getCandidateAnswer();
        //如果没有候选答案则返回一个默认答案同时将分数置为0；
        String intention = q.getQuestionIntention();
        if(!answers.isEmpty()||answers!=null) {
            if (intention.equalsIgnoreCase("when") || intention.equalsIgnoreCase("who") || intention.equalsIgnoreCase("num")) {
                for (Answer ans : answers) {
                    String ansString = ans.getAnswerString();
                    Segmentation.segmentation(ansString);
                    List<Map<String, String>> tokensPos = Segmentation.getTokenPOSList();
                    List<String> tokensSet = new ArrayList<String>();
                    List<String> POSSet = new ArrayList<String>();
                    System.out.println("The tokens and POS of answer are : ");
                    for (Map<String, String> pair : tokensPos) {
                        for (String token : pair.keySet()) {
                            String POS = pair.get(token);
                            tokensSet.add(token);
                            POSSet.add(POS);
                            System.out.print(token + " " + POS + " ");
                        }
                    }
                    System.out.println();
                    boolean legalFlag = false;
                    switch (intention) {
                        case "when":
                            if (POSSet.contains("t") || POSSet.contains("tg") || POSSet.contains("m") || POSSet.contains("mq") || POSSet.contains("en"))  //地名的通用词性
                                legalFlag = true;
                            if (tokensSet.contains("年") || tokensSet.contains("月") || tokensSet.contains("日"))
                                legalFlag = true;
                            break;
                        case "who":
                            if (POSSet.contains("n") || POSSet.contains("nr") || POSSet.contains("nr1") || POSSet.contains("nr2") || POSSet.contains("nrj") || POSSet.contains("nrf") || POSSet.contains("nz") || POSSet.contains("en"))  //人的通用词性
                                legalFlag = true;
                            if (tokensSet.contains("组织") || tokensSet.contains("团体") || tokensSet.contains("协会") || tokensSet.contains("单位"))
                                legalFlag = true;
                            break;
                        case "num":
                            if (POSSet.contains("m") || POSSet.contains("mq") || POSSet.contains("nz"))  //识别量词的词性
                                legalFlag = true;
                            break;
                        default://不做任何操作
                    }
                    if (legalFlag) //只有满足条件的才能作为答案
                        results.add(ans);
                }
            }
            //其他情况不做考虑，因为可能会遗漏答案：
            //例如：稻曲病的为害部位是哪? 答案分词为：穗 j 部 q  与 String string="花生产于哪里？";  前者答案词性的情况无法完全覆盖
            else
            {
                results.addAll(answers);
            }
        }
        q.setCandidateAnswer(results);
        return q;
    }

}
