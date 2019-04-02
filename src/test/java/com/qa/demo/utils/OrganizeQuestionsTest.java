package com.qa.demo.utils;

import com.qa.demo.conf.FileConfig;
import com.qa.demo.utils.trainingcorpus.OrganizeQuestions;
import org.junit.jupiter.api.Test;

class OrganizeQuestionsTest {
    @Test
    void organizeQuestionsMainDriver() {

        String question_file_path = FileConfig.FILE_QUESTION;
        String answer_file_path = FileConfig.FILE_ANSWER;
        String result_file_path = FileConfig.FILE_FAQ;
        OrganizeQuestions.organizeQuestionsMainDriver(question_file_path,answer_file_path,result_file_path);
    }

    @Test
    void writeToFile() {
    }

}