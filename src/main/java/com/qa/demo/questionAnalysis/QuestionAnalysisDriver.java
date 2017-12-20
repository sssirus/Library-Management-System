package com.qa.demo.questionAnalysis;
/**
 *  Created time: 2017_09_01
 *  Author: Devin Hua
 *  Function description:
 *  The main driver interface for question analysis.
 */
import com.qa.demo.dataStructure.Question;

public interface QuestionAnalysisDriver {

    //输入一个Question类型的数据结构，对其进行分词后输出；
    Question segmentationQuestion(Question q);

    //输入一个Question类型的数据结构，对其进行POS分析后输出；
    Question posQuestion(Question q);

    //输入一个Question类型的数据结构，对其进行NER分析后输出；
    Question nerQuestion(Question q);

    //输入一个Question类型的数据结构，对其进行模板匹配后输出；
    Question patternExtractQuestion(Question q);

    //输入一个Question类型的数据结构，对其进行问题意图、答案类型分析后输出；
    Question latQuestion(Question q);

    //输入一个Question类型的数据结构，对其进行关系抽取后输出；
    Question relationExtractQuestion(Question q);
}
