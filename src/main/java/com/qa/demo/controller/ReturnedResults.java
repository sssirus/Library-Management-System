package com.qa.demo.controller;
import com.qa.demo.dataStructure.Entity;
import java.util.ArrayList;
import java.util.List;
/**
 * @author: Seven.wk
 * @description: 数据返回类
 * @create: 2018/07/04
 */
public class ReturnedResults{

  

    private String predicate;

    private List<Entity> entities =new ArrayList<> ();

    public ReturnedResults() {
    }

    public ReturnedResults(String predicate, List<Entity> entities) {
        this.predicate = predicate;
        this.entities = entities;
    }


    public List<Entity> getEntity() {
        return entities;
    }

    public void setEntity(List<Entity> entities) {
        this.entities = entities;
    }

    public String getPredicate() {
        return predicate;
    }

    public void setPredicate(String predicate) {
        this.predicate = predicate;
    }


    
}
