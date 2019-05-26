package com.qa.demo.controller;

import com.qa.demo.answerAnalysis.AnswerAnalysisDriverImpl;
import com.qa.demo.conf.Configuration;
import com.qa.demo.dataStructure.DataSource;
import com.qa.demo.dataStructure.QueryTuple;
import com.qa.demo.dataStructure.Question;
import com.qa.demo.query.*;
import com.qa.demo.questionAnalysis.Segmentation;
import com.qa.demo.systemController.FaqDemo;

import org.nlpcn.commons.lang.util.logging.Log;
import org.nlpcn.commons.lang.util.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.List;
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

        if(q.getReturnedAnswer().getAnswerString().trim().contains
                ("我还得再想想"))
        {
            errorlogger.error("[error] 用户输入的问题为： " + input);
            errorlogger.error("[error] 问题无法回答");
            for(QueryTuple t : q.getQueryTuples())
            {
                errorlogger.error("[error] 返回模板为：");
                errorlogger.error(t.toString());
            }
            errorlogger.error("[error] 处理完成");
        }
        else{
          //  infologger.info("[info] 用户输入的问题为： " + input);

            //输出候选答案
            infologger.info("候选答案：");
            for(int i=0;i<q.getCandidateAnswer().size();i++)
            {
                infologger.info(i +"\t"+q.getCandidateAnswer().get(i).getAnswerString());
            }


            infologger.info("[info] 系统作答 " + q.getReturnedAnswer().getAnswerString().trim());
            infologger.info("[info] 处理完成");
        }
        return "result";
    }

}
