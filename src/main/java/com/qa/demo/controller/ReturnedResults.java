package com.qa.demo.controller;
/**
 * @author: Seven.wk
 * @description: 数据返回类
 * @create: 2018/07/04
 */
public class ReturnedResults{

  

    private String predicate;

    private String entity ;

    public ReturnedResults() {
    }

    public ReturnedResults(String predicate, String entity) {
        this.predicate = predicate;
        this.entity = entity;
    }


    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }


    
}
