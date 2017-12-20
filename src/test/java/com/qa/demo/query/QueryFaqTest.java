package com.qa.demo.query;

import com.qa.demo.dataStructure.Answer;
import com.qa.demo.dataStructure.DataSource;
import com.qa.demo.dataStructure.Question;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QueryFaqTest {
    @Test
    void search() {
        Question question = new Question("啤梨属于什么种？");
        question = new QueryFaq().search(question, DbqaQueryDriver.QueryType.MATCH_PHRASE_QUERY, DataSource.FAQ_T);
        for(Answer candidate: question.getCandidateAnswer()){
            System.out.println(candidate.getAnswerString());
        }
    }

    @Test
    void search1() {


    }

}