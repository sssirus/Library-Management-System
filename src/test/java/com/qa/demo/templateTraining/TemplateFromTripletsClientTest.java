package com.qa.demo.templateTraining;

import com.qa.demo.dataStructure.QuestionTemplate;
import com.qa.demo.utils.trainingcorpus.OrganizeQuestions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import static com.qa.demo.conf.FileConfig.TEMPLATE_REPOSITORY_TRIPLETS;
import static org.junit.jupiter.api.Assertions.*;

class TemplateFromTripletsClientTest {
    @Test
    void getInstance() {
    }

    @Test
    void getTemplateRepository() {

        HashSet<QuestionTemplate> templateRepository =
                TemplateFromTripletsClient.getInstance().getTemplateRepository();
        Iterator ir = templateRepository.iterator();
        ArrayList<String> outputs = new ArrayList<String>();
        while(ir.hasNext())
        {
            QuestionTemplate t = (QuestionTemplate)ir.next();
            String output = "Template: "+t.getTemplateString()+"\t\tPredicate: "
                    + t.getPredicate().getKgPredicateName() + "\r\n";
            outputs.add(output);
        }
        try {
            OrganizeQuestions.writeToFile(outputs,TEMPLATE_REPOSITORY_TRIPLETS);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Write to file is failed!");
        }
        System.out.println("Write to file is ok!");

    }

}