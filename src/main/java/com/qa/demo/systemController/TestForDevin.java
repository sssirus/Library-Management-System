package com.qa.demo.systemController;

import com.qa.demo.answerAnalysis.AnswerAnalysisDriverImpl;
import com.qa.demo.conf.Configuration;
import com.qa.demo.dataStructure.DataSource;
import com.qa.demo.dataStructure.Question;
import com.qa.demo.ontologyProcess.TDBCrudDriver;
import com.qa.demo.ontologyProcess.TDBCrudDriverImpl;
import com.qa.demo.query.*;
import com.qa.demo.utils.es.IndexFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Scanner;

public class TestForDevin {

    private static Logger LOG = LogManager.getLogger(FaqDemo.class.getName());

    public static void main(String[] args) throws IOException {
        //系统初始化操作：es建立索引

        IndexFile.indexFaqData(DataSource.SYNONYM);
        LOG.info(" [info]已建立faq索引！");

        TDBCrudDriver tdbCrudDriver = new TDBCrudDriverImpl();
        tdbCrudDriver.loadTDBModel();
        LOG.info(" [info]已建立TDB MODEL，系统初始化完成！");

        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入问题，换行表示输入下一题，‘#’结束");
        while (true){
            //等待问题输入
//            InputFromConsole input = new InputFromConsole();
//            Question question = input.getQuestion();
//            LOG.info("[info]输入的问题是："+question.getQuestionString());
            String input = scanner.next();
            if (input == "" || input == null || input.equals("#")){
                break;
            }
            for(String punctuation : Configuration.PUNCTUATION_SET)
            {
                input = input.replace(punctuation,"");
            }
            Question question = new Question(input);

            //从ES索引的模板库中匹配模板，并形成查询三元组，最终通过KG三元组匹配得到候选答案;
            KbqaQueryDriver esQuerySynonymKBQADriver = new ESQuerySynonymKBQA();
            question = esQuerySynonymKBQADriver.kbQueryAnswers(question);

            KbqaQueryDriver ALGQuerySynonymKBQADriver = new ALGQuerySynonymKBQA();
                    question = ALGQuerySynonymKBQADriver.kbQueryAnswers(question);

            //对答案进行排序
            AnswerAnalysisDriverImpl analysisDriver = new AnswerAnalysisDriverImpl();
            question = analysisDriver.rankAnswerCandidate(question);

//            //输出排序后的候选答案；
//            for(Answer a : question.getCandidateAnswer())
//            {
//                System.out.println("候选答案来源为： " + a.getAnswerSource());
//                System.out.println("候选答案得分为： " + a.getAnswerScore());
//                System.out.println("候选答案为： " + a.getAnswerString() + "\r\n");
//            }

            //生成答案并返回
            question = analysisDriver.returnAnswer(question);
            LOG.info("[info]处理完成");

            //输出答案
            System.out.println("系统作答：");
            System.out.println(question.getReturnedAnswer().getAnswerString());
        }

        scanner.close();
    }

}
