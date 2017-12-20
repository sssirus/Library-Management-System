package com.qa.demo.utils.trainingcorpus;

import com.qa.demo.conf.Configuration;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

/**
 *  Created time: 2017_08_26
 *  Author: Devin Hua
 *  Function description:
 *  Read questions and corresponding answers from txt respectively
 *  to solve the problem that analyzing answers incorrectly.
 */
public class OrganizeQuestions {

    public static void organizeQuestionsMainDriver(String question_file_path,
                                  String answer_file_path, String resultFilePath){

        ArrayList<String> list = null;
        try {
            list = assembleQuestionsAndAnswers(question_file_path,answer_file_path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            writeToFile(list, resultFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //To assemble question-answer pairs from two separate files with seperate characters "$$$".
    private static ArrayList<String> assembleQuestionsAndAnswers
            (String question_answer_file_path, String answer_file_path) throws IOException {

        ArrayList<String> assembled_lines = new ArrayList<String>();
        ArrayList<String> question_lines = ExtractQuestionsFromText.readLinesFromFile(question_answer_file_path);
        ArrayList<String> answer_lines = ExtractQuestionsFromText.readLinesFromFile(answer_file_path);
        if(question_lines.isEmpty())
            return assembled_lines;
        else{
            String output = "";
            String question_output = "";
            String answer_output = "";
            for(int i=0;i<question_lines.size();i++)
            {
                if(question_lines.get(i).contains(Configuration.SPLITSTRING))
                {
                    if((question_output!=""&&question_output!=null))
                        if(answer_output!=""&&answer_output!=null){
                            output += question_output+answer_output+"\r\n";
                            assembled_lines.add(output);
                            question_output = "";
                            answer_output = "";
                            output="";
                    }
                    String[] temps = question_lines.get(i).split("\t");
                    if(temps.length<2)
                        continue;
                    else {
                        question_output += temps[1] + Configuration.SPLITSTRING + temps[2] + Configuration.SPLITSTRING;
                        answer_output += answer_lines.get(i).trim();
                    }

                }
                else{
                    answer_output += answer_lines.get(i).trim();
                }
            }
            if((question_output!=""&&question_output!=null))
                if(answer_output!=""&&answer_output!=null){
                    output += question_output+answer_output+"\r\n";
                    assembled_lines.add(output);
                }
            return assembled_lines;
        }
    }

    //To write assembled question-answer pair into file.
    public static void writeToFile(ArrayList<String> list, String resultFilePath) throws IOException
    {
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(resultFilePath,false), "UTF-8"));
        for(String value:list)
        {
            out.write(value);
        }
        out.close();
    }
}
