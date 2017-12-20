package com.qa.demo.dataStructure;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Devin Hua on 2017/8/30.
 * 系统中表示查询二元组的数据结构，表示通过问题分析形成的
 * 用于查询的主语和谓语二元组；
 */
public class QueryTuple implements Comparable<QueryTuple>{

    //表示主语;
    private Entity subjectEntity;

    //表示谓语;
    private Predicate predicate;

    //表示匹配的模板;
    private QuestionTemplate template;

    //通过模板匹配得到的tuple，其ES返回的SCORE;
    private double tupleScore = 0.0;

    public Entity getSubjectEntity() {
        return subjectEntity;
    }

    public void setSubjectEntity(Entity subjectEntity) {
        this.subjectEntity = subjectEntity;
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public void setPredicate(Predicate predicate) {
        this.predicate = predicate;
    }

    public QuestionTemplate getTemplate() {
        return template;
    }

    public void setTemplate(QuestionTemplate template) {
        this.template = template;
    }

    public double getTupleScore() {
        return tupleScore;
    }

    public void setTupleScore(double tupleScore) {
        this.tupleScore = tupleScore;
    }

    @Override
    public int compareTo(QueryTuple tuple) {
        if(this.getTupleScore() > tuple.getTupleScore()){
            return 1;
        }else if(this.getTupleScore() == tuple.getTupleScore()){
            return 0;
        }else{
            return -1;
        }
    }

    @Override
    public String toString() {
        String output = "";
        output += "Subject: " + subjectEntity.getEntityURI() + "\r\n";
        output += "Predicate: " + predicate.getKgPredicateName() + "\r\n";
        output += "Template: " + template.getTemplateString() + "\r\n";
        return output;
    }

}
