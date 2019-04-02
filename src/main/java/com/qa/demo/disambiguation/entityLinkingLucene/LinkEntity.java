package com.qa.demo.disambiguation.entityLinkingLucene;

import java.util.List;

public class LinkEntity
{
    private String kbid;
    private String name;
    private String category;
    private List<PropertyPair> fact;
    private String abstruct;

    public String getKbid()
    {
        return kbid;
    }
    public void setKbid(String kbid)
    {
        this.kbid = kbid;
    }
    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public String getCategory()
    {
        return category;
    }
    public void setCategory(String category)
    {
        this.category = category;
    }
    public List<PropertyPair> getFact()
    {
        return fact;
    }
    public void setFact(List<PropertyPair> fact)
    {
        this.fact = fact;
    }
    public String getAbstruct() {

        return abstruct;
    }
    public void setAbstruct(String abstruct) {

        this.abstruct = abstruct;
    }
}
