package com.qa.demo.query;

import com.qa.demo.answerAnalysis.AnswerAnalysisDriverImpl;
import com.qa.demo.conf.Configuration;
import com.qa.demo.dataStructure.Question;
import com.qa.demo.ontologyProcess.TDBCrudDriver;
import com.qa.demo.ontologyProcess.TDBCrudDriverImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.bytedeco.javacpp.Loader;
import org.nd4j.nativeblas.Nd4jCpu;

import java.io.IOException;
import java.util.Scanner;

import static com.qa.demo.conf.FileConfig.W2V_file;

public class TPTest {

    private static final Logger logger = LoggerFactory.getLogger(TPTest.class);


    public static void main(String[] args) throws IOException {

        try {
            Loader.load(Nd4jCpu.class);
        } catch (UnsatisfiedLinkError e) {
            String path = Loader.cacheResource(Nd4jCpu.class, "windows-x86_64/jniNd4jCpu.dll").getPath();
            try {
                new ProcessBuilder(W2V_file, path).start().waitFor();
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }

        TDBCrudDriver tdbCrudDriver = new TDBCrudDriverImpl();
        tdbCrudDriver.loadTDBModel();
        logger.info((" [info]已建立TDB MODEL，系统初始化完成！"));

        //String string="花生什么时候种植?";
        //String string="王绶是哪个民族的";  //民族 n
        String string="翠菊的颜色是什么？";  //基于Word2Vec的测试用句 这里颜色与花色的相似度为0.55
//        String string="翠菊的规范汉字编号是什么？";
//        String string="翠菊是什么？";


        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入问题，换行表示输入下一题，‘#’结束");
        while (true) {
            //等待问题输入
//            InputFromConsole input = new InputFromConsole();
//            Question question = input.getQuestion();
//            LOG.info("[info]输入的问题是："+question.getQuestionString());
            String input = scanner.next();
            if (input == "" || input == null || input.equals("#")) {
                break;
            }
            for (String punctuation : Configuration.PUNCTUATION_SET) {
                input = input.replace(punctuation, "");
            }

            Question question = new Question();
            question.setQuestionString(input);

            //从模板的同义词集合中查询（模板分词之后形成的同义词集合），泛化主要功能；
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
}
