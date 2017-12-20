package com.qa.demo.answerAnalysis;

import com.qa.demo.dataStructure.Answer;
import com.qa.demo.dataStructure.Question;

import java.util.List;

public class AnswerAnalysisDriverImpl implements AnswerAnalysisDriver {

    /**
     * 分析答案类型是否合理，并修改question中候选answer的score；
     * @param q
     * @return
     */
    public Question typeInferenceJudge(Question q) {
        return null;
    }

    /**
     * 根据证据进行重排，并修改question中候选answer的score；
     * @param q
     * @return
     */
    public Question rankAnswerCandidate(Question q) {
        List<Answer> answers = q.getCandidateAnswer();
        answers = RerankAnswer.rank(answers);
        q.setCandidateAnswer(answers);
        return q;
    }

    /**
     * 确定最终答案，并修改question中的returnedAnswer；
     * @param q
     * @return
     */
    public Question returnAnswer(Question q) {
        List<Answer> answers = q.getCandidateAnswer();
        if(answers.size() >= 1){
            Answer answer = answers.get(0);
            q.setReturnedAnswer(answer);
        }else{
            Answer answer = new Answer();
            q.setReturnedAnswer(answer);
        }
        return q;
    }
}
