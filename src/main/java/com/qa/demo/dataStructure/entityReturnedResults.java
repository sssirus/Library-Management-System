package com.qa.demo.dataStructure;

import java.util.List;

public class entityReturnedResults {
    private List<String> remain;

    private List<String> entity ;
    private List<String> url ;

    public entityReturnedResults() {
    }

    public entityReturnedResults(List<String> entity, List<String> url, List<String> remain) {
        this.remain = remain;
        this.entity = entity;
        this.url = url;
    }


    public List<String> getEntity() {
        return entity;
    }

    public void setEntity(List<String> entity) {
        this.entity = entity;
    }

    public List<String> getRemain() {
        return remain;
    }

    public void setRemain(List<String> remain) {
        this.remain = remain;
    }
    public List<String> getUrl() {
        return url;
    }

    public void setUrl(List<String> url) {
        this.url = url;
    }
}
