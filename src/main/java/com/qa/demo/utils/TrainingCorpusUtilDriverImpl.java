package com.qa.demo.utils;

import com.qa.demo.utils.trainingcorpus.ExtractQuestionsFromText;

import java.io.IOException;
import java.util.HashMap;

public class TrainingCorpusUtilDriverImpl implements TrainingCorpusUtilDriver {

    public HashMap<Integer,HashMap<String,String>> getQuestionAnswerPairFromFile(String filepath){

        HashMap<Integer,HashMap<String,String>> question_answer_map = null;
        try {
            question_answer_map = ExtractQuestionsFromText.getQuestionsFromFile(filepath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return question_answer_map;

    }

    public void printQuestionAnswerPairFromFile(String filepath) {
        ExtractQuestionsFromText.printQuestions(getQuestionAnswerPairFromFile(filepath));
    }

}
