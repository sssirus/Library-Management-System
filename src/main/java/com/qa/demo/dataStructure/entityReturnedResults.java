package com.qa.demo.dataStructure;

public class entityReturnedResults {
    private String remain;

    private String entity ;
    private String url ;

    public entityReturnedResults() {
    }

    public entityReturnedResults(String entity, String url, String remain) {
        this.remain = remain;
        this.entity = entity;
        this.url = url;
    }


    public String getEntity() {
        return entity;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public String getRemain() {
        return remain;
    }

    public void setRemain(String remain) {
        this.remain = remain;
    }
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
