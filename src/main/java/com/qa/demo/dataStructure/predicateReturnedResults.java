package com.qa.demo.dataStructure;

import java.util.ArrayList;

/**
 * @author: Seven.wk
 * @description: 数据返回类
 * @create: 2018/07/04
 */
public class predicateReturnedResults {

  

    private ArrayList<String> predicate;



    public predicateReturnedResults() {
    }

    public predicateReturnedResults(ArrayList<String> predicate) {
        this.predicate = predicate;

    }



    public ArrayList<String> getPredicate() {
        return predicate;
    }

    public void setPredicate(ArrayList<String> predicate) {
        this.predicate = predicate;
    }



    
}
