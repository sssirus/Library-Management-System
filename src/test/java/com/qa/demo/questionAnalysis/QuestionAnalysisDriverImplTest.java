package com.qa.demo.questionAnalysis;

import com.qa.demo.dataStructure.Question;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuestionAnalysisDriverImplTest {
    @Test
    void segmentationQuestion() throws Exception {
        Question question=new Question();
        String string="花生什么时候种植?";

        question.setQuestionString(string);

        QuestionAnalysisDriver qAnalysisDriver=new QuestionAnalysisDriverImpl();
        question = qAnalysisDriver.segmentationQuestion(question);
        question.printQuestionToken();
    }

    @Test
    void posQuestion() {
    }

    @Test
    void nerQuestion() {
    }

    @Test
    void patternExtractQuestion() {
    }

    @Test
    void latQuestion() {
    }

    @Test
    void relationExtractQuestion() {
    }

}