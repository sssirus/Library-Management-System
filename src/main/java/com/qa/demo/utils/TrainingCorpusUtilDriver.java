package com.qa.demo.utils;

import java.util.Map;

/**
 *  Created time: 2017_08_30
 *  Author: Devin Hua
 *  Function description:
 *  The main driver interface for training corpus utils.
 */

public interface TrainingCorpusUtilDriver {

    //从文件资源中读取QA对作为训练语料；
    Map<?,?> getQuestionAnswerPairFromFile(String path);

    //打印训练语料中的QA对；
    void printQuestionAnswerPairFromFile(String path);

}
