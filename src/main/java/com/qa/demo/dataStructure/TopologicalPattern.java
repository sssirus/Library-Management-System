package com.qa.demo.dataStructure;

import java.util.ArrayList;

/**
 * Created by Devin Hua on 2018/1/28.
 * 系统中表示词性组合模板的数据结构；
 * topological pattern指的是类似VB → VB+NP的一个句法树子树；
 */

public class TopologicalPattern {

    //表示子树根节点的词性；
    private String root_POS = null;

    //表示子树叶节点的词性；以一个链表表示叶节点；
    //只有当字符和顺序都相同时，才认为当前的子树是匹配上的；
    private ArrayList<String> leaves_POS = new ArrayList<>();

    public String getRoot_POS() {
        return root_POS;
    }

    public void setRoot_POS(String root_POS) {
        this.root_POS = root_POS;
    }

    public ArrayList<String> getLeaves_POS() {
        return leaves_POS;
    }

    public void setLeaves_POS(ArrayList<String> leaves_POS) {
        this.leaves_POS = leaves_POS;
    }

    public String printTopologicalPattern()
    {
        String output = "";
        output += this.getRoot_POS() + " -> ";
        for(String temp : this.getLeaves_POS())
        {
            output += temp + " ";
        }
//        System.out.println("Topological pattern is : " + output);
        return output;
    }

    public boolean sameTopologicalPattern(TopologicalPattern bPattern)
    {
        String aPatternRoot = this.getRoot_POS();
        ArrayList<String> aLeavesPOS = this.getLeaves_POS();
        String bPatternRoot = bPattern.getRoot_POS();
        ArrayList<String> bLeavesPOS = bPattern.getLeaves_POS();

        if(aPatternRoot==null||bPatternRoot==null)
            return false;
        if(aLeavesPOS.size()==0||bLeavesPOS.size()==0)
            return false;
        if(!aPatternRoot.equalsIgnoreCase(bPatternRoot))
            return false;
        if(aLeavesPOS.size()!=bLeavesPOS.size())
            return false;
        else
        {
            for(int i=0;i<aLeavesPOS.size();i++)
            {
                if(!aLeavesPOS.get(i).equalsIgnoreCase(bLeavesPOS.get(i)))
                    return false;
            }
        }
        return true;
    }
}
