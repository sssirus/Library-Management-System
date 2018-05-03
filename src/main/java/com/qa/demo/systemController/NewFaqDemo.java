package com.qa.demo.systemController;

import com.qa.demo.answerAnalysis.AnswerAnalysisDriverImpl;
import com.qa.demo.conf.Configuration;
import com.qa.demo.conf.FileConfig;
import com.qa.demo.dataStructure.*;
import com.qa.demo.ontologyProcess.TDBCrudDriver;
import com.qa.demo.ontologyProcess.TDBCrudDriverImpl;
import com.qa.demo.query.*;
import com.qa.demo.utils.es.IndexFile;
import com.qa.demo.utils.io.IOTool;
import org.apache.log4j.PropertyConfigurator;
import org.bytedeco.javacpp.Loader;
import org.nd4j.nativeblas.Nd4jCpu;
import org.nlpcn.commons.lang.util.logging.Log;
import org.nlpcn.commons.lang.util.logging.LogFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
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

    public static void testByInput(){
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

            //
            KbqaQueryDriver OpenKBQADriver = new OpenKBQA();
            question = OpenKBQADriver.kbQueryAnswers(question);
            // 对答案进行排序
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

    public static  void testByFiles(){
        //取得问题集合;
        ArrayList<Question> questions = IOTool.getQuestionsFromTripletGeneratedQuestionFile();

        ArrayList<String> outputs = new ArrayList<>();
        String stringtemp = "";
        String question_string = "";
        int count = 0;
        int wrongCount = 0;
        double rightAnswerCount = 0;
        int noAnswerCount = 0;
        long startMili=System.currentTimeMillis();// 当前时间对应的毫秒数

        for(Question q:questions) {
            String qstring = q.getQuestionString();
            if (qstring == "" || qstring == null){
                break;
            }
            for(String punctuation : Configuration.PUNCTUATION_SET)
            {
                qstring = qstring.replace(punctuation,"");
            }
            q.setQuestionString(qstring);
            question_string = qstring;

            count++;
            //
            KbqaQueryDriver OpenKBQADriver = new OpenKBQA();
            q = OpenKBQADriver.kbQueryAnswers(q);
            // 对答案进行排序
            AnswerAnalysisDriverImpl analysisDriver = new AnswerAnalysisDriverImpl();
            q = analysisDriver.rankAnswerCandidate(q);

            //生成答案并返回
            q = analysisDriver.returnAnswer(q);

            stringtemp = "question "+ count+": " + question_string + "\r\n";
            System.out.print(stringtemp);
            outputs.add(stringtemp);

            HashSet<String> triplets = new HashSet<>();
            String tripletsString = "";
            for(Triplet triplet: q.getTripletList()){
                if (!triplets.contains(triplet.getSubjectURI())){
                    triplets.add(triplet.getSubjectURI());
                    tripletsString+=triplet.getSubjectURI();
                    tripletsString+=",";
                }


            }


            stringtemp = "找到的相关三元组："+ tripletsString + "\r\n";
            System.out.print(stringtemp);
            outputs.add(stringtemp);

            String returnedAnswer = q.getReturnedAnswer().getAnswerString().trim();
            String acturalAnswer = q.getActuralAnswer();

            stringtemp = "Actural answer is: "+acturalAnswer + "\r\n";
            System.out.print(stringtemp);
            outputs.add(stringtemp);

            stringtemp = "Returned answer is: "+returnedAnswer + "\r\n";
            System.out.print(stringtemp);
            outputs.add(stringtemp);

            stringtemp = "Answer source is: "+q.getReturnedAnswer().getAnswerSource() + "\r\n";
            System.out.print(stringtemp);
            outputs.add(stringtemp);

            int flag = 0;
            String flag_string = "";
            if(returnedAnswer.contains(acturalAnswer)){
                rightAnswerCount++;
                flag = 1;
            }
            else if(returnedAnswer.contains("我还得再想想，以后再告诉你")){
                noAnswerCount++;
                flag = 0;
            }
            else{
                wrongCount++;
                flag = -1;
            }

            switch(flag)
            {
                case 0 :
                    flag_string = "无答案";
                    break;
                case 1:
                    flag_string = "正确答案";
                    break;
                case -1:
                    flag_string = "错误答案";
                    break;
                default :
                    flag_string = "无答案";
                    break;
            }


            stringtemp = "Answer result is: " + flag_string + "\r\n";
            System.out.print(stringtemp);
            outputs.add(stringtemp);

            stringtemp = "------------------------------------------------------\r\n";
            System.out.print(stringtemp);
            outputs.add(stringtemp);
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

        try {
            IOTool.writeToFile(outputs, "src/main/resources/data/newdemo_result200.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        try {
            Loader.load(Nd4jCpu.class);
        } catch (UnsatisfiedLinkError e) {
            String path = Loader.cacheResource(Nd4jCpu.class, "windows-x86_64/jniNd4jCpu.dll").getPath();
            new ProcessBuilder(W2V_file, path).start().waitFor();
        }

        //testByInput();
        testByFiles();


    }
}
