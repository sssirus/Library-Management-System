package com.qa.demo.templateTraining;
/**
 *  Created time: 2017_09_01
 *  Author: Devin Hua
 *  Function description:
 *  The main driver interface for template training.
 */
import com.qa.demo.dataStructure.QuestionTemplate;

import java.util.HashSet;

public interface TemplateTrainingDriver {

    //得到模板库；
    HashSet<QuestionTemplate> getTemplate();

}
