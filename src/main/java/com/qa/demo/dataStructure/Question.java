package com.qa.demo.dataStructure;
/**
 * Created by Devin Hua on 2017/8/30.
 * 系统中表示问题及相关信息的数据结构；
 */

import java.util.*;

public class Question {
    //表示问题字符串，例如“水稻的天敌是什么？”;
    private String questionString;

    //表示问句的意图,例如“大白菜分布在哪儿”中的“在哪儿”对应用户意图where
    private String qustionIntention="what";

    //分词之后的word列表;
    private HashMap<Entity,ArrayList<String>> questionToken;

    //分词之后的word词性列表，可能一个词对应两个及以上的词性;
    private List<Map<String,String>> questionTokenPOS;

    //分词之后的每个实体对应的词性标注；
    private HashMap<Entity,List<Map<String,String>>> questionEntityPOS;

    //问题包含的实体列表;
    private List<Entity> questionEntity;

    //问题包含的谓词列表;
    private List<Predicate> questionPredicate;

    //问题匹配的模板列表;
    private List<QuestionTemplate> questionTemplateList;

    //候选答案集;
    private List<Answer> candidateAnswer = new ArrayList<>();

    //返回的答案;
    private Answer returnedAnswer;

    //在做模型训练时，实际正确的问题答案，用来验证算法正确性，非训练时不要使用;
    private String acturalAnswer;

    //表示支持该问题的文本证据；
    private List<Evidence> questionEvidence = new ArrayList<>();

    //问题类型;
    private String LAT;

    //查询二元组;
    private ArrayList<QueryTuple> queryTuples = null;

    public String getQuestionString() {
        return questionString;
    }

    public void setQuestionString(String questionString) {
        this.questionString = questionString;
    }

    public String getQuestionIntention() {
        return qustionIntention;
    }

    public  void setQustionIntention(String qustionIntention){this.qustionIntention= qustionIntention;}

    public HashMap<Entity,ArrayList<String>> getQuestionToken() {
        return questionToken;
    }

    public void setQuestionToken(HashMap<Entity,ArrayList<String>> map) {
        this.questionToken = new HashMap<>();
        Iterator iterator = map.entrySet().iterator();
        while(iterator.hasNext())
        {
            Map.Entry<Entity,ArrayList<String>> entry = (Map.Entry) iterator.next();
            Entity entity = new Entity(entry.getKey());
            ArrayList<String> list = new ArrayList<>();
            for(String s : entry.getValue())
            {
                list.add(s);
            }
            this.questionToken.put(entity, list);
        }
    }

    public List<Map<String, String>> getQuestionTokenPOS() {
        return questionTokenPOS;
    }

    public void setQuestionTokenPOS(List<Map<String, String>> questionTokenPOS) {
        this.questionTokenPOS = questionTokenPOS;
    }

    public HashMap<Entity,List<Map<String,String>>> getQuestionEntityPOS(){return questionEntityPOS;}

    public void setQuestionEntityPOS(HashMap<Entity,List<Map<String,String>>> map) {
        this.questionEntityPOS = new HashMap<>();
        Iterator iterator = map.entrySet().iterator();
        while(iterator.hasNext())
        {
            Map.Entry<Entity,List<Map<String,String>>> entry = (Map.Entry) iterator.next();
            Entity entity = new Entity(entry.getKey());
            List<Map<String,String>> list = new ArrayList<>();
            for(Map<String,String> s : entry.getValue())
            {
                list.add(s);
            }
            this.questionEntityPOS.put(entity, list);
        }
    }

    public List<Entity> getQuestionEntity() {
        return questionEntity;
    }

    public void setQuestionEntity(List<Entity> questionEntity) {
        this.questionEntity = questionEntity;
    }
    public List<Answer> getCandidateAnswer() {
        return candidateAnswer;
    }

    public void setCandidateAnswer(List<Answer> candidateAnswer) {
        this.candidateAnswer = new ArrayList<>();
        for(Answer a : candidateAnswer)
        {
            this.candidateAnswer.add(a);
        }
    }

    public Answer getReturnedAnswer() {
        return returnedAnswer;
    }

    public void setReturnedAnswer(Answer returnedAnswer) {
        this.returnedAnswer = returnedAnswer;
    }

    public String getLAT() {
        return LAT;
    }

    public void setLAT(String LAT) {
        this.LAT = LAT;
    }

    public List<Predicate> getQuestionPredicate() {
        return questionPredicate;
    }

    public void setQuestionPredicate(List<Predicate> questionPredicate) {
        this.questionPredicate = questionPredicate;
    }

    public List<QuestionTemplate> getQuestionTemplateList() {
        return questionTemplateList;
    }

    public void setQuestionTemplateList(List<QuestionTemplate> questionTemplateList) {
        this.questionTemplateList = questionTemplateList;
    }

    public List<Evidence> getQuestionEvidence() {
        return questionEvidence;
    }

    public void setQuestionEvidence(List<Evidence> questionEvidence) {
        this.questionEvidence = questionEvidence;
    }

    public void printQuestionToken(){

        Iterator iterator = this.getQuestionToken().entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry<Entity, ArrayList<String>> entry = (Map.Entry) iterator.next();
            String output = "";
            output += "ENTITY: " + entry.getKey().getKgEntityName() + "\r\n";
            for (String temp : entry.getValue()) {
                output += temp + " ";
            }
            System.out.println(output);
        }
    }

    public Question() {

        this.questionToken = new HashMap<Entity,ArrayList<String>>();
        this.questionTokenPOS = new ArrayList<Map<String, String>>();
        this.questionEntity = new ArrayList<Entity>();
        this.questionPredicate = new ArrayList<Predicate>();
        this.questionTemplateList = new ArrayList<QuestionTemplate>();
        this.candidateAnswer = new ArrayList<Answer>();
        this.returnedAnswer = new Answer();
        }

    public Question(String questionStr){
        this.questionString = questionStr;
    }

    public ArrayList<QueryTuple> getQueryTuples() {
        return queryTuples;
    }

    public void setQueryTuples(ArrayList<QueryTuple> queryTuples) {
        this.queryTuples = queryTuples;
    }

    public String getActuralAnswer() {
        return acturalAnswer;
    }

    public void setActuralAnswer(String acturalAnswer) {
        this.acturalAnswer = acturalAnswer;
    }

}
