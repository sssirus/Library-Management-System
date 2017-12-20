package com.qa.demo.disambiguation.entityLinkingLucene;

public class Mention
{
    private String name;
    private String qaid;
    private String context;
    private String groupNameId;
    private int startoffset;
    private int endoffset;
    
    public String getName()
    {
        return name;
    }
    public void setName(String mention)
    {
        this.name = mention;
    }
    public String getWeiboId()
    {
        return qaid;
    }
    public void setWeiboId(String qaid)
    {
        this.qaid = qaid;
    }
    public String getContext()
    {
        return context;
    }
    public void setContext(String context)
    {
        this.context = context;
    }
    public String getGroupNameId()
    {
        return groupNameId;
    }
    public void setGroupNameId(String groupNameId)
    {
        this.groupNameId = groupNameId;
    }
    public int getStartoffset()
    {
        return startoffset;
    }
    public void setStartoffset(int startoffset)
    {
        this.startoffset = startoffset;
    }
    public int getEndoffset()
    {
        return endoffset;
    }
    public void setEndoffset(int endoffset)
    {
        this.endoffset = endoffset;
    }
}
