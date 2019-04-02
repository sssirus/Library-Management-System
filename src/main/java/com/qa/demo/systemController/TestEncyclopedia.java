package com.qa.demo.systemController;

import com.qa.demo.answerAnalysis.AnswerAnalysisDriverImpl;
import com.qa.demo.conf.Configuration;
import com.qa.demo.dataStructure.DataSource;
import com.qa.demo.dataStructure.Question;
import com.qa.demo.query.DbqaQueryDriver;
import com.qa.demo.query.KbqaQueryDriver;
import com.qa.demo.query.QueryEncyclopedia;
import com.qa.demo.query.QueryFaq;
import com.qa.demo.questionAnalysis.QuestionAnalysisDriverImpl;
import com.qa.demo.utils.es.IndexFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Scanner;

/**
 * Description:
 * Created by T.Wu on 2017/10/14.
 */
public class TestEncyclopedia {
    private static Logger LOG = LogManager.getLogger(FaqDemo.class.getName());

    public static void main(String[] args) throws IOException {
        //系统初始化操作：es建立索引
        IndexFile.indexEncyclopediaData(DataSource.ENCYCLOPEDIA);
        //SYNONYM为模板做成同义词组的索引;
        LOG.info("[info]已建立faq索引，系统初始化完成");
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

            //分析问题
//            QuestionAnalysisDriverImpl questionAnalysisDriver = new QuestionAnalysisDriverImpl();
//            question = questionAnalysisDriver.segmentationQuestion(question);

            //从ES中检索faq;
            QueryEncyclopedia queryEncyclopedia = new QueryEncyclopedia();
            question = queryEncyclopedia.search(question, DbqaQueryDriver.QueryType.MATCH_PHRASE_QUERY, DataSource.ENCYCLOPEDIA);

            //对答案进行排序S
            AnswerAnalysisDriverImpl analysisDriver = new AnswerAnalysisDriverImpl();
            question = analysisDriver.rankAnswerCandidate(question);
            //生成答案并返回S
            question = analysisDriver.returnAnswer(question);
            LOG.info("[info]处理完成");

            //输出答案
            System.out.println("系统作答：");
            System.out.println(question.getReturnedAnswer().getAnswerString());
        }

        scanner.close();
    }
}
