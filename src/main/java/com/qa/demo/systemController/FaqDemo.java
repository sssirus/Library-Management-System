package com.qa.demo.systemController;

import com.qa.demo.answerAnalysis.AnswerAnalysisDriverImpl;
import com.qa.demo.conf.Configuration;
import com.qa.demo.dataStructure.DataSource;
import com.qa.demo.dataStructure.QueryTuple;
import com.qa.demo.dataStructure.Question;
import com.qa.demo.ontologyProcess.TDBCrudDriver;
import com.qa.demo.ontologyProcess.TDBCrudDriverImpl;
import com.qa.demo.query.*;
import com.qa.demo.questionAnalysis.Segmentation;
import com.qa.demo.utils.es.IndexFile;
import org.apache.log4j.PropertyConfigurator;
import org.bytedeco.javacpp.Loader;
import org.nd4j.nativeblas.Nd4jCpu;
import org.nlpcn.commons.lang.util.logging.Log;
import org.nlpcn.commons.lang.util.logging.LogFactory;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import static com.qa.demo.conf.FileConfig.LOG_PROPERTY;
import static com.qa.demo.conf.FileConfig.W2V_file;

/**
 * Description: 一个完全依赖于模板、同义词集合、es、常见问答对构建的基于文本的问答系统demo：
 * 问题输入，计算最相关的问题，返回最相关问题对应的答案
 * Created by TT. Wu on 2017/9/3.
 */
public class FaqDemo {

    public static final Log LOG = LogFactory.getLog(FaqDemo.class);
//    private static Logger LOG = LogManager.getLogger(FaqDemo.class.getName());

    public static void main(String[] args) throws IOException, InterruptedException {

        try {
            Loader.load(Nd4jCpu.class);
        } catch (UnsatisfiedLinkError e) {
            String path = Loader.cacheResource(Nd4jCpu.class, "windows-x86_64/jniNd4jCpu.dll").getPath();
            new ProcessBuilder(W2V_file, path).start().waitFor();
        }

        PropertyConfigurator.configure(LOG_PROPERTY);
        //系统初始化操作：es建立索引
        //SYNONYM为分词之后的模板；
        IndexFile.indexFaqData(DataSource.SYNONYM);
        //为19000条百科知识的索引；
        IndexFile.indexEncyclopediaData(DataSource.ENCYCLOPEDIA);
        //FAQ为常用问答对的索引；PATTERN为模板的索引;FAQ_T为生成的所有问题的模板；
//      IndexFile.indexFaqData(DataSource.FAQ, DataSource.PATTERN, DataSource.FAQ_T);
        IndexFile.indexFaqData(DataSource.FAQ, DataSource.PATTERN);
//      IndexFile.indexFaqData(DataSource.FAQ);
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

            //从模板的同义词集合中查询（模板分词之后形成的同义词集合），泛化主要功能；
            KbqaQueryDriver ALGQuerySynonymKBQADriver = new ALGQuerySynonymKBQA();
            question = ALGQuerySynonymKBQADriver.kbQueryAnswers(question);

//            //从ES索引的模板库中匹配模板（模板分词之后形成的关键词组合），并形成查询三元组，最终通过KG三元组匹配得到候选答案;
//            KbqaQueryDriver esQuerySynonymKBQADriver = new ESQuerySynonymKBQA();
//            question = esQuerySynonymKBQADriver.kbQueryAnswers(question);
//
//            //从ES索引的模板库中匹配模板（从自然问句中将实体去掉后的模板），并形成查询三元组，最终通过KG三元组匹配得到候选答案;
//            QueryPatternKBQA queryPatternKBQA = new QueryPatternKBQA();
//            question = queryPatternKBQA.kbQueryAnswers(question);

            //从ES中检索faq;
            QueryFaq queryFaq = new QueryFaq();
//            question = queryFaq.search(question, DbqaQueryDriver.QueryType.MATCH_PHRASE_QUERY, DataSource.FAQ_T, DataSource.FAQ);
            question = queryFaq.search(question, DbqaQueryDriver.QueryType.MATCH_PHRASE_QUERY, DataSource.FAQ);

            //从ES中索引的百科知识检索faq;
            QueryEncyclopedia queryEncyclopedia = new QueryEncyclopedia();
            question = queryEncyclopedia.search(question, DbqaQueryDriver.QueryType.MATCH_PHRASE_QUERY, DataSource.ENCYCLOPEDIA);

            //将question_string分词之后再查询一次;
            Segmentation.segmentation(input);
            List<String> tokens = Segmentation.getTokens();
            String token_string = "";
            for(String token : tokens)
            {
                token_string += token + " ";
            }
            token_string = token_string.trim();
            question.setQuestionString(token_string);
            question = queryEncyclopedia.search(question, DbqaQueryDriver.QueryType.MATCH_PHRASE_QUERY, DataSource.ENCYCLOPEDIA);

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
//                for(QueryTuple t : question.getQueryTuples())
//                {
//                    LOG.info("[info] 返回模板为：");
//                    LOG.info(t.toString());
//                }
                LOG.info("[info] 处理完成");
            }
        }
        scanner.close();
    }
}
