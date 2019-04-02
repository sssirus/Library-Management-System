package com.qa.demo.utils.topologicalpattern;

import com.qa.demo.dataStructure.TopologicalStructure;

import java.util.ArrayList;

/**
 *  Created time: 2018_01_28
 *  Author: Devin Hua
 *  Function description:
 *  The singleton for getting topological pattern template from relevant files.
 */

public class TopologicalPatternClient {

    //单例模式，全局访问从文件中分割出来的拓扑模式模板库；
    private static TopologicalPatternClient uniqueInstance;
    private static ArrayList<TopologicalStructure> topologicalStructureRepository;

    private TopologicalPatternClient(){
        topologicalStructureRepository = ExtractPatternFromFile.getTopologicalStructure();
    }

    public static synchronized TopologicalPatternClient getInstance()
    {
        if(uniqueInstance==null)
        {
            uniqueInstance = new TopologicalPatternClient();
        }
        return uniqueInstance;
    }

    public ArrayList<TopologicalStructure> getTopologicalStructureRepository() {
        return topologicalStructureRepository;
    }





}
