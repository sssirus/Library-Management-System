package com.qa.demo.utils.qgeneration;

import com.qa.demo.dataStructure.Triplet;
import com.qa.demo.utils.trainingcorpus.OrganizeQuestions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *  Created time: 2017_09_05
 *  Author: Devin Hua
 *  Function description:
 *  To count hoy many times that one property occurred in the file.
 */

public class PropertyCount {

    public static HashMap<String,Integer> count()
    {
        String filepath = "src\\main\\resources\\data\\kbfile\\rdf_datatype.txt";
        ArrayList<Triplet> triplets = KBTripletBasedQuestionGeneration.generateTriplets(filepath);
        HashMap<String,Integer> map = new HashMap<String,Integer>();
        for(Triplet t:triplets){
            String predicate_name = t.getPredicateName();
            if(map.containsKey(predicate_name))
                map.put(predicate_name,map.get(predicate_name)+1);
            else
                map.put(predicate_name,1);
        }
        return map;
    }

    public static void writeCountsToFile(HashMap<String,Integer> map){

        Iterator it = map.entrySet().iterator();
        ArrayList<String> list = new ArrayList<String>();
        while(it.hasNext())
        {
            String output = "";
            Map.Entry entry = (Map.Entry<String,Integer>)it.next();
            output += entry.getKey() + ", " + entry.getValue()+"\r\n";
            list.add(output);
            System.out.print(output);
        }
        String outputfile = "src\\main\\resources\\data\\kbfile\\property_count.txt";
        try {
            OrganizeQuestions.writeToFile(list,outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
