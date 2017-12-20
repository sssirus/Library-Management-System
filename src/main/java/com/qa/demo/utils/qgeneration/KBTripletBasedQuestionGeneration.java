package com.qa.demo.utils.qgeneration;
/**
 *  Created time: 2017_09_05
 *  Author: Devin Hua
 *  Function description:
 *  To generate question-answer pairs from KB triplets.
 */

import com.qa.demo.conf.Configuration;
import com.qa.demo.conf.FileConfig;
import com.qa.demo.dataStructure.PredicateType;
import com.qa.demo.dataStructure.Triplet;
import com.qa.demo.utils.trainingcorpus.ExtractQuestionsFromText;
import com.qa.demo.utils.trainingcorpus.OrganizeQuestions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

public class KBTripletBasedQuestionGeneration {

    public static ArrayList<Triplet> generateTriplets(String filepath){

        ArrayList<String> lines = new ArrayList<String>();
        ArrayList<Triplet> triplets = new ArrayList<Triplet>();
        try {
            lines = ExtractQuestionsFromText.readLinesFromFile(filepath);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("读取文件失败！");
        }

        if(lines.size()>0)
        {
//            int count = 0;
            for(String temp:lines)
            {
                temp = temp.trim();
                if(temp==null||temp=="")
                    continue;
//                System.out.println("generateTriplet count: " + count++);
                String[] temps = temp.split(Configuration.SPLITSTRING);
                if(temps.length<5)
                    continue;
                else{
                    Triplet triplet = new Triplet(temp);
                    triplets.add(triplet);
                }
            }
        }
        return triplets;
    }


