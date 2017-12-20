package com.qa.demo.utils.kbgeneration;

import com.qa.demo.conf.Configuration;
import com.qa.demo.conf.FileConfig;
import com.qa.demo.utils.io.IOTool;

import java.io.IOException;
import java.util.ArrayList;

import static com.qa.demo.conf.FileConfig.NT_TRIPLETS;

/**
 *  Created time: 2017_11_12
 *  Author: Devin Hua
 *  Function description:
 *  To transform KB txt file to NT file.
 */
public class KBNTGeneration {

    public static void ChangeTXT2NT(){

        ArrayList<String> strings = new ArrayList<String>();
        ArrayList<String> objstrings =  new ArrayList<String>();
        ArrayList<String> outputstrings =  new ArrayList<String>();
        try {
            strings =
                    IOTool.readLinesFromFile(FileConfig.DATATYPE_PROPERTY_TRIPLETS_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            objstrings =
                    IOTool.readLinesFromFile(FileConfig.OBJECT_PROPERTY_TRIPLETS_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        strings.addAll(objstrings);

        for(String line : strings)
        {
            String[] temps = line.split(Configuration.SPLITSTRING);
            if(temps.length<5)
                continue;
            String object = temps[2];
            object = object.replace("\"","");
            object = object.replace("\\","");
            String subject = temps[0];
            subject = subject.replace("[","(");
            subject = subject.replace("]",")");
            subject = subject.replace(" ","_");
            subject = "<" + subject + ">";
            String predicate = temps[1];
            predicate = "<" + predicate + ">";
            if(temps.length==5){
                object = "\"" + object + "\"" + " .";
            }
            if(temps.length==6){
                object = "<" + object + ">" + " .";
            }
            String output = subject + " " + predicate + " " + object + "\r\n";
            outputstrings.add(output);
        }

        try {
            IOTool.writeToFile(outputstrings, NT_TRIPLETS);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
