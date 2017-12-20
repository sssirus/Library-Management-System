package com.qa.demo.dataStructure;
/**
 * Created by Devin Hua on 2017/9/01.
 * 系统中表示谓词及相关信息的数据结构；
 */

public class Predicate {

    //表示谓词的mention名字；
    private String mentionName;

    //表示谓词的URI;
    private String predicateURI;

    //表示谓词在KG中的名称;
    private String kgPredicateName;

    //表示谓词来源（百度百科、维基百科、互动百科、COIN自制KG）;
    private DataSource dataSource;

    public String getMentionName() {
        return mentionName;
    }

    public void setMentionName(String mentionName) {
        this.mentionName = mentionName;
    }

    public String getPredicateURI() {
        return predicateURI;
    }

    public void setPredicateURI(String predicateURI) {
        this.predicateURI = predicateURI;
    }

    public String getKgPredicateName() {
        return kgPredicateName;
    }

    public void setKgPredicateName(String kgPredicateName) {
        this.kgPredicateName = kgPredicateName;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
