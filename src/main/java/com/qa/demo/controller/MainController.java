package com.qa.demo.controller;

import com.qa.demo.answerAnalysis.AnswerAnalysisDriverImpl;
import com.qa.demo.conf.Configuration;
import com.qa.demo.dataStructure.Question;
import com.qa.demo.query.KbqaQueryDriver;
import com.qa.demo.query.OpenKBQA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Scanner;



/**
 * Created by hyh on 2017/8/14.
 */
@Controller
public class MainController {

    private static final Logger infologger = LoggerFactory.getLogger("queryLoggerInfo");
    private static final Logger errorlogger = LoggerFactory.getLogger("queryLoggerError");

    @RequestMapping(value = "/",method = RequestMethod.GET)
    public String Index()
    {
        return "index";
    }

    @RequestMapping(value = "/question", method = RequestMethod.POST)
    public String IndexSearch(@RequestParam("question") String questionstring, Model model) throws IOException
    {

        System.out.println("aaaa");
        infologger.info("[info] 用户输入的问题为： " +questionstring);
        Scanner scanner = new Scanner(questionstring);

        String input = scanner.next();

        String qstring = input.replace(" ","");
        if (qstring == "" || qstring == null){
            return "noquestion";
        }
        for(String punctuation : Configuration.PUNCTUATION_SET)
        {
            qstring = qstring.replace(punctuation,"");
        }
        Question q = new Question(input);
        q.setQuestionString(qstring);
        KbqaQueryDriver OpenKBQADriver = new OpenKBQA();
        q = OpenKBQADriver.kbQueryAnswers(q);



        // 对答案进行排序
        AnswerAnalysisDriverImpl analysisDriver = new AnswerAnalysisDriverImpl();
        q = analysisDriver.rankAnswerCandidate(q);

        //生成答案并返回
        q = analysisDriver.returnAnswer(q);

        System.out.println(q.getReturnedAnswer().getAnswerString().trim());
        model.addAttribute("answer",q.getReturnedAnswer().getAnswerString().trim());


        return "result";
    }
    @RequestMapping(value = "/question2", method = RequestMethod.POST)
    public String IndexSearch2(@RequestParam("question2") String questionstring, Model model) throws IOException
    {

        System.out.println("bbbb");
        infologger.info("[info] 用户输入的问题为： " +questionstring);
        Scanner scanner = new Scanner(questionstring);

        String input = scanner.next();

        String qstring = input.replace(" ","");
        if (qstring == "" || qstring == null){
            return "noquestion";
        }
        for(String punctuation : Configuration.PUNCTUATION_SET)
        {
            qstring = qstring.replace(punctuation,"");
        }
        Question q = new Question(input);
        q.setQuestionString(qstring);
        KbqaQueryDriver OpenKBQADriver = new OpenKBQA();
        q = OpenKBQADriver.kbQueryAnswers(q);



        // 对答案进行排序
        AnswerAnalysisDriverImpl analysisDriver = new AnswerAnalysisDriverImpl();
        q = analysisDriver.rankAnswerCandidate(q);

        //生成答案并返回
        q = analysisDriver.returnAnswer(q);

        System.out.println(q.getReturnedAnswer().getAnswerString().trim());
        model.addAttribute("answer",q.getReturnedAnswer().getAnswerString().trim());


        return "result";
    }
}
