package com.qa.demo.query;

import com.qa.demo.dataStructure.Question;

/**
 *  Created time: 2017_09_01
 *  Author: Devin Hua
 *  Function description:
 *  The main driver interface for query generation and query conduct by KB.
 */
public interface KbqaQueryDriver {

    //对问题进行基于KB的查询，返回候选答案集等相关信息，放在question数据结构中；
    Question kbQueryAnswers(Question q);

    enum QueryType{
        TERM_QUERY,
        MATCH_PHRASE_QUERY,
        PATTERN_QUERY,
        MULTI_QUERY,
        FUZZY_QUERY,
        SPAN_OR_QUERY,
        TERMS_QUERY,
        COMMON_TERMS_QUERY,
        MATCH_QUERY
    }

}
