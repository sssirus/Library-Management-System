package com.qa.demo.questionAnalysis;

import com.qa.demo.conf.Configuration;
import com.qa.demo.conf.FileConfig;
import com.qa.demo.dataStructure.Entity;
import com.qa.demo.dataStructure.QueryTuple;
import com.qa.demo.dataStructure.Question;
import com.qa.demo.utils.alias_dict.AliasDictionary;
import org.ansj.domain.Result;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.nlpcn.commons.lang.tire.domain.Forest;
import org.nlpcn.commons.lang.tire.library.Library;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionAnalysisDriverImpl implements QuestionAnalysisDriver {

    //TODO 仓促写之，需要检查;
    //输入一个Question类型的数据结构，对其进行分词后输出；
    public Question segmentationQuestion(Question q) {

        HashMap<Entity, ArrayList<String>> map = new HashMap<>();
        //建议加上，但不强求
        if(q.getQuestionString().contains("[")&&q.getQuestionString().contains("]")) //知识库中只包含"( )"的形式
        {
            String query=q.getQuestionString().replace("[", "(").replace("]", ")");
            q.setQuestionString(query);
        }
        for (Entity e : q.getQuestionEntity()) {
            String sentence =
                    q.getQuestionString().replace(e.getKgEntityName(), Configuration.SPLITSTRING);
            for (String punctuation : Configuration.PUNCTUATION_SET) {
                sentence = sentence.replace(punctuation, "");
            }
            sentence = sentence.trim();
            //System.out.println("待分词的句子为：" + sentence);
            Segmentation.segmentation(sentence);
            ArrayList<String> tokens = (ArrayList<String>) Segmentation.getTokens();
            map.put(e, tokens);
        }
        q.setQuestionToken(map);
        return q;
    }

    public Question segmentationQuestionPOS(Question q) {
        HashMap<Entity, ArrayList<String>> map = new HashMap<>();
        HashMap<Entity,List<Map<String,String>>> posMap=new HashMap<>();
        if(q.getQuestionString().contains("[")&&q.getQuestionString().contains("]")) //知识库中只包含"( )"的形式
        {
            String query=q.getQuestionString().replace("[", "(").replace("]", ")");
            q.setQuestionString(query);
        }
        for (Entity e : q.getQuestionEntity()) {
            String sentence =
                    q.getQuestionString().replace(e.getKgEntityName(), Configuration.SPLITSTRING);

            for (String punctuation : Configuration.PUNCTUATION_SET) {
                sentence = sentence.replace(punctuation, "");
            }
            sentence = sentence.trim();
            //System.out.println("待分词的句子为：" + sentence);
            Segmentation.segmentation(sentence);
            ArrayList<String> tokens = (ArrayList<String>) Segmentation.getTokens();
            map.put(e, tokens);
            //获取该实体对应的词性标注
            List<Map<String, String>> tokensPos= Segmentation.getTokenPOSList();
            posMap.put(e,tokensPos);
        }
        q.setQuestionToken(map);
        //存储的词性标注
        q.setQuestionEntityPOS(posMap);

        String StringWithoutEntity=q.getQuestionString();
        for (Entity e : q.getQuestionEntity()) {
            StringWithoutEntity =StringWithoutEntity.replace(e.getKgEntityName(), Configuration.SPLITSTRING);
            for (String punctuation : Configuration.PUNCTUATION_SET) {
                StringWithoutEntity = StringWithoutEntity.replace(punctuation, "");
            }
        }
        StringWithoutEntity = StringWithoutEntity.trim();

        //意图分析
        IntentionAnlysis analysis=new IntentionAnlysis();
        String intention = analysis.intentionAnlysis(StringWithoutEntity);
        q.setQustionIntention(intention);
        /*//词性标注  (可能未来做多实体识别的时候会有用)
        //提取词性标注
        List<Map<String,String>> tokenPOSList=new ArrayList<Map<String,String>>();
        Segmentation.segmentationWithoutStopWord(StringWithoutEntity);
        tokenPOSList=Segmentation.getTokenPOSList();
        //保存词性标注
        q.setQuestionTokenPOS(tokenPOSList);*/
        return q;
    }

    public Question intentionQuestion(Question q) {
        HashMap<Entity, ArrayList<String>> map = new HashMap<>();
        HashMap<Entity,List<Map<String,String>>> posMap=new HashMap<>();
        if(q.getQuestionString().contains("[")&&q.getQuestionString().contains("]")) //知识库中只包含"( )"的形式
        {
            String query=q.getQuestionString().replace("[", "(").replace("]", ")");
            q.setQuestionString(query);
        }
        String StringWithoutEntity=q.getQuestionString();
        for (Entity e : q.getQuestionEntity()) {
            StringWithoutEntity =StringWithoutEntity.replace(e.getKgEntityName(), Configuration.SPLITSTRING);
            for (String punctuation : Configuration.PUNCTUATION_SET) {
                StringWithoutEntity = StringWithoutEntity.replace(punctuation, "");
            }
        }
        StringWithoutEntity = StringWithoutEntity.trim();

        //意图分析
        IntentionAnlysis analysis=new IntentionAnlysis();
        String intention = analysis.intentionAnlysis(StringWithoutEntity);
        q.setQustionIntention(intention);
        return q;
    }

    //输入一个Question类型的数据结构，对其进行POS分析后输出；
    public Question posQuestion(Question q) {
            HashMap<Entity, ArrayList<String>> map = new HashMap<>();
            HashMap<Entity,List<Map<String,String>>> posMap=new HashMap<>();
            if(q.getQuestionString().contains("[")&&q.getQuestionString().contains("]")) //知识库中只包含"( )"的形式
            {
                String query=q.getQuestionString().replace("[", "(").replace("]", ")");
                q.setQuestionString(query);
            }

            for (Entity e : q.getQuestionEntity()) {
                String sentence =
                        q.getQuestionString().replace(e.getKgEntityName(), "ENTITY");

                for (String punctuation : Configuration.PUNCTUATION_SET) {
                    sentence = sentence.replace(punctuation, "");
                }
                sentence = sentence.trim();
//                System.out.println("待分词的句子为：" + sentence);
                Segmentation.segmentationWithoutStopwords(sentence);
                ArrayList<String> tokens = (ArrayList<String>) Segmentation.getTokens();
                map.put(e, tokens);
                //获取该实体对应的词性标注
                List<Map<String, String>> tokensPos= Segmentation.getTokenPOSList();
                posMap.put(e,tokensPos);
            }
            q.setQuestionToken(map);
            //存储的词性标注
            q.setQuestionEntityPOS(posMap);

//            //意图分析
//            String StringWithoutEntity=q.getQuestionString();
//            for (Entity e : q.getQuestionEntity()) {
//                StringWithoutEntity =StringWithoutEntity.replace(e.getKgEntityName(), Configuration.SPLITSTRING);
//                for (String punctuation : Configuration.PUNCTUATION_SET) {
//                    StringWithoutEntity = StringWithoutEntity.replace(punctuation, "");
//                }
//            }
//            StringWithoutEntity = StringWithoutEntity.trim();
//
//            IntentionAnlysis analysis=new IntentionAnlysis();
//            String intention = analysis.intentionAnlysis(StringWithoutEntity);
//            q.setQustionIntention(intention);
            return q;
    }

    //输入一个Question类型的数据结构，对其进行NER分析后输出；
    public Question nerQuestion(Question q) {
        q.setQuestionEntity(NER.getEntities(q.getQuestionString()));
        // add by yaoleo about aliasDict if NER.getEntities()没找到实体 那么尝试别名
//        if(NER.getEntities(q.getQuestionString()).size() == 1){
//            q.setQuestionEntity(AliasEntity.getAliasEntities(q.getQuestionString()));
//        }
        return q;
    }

    //输入一个Question类型的数据结构，对其进行模板匹配后输出；
    public Question patternExtractQuestion(Question q) {
        ArrayList<QueryTuple> tuples = PatternMatch.patternmatch(q);
        q.setQueryTuples(tuples);
        return q;
    }

    //输入一个Question类型的数据结构，对其进行问题意图、答案类型分析后输出；
    public Question latQuestion(Question q) {
        return null;
    }

    //输入一个Question类型的数据结构，对其进行关系抽取后输出；
    public Question relationExtractQuestion(Question q) {
        return null;
    }
}
