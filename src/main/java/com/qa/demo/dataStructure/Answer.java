package com.qa.demo.dataStructure;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Devin Hua on 2017/8/30.
 * 系统中表示答案及相关信息的数据结构；
 */

public class Answer implements Comparable<Answer> {

    //表示答案字符串，如：脊索动物门；
    private String answerString;

    //表示得到答案的三元组列表，之所以是列表，是有对嵌套问题可能生成多个三元组；
    private List<Triplet> answerTriplet;

    //表示支持答案的证据；
    private List<Evidence> answerEvidence;

    //表示答案的得分；
    private double answerScore;

    //表示答案的类型；
    private AnswerType LAT;

    //表示答案的来源是ES中FAQ问答对，还是三元组形成的问答对，还是模板;
    private String answerSource;

    public Answer(){
        this.answerString = "我还得再想想，以后再告诉你";
        ArrayList<Triplet> answerTriplet = new ArrayList<Triplet>();
        this.setAnswerTriplet(answerTriplet);
    }

    public Answer(String answerString){
        this.answerString = answerString;
        ArrayList<Triplet> answerTriplet = new ArrayList<Triplet>();
        this.setAnswerTriplet(answerTriplet);
    }

    public String getAnswerString() {
        return answerString;
    }

    public void setAnswerString(String answerString) {
        this.answerString = answerString;
    }

    public List<Triplet> getAnswerTriplet() {
        return answerTriplet;
    }

    public void setAnswerTriplet(List<Triplet> answerTriplet) {
        this.answerTriplet = new ArrayList<>();
        for(Triplet t : answerTriplet)
        {
            this.answerTriplet.add(t);
        }
    }

    public List<Evidence> getAnswerEvidence() {
        return answerEvidence;
    }

    public void setAnswerEvidence(List<Evidence> answerEvidence) {
        this.answerEvidence = answerEvidence;
    }

    public double getAnswerScore() {
        return answerScore;
    }

    public void setAnswerScore(double answerScore) {
        this.answerScore = answerScore;
    }

    public AnswerType getLAT() {
        return LAT;
    }

    public void setLAT(AnswerType LAT) {
        this.LAT = LAT;
    }

    @Override
    public int compareTo(Answer answer) {
        if(this.answerScore > answer.answerScore){
            return 1;
        }else if(this.answerScore == answer.answerScore){
            return 0;
        }else{
            return -1;
        }
    }

    public String getAnswerSource() {
        return answerSource;
    }

    public void setAnswerSource(String answerSource) {
        this.answerSource = answerSource;
    }
}
