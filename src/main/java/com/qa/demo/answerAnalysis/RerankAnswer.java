package com.qa.demo.answerAnalysis;

import com.qa.demo.dataStructure.Answer;

import java.util.Collections;
import java.util.List;

/**
 * Description: 对候选答案进行排序
 * Created by TT. Wu on 2017/9/4.
 */
public class RerankAnswer {
    /**
     * 基本实现：根据计算得到的分数，将候选答案集整理排序后返回
     * @param answers
     * @return
     */
    protected static List<Answer> rank(List<Answer> answers){
        Collections.sort(answers);
        Collections.reverse(answers);
        return answers;
    }
}
