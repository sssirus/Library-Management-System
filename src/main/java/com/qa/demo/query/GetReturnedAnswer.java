package com.qa.demo.query;

import com.qa.demo.dataStructure.Answer;
import com.qa.demo.dataStructure.Question;

import java.util.HashSet;

public class GetReturnedAnswer {

    public static Question getReturnedAnswer(Question q){

        HashSet<String> answerStrings = new HashSet<>();
        for(Answer a : q.getCandidateAnswer())
        {
            answerStrings.add(a.getAnswerString());
        }

        String answerResult = "";
        for(String temp : answerStrings)
        {
            answerResult+=temp;
            answerResult+="\t";
        }
        answerResult = answerResult.trim();
        Answer answer = new Answer(answerResult);
        //TODO:这里只是粗糙地返回答案，后续需要更改;
        q.setReturnedAnswer(answer);
        return q;
    }

}
