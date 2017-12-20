package com.qa.demo.systemController;

import com.qa.demo.answerAnalysis.AnswerAnalysisDriverImpl;
import com.qa.demo.conf.Configuration;
import com.qa.demo.conf.FileConfig;
import com.qa.demo.dataStructure.DataSource;
import com.qa.demo.dataStructure.Question;
import com.qa.demo.query.*;
import com.qa.demo.questionAnalysis.Segmentation;
import com.qa.demo.utils.es.IndexFile;
import com.qa.demo.utils.io.IOTool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Devin Hua on 2017/8/23.
 * MAIN DRIVER FUNCTION:
 * to get users' questions and return relevant answers;
 */

public class MainDriver {

    private static Logger LOG = LogManager.getLogger(FaqDemo.class.getName());

    public static String inputQuestion(){

        String input_string = "";
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            input_string = br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return input_string;
    }

    public static ArrayList<HashMap<String,String>> processQuestionByES(String input_string){

        //TODO 根据ES处理问题，返回的为map的list，表示返回了多个候选问题-答案对；
        //map中的键为：问题(QUESTION)、答案(ANSWER)、类型(CATEGORY)、ES评分(SCORE)，值均为string类型；
        //每一个被ES检索到的问题-答案对，用一个map表示，最多返回10个候选问题-答案对；
        ArrayList<HashMap<String,String>> list = null;
        String input_question = input_string;
        return list;
    }

    public static String getAnswers(ArrayList<HashMap<String,String>> list){

        int count_number = 0;
        String output = "";
        for(HashMap<String,String> temp_map:list)
        {
            output+=count_number++;
            output+=":\r\n";
            output+="Question: "+temp_map.get(Configuration.QUESTION)+"\r\n";
            output+="Answer: "+temp_map.get(Configuration.ANSWER)+"\r\n";
            output+="ES Score: "+temp_map.get(Configuration.ESSCORE)+"\r\n";
        }
        return output;
    }

    public static void IOTest(){

        String input_question = "";
        String output_question = "";
        ArrayList<HashMap<String,String>> list = null;
        System.out.println("Enter a string:");
        input_question = inputQuestion();
        while((!input_question.equals("EXIT!"))
                &&(!input_question.equals(""))&&input_question!=null)
        {
            list = processQuestionByES(input_question);
            output_question = getAnswers(list);
            System.out.print("After ES processing, returned answers are:\r\n");
            System.out.println(output_question);
            System.out.println("Enter a string:");
            input_question = inputQuestion();
        }
    }

    @Test
    public static void main(String[] args) throws IOException {

        //系统初始化操作：es建立索引
        //SYNONYM为分词之后的模板；
        IndexFile.indexFaqData(DataSource.SYNONYM);
        //为19000条百科知识的索引；
        IndexFile.indexEncyclopediaData(DataSource.ENCYCLOPEDIA);
        //FAQ为常用问答对的索引；PATTERN为模板的索引;FAQ_T为生成的所有问题的模板；
//      IndexFile.indexFaqData(DataSource.FAQ, DataSource.PATTERN, DataSource.FAQ_T);
        IndexFile.indexFaqData(DataSource.FAQ, DataSource.PATTERN);
//      IndexFile.indexFaqData(DataSource.FAQ);
        LOG.info("[info]已建立faq索引，系统初始化完成");

        //取得问题集合;
        ArrayList<Question> questions = IOTool.getQuestionsFromTripletGeneratedQuestionFile();
        ArrayList<String> outputs = new ArrayList<>();
        String stringtemp = "";
        int count = 0;
        double rightAnswerCount = 0;
        int noAnswerCount = 0;
        int wrongCount = 0;
        String question_string = "";

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

            //从模板的同义词集合中查询（模板分词之后形成的同义词集合），泛化主要功能；
            KbqaQueryDriver ALGQuerySynonymKBQADriver = new ALGQuerySynonymKBQA();
            q = ALGQuerySynonymKBQADriver.kbQueryAnswers(q);

            //从ES索引的模板库中匹配模板（模板分词之后形成的关键词组合），并形成查询三元组，最终通过KG三元组匹配得到候选答案;
            KbqaQueryDriver esQuerySynonymKBQADriver = new ESQuerySynonymKBQA();
            q = esQuerySynonymKBQADriver.kbQueryAnswers(q);

            //从ES索引的模板库中匹配模板（从自然问句中将实体去掉后的模板），并形成查询三元组，最终通过KG三元组匹配得到候选答案;
            QueryPatternKBQA queryPatternKBQA = new QueryPatternKBQA();
            q = queryPatternKBQA.kbQueryAnswers(q);

            //从ES中检索faq;
            QueryFaq queryFaq = new QueryFaq();
//            question = queryFaq.search(question, DbqaQueryDriver.QueryType.MATCH_PHRASE_QUERY, DataSource.FAQ_T, DataSource.FAQ);
            q = queryFaq.search(q, DbqaQueryDriver.QueryType.MATCH_PHRASE_QUERY, DataSource.FAQ);

            //从ES中索引的百科知识检索faq;
            QueryEncyclopedia queryEncyclopedia = new QueryEncyclopedia();
            q = queryEncyclopedia.search(q, DbqaQueryDriver.QueryType.MATCH_PHRASE_QUERY, DataSource.ENCYCLOPEDIA);

            //将question_string分词之后再查询一次;
            Segmentation.segmentation(qstring);
            List<String> tokens = Segmentation.getTokens();
            String token_string = "";
            for(String token : tokens)
            {
                token_string += token + " ";
            }
            token_string = token_string.trim();
            q.setQuestionString(token_string);
            q = queryEncyclopedia.search(q, DbqaQueryDriver.QueryType.MATCH_PHRASE_QUERY, DataSource.ENCYCLOPEDIA);

            //对答案进行排序
            AnswerAnalysisDriverImpl analysisDriver = new AnswerAnalysisDriverImpl();
            q = analysisDriver.rankAnswerCandidate(q);

            //生成答案并返回
            q = analysisDriver.returnAnswer(q);
            LOG.info("[info]处理完成");

            stringtemp = "question "+ count+": " + question_string + "\r\n";
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
        stringtemp = "共回答" + (count) + "道问题\r\n";
        System.out.print(stringtemp);
        outputs.add(stringtemp);

        stringtemp = "未回答数为： "+ noAnswerCount + "\r\n";
        System.out.print(stringtemp);
        outputs.add(stringtemp);

        stringtemp = "错误回答数为： "+ wrongCount + "\r\n";
        System.out.print(stringtemp);
        outputs.add(stringtemp);

        stringtemp = "正确率为： "+ (double)(rightAnswerCount/count)*100+"%" + "\r\n";
        System.out.print(stringtemp);
        outputs.add(stringtemp);

        stringtemp = "总耗时为：" + ((endMili-startMili)/1000.0) + "秒" + "\r\n";
        System.out.print(stringtemp);
        outputs.add(stringtemp);

        IOTool.writeToFile(outputs, FileConfig.QA_SYSTEM_RESULT);
    }


}
