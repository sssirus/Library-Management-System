package com.qa.demo.query;

import com.qa.demo.dataStructure.Answer;
import com.qa.demo.dataStructure.Question;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ALGQuerySynonymKBQATest {
    @Test
    void kbQueryAnswers() {

        Question question=new Question();
        //String string="花生什么时候种植?";
        //String string="王绶是哪个民族的";  //民族 n
        String string="翠菊的颜色是什么？";  //基于Word2Vec的测试用句 这里颜色与花色的相似度为0.55

        question.setQuestionString(string);

        //从模板的同义词集合中查询（模板分词之后形成的同义词集合），泛化主要功能；
        KbqaQueryDriver ALGQuerySynonymKBQADriver = new ALGQuerySynonymKBQA();
        question = ALGQuerySynonymKBQADriver.kbQueryAnswers(question);

        question.printQuestionToken();

        List<Answer> answers= question.getCandidateAnswer();
        System.out.println("The answer is :");
        for(Answer ans: answers)
        {
            System.out.println(ans.getAnswerString()+" "+ans.getAnswerScore());
        }

    }

}