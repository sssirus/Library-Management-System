package com.qa.demo.score;

import com.qa.demo.dataStructure.Question;

/**
 *  Created time: 2017_09_01
 *  Author: Devin Hua
 *  Function description:
 *  The main driver interface for answer and evidence ranking.
 */

public interface ScoreDriver {

    //设置多种评分组件，根据各个证据对各个候选答案的支持力度进行答案的打分。
    Question answerScoring(Question q);

    //设置多种评分组件，根据证据对答案支持的力度，进行证据的打分；
    Question evidenceScoring(Question q);

}
