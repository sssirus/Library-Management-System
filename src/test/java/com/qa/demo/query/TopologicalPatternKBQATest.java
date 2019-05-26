package com.qa.demo.query;

import com.qa.demo.answerAnalysis.AnswerAnalysisDriverImpl;
import com.qa.demo.dataStructure.Answer;
import com.qa.demo.dataStructure.Question;
import com.qa.demo.ontologyProcess.TDBCrudDriver;
import com.qa.demo.ontologyProcess.TDBCrudDriverImpl;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TopologicalPatternKBQATest {
    @Test
    void kbQueryAnswers() {

        TDBCrudDriver tdbCrudDriver = new TDBCrudDriverImpl();
        tdbCrudDriver.loadTDBModel();
        System.out.println(" [info]已建立TDB MODEL，系统初始化完成！");

        Question question=new Question();
        //String string="花生什么时候种植?";
        //String string="王绶是哪个民族的";  //民族 n
        //String string="翠菊的烟色是什么？";  // 基于字向量测试 谓词指称 烟 谓词颜色 相似度：0.03430993729185683
                                              //                 谓词指称 色 谓词颜色 相似度：0.44784861285738065
        String string="翠菊的花色是什么？";//基于Word2Vec的测试用句 这里颜色与花色的相似度为0.55
//        String string="翠菊的规范汉字编号是什么？";
//        String string="翠菊是什么？";
//        String string = "哪里有木麻黄？";
//        String string = "PigWIN的operatingSystem是什么？";

        question.setQuestionString(string);

        KbqaQueryDriver topologocalPatternKBQADriver = new TopologicalPatternKBQA();
         question = topologocalPatternKBQADriver.kbQueryAnswers(question);

        //对答案进行排序
        AnswerAnalysisDriverImpl analysisDriver = new AnswerAnalysisDriverImpl();
        question = analysisDriver.rankAnswerCandidate(question);

        //生成答案并返回
        question = analysisDriver.returnAnswer(question);

        //输出答案
        System.out.println("系统作答：");
        System.out.println(question.getReturnedAnswer().getAnswerString().trim());

    }

}