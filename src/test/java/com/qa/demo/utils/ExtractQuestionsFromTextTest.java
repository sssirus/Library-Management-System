package com.qa.demo.utils;

import com.qa.demo.conf.FileConfig;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class ExtractQuestionsFromTextTest {
    @Test
    void readLinesFromFile() {
    }

    @Test
    void getQuestionsFromLines() {

        String filepath = FileConfig.FILE_FAQ_T;
        TrainingCorpusUtilDriver driver = new TrainingCorpusUtilDriverImpl();
        driver.getQuestionAnswerPairFromFile(filepath);
    }

    @Test
    void printQuestions() throws IOException {

        String filepath = FileConfig.FILE_FAQ_T;
        TrainingCorpusUtilDriver driver = new TrainingCorpusUtilDriverImpl();
        driver.printQuestionAnswerPairFromFile(filepath);

    }

}