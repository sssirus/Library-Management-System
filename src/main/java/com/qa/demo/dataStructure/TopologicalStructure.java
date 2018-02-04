package com.qa.demo.dataStructure;

import java.util.ArrayList;

/**
 * Created by Devin Hua on 2018/1/28.
 * 系统中表示词性组合与对应谓词指称组合的数据结构；
 * topological pattern指的是类似VB → VB + NP的一个句法树子树；
 * predicate mention指的是通过事先人工分析，标示出pattern中的谓词指称词性；
 */

public class TopologicalStructure {

    private TopologicalPattern topologicalPattern;

    //词性顺序组合标示谓词指称的位置；
    private ArrayList<String> predicate_mention;

    public TopologicalPattern getTopologicalPattern() {
        return topologicalPattern;
    }

    public void setTopologicalPattern(TopologicalPattern topologicalPattern) {
        this.topologicalPattern = topologicalPattern;
    }

    public ArrayList<String> getPredicate_mention() {
        return predicate_mention;
    }

    public void setPredicate_mention(ArrayList<String> predicate_mention) {
        this.predicate_mention = predicate_mention;
    }

    public String printTopologicalStructure()
    {
        String output = "";
        output += this.getTopologicalPattern().printTopologicalPattern() + " : ";
        for(String temp : this.getPredicate_mention())
        {
            output += temp + " ";
        }
        System.out.println("Topological structure is : " + output);
        return output;
    }
}

