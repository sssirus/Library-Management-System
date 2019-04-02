package com.qa.demo.query;

import com.qa.demo.answerAnalysis.AnswerAnalysisDriverImpl;
import com.qa.demo.dataStructure.Answer;
import com.qa.demo.dataStructure.Entity;
import com.qa.demo.dataStructure.Question;
import com.qa.demo.ontologyProcess.TDBCrudDriver;
import com.qa.demo.ontologyProcess.TDBCrudDriverImpl;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TopologicalPatternKBQAWithIntentionTest {
    @Test
    void kbQueryAnswers() {

        TDBCrudDriver tdbCrudDriver = new TDBCrudDriverImpl();
        tdbCrudDriver.loadTDBModel();
        System.out.println(" [info]已建立TDB MODEL，系统初始化完成！");

        Question question = new Question();
        //String string="花生什么时候种植?";
        //String string="王绶是哪个民族的";  //民族 n
//        String string="翠菊的颜色是什么？";  //基于Word2Vec的测试用句 这里颜色与花色的相似度为0.55
//        String string="翠菊的规范汉字编号是什么？";
//         String string="翠菊是什么？";

        //无答案
        // String string="Herdsman的operatingSystem";
        //String string="哪里有木麻黄";  //解决，调整模板的顺序IP->VP,NP
        //String string="木麻黄的二名法是什么"; //增加模板39,NP,DNP QP 叶子结点为QP
        //String string="史来贺评传的ISBN";

        //错误答案
        //String string="哪些部位被柑桔黑色蒂腐病危害了";
        //String string="杨屾去世于哪年";
        //String string="山荆子属于哪个亚纲";
        //String string="蒋德麒生于哪里";
        //String string = "江口县位于哪里";
        //String string="章文才去世于哪天";
        //String string="怎么用二名法命名茶条槭";
        //String string="袁隆平的国籍是什么";
        //String string="二名法命名木麻黄的方式是什么";

        //String string="FarmVille的developer";

        //String string="水萝卜又名什么";
        //String string="葡萄褐斑病又名什么";
        String string="没药有什么作用";
        //String string="青瓜又名什么";
        //String string="史来贺评传有几页";


        question.setQuestionString(string);

        //从模板的同义词集合中查询（模板分词之后形成的同义词集合），泛化主要功能；
        KbqaQueryDriver topologocalPatternKBQADriver = new TopologicalPatternKBQAWithIntention();
        question = topologocalPatternKBQADriver.kbQueryAnswers(question);

        AnswerAnalysisDriverImpl analysisDriver = new AnswerAnalysisDriverImpl();
        question = analysisDriver.rankAnswerCandidate(question);

        //对答案进行排序
        System.out.print("The intention of query: ");
        System.out.println(question.getQuestionIntention());

        List<Entity> questionEntity =question.getQuestionEntity();
        for(Entity entity:questionEntity) {
            List<Map<String, String>> entityPos = question.getQuestionEntityPOS().get(entity);
            //输入2个名字entity，但可能来源不同 互动 或者百度百科
            System.out.println("The segment and POS of entity is : "+ entity.getEntityURI());
            for (Map<String, String> b : entityPos)
            {
                for (String token : b.keySet())
                {
                    System.out.print(token + " " + b.get(token) + " ");
                }
            }
            System.out.println();
        }
        List<Answer> answers= question.getCandidateAnswer();
        System.out.println("The answer is :");
        for(Answer ans: answers)
        {
            System.out.println(ans.getAnswerString()+" "+ans.getAnswerScore());
        }
    }

}