    public static HashSet<String> questionTemplates(String s, String p)
    {
        HashSet<String> specialWordSet = Configuration.SPECIAL_WORD_SET;

        HashSet<String> questionSet = new HashSet<>();
        String question = "";

        //使用模板匹配属性和应该生成的问题；
        if(specialWordSet.contains(p))
        {
            question = s + "属于哪个" + p + "？";
            questionSet.add(question);
            question = s + "属于什么" + p + "？";
            questionSet.add(question);
            question = s + "是什么" + p + "？";
            questionSet.add(question);
        }
        else if(!p.equalsIgnoreCase("面积")&&p.contains("面积"))
        {
            question = s + "的" + p + "是多少？";
            questionSet.add(question);
            question = s + "有多少" + p + "？";
            questionSet.add(question);
            question = s + "有多大" + p + "？";
            questionSet.add(question);
            question = s + "的" + p + "占地多少？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("面积"))
        {
            question = s + "的" + p + "是多少？";
            questionSet.add(question);
            question = s + "有多少" + p + "？";
            questionSet.add(question);
            question = s + "有多大" + p + "？";
            questionSet.add(question);
            question = s + "占地多少？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("分布区域")||p.equalsIgnoreCase("分布"))
        {
            question = s + "的主要" + p + "是什么？";
            questionSet.add(question);
            question = s + "的主要" + p + "是哪里？";
            questionSet.add(question);
            question = s + "的" + p + "是哪里？";
            questionSet.add(question);
            question = s + "分布于哪里？";
            questionSet.add(question);
            question = s + "分布在哪里？";
            questionSet.add(question);
            question = s + "主要分布于哪里？";
            questionSet.add(question);
            question = s + "主要分布在哪里？";
            questionSet.add(question);
            question = s + "主要分布在哪儿？";
            questionSet.add(question);
            question = s + "主要分布在哪？";
            questionSet.add(question);
            question = s + "主要分布于什么区域？";
            questionSet.add(question);
            question = s + "主要分布于什么地方？";
            questionSet.add(question);
            question = s + "分布于什么区域？";
            questionSet.add(question);
            question = s + "分布于什么地方？";
            questionSet.add(question);
            question = "哪里有" + s + "？";
            questionSet.add(question);
            question = "哪里分布有" + s + "？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("所属地区"))
        {
            question = s + "的" + p + "是哪里？";
            questionSet.add(question);
            question = s + "的" + p + "在哪里？";
            questionSet.add(question);
            question = s + "的" + p + "在哪儿？";
            questionSet.add(question);
            question = s + "的" + p + "在哪？";
            questionSet.add(question);
            question = s + "属于什么地区？";
            questionSet.add(question);
            question = s + "属于哪个地区？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("描述"))
        {
            question = s + "是什么？";
            questionSet.add(question);
            question = s + "是什么东西？";
            questionSet.add(question);
            question = s + "是啥？";
            questionSet.add(question);
            question = "什么是" + s + "？";
            questionSet.add(question);
            question = "怎么描述" + s + "？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("二名法"))
        {
            question = s + "的" + p + "命名是什么？";
            questionSet.add(question);
            question = s + "的" + p + "命名名称是什么？";
            questionSet.add(question);
            question = s + "的" + p + "名称是什么？";
            questionSet.add(question);
            question = s + "用" + p + "怎么命名？";
            questionSet.add(question);
            question = "用" + p + "怎么命名" + s +"？";
            questionSet.add(question);
            question = "如何用" + p + "命名" + s +"？";
            questionSet.add(question);
            question = "怎么用" + p + "命名" + s +"？";
            questionSet.add(question);
            question = p + "命名" + s + "的方式？";
            questionSet.add(question);
            question = p + "命名" + s + "的方式是什么？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("别名")||p.equalsIgnoreCase("别称"))
        {
            question = s + "的别名是什么？";
            questionSet.add(question);
            question = s + "的别称是什么？";
            questionSet.add(question);
            question = s + "有什么其他别名？";
            questionSet.add(question);
            question = s + "有什么其他别称？";
            questionSet.add(question);
            question = s + "有什么别名？";
            questionSet.add(question);
            question = s + "有什么别称？";
            questionSet.add(question);
            question = s + "有什么其他名字？";
            questionSet.add(question);
            question = s + "有什么其他名称？";
            questionSet.add(question);
            question = s + "又名什么？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("名称")||p.equalsIgnoreCase("名字"))
        {
            question = s + "的名字是什么？";
            questionSet.add(question);
            question = s + "的名称是什么？";
            questionSet.add(question);
            question = s + "叫什么？";
            questionSet.add(question);
            question = s + "被称作什么？";
            questionSet.add(question);
            question = s + "被称为什么？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("出生日期")||p.equalsIgnoreCase("出生年月"))
        {
            question = s + "的出生日期是什么？";
            questionSet.add(question);
            question = s + "的出生年月是什么？";
            questionSet.add(question);
            question = s + "的生日是什么？";
            questionSet.add(question);
            question = s + "是什么时候出生的？";
            questionSet.add(question);
            question = s + "生于什么时候？";
            questionSet.add(question);
            question = s + "出生于什么时候？";
            questionSet.add(question);
            question = s + "出生于哪年？";
            questionSet.add(question);
            question = s + "出生于哪天？";
            questionSet.add(question);
            question = s + "的出生时间是什么？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("逝世日期")||p.equalsIgnoreCase("去世年月"))
        {
            question = s + "的逝世日期是什么？";
            questionSet.add(question);
            question = s + "的逝世时间是什么？";
            questionSet.add(question);
            question = s + "是什么时候逝世的？";
            questionSet.add(question);
            question = s + "的去世年月是什么？";
            questionSet.add(question);
            question = s + "的去世时间是什么？";
            questionSet.add(question);
            question = s + "是什么时候去世的？";
            questionSet.add(question);
            question = s + "逝于什么时候？";
            questionSet.add(question);
            question = s + "去世于哪年？";
            questionSet.add(question);
            question = s + "去世于哪天？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("国籍"))
        {
            question = s + "是哪国人？";
            questionSet.add(question);
            question = s + "来自哪个国家？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("民族"))
        {
            question = s + "是什么" + p + "？";
            questionSet.add(question);
            question = s + "是什么" + p + "的？";
            questionSet.add(question);
            question = s + "是什么少数" + p + "的？";
            questionSet.add(question);
            question = s + "是什么少数" + p + "？";
            questionSet.add(question);
            question = s + "是哪个" + p + "？";
            questionSet.add(question);
            question = s + "是哪个" + p + "的？";
            questionSet.add(question);
            question = s + "属于哪个" + p + "？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("出生地"))
        {
            question = s + "的" + p + "是哪里？";
            questionSet.add(question);
            question = s + "的" + p + "在哪里？";
            questionSet.add(question);
            question = s + "的" + p + "在哪？";
            questionSet.add(question);
            question = s + "的" + p + "在哪儿？";
            questionSet.add(question);
            question = s + "的" + p + "是哪？";
            questionSet.add(question);
            question = s + "的" + p + "是什么地方？";
            questionSet.add(question);
            question = s + "出生于哪里？";
            questionSet.add(question);
            question = s + "生于哪里？";
            questionSet.add(question);
            question = s + "是在哪里出生的？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("地理位置"))
        {
            question = s + "的" + p + "是哪里？";
            questionSet.add(question);
            question = s + "的" + p + "在哪里？";
            questionSet.add(question);
            question = s + "的" + p + "在哪儿？";
            questionSet.add(question);
            question = s + "的" + p + "在哪？";
            questionSet.add(question);
            question = s + "位于哪里？";
            questionSet.add(question);
            question = s + "的" + p + "是哪？";
            questionSet.add(question);
            question = s + "的" + p + "在哪？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("花（果）期"))
        {
            question = s + "的" + p + "是什么时候？";
            questionSet.add(question);
            question = s + "的花期是什么？";
            questionSet.add(question);
            question = s + "的果期是什么？";
            questionSet.add(question);
            question = s + "的花期？";
            questionSet.add(question);
            question = s + "的果期？";
            questionSet.add(question);
            question = s + "的花期是什么时候？";
            questionSet.add(question);
            question = s + "的果期是什么时候？";
            questionSet.add(question);
            question = s + "的花果期是什么？";
            questionSet.add(question);
            question = s + "的花果期？";
            questionSet.add(question);
            question = s + "的花果期是什么时候？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("中文名")||
                p.equalsIgnoreCase("中文名称"))
        {
            question = s + "的中文名是什么？";
            questionSet.add(question);
            question = s + "的中文名称是什么？";
            questionSet.add(question);
            question = s + "的中文名字是什么？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("英文名")||
                p.equalsIgnoreCase("英文名称"))
        {
            question = s + "的英文名是什么？";
            questionSet.add(question);
            question = s + "的英文名称是什么？";
            questionSet.add(question);
            question = s + "的英文名字是什么？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("外文名")||
                p.equalsIgnoreCase("外文名称"))
        {
            question = s + "的外文名是什么？";
            questionSet.add(question);
            question = s + "的外文名称是什么？";
            questionSet.add(question);
            question = s + "的外文名字是什么？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("命名人")||
                p.equalsIgnoreCase("命名者"))
        {
            question = s + "的命名人是什么？";
            questionSet.add(question);
            question = s + "的命名人是谁？";
            questionSet.add(question);
            question = s + "的命名者是什么？";
            questionSet.add(question);
            question = s + "的命名者是谁？";
            questionSet.add(question);
            question = s + "的命名人？";
            questionSet.add(question);
            question = s + "的命名者？";
            questionSet.add(question);
            question = s + "由谁命名？";
            questionSet.add(question);
            question = s + "是谁命名的？";
            questionSet.add(question);
            question = "谁命名了" + s + "？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("物种命名人")||
                p.equalsIgnoreCase("物种命名者"))
        {
            question = s + "的命名人是什么？";
            questionSet.add(question);
            question = s + "的命名人是谁？";
            questionSet.add(question);
            question = s + "的命名者是什么？";
            questionSet.add(question);
            question = s + "的命名者是谁？";
            questionSet.add(question);
            question = s + "的物种命名人是什么？";
            questionSet.add(question);
            question = s + "的物种命名人是谁？";
            questionSet.add(question);
            question = s + "的物种命名者是什么？";
            questionSet.add(question);
            question = s + "的物种命名者是谁？";
            questionSet.add(question);
            question = s + "的物种命名人？";
            questionSet.add(question);
            question = s + "的物种命名者？";
            questionSet.add(question);
            question = s + "由谁命名？";
            questionSet.add(question);
            question = s + "是谁命名的？";
            questionSet.add(question);
            question = "谁命名了" + s + "这个物种？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("译者"))
        {
            question = s + "的" + p + "是谁？";
            questionSet.add(question);
            question = s + "是谁翻译的？";
            questionSet.add(question);
            question = s + "由谁翻译的？";
            questionSet.add(question);
            question = "谁翻译了" + s + "？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("危害作物")||
                p.equalsIgnoreCase("主要危害作物"))
        {
            question = s + "的危害作物是什么？";
            questionSet.add(question);
            question = s + "的主要危害作物是什么？";
            questionSet.add(question);
            question = s + "危害了哪些作物？";
            questionSet.add(question);
            question = s + "主要危害了哪些作物？";
            questionSet.add(question);
            question = s + "危害了什么作物？";
            questionSet.add(question);
            question = s + "主要危害了什么作物？";
            questionSet.add(question);
            question = "哪些作物被" + s + "危害？";
            questionSet.add(question);
            question = "哪些作物被" + s + "危害了？";
            questionSet.add(question);
            question = "哪些作物被" + s + "所危害？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("主要为害部位")||
                p.equalsIgnoreCase("为害部位")||
                p.equalsIgnoreCase("危害部位"))
        {
            question = s + "的危害部位是什么？";
            questionSet.add(question);
            question = s + "的为害部位是什么？";
            questionSet.add(question);
            question = s + "的主要危害部位是什么？";
            questionSet.add(question);
            question = s + "的主要为害部位是什么？";
            questionSet.add(question);
            question = s + "为害了哪些部位？";
            questionSet.add(question);
            question = s + "主要为害哪些部位？";
            questionSet.add(question);
            question = s + "危害了哪些部位？";
            questionSet.add(question);
            question = s + "主要危害哪些部位？";
            questionSet.add(question);
            question = s + "的为害部位是哪里？";
            questionSet.add(question);
            question = s + "的为害部位是哪儿？";
            questionSet.add(question);
            question = s + "的为害部位是哪？";
            questionSet.add(question);
            question = s + "的主要为害部位是哪里？";
            questionSet.add(question);
            question = s + "的危害部位是哪里？";
            questionSet.add(question);
            question = s + "的主要危害部位是哪里？";
            questionSet.add(question);
            question = "哪些部位被" + s + "所危害？";
            questionSet.add(question);
            question = "哪些部位被" + s + "危害？";
            questionSet.add(question);
            question = "哪些部位被" + s + "危害了？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("人口"))
        {
            question = s + "的" + p + "是多少？";
            questionSet.add(question);
            question = s + "有多少" + p +"？";
            questionSet.add(question);
            question = s + "有多少人？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("发现者"))
        {
            question = "谁发现了" + s + "？";
            questionSet.add(question);
            question = s + "是谁发现的？";
            questionSet.add(question);
            question = s + "由谁发现的？";
            questionSet.add(question);
            question = s + "的" + p + "是谁？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("营养成分")||
                p.equalsIgnoreCase("主要营养成分"))
        {
            question = s + "的营养成分是什么？";
            questionSet.add(question);
            question = s + "的主要营养成分是什么？";
            questionSet.add(question);
            question = s + "有什么营养成分？";
            questionSet.add(question);
            question = s + "有哪些营养成分？";
            questionSet.add(question);
            question = s + "有什么营养？";
            questionSet.add(question);
            question = s + "有哪些营养？";
            questionSet.add(question);
            question = s + "有什么主要营养成分？";
            questionSet.add(question);
            question = s + "有哪些主要营养成分？";
            questionSet.add(question);
            question = s + "的营养成分有哪些？";
            questionSet.add(question);
            question = s + "的营养有哪些？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("PH"))
        {
            question = s + "的" + p + "是多少？";
            questionSet.add(question);
            question = s + "的" + p + "值是多少？";
            questionSet.add(question);
            question = s + "的" + p + "值是什么？";
            questionSet.add(question);
            question = "适合" + s + "的" + p + "值是什么？";
            questionSet.add(question);
            question = "适合" + s + "的" + p + "值是多少？";
            questionSet.add(question);
            question = s + "适合的" + p + "值是多少？";
            questionSet.add(question);
            question = s + "适合的" + p + "值是什么？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("水温"))
        {
            question = s + "的" + p + "是多少？";
            questionSet.add(question);
            question = s + "的适宜" + p + "是什么？";
            questionSet.add(question);
            question = s + "的适宜" + p + "是多少？";
            questionSet.add(question);
            question = "适合" + s + "的" + p + "是什么？";
            questionSet.add(question);
            question = "适合" + s + "的" + p + "是多少？";
            questionSet.add(question);
            question = s + "适合的" + p + "是多少？";
            questionSet.add(question);
            question = s + "适合的" + p + "是什么？";
            questionSet.add(question);
            question = "对于" + s + "合适的" + p + "？";
            questionSet.add(question);
            question = s + "适合的水的温度？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("数量"))
        {
            question = s + "的" + p + "是多少？";
            questionSet.add(question);
            question = s + "的" + p + "有多少？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("籍贯")||
                p.equalsIgnoreCase("政府驻地"))
        {
            question = s + "的" + p + "是哪里？";
            questionSet.add(question);
            question = s + "的" + p + "在哪里？";
            questionSet.add(question);
            question = s + "的" + p + "在哪？";
            questionSet.add(question);
            question = s + "的" + p + "在哪儿？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("作者"))
        {
            question = s + "的" + p + "是谁？";
            questionSet.add(question);
            question = "谁创作了" + s + "？";
            questionSet.add(question);
            question = "谁写了" + s + "？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("预防措施"))
        {
            question = s + "的" + p + "有哪些？";
            questionSet.add(question);
            question = "怎么预防" + s + "？";
            questionSet.add(question);
            question = "如何预防" + s + "？";
            questionSet.add(question);
            question = "有什么预防" + s + "的方法？";
            questionSet.add(question);
            question = "有哪些预防" + s + "的方法？";
            questionSet.add(question);
            question = "有什么预防" + s + "的措施？";
            questionSet.add(question);
            question = "有哪些预防" + s + "的措施？";
            questionSet.add(question);
            question = "有什么方法预防" + s + "？";
            questionSet.add(question);
            question = "有哪些方法预防" + s + "？";
            questionSet.add(question);
            question = "有什么措施预防" + s + "？";
            questionSet.add(question);
            question = "有哪些措施预防" + s + "？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("传播途径"))
        {
            question = s + "的" + p + "有哪些？";
            questionSet.add(question);
            question = "通过什么途径可以传播" + s + "？";
            questionSet.add(question);
            question = "通过什么途径能传播" + s + "？";
            questionSet.add(question);
            question = "有什么途径传播" + s + "？";
            questionSet.add(question);
            question = "有哪些途径传播" + s + "？";
            questionSet.add(question);
            question = s + "通过哪些途径传播？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("邮编区码")||
                p.equalsIgnoreCase("邮政区码"))
        {
            question = s + "的邮编区码是什么？";
            questionSet.add(question);
            question = s + "的邮政区码是什么？";
            questionSet.add(question);
            question = s + "的邮政编码是什么？";
            questionSet.add(question);
            question = s + "的邮编是什么？";
            questionSet.add(question);
            question = s + "的邮编码是什么？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("祖籍"))
        {
            question = s + "的" + p + "在哪里？";
            questionSet.add(question);
            question = s + "的" + p + "在哪？";
            questionSet.add(question);
            question = s + "的" + p + "在哪儿？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("发现命名"))
        {
            question = s + "的" + p + "人是谁？";
            questionSet.add(question);
            question = s + "的" + p + "者是谁？";
            questionSet.add(question);
            question = "谁" + p + "了" + s + "？";
            questionSet.add(question);
            question = "谁" + p + s + "？";
            questionSet.add(question);
            question = s + "被谁" + p + "？";
            questionSet.add(question);
            question = s + "是被谁" + p + "的？";
            questionSet.add(question);
            question = s + "被谁" + p + "了？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("药用部位"))
        {
            question = s + "的" + p + "是哪里？";
            questionSet.add(question);
            question = s + "的" + p + "是哪？";
            questionSet.add(question);
            question = s + "的" + p + "是哪儿？";
            questionSet.add(question);
            question = "在哪里对" + s + "用药？" ;
            questionSet.add(question);
            question = "在哪些部位对" + s + "用药？" ;
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("总部地点")||
                p.equalsIgnoreCase("总部所在地"))
        {
            question = s + "的总部地点是什么？";
            questionSet.add(question);
            question = s + "的总部地点是哪里？";
            questionSet.add(question);
            question = s + "的总部地点是哪？";
            questionSet.add(question);
            question = s + "的总部地点是哪儿？";
            questionSet.add(question);
            question = s + "的总部地点在哪里？";
            questionSet.add(question);
            question = s + "的总部所在地是什么？";
            questionSet.add(question);
            question = s + "的总部所在地是哪里？";
            questionSet.add(question);
            question = s + "的总部所在地在哪里？";
            questionSet.add(question);
            question = s + "的总部所在地在哪？";
            questionSet.add(question);
            question = s + "的总部地点在哪？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("功效")||
                p.equalsIgnoreCase("主要功效"))
        {
            question = s + "的" + p + "有哪些？";
            questionSet.add(question);
            question = s + "有什么" + p + "？";
            questionSet.add(question);
            question = s + "有哪些" + p + "？";
            questionSet.add(question);
            question = s + "有哪些效果？";
            questionSet.add(question);
            question = s + "有什么效果？";
            questionSet.add(question);
            question = s + "有哪些作用？";
            questionSet.add(question);
            question = s + "有什么作用？";
            questionSet.add(question);
            question = s + "有哪些功用？";
            questionSet.add(question);
            question = s + "有什么功用？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("副作用"))
        {
            question = s + "的" + p + "有哪些？";
            questionSet.add(question);
            question = s + "有什么" + p + "？";
            questionSet.add(question);
            question = s + "有哪些" + p + "？";
            questionSet.add(question);
        }

        else if(p.equalsIgnoreCase("起源于"))
        {
            question = s + p + "什么？";
            questionSet.add(question);
            question = s + p + "？";
            questionSet.add(question);
            question = s + p + "什么时候？";
            questionSet.add(question);
            question = s + p + "哪个年代？";
            questionSet.add(question);
            question = s + p + "什么年代？";
            questionSet.add(question);
            question = s + p + "哪个时代？";
            questionSet.add(question);
            question = s + p + "什么时代？";
            questionSet.add(question);
            question = s + "的起源时间是什么？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("创建地点"))
        {
            question = s + "的" + p + "是哪里？";
            questionSet.add(question);
            question = s + "的" + p + "在哪里？";
            questionSet.add(question);
            question = s + "的" + p + "在哪儿？";
            questionSet.add(question);
            question = s + "的" + p + "在哪？";
            questionSet.add(question);
            question = s + "创建于哪里？";
            questionSet.add(question);
            question = s + "是在哪里被创建的？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("电话区号")||
                p.equalsIgnoreCase("电话区码"))
        {
            question = s + "的电话区号是什么？";
            questionSet.add(question);
            question = s + "的电话区码是什么？";
            questionSet.add(question);
            question = s + "的电话区号是多少？";
            questionSet.add(question);
            question = s + "的电话区码是多少？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("命名者"))
        {
            question = s + "的" + p + "是谁？";
            questionSet.add(question);
            question = s + "被谁命名？";
            questionSet.add(question);
            question = s + "是被谁命名的？";
            questionSet.add(question);
            question = "谁命名了" + s + "？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("产地")||
                p.equalsIgnoreCase("原产地")||
                p.equalsIgnoreCase("原产地区"))
        {
            question = s + "的产地是什么？";
            questionSet.add(question);
            question = s + "的原产地是什么？";
            questionSet.add(question);
            question = s + "的原产地区是什么？";
            questionSet.add(question);
            question = s + "的产地是哪里？";
            questionSet.add(question);
            question = s + "的产地是哪儿？";
            questionSet.add(question);
            question = s + "的产地是哪？";
            questionSet.add(question);
            question = s + "的原产地是哪里？";
            questionSet.add(question);
            question = s + "的原产地在哪里？";
            questionSet.add(question);
            question = s + "产于哪里？";
            questionSet.add(question);
            question = s + "原产于哪里？";
            questionSet.add(question);
            question = s + "产于什么地方？";
            questionSet.add(question);
            question = s + "原产于什么地方？";
            questionSet.add(question);
            question = s + "产于哪？";
            questionSet.add(question);
            question = s + "产自哪里？";
            questionSet.add(question);
            question = s + "原产自哪里？";
            questionSet.add(question);
            question = s + "产自什么地方？";
            questionSet.add(question);
            question = s + "原产自什么地方？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("员工数"))
        {
            question = s + "的" + p + "是多少？";
            questionSet.add(question);
            question = s + "的" + p + "有多少？";
            questionSet.add(question);
            question = s + "有多少员工？";
            questionSet.add(question);
            question = s + "有几个员工？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("主产地"))
        {
            question = s + "的主产地方是什么？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("气门数"))
        {
            question = s + "的气门有多少？";
            questionSet.add(question);
            question = s + "的" + p + "有多少？";
            questionSet.add(question);
            question = s + "有多少气门？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("逝世地"))
        {
            question = s + "的" + p + "是哪里？";
            questionSet.add(question);
            question = s + "的" + p + "是哪儿？";
            questionSet.add(question);
            question = s + "的" + p + "是哪？";
            questionSet.add(question);
            question = s + "的" + p + "在哪里？";
            questionSet.add(question);
            question = s + "逝于哪里？";
            questionSet.add(question);
            question = s + "逝世于哪里？";
            questionSet.add(question);
            question = s + "死于哪里？";
            questionSet.add(question);
            question = s + "逝于什么地方？";
            questionSet.add(question);
            question = s + "逝世于什么地方？";
            questionSet.add(question);
            question = s + "死于什么地方？";
            questionSet.add(question);
            question = s + "是在哪里逝世的？";
            questionSet.add(question);
            question = s + "是在哪里去世的？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("创建时间"))
        {
            question = s + "创建于什么时间？";
            questionSet.add(question);
            question = s + "创建于什么时候？";
            questionSet.add(question);
            question = s + "是什么时候创建的？";
            questionSet.add(question);
            question = s + "创建于哪年的？";
            questionSet.add(question);
            question = s + "创建于哪天的？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("释义"))
        {
            question = "如何解释" + s + "？";
            questionSet.add(question);
            question = "怎么解释" + s + "？";
            questionSet.add(question);
            question = s + "的含义是什么？";
            questionSet.add(question);
            question = s + "有什么含义？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("笔画"))
        {
            question = s + "的" + p + "是多少？";
            questionSet.add(question);
            question = s + "有多少" + p + "？";
            questionSet.add(question);
            question = s + "有多少笔画？";
            questionSet.add(question);
            question = s + "有几画？";
            questionSet.add(question);
            question = s + "有多少画？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("行业")) {
            question = s + "的所处" + p + "是什么？";
            questionSet.add(question);
            question = s + "是做哪一行业的？";
            questionSet.add(question);
            question = s + "是做什么行业的？";
            questionSet.add(question);
            question = s + "是做哪一行的？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("页数")) {
            question = s + "的" + p + "是多少？";
            questionSet.add(question);
            question = s + "有多少页？";
            questionSet.add(question);
            question = s + "有几页？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("价格"))
        {
            question = s + "的" + p + "是多少？";
            questionSet.add(question);
            question = s + "多少钱？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("诗名"))
        {
            question = s + "的诗的名字是什么？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("所处时代")||
                p.equalsIgnoreCase("所处年代"))
        {
            question = s + "处于哪个时代？";
            questionSet.add(question);
            question = s + "处于哪个年代？";
            questionSet.add(question);
            question = s + "的所处时代是什么？";
            questionSet.add(question);
            question = s + "的所处年代是什么？";
            questionSet.add(question);
            question = s + "的所处时代？";
            questionSet.add(question);
            question = s + "的所处年代？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("性味与归经")||
                p.equalsIgnoreCase("性味归经"))
        {
            question = s + "的性味与归经？";
            questionSet.add(question);
            question = s + "的性味归经？";
            questionSet.add(question);
            question = s + "的性味归经是什么？";
            questionSet.add(question);
            question = s + "的性味与归经是什么？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("成就")) {
            question = s + "有哪些" + p + "？";
            questionSet.add(question);
            question = s + "获得过哪些" + p + "？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("单行本册数")) {
            question = s + "的" + p + "是多少？";
            questionSet.add(question);
            question = s + "有多少单行本？";
            questionSet.add(question);
            question = s + "的单行本的数量是多少？";
            questionSet.add(question);
            question = s + "的单行本的册数有多少？";
            questionSet.add(question);
            question = s + "的单行本发行了多少册？";
            questionSet.add(question);
            question = s + "的单行本发行册数是多少？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("产品服务")) {
            question = s + "提供了什么" + p + "？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("保存方法"))
        {
            question = "怎么保存" + s + "？";
            questionSet.add(question);
            question = "怎么储存" + s + "？";
            questionSet.add(question);
            question = "如何保存" + s + "？";
            questionSet.add(question);
            question = "如何储存" + s + "？";
            questionSet.add(question);
            question = "保存" + s + "的方法是什么？";
            questionSet.add(question);
            question = "储存" + s + "的方式是什么？";
            questionSet.add(question);
            question = "怎么保藏" + s + "？";
            questionSet.add(question);
            question = "怎么储藏" + s + "？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("冬剪技术"))
        {
            question = s + "的冬剪方法是什么？";
            questionSet.add(question);
            question = "怎么修剪" + s + "？";
            questionSet.add(question);
            question = "如何修剪" + s + "？";
            questionSet.add(question);
            question = "怎么在冬天修剪" + s + "？";
            questionSet.add(question);
            question = "如何在冬季修剪" + s + "？";
            questionSet.add(question);
            question = "怎么对" + s + "进行冬剪？";
            questionSet.add(question);
            question = "怎么对" + s + "冬剪？";
            questionSet.add(question);
            question = "怎么冬剪" + s + "？";
            questionSet.add(question);
            question = s + "怎么进行冬剪？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("发病规律"))
        {
            question = s + "发病的规律是什么？";
            questionSet.add(question);
            question = s + "发生的一般规律是什么？";
            questionSet.add(question);
            question = s + "发展的规律是什么？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("发生原因"))
        {
            question = s + "发生的原因是什么？";
            questionSet.add(question);
            question = s + "发生的主要原因是什么？";
            questionSet.add(question);
            question = "怎么会发生" + s + "的？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("防治措施"))
        {
            question = "怎么防治" + s + "？";
            questionSet.add(question);
            question = "采取什么措施能防治" + s + "？";
            questionSet.add(question);
            question = "如何治疗" + s + "？";
            questionSet.add(question);
            question = "怎么预防" + s + "？";
            questionSet.add(question);
            question = "采取什么方法能防治" + s + "？";
            questionSet.add(question);
            question = s + "的防治方法是什么？";
            questionSet.add(question);
            question = s + "的防治办法是什么？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("简介"))
        {
            question = s + "的简明介绍是什么？";
            questionSet.add(question);
            question = s + "的简单介绍是什么？";
            questionSet.add(question);
            question = s + "是什么？";
            questionSet.add(question);
            question = "简单介绍一下" + s + "是什么？";
            questionSet.add(question);
            question = "简明介绍一下" + s + "？";
            questionSet.add(question);
            question = "简单描述一下" + s + "？";
            questionSet.add(question);
            question = s + "的基本介绍是什么？";
            questionSet.add(question);
            question = s + "的基本情况是什么？";
            questionSet.add(question);
            question = "对" + s + "做一个概述？";
            questionSet.add(question);
            question = "对" + s + "做一个简单介绍？";
            questionSet.add(question);
            question = "对" + s + "做一下简单描述？";
            questionSet.add(question);
            question = s + "是什么东西？";
            questionSet.add(question);
            question = "说一下" + s + "是什么吧？";
            questionSet.add(question);
            question = "说一下" + s + "是什么东西吧？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("配制方法"))
        {
            question = "怎么配制" + s + "？";
            questionSet.add(question);
            question = "采取什么方法能配制" + s + "？";
            questionSet.add(question);
            question = "如何配制" + s + "？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("缺点"))
        {
            question = s + "的缺陷是什么？";
            questionSet.add(question);
            question = s + "的不足之处是什么？";
            questionSet.add(question);
            question = s + "的不足在哪里？";
            questionSet.add(question);
            question = s + "的哪里有不足？";
            questionSet.add(question);
            question = s + "有什么不好的地方？";
            questionSet.add(question);
            question = s + "有什么不好之处？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("施肥方法"))
        {
            question = "怎么对" + s + "施肥？";
            questionSet.add(question);
            question = "采取什么方法能施肥" + s + "？";
            questionSet.add(question);
            question = "如何对" + s + "上肥料？";
            questionSet.add(question);
            question = "如何对" + s + "施肥料？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("识别方法"))
        {
            question = "怎么识别" + s + "？";
            questionSet.add(question);
            question = "如何鉴别" + s + "？";
            questionSet.add(question);
            question = "如何甄别" + s + "？";
            questionSet.add(question);
            question = s + "的鉴别方法是什么？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("食用方法"))
        {
            question = "怎么食用" + s + "？";
            questionSet.add(question);
            question = "怎么吃" + s + "？";
            questionSet.add(question);
            question = "如何食用" + s + "？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("使用方法"))
        {
            question = "怎么使用" + s + "？";
            questionSet.add(question);
            question = "如何使用" + s + "？";
            questionSet.add(question);
            question = "怎么用" + s + "？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("危害"))
        {
            question = s + "对人有什么坏处？";
            questionSet.add(question);
            question = s + "是否对人有害？";
            questionSet.add(question);
            question = s + "对人有什么危害？";
            questionSet.add(question);
            question = s + "对人有什么不良影响？";
            questionSet.add(question);
            question = s + "对人有什么不好的地方？";
            questionSet.add(question);
            question = s + "的害处是什么？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("修剪时间"))
        {
            question = "何时修剪" + s + "合适？";
            questionSet.add(question);
            question = "应该什么时候修剪" + s + "？";
            questionSet.add(question);
            question = "什么季节修剪" + s + "最好？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("养殖方法"))
        {
            question = "怎么养殖" + s + "？";
            questionSet.add(question);
            question = "如何养" + s + "？";
            questionSet.add(question);
        }
        else if(p.equalsIgnoreCase("栽培技术"))
    {
        question = "怎么栽培" + s + "？";
        questionSet.add(question);
        question = "如何种植" + s + "？";
        questionSet.add(question);
        question = "如何养植" + s + "？";
        questionSet.add(question);
        question = "如何培育" + s + "？";
        questionSet.add(question);
        question = s + "的栽培方法是什么？";
        questionSet.add(question);
        question = s + "的栽培技术是什么？";
        questionSet.add(question);
        question = s + "的养植方法是什么？";
        questionSet.add(question);
        question = s + "的培育技术是什么？";
        questionSet.add(question);
    }
        //问题的默认两种问法；
        question = s + "的" + p + "？";
        questionSet.add(question);
        question = s + "的" + p + "是什么？";
        questionSet.add(question);
        return questionSet;
    }

    //根据三元组，结合不同问法，形成一系列问题；
    private static ArrayList<String> generateQuestionAnswerPair(Triplet t)
    {
        ArrayList<String> results = new ArrayList<String>();
        String answer = t.getObjectName();
        String p = t.getPredicateName();
        String s = t.getSubjectName();

        HashSet<String> questionSet = questionTemplates(s,p);

        for(String qTemp:questionSet)
        {
            String output = "";
            output += "是什么"+ Configuration.SPLITSTRING+qTemp
                    + Configuration.SPLITSTRING+answer;
            //在答案之后加上映射到的三元组，对象属性为主语URI，谓语URI，宾语URI；
            if(t.getPredicateType().equals(PredicateType.OBJECTPROPERTY))
            {
                output += Configuration.SPLITSTRING + t.getSubjectURI()
                        + Configuration.SPLITSTRING + t.getPredicateURI()
                        + Configuration.SPLITSTRING + t.getObjectURI() + "\r\n";
            }
            //在答案之后加上映射到的三元组，数值属性为主语URI，谓语URI，宾语值；
            else if(t.getPredicateType().equals(PredicateType.DATATYPEPROPERTY))
            {
                output += Configuration.SPLITSTRING + t.getSubjectURI()
                        + Configuration.SPLITSTRING + t.getPredicateURI()
                        + Configuration.SPLITSTRING + t.getObjectName() + "\r\n";
            }
            results.add(output);
        }
        return results;
    }

    //将三元组list转换为问题-答案对的list；
    private static ArrayList<String> generateQuestionAnswerPairLists(ArrayList<Triplet> triplets){
        ArrayList<String> questionAnswerPairs = new ArrayList<String>();
        if(triplets.size()==0||triplets.isEmpty()||triplets==null)
            return questionAnswerPairs;
        else{
//            int count = 0;
            for(Triplet t:triplets)
            {
//                System.out.println("generateQuestionAnswerPair count: "+count++);
                ArrayList<String> qaPairs = generateQuestionAnswerPair(t);
                questionAnswerPairs.addAll(qaPairs);
            }
        }
        return questionAnswerPairs;
    }

    public static void mainDriver(){

        ArrayList<Triplet> triplets = generateTriplets(FileConfig.DATATYPE_PROPERTY_TRIPLETS_FILE);
        ArrayList<String> questionAnswerPairs = generateQuestionAnswerPairLists(triplets);
        triplets = generateTriplets(FileConfig.OBJECT_PROPERTY_TRIPLETS_FILE);
        ArrayList<String> data_questionAnswerPairs = generateQuestionAnswerPairLists(triplets);
        questionAnswerPairs.addAll(data_questionAnswerPairs);
        try {
            OrganizeQuestions.writeToFile(questionAnswerPairs, FileConfig.FILE_FAQ_T);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("写入文件失败！");
        }
    }

}
