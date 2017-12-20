package com.qa.demo.score;

import com.qa.demo.dataStructure.Question;

public class ScoreDriverImpl implements ScoreDriver {


    //设置多种评分组件，根据各个证据对各个候选答案的支持力度进行答案的打分。
    public Question answerScoring(Question q) {
        return null;
    }

    //设置多种评分组件，根据证据对答案支持的力度，进行证据的打分；
    public Question evidenceScoring(Question q) {
        return null;
    }
}
