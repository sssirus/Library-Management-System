package com.qa.demo.query;

import com.qa.demo.conf.Configuration;
import com.qa.demo.dataStructure.*;
import com.qa.demo.ontologyProcess.TDBCrudDriver;
import com.qa.demo.ontologyProcess.TDBCrudDriverImpl;
import com.qa.demo.utils.kgprocess.KGTripletsClient;

import java.util.ArrayList;

import static com.qa.demo.conf.Configuration.TDB_MODEL_NAME;

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



}
