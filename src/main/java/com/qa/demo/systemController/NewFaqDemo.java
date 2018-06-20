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
import java.util.List;
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
            String candidateString = "#############\r\n";
            for(Answer candidateAnswer:question.getCandidateAnswer()){
                String preUri = candidateAnswer.getAnswerTriplet().get(0).getPredicateURI();
                candidateString += preUri.substring(preUri.lastIndexOf("/")+1);
                candidateString += "::(" + candidateAnswer.getAnswerScore()+") ::  "+ candidateAnswer.getAnswerString() + "\r\n";
                if(candidateAnswer.getAnswerString() == null || candidateAnswer.getAnswerString().length() == 0) continue;
            }
            if(question.getReturnedAnswer().getAnswerString().trim().contains
                    ("我还得再想想"))
            {
                LOG.error("[error] 用户输入的问题为： " + input);
                LOG.error("[error] 问题无法回答");

                LOG.error("[error] 处理完成");
            }
            else{
                LOG.info("[info] 用户输入的问题为： " + input);
                LOG.info("[info] 系统作答：");
                LOG.info("[info] " + question.getReturnedAnswer().getAnswerString().trim());
                LOG.info("[info] 处理完成");
                LOG.info("[info] " + candidateString);
            }

            LOG.info(question.toString());
        }
    }

    public static  void testByFiles(List<Question> questions, int i){

        ArrayList<String> outputs = new ArrayList<>();
        String stringtemp = "";
        String question_string = "";
        int count = 0;
        int wrongCount = 0;
        double rightAnswerCount = 0;
        int noAnswerCount = 0;
        int noEntityCount = 0;
        int rightTripletCount = 0;
        long startMili=System.currentTimeMillis();// 当前时间对应的毫秒数

        for(Question q:questions) {
            String qstring = q.getQuestionString().replace(" ","");
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

            if(q.getQuestionEntity().size() == 0){
                noEntityCount += 1;
            }
            stringtemp = "找的实体是： ";
            for(Entity entity: q.getQuestionEntity()){
                stringtemp += entity.getKgEntityName();
                stringtemp += " ";
            }
            stringtemp += "\r\n";
            System.out.print(stringtemp);
            outputs.add(stringtemp);

            HashSet<String> triplets = new HashSet<>();
            String tripletsString = "#############\r\n";
            int containflag = 0;
//            for(Triplet triplet: q.getTripletList()){
//                tripletsString+=triplet.getPredicateURI().substring(triplet.getPredicateURI().lastIndexOf("/")+1);
//                tripletsString+=" :: ";
//                tripletsString+=triplet.getObjectURI();
//                tripletsString+="\r\n";
//                if(triplet.getObjectURI() == null || triplet.getObjectURI().length() == 0) break;
//                if(triplet.getObjectURI().contains(q.getActuralAnswer())){
//                    containflag = 1;
//                }
//            }
            String candidateString = "#############\r\n";
            for(Answer candidateAnswer:q.getCandidateAnswer()){
                String preUri = candidateAnswer.getAnswerTriplet().get(0).getPredicateURI();
                candidateString += preUri.substring(preUri.lastIndexOf("/")+1);
                candidateString += "::(" + candidateAnswer.getAnswerScore()+") ::  "+ candidateAnswer.getAnswerString() + "\r\n";
                if(candidateAnswer.getAnswerString() == null || candidateAnswer.getAnswerString().length() == 0) break;
                if(candidateAnswer.getAnswerString().contains(q.getActuralAnswer())){
                    containflag = 1;
                }
            }

            if(containflag == 1){
                stringtemp  = "找到的相关三元组有正确答案\r\n";
                rightTripletCount += 1;
            }else{
                stringtemp  = "找到的相关三元组没有正确答案\r\n";
            }
            System.out.println(stringtemp);
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
            if(returnedAnswer.contains(acturalAnswer) || acturalAnswer.contains(returnedAnswer)){
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
                    flag_string = "错误答案\r\n"  +candidateString;
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
        stringtemp = "共回答"+(count)+"道问题\r\n";
        System.out.print(stringtemp);
        outputs.add(stringtemp);

        stringtemp = "未回答数为： "+noAnswerCount + "\r\n";
        System.out.print(stringtemp);
        outputs.add(stringtemp);

        stringtemp = "找到的三元组有正确答案的有： "+ rightTripletCount + "\r\n";
        System.out.print(stringtemp);
        outputs.add(stringtemp);

        stringtemp = "没找到实体的有： "+ noEntityCount + "\r\n";
        System.out.print(stringtemp);
        outputs.add(stringtemp);


        stringtemp = "正确率为： "+(double)(rightAnswerCount/count)*100+"%" + "\r\n";
        System.out.print(stringtemp);
        outputs.add(stringtemp);

        stringtemp = "总耗时为："+((endMili-startMili)/1000.0)+"秒" + "\r\n";
        System.out.print(stringtemp);
        outputs.add(stringtemp);

        try {
            IOTool.writeToFile(outputs, "src/main/resources/data/newdemo_result_" + i+".txt");
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

        //取得问题集合;
        ArrayList<Question> questions = IOTool.getQuestionsFromTripletGeneratedQuestionFile();

        int times = questions.size()/100;
        int timess = questions.size()%100;
// 是什么_____隐讳号的项目符号是什么？_____•_____<http://zhishi.me/zhwiki/resource/隐讳号>_____<http://zhishi.me/zhwiki/property/项目符号>_____•
// 是什么_____新海诚动漫《你的名字》由哪个公司出品？_____<东宝株式会社>_____<http://zhishi.me/zhwiki/resource/subject>_____<http://zhishi.me/zhwiki/property/predict>_____<东宝株式会社>
        for(int i = 0; i <= times; i++){
            if(i == times) testByFiles(questions.subList(i*100,(i*100+timess)),i);
            else{
                testByFiles(questions.subList(i*100,(i+1)*100), i);
            }

            System.out.println("wenjian--"+i);
        }
    }
}
