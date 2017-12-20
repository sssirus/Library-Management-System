package com.qa.demo.query;

import com.qa.demo.dataStructure.DataSource;
import com.qa.demo.dataStructure.Question;
import com.qa.demo.questionAnalysis.QuestionAnalysisDriver;
import com.qa.demo.questionAnalysis.QuestionAnalysisDriverImpl;

public class KbqaQueryDriverImpl implements KbqaQueryDriver {

    //对问题进行基于KB的查询，返回候选答案集等相关信息，放在question数据结构中；
    //该查询中，通过模板库比对得到谓词；这里不通过ES而是内存中的模板库查询;
    @Override
    public Question kbQueryAnswers(Question q) {

        //取得问题分析器驱动；
        QuestionAnalysisDriver qAnalysisDriver = new QuestionAnalysisDriverImpl();
        q = qAnalysisDriver.nerQuestion(q);
        q = qAnalysisDriver.patternExtractQuestion(q);
        q = GetCandidateAnswers.getCandidateAnswers(q, DataSource.PATTERN);
        q = GetReturnedAnswer.getReturnedAnswer(q);
        return q;
    }
}
