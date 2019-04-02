package com.qa.demo.query;
import com.qa.demo.dataStructure.QueryTuple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Description: To rank the query tuple by tuple scores in descending order.
 * Created by Devin Hua on 2017/10/12.
 */

public class RerankQueryTuple {

    /**
     * 基本实现：根据计算得到的分数，将查询二元组整理排序后返回；
     * @param tuples
     * @return
     */
    protected static ArrayList<QueryTuple> rankTuples(ArrayList<QueryTuple> tuples){
        Collections.sort(tuples);
        Collections.reverse(tuples);
        return tuples;
    }

}
