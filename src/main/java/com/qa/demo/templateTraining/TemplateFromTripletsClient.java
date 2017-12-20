package com.qa.demo.templateTraining;

import com.qa.demo.dataStructure.QuestionTemplate;

import java.util.HashSet;

/**
 *  Created time: 2017_09_01
 *  Author: Devin Hua
 *  Function description:
 *  The singleton for getting template repository that generates from triplets.
 */

public class TemplateFromTripletsClient {

    //单例模式，全局访问从三元组生成问题中分割出来的模板库；
    private static TemplateFromTripletsClient uniqueInstance;
    private static HashSet<QuestionTemplate> templateRepository;

    private TemplateFromTripletsClient(){
        TemplateTrainingDriver driver =
                new TemplateTrainingDriverFromTripletsImpl();
        this.templateRepository = driver.getTemplate();
    }

    public static synchronized TemplateFromTripletsClient getInstance()
    {
        if(uniqueInstance==null)
        {
            uniqueInstance = new TemplateFromTripletsClient();
        }
        return uniqueInstance;
    }

    public HashSet<QuestionTemplate> getTemplateRepository() {
        return templateRepository;
    }
}
