package com.qa.demo.controller;

import com.qa.demo.answerAnalysis.AnswerAnalysisDriverImpl;
import com.qa.demo.conf.Configuration;
import com.qa.demo.dataStructure.DataSource;
import com.qa.demo.dataStructure.QueryTuple;
import com.qa.demo.dataStructure.Question;
import com.qa.demo.query.*;
import com.qa.demo.questionAnalysis.Segmentation;
import com.qa.demo.systemController.FaqDemo;
import org.apache.log4j.PropertyConfigurator;
import org.nlpcn.commons.lang.util.logging.Log;
import org.nlpcn.commons.lang.util.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import static com.qa.demo.conf.FileConfig.LOG_PROPERTY;

/**
 * Created by hyh on 2017/8/14.
 */
@Controller
public class MainController {

    public static final Log LOG = LogFactory.getLog(FaqDemo.class);

    @RequestMapping(value = "/",method = RequestMethod.GET)
    public String Index()
    {
        return "index";
    }

    @RequestMapping(value = "/question", method = RequestMethod.POST)
    public String IndexSearch(@RequestParam("question") String questionstring, Model model) throws IOException
    {
        PropertyConfigurator.configure(LOG_PROPERTY);

        System.out.println(questionstring);
        Scanner scanner = new Scanner(questionstring);

        String input = scanner.next();

        for(String punctuation : Configuration.PUNCTUATION_SET)
        {
            input = input.replace(punctuation,"");
        }

        Question question = new Question(input);

        //从模板的同义词集合中查询，泛化主要功能；
        KbqaQueryDriver ALGQuerySynonymKBQADriver = new ALGQuerySynonymKBQA();
        question = ALGQuerySynonymKBQADriver.kbQueryAnswers(question);

        //从ES索引的模板库中匹配模板，并形成查询三元组，最终通过KG三元组匹配得到候选答案;
        KbqaQueryDriver esQuerySynonymKBQADriver = new ESQuerySynonymKBQA();
        question = esQuerySynonymKBQADriver.kbQueryAnswers(question);

        //从ES索引的模板库中匹配模板，并形成查询三元组，最终通过KG三元组匹配得到候选答案;
        QueryPatternKBQA queryPatternKBQA = new QueryPatternKBQA();
        question = queryPatternKBQA.kbQueryAnswers(question);

        //从ES中检索faq;
        QueryFaq queryFaq = new QueryFaq();
//      question = queryFaq.search(question, DbqaQueryDriver.QueryType.MATCH_PHRASE_QUERY, DataSource.FAQ_T, DataSource.FAQ);
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
        System.out.println(question.getReturnedAnswer().getAnswerString().trim());
        model.addAttribute("answer",question.getReturnedAnswer().getAnswerString().trim());

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
        return "result";
    }

}
