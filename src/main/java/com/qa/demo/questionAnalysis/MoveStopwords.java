package com.qa.demo.questionAnalysis;
/**
 * Created by Devin Hua on 2017/10/06.
 * Function description:
 * To remove stop-words from sentence.
 */

import com.qa.demo.utils.trainingcorpus.ExtractQuestionsFromText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import static com.qa.demo.conf.FileConfig.STOPWORD_FILE;

public class MoveStopwords {

    //单例模式，全局访问从三元组生成问题中分割出来的模板库；
    private static MoveStopwords uniqueInstance;
    private static HashSet<String> stopwordSet = new HashSet<String>();

    public HashSet<String> getStopwordSet() {
        return stopwordSet;
    }

    private MoveStopwords(){
        ArrayList<String> stopwordList = new ArrayList<String>();
        try {
            stopwordList = ExtractQuestionsFromText.readLinesFromFile(STOPWORD_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(String stopword : stopwordList)
        {
            if(stopwordSet.isEmpty()||!stopwordSet.contains(stopword))
            {
                stopwordSet.add(stopword);
            }
        }
    }

    public static synchronized MoveStopwords getInstance()
    {
        if(uniqueInstance==null)
        {
            uniqueInstance = new MoveStopwords();
        }
        return uniqueInstance;
    }

}
