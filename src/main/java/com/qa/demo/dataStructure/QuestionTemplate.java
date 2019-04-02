package com.qa.demo.dataStructure;

/**
 * Created by Devin Hua on 2017/8/30.
 * 系统中表示模板及相关信息的数据结构；
 */

public class QuestionTemplate {

    //表示模板的字符串；
    private String templateString;

    //表示这个模板映射到的谓词；
    private Predicate predicate;

    public String getTemplateString() {
        return templateString;
    }

    public void setTemplateString(String templateString) {
        this.templateString = templateString;
    }

    public Predicate getPredicate() {
        return predicate;
    }

    public void setPredicate(Predicate predicate) {
        this.predicate = predicate;
    }

    public void printQuestionTemplate(){
        System.out.println(this.predicate.getKgPredicateName());
        System.out.println(this.predicate.getPredicateURI());
        System.out.println(this.getTemplateString());
    }
}
