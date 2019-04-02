package com.qa.demo.answerAnalysis;
/**
 *  Created time: 2017_09_01
 *  Author: Devin Hua
 *  Function description:
 *  The main driver interface for answer analysis.
 */
import com.qa.demo.dataStructure.Question;

public interface AnswerAnalysisDriver {

    //分析答案类型是否合理，并修改question中候选answer的score；
    Question typeInferenceJudge(Question q);

    //根据证据进行重排；
    Question rankAnswerCandidate(Question q);

    //确定最终答案，并修改question中的returnedAnswer；
    Question returnAnswer(Question q);

}
