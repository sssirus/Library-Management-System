package com.qa.demo.query;

import com.qa.demo.dataStructure.Question;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ALGQuerySynonymKBQATest {
    @Test
    void kbQueryAnswers() {

        Question question=new Question();
        String string="花生什么时候种植?";

        question.setQuestionString(string);

        //从模板的同义词集合中查询（模板分词之后形成的同义词集合），泛化主要功能；
        KbqaQueryDriver ALGQuerySynonymKBQADriver = new ALGQuerySynonymKBQA();
        question = ALGQuerySynonymKBQADriver.kbQueryAnswers(question);

        question.printQuestionToken();

    }

}