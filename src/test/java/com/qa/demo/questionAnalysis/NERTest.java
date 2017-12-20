package com.qa.demo.questionAnalysis;

import com.qa.demo.dataStructure.Entity;
import com.qa.demo.dataStructure.Question;
import com.qa.demo.inputQuestion.InputFromConsole;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class NERTest {
    @Test
    void getEntities() {

        String input = "风信子[风信子科多年生草本植物]有什么其他名字？";
        Question question = new Question(input);

        ArrayList<Entity> entities = NER.getEntities(question.getQuestionString());
        if(!entities.isEmpty()||entities!=null)
        {
            for(Entity e : entities) {
                System.out.println(e.getKgEntityName() + "\t\t" + e.getEntityURI());
             }
        }
        else
            System.out.println("No result returned!");

    }

}