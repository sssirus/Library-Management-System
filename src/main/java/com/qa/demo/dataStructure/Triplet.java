package com.qa.demo.dataStructure;

import com.qa.demo.conf.Configuration;

/**
 * Created by Devin Hua on 2017/8/30.
 * 系统中表示知识图谱中的一个三元组；
 */
public class Triplet {

    //表示三元组的字符串，如：<http://zhishi.me/baidubaike/resource/可蒙犬> <http://zhishi.me/baidubaike/property/拉丁学名> "Komondor"@zh .
    private String tripletString;

    //表示三元组的主语URI;
    private String subjectURI;

    //表示三元组的谓语URI;
    private String predicateURI;

    //表示三元组的宾语URI;
    private String objectURI;

    //表示三元组的主语名称;
    private String subjectName;

    //表示三元组的谓语名称;
    private String predicateName;

    //表示三元组的宾语名称;
    private String objectName;

    //表示谓语的性质是对象属性还是数值属性;
    private PredicateType predicateType;

    public Triplet(String tripletString, String subjectURI, String predicateURI, String objectURI, String subjectName, String predicateName, String objectName, PredicateType predicateType) {
        this.tripletString = tripletString;
        this.subjectURI = subjectURI;
        this.predicateURI = predicateURI;
        this.objectURI = objectURI;
        this.subjectName = subjectName;
        this.predicateName = predicateName;
        this.objectName = objectName;
        this.predicateType = predicateType;
    }

    public Triplet(String tripletString) {
        this.tripletString = tripletString;
        setMemberWithString();
    }

    public void printTriplet()
    {
        System.out.println("Subject URI: " + this.getSubjectURI());
        System.out.println("Predicate URI: " + this.getPredicateURI());
        System.out.println("Object URI: " + this.getObjectURI());
        System.out.println("Object Value: " + this.getObjectName());
    }

    public Triplet(){

    }

    public void setMemberWithString(){

        String[] temps = this.tripletString.split(Configuration.SPLITSTRING);

        if(temps.length==5){
            this.setSubjectURI(temps[0].trim());
            this.setPredicateURI(temps[1].trim());
            this.setObjectURI("");
            this.setObjectName(temps[2].trim());
            this.setSubjectName(temps[3].trim());
            this.setPredicateName(temps[4].trim());
            this.setPredicateType(PredicateType.DATATYPEPROPERTY);
        }
        else if(temps.length==6){
            this.setSubjectURI(temps[0].trim());
            this.setPredicateURI(temps[1].trim());
            this.setObjectURI(temps[2].trim());
            this.setSubjectName(temps[3].trim());
            this.setPredicateName(temps[4].trim());
            this.setObjectName(temps[5].trim());
            this.setPredicateType(PredicateType.OBJECTPROPERTY);
        }
        else
            return;
    }

    public String getTripletString() {
        return tripletString;
    }

    public void setTripletString(String tripletString) {
        this.tripletString = tripletString;
    }

    public String getSubjectURI() {
        return subjectURI;
    }

    public void setSubjectURI(String subjectURI) {
        this.subjectURI = subjectURI;
    }

    public String getPredicateURI() {
        return predicateURI;
    }

    public void setPredicateURI(String predicateURI) {
        this.predicateURI = predicateURI;
    }

    public String getObjectURI() {
        return objectURI;
    }

    public void setObjectURI(String objectURI) {
        this.objectURI = objectURI;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getPredicateName() {
        return predicateName;
    }

    public void setPredicateName(String predicateName) {
        this.predicateName = predicateName;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public PredicateType getPredicateType() {
        return predicateType;
    }

    public void setPredicateType(PredicateType predicateType) {
        this.predicateType = predicateType;
    }
}

