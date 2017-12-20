package com.qa.demo.query;

import com.qa.demo.dataStructure.DataSource;
import com.qa.demo.dataStructure.Question;

import java.net.UnknownHostException;

/**
 *  Created time: 2017_09_01
 *  Author: Devin Hua
 *  Function description: 基于文本的问答系统相关接口。
 *  The main driver interface for query generation and query conduct by ES.
 */

public interface DbqaQueryDriver {
    /**
     * 执行查询操作：将检索得到的“文本证据”封装至Question对象内返回。
     * 默认查询方式：matchQuery
     * @param question
     * @return
     */
    Question search(Question question) throws UnknownHostException;

    /**
     * 执行查询操作：将检索得到的“文本证据”封装至Question对象内返回, 检索时指定查询类型。
     * @param question
     * @param type
     * @return
     */
    Question search(Question question, QueryType type);

    /**
     * 执行查询操作：将检索得到的“文本证据”封装至Question对象内返回，检索时制定查询类型以及检索数据源。
     * @param question
     * @param type
     * @param dataSources
     * @return
     */
    Question search(Question question, QueryType type, DataSource... dataSources);

    enum QueryType{
        TERM_QUERY,
        MATCH_PHRASE_QUERY,
        PATTERN_QUERY,
        MULTI_QUERY,
        FUZZY_QUERY,
        SPAN_OR_QUERY,
        TERMS_QUERY,
        COMMON_TERMS_QUERY

    }
}


