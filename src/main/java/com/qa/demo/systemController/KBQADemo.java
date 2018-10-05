package com.qa.demo.systemController;

import com.qa.demo.answerAnalysis.AnswerAnalysisDriverImpl;
import com.qa.demo.conf.FileConfig;
import com.qa.demo.dataStructure.Answer;
import com.qa.demo.dataStructure.DataSource;
import com.qa.demo.dataStructure.Question;
import com.qa.demo.dataStructure.Triplet;
import com.qa.demo.ontologyProcess.TDBCrudDriver;
import com.qa.demo.ontologyProcess.TDBCrudDriverImpl;
import com.qa.demo.query.ALGQuerySynonymKBQA;
import com.qa.demo.query.ESQuerySynonymKBQA;
import com.qa.demo.query.KbqaQueryDriver;
import com.qa.demo.query.KbqaQueryDriverImpl;
import com.qa.demo.utils.es.IndexFile;
import com.qa.demo.utils.io.IOTool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Devin Hua on 2017/9/8.
 * MAIN DRIVER FUNCTION:
 * to get users' questions and return relevant answers;
 */
public class KBQADemo {

    private static final Logger logger = LoggerFactory.getLogger(FaqDemo.class.getName());

    @Test
    public static void main(String[] args) throws IOException {

        IndexFile.indexFaqData(DataSource.SYNONYM);
        logger.info(" [info]已建立faq索引！");

        TDBCrudDriver tdbCrudDriver = new TDBCrudDriverImpl();
        tdbCrudDriver.loadTDBModel();
        logger.info(" [info]已建立TDB MODEL，系统初始化完成！");

        //解决“Comparison method violates its general contract!”的BUG；
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");

        //取得问题集合;
        ArrayList<Question> questions = IOTool.getQuestionsFromTripletGeneratedQuestionFile();
        KbqaQueryDriver kbqaQueryDriver = new KbqaQueryDriverImpl();
        ArrayList<String> outputs = new ArrayList<>();
        String stringtemp = "";
        int count = 0;
        double rightAnswerCount = 0;
        int noAnswerCount = 0;

        long startMili=System.currentTimeMillis();// 当前时间对应的毫秒数

        for(Question q:questions) {
            count++;
            //从ES索引的模板库中匹配模板，并形成查询三元组，最终通过KG三元组匹配得到候选答案;
            KbqaQueryDriver esQuerySynonymKBQADriver = new ESQuerySynonymKBQA();
            q = esQuerySynonymKBQADriver.kbQueryAnswers(q);

            KbqaQueryDriver ALGQuerySynonymKBQADriver = new ALGQuerySynonymKBQA();
            q = ALGQuerySynonymKBQADriver.kbQueryAnswers(q);

            //对答案进行排序
            AnswerAnalysisDriverImpl analysisDriver = new AnswerAnalysisDriverImpl();
            q = analysisDriver.rankAnswerCandidate(q);

            stringtemp = "question "+ count+": " + q.getQuestionString() + "\r\n";
            System.out.print(stringtemp);
            outputs.add(stringtemp);
            for(Answer answer : q.getCandidateAnswer())
            {
                stringtemp = "Candidate Answer: " + answer.getAnswerString() + "\r\n";
                System.out.print(stringtemp);
                outputs.add(stringtemp);
                if(answer.getAnswerTriplet()!=null&&!answer.getAnswerTriplet().isEmpty())
                {
                    for(Triplet triplet : answer.getAnswerTriplet())
                    {
                        stringtemp = "Subject: " + triplet.getSubjectURI() + "\r\n";
                        System.out.print(stringtemp);
                        outputs.add(stringtemp);
                        stringtemp = "Predicate: " + triplet.getPredicateURI() + "\r\n";
                        System.out.print(stringtemp);
                        outputs.add(stringtemp);
                        stringtemp = "Object: " + triplet.getObjectName() + "\r\n";
                        System.out.print(stringtemp);
                        outputs.add(stringtemp);
                        stringtemp = "Score: " + answer.getAnswerScore() + "\r\n";
                        System.out.print(stringtemp);
                        outputs.add(stringtemp);
                    }
                }
            }
            q = analysisDriver.returnAnswer(q);
            String returnedAnswer = q.getReturnedAnswer().getAnswerString();
            String acturalAnswer = q.getActuralAnswer();

            stringtemp = "Actural answer is: "+acturalAnswer + "\r\n";
            System.out.print(stringtemp);
            outputs.add(stringtemp);

            stringtemp = "Returned answer is: "+returnedAnswer + "\r\n";
            System.out.print(stringtemp);
            outputs.add(stringtemp);

            stringtemp = "------------------------------------------------------\r\n";
            System.out.print(stringtemp);
            outputs.add(stringtemp);

            if(returnedAnswer.contains(acturalAnswer))
                rightAnswerCount++;
            if(returnedAnswer.contains("我还得再想想，以后再告诉你"))
                noAnswerCount++;
        }
        long endMili=System.currentTimeMillis();
        stringtemp = "共回答"+(count-1)+"道问题\r\n";
        System.out.print(stringtemp);
        outputs.add(stringtemp);

        stringtemp = "未回答数为： "+noAnswerCount + "\r\n";
        System.out.print(stringtemp);
        outputs.add(stringtemp);

        stringtemp = "正确率为： "+(double)(rightAnswerCount/count)*100+"%" + "\r\n";
        System.out.print(stringtemp);
        outputs.add(stringtemp);

        stringtemp = "总耗时为："+((endMili-startMili)/1000.0)+"秒" + "\r\n";
        System.out.print(stringtemp);
        outputs.add(stringtemp);

        IOTool.writeToFile(outputs, FileConfig.KBQA_TEMPLATE_RESULT);
    }

}
