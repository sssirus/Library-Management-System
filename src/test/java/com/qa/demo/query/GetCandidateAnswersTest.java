package com.qa.demo.query;

import com.qa.demo.answerAnalysis.AnswerAnalysisDriverImpl;
import com.qa.demo.conf.Configuration;
import com.qa.demo.conf.FileConfig;
import com.qa.demo.dataStructure.DataSource;
import com.qa.demo.dataStructure.Entity;
import com.qa.demo.dataStructure.Question;
import com.qa.demo.dataStructure.Triplet;
import com.qa.demo.ontologyProcess.TDBCrudDriver;
import com.qa.demo.ontologyProcess.TDBCrudDriverImpl;
import com.qa.demo.questionAnalysis.Segmentation;
import com.qa.demo.utils.es.IndexFile;
import com.qa.demo.utils.io.IOTool;
import com.qa.demo.utils.io.WebServiceAccessor;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GetCandidateAnswersTest {

    @Test
    void getCandidateAnswers() throws IOException {
        //系统初始化操作：es建立索引
        //SYNONYM为分词之后的模板；
        IndexFile.indexFaqData(DataSource.SYNONYM);
        //为19000条百科知识的索引；
        IndexFile.indexEncyclopediaData(DataSource.ENCYCLOPEDIA);
        //FAQ为常用问答对的索引；PATTERN为模板的索引;FAQ_T为生成的所有问题的模板；
        IndexFile.indexFaqData(DataSource.FAQ, DataSource.PATTERN);
        // LOG.info(" [info]已建立faq索引！");

        TDBCrudDriver tdbCrudDriver = new TDBCrudDriverImpl();
        tdbCrudDriver.loadTDBModel();
        // LOG.info(" [info]已建立TDB MODEL，系统初始化完成！");

        //取得问题集合;
        ArrayList<Question> questions = IOTool.getQuestionsFromTripletGeneratedQuestionFile();
        ArrayList<String> outputs = new ArrayList<>();
        String stringtemp = "";
        int count = 0;
        double rightAnswerCount = 0;
        int noAnswerCount = 0;
        int wrongCount = 0;
        String question_string = "";

        long startMili = System.currentTimeMillis();// 当前时间对应的毫秒数

        for (Question q : questions) {

            String qstring = q.getQuestionString();
            if (qstring == "" || qstring == null) {
                break;
            }
            for (String punctuation : Configuration.PUNCTUATION_SET) {
                qstring = qstring.replace(punctuation, "");
            }
            q.setQuestionString(qstring);
            question_string = qstring;

            count++;

            //根据依存分析树生成拓扑模板得到对应的谓词指称，并与同义词词典进行基于词向量的相似度计算，返回答案，泛化主要功能；
//            KbqaQueryDriver TopologicalPatternKBQADriver = new TopologicalPatternKBQAWithIntention();
//            q = TopologicalPatternKBQADriver.kbQueryAnswers(q);

            //根据依存分析树生成拓扑模板得到对应的谓词指称，并与同义词词典进行基于词向量的相似度计算，返回答案，泛化主要功能；
//            KbqaQueryDriver TopologicalPatternKBQADriver = new TopologicalPatternKBQA();
//            q = TopologicalPatternKBQADriver.kbQueryAnswers(q);

            //利用词性标注的模板与用户意图来进行tokens的生成，再基于同义词集合来进行相似度的计算；
//            KbqaQueryDriver QueryPOSKBQADriver = new QueryPOSKBQA();
//            q = QueryPOSKBQADriver.kbQueryAnswers(q);

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
            q = queryFaq.search(q, DbqaQueryDriver.QueryType.MATCH_PHRASE_QUERY, DataSource.FAQ);

            //从ES中索引的百科知识检索faq;
            QueryEncyclopedia queryEncyclopedia = new QueryEncyclopedia();
            q = queryEncyclopedia.search(q, DbqaQueryDriver.QueryType.MATCH_PHRASE_QUERY, DataSource.ENCYCLOPEDIA);

            //将question_string分词之后再查询一次;
            Segmentation.segmentation(qstring);
            List<String> tokens = Segmentation.getTokens();
            String token_string = "";
            for (String token : tokens) {
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
            // LOG.info("[info]处理完成");


            stringtemp = "question " + count + ": " + question_string + "\r\n";
            System.out.print(stringtemp);

            outputs.add(stringtemp);
            String returnedAnswer = q.getReturnedAnswer().getAnswerString().trim();
            String acturalAnswer = q.getActuralAnswer().trim();

            stringtemp = "Actural answer is: " + acturalAnswer + "\r\n";
            System.out.print(stringtemp);
            outputs.add(stringtemp);

            stringtemp = "Returned answer is: " + returnedAnswer + "\r\n";
            System.out.print(stringtemp);
            outputs.add(stringtemp);

            stringtemp = "Answer source is: " + q.getReturnedAnswer().getAnswerSource() + "\r\n";
            System.out.print(stringtemp);
            outputs.add(stringtemp);

            int flag = 0;
            String flag_string = "";
            //returnedAnswer=returnedAnswer+"."; //小bug
            if (returnedAnswer.contains(acturalAnswer) || acturalAnswer.contains(returnedAnswer)) {
                rightAnswerCount++;
                flag = 1;
            } else if (returnedAnswer.contains("我还得再想想，以后再告诉你")) {
                noAnswerCount++;
                flag = 0;
            } else {
                wrongCount++;
                flag = -1;
            }

            switch (flag) {
                case 0:
                    flag_string = "无答案";
                    break;
                case 1:
                    flag_string = "正确答案";
                    break;
                case -1:
                    flag_string = "错误答案";
                    break;
                default:
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
        long endMili = System.currentTimeMillis();
        stringtemp = "共回答" + (count) + "道问题\r\n";
        System.out.print(stringtemp);
        outputs.add(stringtemp);

        stringtemp = "未回答数为： " + noAnswerCount + "\r\n";
        System.out.print(stringtemp);
        outputs.add(stringtemp);

        stringtemp = "错误回答数为： " + wrongCount + "\r\n";
        System.out.print(stringtemp);
        outputs.add(stringtemp);

        stringtemp = "正确率为： " + (double) (rightAnswerCount / count) * 100 + "%" + "\r\n";
        System.out.print(stringtemp);
        outputs.add(stringtemp);

        stringtemp = "总耗时为：" + ((endMili - startMili) / 1000.0) + "秒" + "\r\n";
        System.out.print(stringtemp);
        outputs.add(stringtemp);

        IOTool.writeToFile(outputs, FileConfig.QA_SYSTEM_RESULT);
    }

    @Test
    void getCandidateTripletsByEntity() {

        // 作为载体的三元组
        Triplet triplet = new Triplet();
        Entity entity = new Entity();
        entity.setEntityURI("http://zhishi.me/hudongbaike/resource/柏峪乡");

        // 存放结果
        List<Triplet> tripletList = null;
        tripletList  = GetCandidateAnswers.getCandidateTripletsByEntity(entity);

        // 输出结果
        System.out.println();
        System.out.println(tripletList.size());
        System.out.println();
        for(Triplet triplet1 : tripletList) {
            System.out.println(triplet1.getSubjectURI());
            System.out.println(triplet1.getPredicateURI());
            System.out.println(triplet1.getObjectURI());
        }
        // "select ?s ?p ?o {?s ?p ?o FILTER (?s IN ( <http://zhishi.me/hudongbaike/resource/%E6%96%B0%E7%96%86%E5%B0%8F%E9%BA%A6> ))}"
    }
}