package com.qa.demo.templateTraining;

import com.qa.demo.dataStructure.QuestionTemplate;

import java.util.HashSet;

public class TemplateTrainingDriverFromTripletsImpl implements TemplateTrainingDriver {

    //得到模板库；
    public HashSet<QuestionTemplate> getTemplate() {

        return(TemplateTrainingFromTriplets.mainDriver());

    }

}
