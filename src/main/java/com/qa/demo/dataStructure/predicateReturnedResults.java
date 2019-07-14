package com.qa.demo.dataStructure;

/**
 * @author: Seven.wk
 * @description: 数据返回类
 * @create: 2018/07/04
 */
public class predicateReturnedResults {

  

    private String predicate;
    private Double score;


    public predicateReturnedResults() {
    }

    public predicateReturnedResults(String predicate,String score) {
        this.predicate = predicate;
        this.score = Double.parseDouble(score);

    }



    public String getPredicate() {
        return predicate;
    }

    public void setScore(Double score) {
        this.score = score;
    }
    public Double getScore() {
        return score;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }



    
}
