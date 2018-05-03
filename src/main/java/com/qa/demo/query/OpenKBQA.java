package com.qa.demo.query;

import com.qa.demo.algorithm.EditingDistance;
import com.qa.demo.dataStructure.*;
import com.qa.demo.questionAnalysis.QuestionAnalysisDriver;
import com.qa.demo.questionAnalysis.QuestionAnalysisDriverImpl;
import com.qa.demo.utils.w2v.Result;
import com.qa.demo.utils.w2v.Word2VecGensimModel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

import static com.qa.demo.utils.w2v.Subword2Vec.calVec;
import static com.qa.demo.utils.w2v.Subword2Vec.check_words;

/**
 * @author J.Y.Zhang
 * @create 2018-04-14
 * Function description:
 *  获取以问题中实体为主语或宾语的所有相关三元组
 *  并计算三元组中的谓词与问题中其他词的三种相似度
 *  结果分别存储在Question结构中的tripletList和questionTokenTripletSim中
 **/
public class OpenKBQA implements KbqaQueryDriver {

    @Override
    public Question kbQueryAnswers(Question q) {

        //取得问题分析器驱动；
        QuestionAnalysisDriver qAnalysisDriver = new QuestionAnalysisDriverImpl();
        q = qAnalysisDriver.nerQuestion(q);
        q = qAnalysisDriver.segmentationQuestion(q);
        // 得到所有以问题中实体为主语或宾语的所有相关三元组
        q = this.getCandidateTriplets(q);
        // 计算三元组里的谓词和问题除了实体以外部分的相似度
        q = this.calSimilarity(q);

        return q;
    }

    private Question getCandidateTriplets(Question q){

        Set<Triplet> set = new HashSet<>();
        for(Entity entity: q.getQuestionEntity()){ // 问题中所有实体对应作为主语或宾语的三元组 去重
            List<Triplet> temp  = GetCandidateAnswers.getCandidateTripletsByEntity(entity);
            set.addAll(temp);
        }
        List<Triplet> tripletList = new ArrayList<>(set);
        q.setTripletList(tripletList);

        return q;
    }

    private Question calSimilarity(Question q){
        HashMap<Entity,ArrayList<String>> questionToken = q.getQuestionToken();
        HashMap<Triplet,HashMap<String,List<Double>>> questionTokenTripletSim = new HashMap<>();
        ArrayList<Answer> answers = new ArrayList<>();
        if (q.getTripletList() == null || q.getTripletList().isEmpty()){
            q.setCandidateAnswer(answers);
            return q;
        }
        HashMap<List<String>,List<Double>> map = new HashMap<>();
        for(Triplet triplet: q.getTripletList()){ // 这个循环的是所有三元组
            if(triplet.getObjectURI()==null || triplet.getPredicateURI()==null) break;
            if(triplet.getObjectURI().split("/").length < 1 || triplet.getPredicateURI().split("/").length < 1) break;
            String predicateName = triplet.getPredicateURI().split("/")[triplet.getPredicateURI().split("/").length-1];
            String objectName = triplet.getObjectURI().split("/")[triplet.getObjectURI().split("/").length-1];
            if(predicateName == "" || objectName == "") break;

            Iterator<Map.Entry<Entity,ArrayList<String>>> it = questionToken.entrySet().iterator();
            HashMap<String,List<Double>> preSim = new HashMap<>();
            Answer ans = new Answer();
            double score = 0;
            while (it.hasNext()) { // 这个循环的是问题里所有非实体的词
                Map.Entry<Entity,ArrayList<String>> entry = it.next();

                for (String entryName:  entry.getValue()){


                    List<String> ss = Arrays.asList(predicateName,entryName);
                    if(!map.containsKey(ss)){// 避免重复计算
                        // 计算相似度 **新疆小麦的分布和颜色？** 新疆小麦对应三元组谓词有：中文名，产地等 这里计算的是中文名和分布的相似度
                        double word_sim = this.calTwoWordsSimilarityMethods(predicateName,entryName,"word");
                        double char_sim = 0;// 太慢了！！
                        //double char_sim = this.calTwoWordsSimilarityMethods(predicateName,entryName,"character");
                        double dis_sim = this.calTwoWordsSimilarityMethods(predicateName,entryName,"distance");
                        double total_sim = word_sim + char_sim + dis_sim; // 不同的相似度区间不一样  直接加0.0待优化啊
                        System.out.println("三元组里的谓词:" + predicateName + " 问题里非实体词:" + entryName
                                + " 词相似:" + word_sim+ " 字相似:" + char_sim+ " 距离相似:" + dis_sim);


                        List<Double> list = Arrays.asList(word_sim, char_sim, dis_sim, total_sim);
                        score += total_sim;
                        preSim.put(entryName,list);

                        map.put(ss,list);
                    }else{
                        score = map.get(ss).get(3);
                        preSim.put(entryName,map.get(ss));
                    }




                }
            }
            ans.setAnswerTriplet(Collections.singletonList(triplet));
            ans.setAnswerScore(score);
            ans.setAnswerString(objectName);
            ans.setAnswerSource("net");
            answers.add(ans);
            questionTokenTripletSim.put(triplet,preSim);
        }
        q.setCandidateAnswer(answers);
        q.setQuestionTokenTripletSim(questionTokenTripletSim);
        return q;
    }

    private double calTwoWordsSimilarityMethods(String w1, String w2, String methods){
        switch(methods){
            case "word" :
                //语句
                Word2VecGensimModel w2vModel = null;
                try {
                    w2vModel = Word2VecGensimModel.getInstance();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                double temp_score = w2vModel.calcWordSimilarity(w1, w2);
                return temp_score;
            case "character" :
                Result res = null;
                try {
                    res = check_words(w1);
                    res = calVec(res);
                    Result presult = check_words(w2);
                    presult = calVec(presult);
                    double sim = Word2VecGensimModel.calcVecSimilarity(res.vec, presult.vec);
                    return sim;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return 0.;
            case "distance" :
                double ed1 = EditingDistance.getRepetitiveRate(w1, w2);
                double ed2 = EditingDistance.getRepetitiveRate(w2, w1);
                double ed_score = ed1 >= ed2 ? ed1 : ed2;
                return  ed_score;
            default : //可选
                //语句
                return 0.;
        }
    }
}
