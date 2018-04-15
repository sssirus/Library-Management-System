package com.qa.demo.systemController;

import com.qa.demo.answerAnalysis.AnswerAnalysisDriverImpl;
import com.qa.demo.conf.Configuration;
import com.qa.demo.dataStructure.DataSource;
import com.qa.demo.dataStructure.QueryTuple;
import com.qa.demo.dataStructure.Question;
import com.qa.demo.ontologyProcess.TDBCrudDriver;
import com.qa.demo.ontologyProcess.TDBCrudDriverImpl;
import com.qa.demo.query.ALGQuerySynonymKBQA;
import com.qa.demo.query.KbqaQueryDriver;
import com.qa.demo.query.OpenKBQA;
import com.qa.demo.utils.es.IndexFile;
import org.apache.log4j.PropertyConfigurator;
import org.bytedeco.javacpp.Loader;
import org.nd4j.nativeblas.Nd4jCpu;
import org.nlpcn.commons.lang.util.logging.Log;
import org.nlpcn.commons.lang.util.logging.LogFactory;

import java.io.IOException;
import java.util.Scanner;

import static com.qa.demo.conf.FileConfig.LOG_PROPERTY;
import static com.qa.demo.conf.FileConfig.W2V_file;

/**
 * @author J.Y.Zhang
 * @create 2018-04-14
 * Function description:
 **/
public class NewFaqDemo {
    public static final Log LOG = LogFactory.getLog(NewFaqDemo.class);

    public static void main(String[] args) throws IOException, InterruptedException {

        try {
            Loader.load(Nd4jCpu.class);
        } catch (UnsatisfiedLinkError e) {
            String path = Loader.cacheResource(Nd4jCpu.class, "windows-x86_64/jniNd4jCpu.dll").getPath();
            new ProcessBuilder(W2V_file, path).start().waitFor();
        }

//        PropertyConfigurator.configure(LOG_PROPERTY);
//        //系统初始化操作：es建立索引
//        //SYNONYM为分词之后的模板；
//        IndexFile.indexFaqData(DataSource.SYNONYM);
//        //为19000条百科知识的索引；
//        IndexFile.indexEncyclopediaData(DataSource.ENCYCLOPEDIA);
//        //FAQ为常用问答对的索引；PATTERN为模板的索引;FAQ_T为生成的所有问题的模板；
//        IndexFile.indexFaqData(DataSource.FAQ, DataSource.PATTERN);
//        LOG.info(" [info]已建立faq索引！");
//
//        TDBCrudDriver tdbCrudDriver = new TDBCrudDriverImpl();
//        tdbCrudDriver.loadTDBModel();
//        LOG.info(" [info]已建立TDB MODEL，系统初始化完成！");


        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入问题，换行表示输入下一题，‘#’结束");
        while (true) {
            //等待问题输入
            String input = scanner.next();
            if (input == "" || input == null || input.equals("#")) {
                break;
            }
            for (String punctuation : Configuration.PUNCTUATION_SET) {
                input = input.replace(punctuation, "");
            }
            Question question = new Question(input);

            //从模板的同义词集合中查询（模板分词之后形成的同义词集合），泛化主要功能；
            KbqaQueryDriver OpenKBQADriver = new OpenKBQA();
            question = OpenKBQADriver.kbQueryAnswers(question);
            //对答案进行排序
            AnswerAnalysisDriverImpl analysisDriver = new AnswerAnalysisDriverImpl();
            question = analysisDriver.rankAnswerCandidate(question);

            //生成答案并返回
            question = analysisDriver.returnAnswer(question);

            //输出答案
            System.out.println("系统作答：");
            System.out.println(question.getReturnedAnswer().getAnswerString().trim());
            if(question.getReturnedAnswer().getAnswerString().trim().contains
                    ("我还得再想想"))
            {
                LOG.error("[error] 用户输入的问题为： " + input);
                LOG.error("[error] 问题无法回答");
                for(QueryTuple t : question.getQueryTuples())
                {
                    LOG.error("[error] 返回模板为：");
                    LOG.error(t.toString());
                }
                LOG.error("[error] 处理完成");
            }
            else{
                LOG.info("[info] 用户输入的问题为： " + input);
                LOG.info("[info] 系统作答：");
                LOG.info("[info] " + question.getReturnedAnswer().getAnswerString().trim());
                LOG.info("[info] 处理完成");
            }

            LOG.info(question.toString());
        }
    }
}
