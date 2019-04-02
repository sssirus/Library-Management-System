package com.qa.demo.questionAnalysis;

import com.qa.demo.conf.FileConfig;
import org.ansj.domain.Result;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.nlpcn.commons.lang.tire.domain.Forest;
import org.nlpcn.commons.lang.tire.domain.Value;
import org.nlpcn.commons.lang.tire.library.Library;

import java.util.HashMap;

/**
 *  Created time: 2017_12_27
 *  Author: Weizhuo Li
 *  Function description:
 *  To analysis the user intention of query and return the predefined intention.
 */
public class IntentionAnlysis {
    //存储Intention的列表
    private static HashMap<String,Integer> intentionList;
    private static HashMap<Integer,String> intentionMap;

    public static String intentionAnlysis(String sentence)
    {
        intentionList=new HashMap<String,Integer>();
        intentionMap=new HashMap<Integer,String>();
        initIntentionListandMap();
        Forest forest = null;
        //官方预设的自定分词词典;
        try {
            forest = Library.makeForest(FileConfig.DICTIONARY_FILE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //添加用户词典是为了更好地识别用户意图
        Forest forest1 = new Forest();

        Library.insertWord(forest1, new Value("什么地方", "n", "1000"));
        Library.insertWord(forest1, new Value("什么区域", "n", "1000"));
        Library.insertWord(forest1, new Value("哪", "n", "1000"));
        Library.insertWord(forest1, new Value("哪里", "n", "1000"));
        Library.insertWord(forest1, new Value("哪儿", "n", "1000"));
        Library.insertWord(forest1, new Value("哪里被", "n", "1000"));

        Library.insertWord(forest1, new Value("哪天", "n", "1000"));
        Library.insertWord(forest1, new Value("哪年", "n", "1000"));
        Library.insertWord(forest1, new Value("什么时候", "n", "1000"));
        Library.insertWord(forest1, new Value("什么年代", "n", "1000"));
        Library.insertWord(forest1, new Value("哪个年代", "n", "1000"));
        Library.insertWord(forest1, new Value("哪个时代", "n", "1000"));
        Library.insertWord(forest1, new Value("何时", "n", "1000"));
        Library.insertWord(forest1, new Value("什么季节", "n", "1000"));

        Library.insertWord(forest1, new Value("谁", "n", "1000"));
        Library.insertWord(forest1, new Value("由谁", "n", "1000"));
        Library.insertWord(forest1, new Value("被谁", "n", "1000"));
        Library.insertWord(forest1, new Value("是谁", "n", "1000"));

        Library.insertWord(forest1, new Value("如何", "n", "1000"));
        Library.insertWord(forest1, new Value("如何对", "n", "1000"));
        Library.insertWord(forest1, new Value("如何用", "n", "1000"));
        Library.insertWord(forest1, new Value("怎么", "n", "1000"));
        Library.insertWord(forest1, new Value("怎么用", "n", "1000"));
        Library.insertWord(forest1, new Value("怎么对", "n", "1000"));
        Library.insertWord(forest1, new Value("什么途径", "n", "1000"));
        Library.insertWord(forest1, new Value("什么方法", "n", "1000"));
        Library.insertWord(forest1, new Value("什么措施", "n", "1000"));
        Library.insertWord(forest1, new Value("哪些途径", "n", "1000"));
        Library.insertWord(forest1, new Value("哪些方法", "n", "1000"));
        Library.insertWord(forest1, new Value("哪些措施", "n", "1000"));

        Library.insertWord(forest1, new Value("有哪", "n", "1000"));
        Library.insertWord(forest1, new Value("有哪些", "n", "1000"));
        Library.insertWord(forest1, new Value("有什么", "n", "1000"));

        Library.insertWord(forest1, new Value("多少", "n", "1000"));
        Library.insertWord(forest1, new Value("多少钱", "n", "1000"));
        Library.insertWord(forest1, new Value("有多少", "n", "1000"));
        Library.insertWord(forest1, new Value("有多大", "n", "1000"));
        Library.insertWord(forest1, new Value("有几", "n", "1000"));
        Library.insertWord(forest1, new Value("有几个", "n", "1000"));

        Library.insertWord(forest1, new Value("为什么", "n", "1000"));

        Library.insertWord(forest1, new Value("行不行", "n", "1000"));
        Library.insertWord(forest1, new Value("能不能", "n", "1000"));
        Library.insertWord(forest1, new Value("可不可以", "n", "1000"));

        Library.insertWord(forest1, new Value("是什么", "n", "1000"));
        Library.insertWord(forest1, new Value("属于什么", "n", "1000"));
        Library.insertWord(forest1, new Value("属于哪个", "n", "1000"));


//      Result terms = ToAnalysis.parse(sentence, forest1);
//      Result terms = ToAnalysis.parse(sentence, forest);
        Result terms = ToAnalysis.parse(sentence, forest, forest1);

        //定义索引的力度 0->where,1->when，2->who，3->way，4->number... 多个实体组合问句的时候，意图可能会叠加
        int intentionStrength[]=new int[9];

        for(int i=0; i<terms.size(); i++) {
            String word = terms.get(i).getName(); //拿到词
            if(intentionList.get(word)!=null)
            {
                switch (intentionList.get(word)) {
                    case 0:
                        intentionStrength[0]++;
                        break;
                    case 1:
                        intentionStrength[1]++;
                        break;
                    case 2:
                        intentionStrength[2]++;
                        break;
                    case 3:
                        intentionStrength[3]++;
                        break;
                    case 4:
                        intentionStrength[4]++;
                        break;
                    case 5:
                        intentionStrength[5]++;
                        break;
                    case 6:
                        intentionStrength[6]++;
                        break;
                    case 7:
                        intentionStrength[7]++;
                        break;
                    case 8:
                        intentionStrength[8]++;
                        break;
                    default://不做任何操作
                }
            }
        }
        int max=0;  //强度默认为0
        int possibleIntentionIndex=8;  //默认"是什么"的用户意图
        for(int i=0;i<intentionStrength.length;i++)
        {
            if(max<intentionStrength[i]) {
                max = intentionStrength[i];
                possibleIntentionIndex =i;
            }
        }
        String intention="";
        intention=intentionMap.get(possibleIntentionIndex);
        return intention;
    }

    public static void  initIntentionListandMap()
    {
        intentionList.put("什么地方",0);
        intentionList.put("什么区域",0);
        intentionList.put("哪",0);
        intentionList.put("哪里",0);
        intentionList.put("哪儿",0);
        intentionList.put("哪里被",0);

        intentionList.put("哪天",1);
        intentionList.put("哪年",1);
        intentionList.put("什么时候",1);
        intentionList.put("什么年代",1);
        intentionList.put("哪个年代",1);
        intentionList.put("哪个时代",1);
        intentionList.put("何时",1);
        intentionList.put("什么季节",1);

        intentionList.put("谁",2);
        intentionList.put("由谁",2);
        intentionList.put("被谁",2);
        intentionList.put("是谁",2);

        intentionList.put("如何",3);
        intentionList.put("如何对",3);
        intentionList.put("如何用",3);
        intentionList.put("怎么",3);
        intentionList.put("怎么对",3);
        intentionList.put("怎么用",3);
        intentionList.put("什么途径",3);
        intentionList.put("什么方法",3);
        intentionList.put("什么措施",3);
        intentionList.put("哪些途径",3);
        intentionList.put("哪些方法",3);
        intentionList.put("哪些措施",3);

        intentionList.put("哪些",4);
        intentionList.put("有哪些",4);
        intentionList.put("有什么",4);

        intentionList.put("多少",5);
        intentionList.put("多少钱",5);
        intentionList.put("有多少",5);
        intentionList.put("有多大",5);
        intentionList.put("有几",5);
        intentionList.put("有几个",5);

        intentionList.put("为什么",6);

        intentionList.put("行不行",7);
        intentionList.put("能不能",7);
        intentionList.put("可不可以",7);


        intentionList.put("是什么",8);
        intentionList.put("属于什么",8);
        intentionList.put("属于哪个",8);


        intentionMap.put(0,"where");
        intentionMap.put(1,"when");
        intentionMap.put(2,"who");
        intentionMap.put(3,"how");
        intentionMap.put(4,"enumerate");
        intentionMap.put(5,"num");
        intentionMap.put(6,"why");
        intentionMap.put(7,"IsIt");
        intentionMap.put(8,"what");   //默认设定
    }

}
