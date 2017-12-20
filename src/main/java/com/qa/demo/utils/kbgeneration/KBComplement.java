package com.qa.demo.utils.kbgeneration;

import com.qa.demo.conf.Configuration;
import com.qa.demo.conf.FileConfig;
import com.qa.demo.utils.io.IOTool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.qa.demo.conf.FileConfig.COMPLEMENTKB;
import static com.qa.demo.conf.FileConfig.COMPLEMENT_KB_TRIPLETS;

/**
 *  Created time: 2017_10_17
 *  Author: Devin Hua
 *  Function description:
 *  To complement KB with additional properties.
 */

public class KBComplement {

    public static void maindriver()
    {
        ArrayList<String> triplets = getTripletString();
        try {
            IOTool.concatenateToFile(triplets, FileConfig.DATATYPE_PROPERTY_TRIPLETS_FILE);
//            IOTool.writeToFile(triplets, COMPLEMENT_KB_TRIPLETS);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(String triplet : triplets)
        {
            System.out.print(triplet);
        }
    }

    private static ArrayList<String> getTripletString()
    {
        ArrayList<String> triplets = new ArrayList<>();
        IOTool.getFile(COMPLEMENTKB);
        HashMap<String, String> files = IOTool.getFilename_filepath_mapping();
        Iterator iterator = files.entrySet().iterator();
        while(iterator.hasNext())
        {
            Map.Entry<String, String> entry = (Map.Entry)iterator.next();
            ArrayList<String> list = _generateTriplets(entry.getKey(), entry.getValue());
            if(!(list.size() ==0) && !list.isEmpty())
                triplets.addAll(list);
        }
        return triplets;
    }

    private static ArrayList<String> _generateTriplets(String predicate_name, String path)
    {
        ArrayList<String> linesFromFile = new ArrayList<>();
        ArrayList<String> triplets = new ArrayList<>();
        try {
            linesFromFile = IOTool.readLinesFromFile(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(String temp : linesFromFile)
        {
            temp = temp.trim();
            if(temp==null||temp=="")
                continue;
            String[] temp_tokens = temp.split("\t");
            if(temp_tokens.length<2)
                continue;
            else {
                String triplet = "";
                triplet += Configuration.ENTITY_PREFIX_BAIDU + temp_tokens[0] + Configuration.SPLITSTRING
                        + Configuration.PREDICATE_PREFIX_BAIDU + predicate_name + Configuration.SPLITSTRING
                        + temp_tokens[1].trim() + Configuration.SPLITSTRING
                        + temp_tokens[0] + Configuration.SPLITSTRING
                        + predicate_name + "\r\n";
                triplets.add(triplet);
            }
        }
        return triplets;
    }




}
