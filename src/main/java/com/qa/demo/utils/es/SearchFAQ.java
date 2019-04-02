package com.qa.demo.utils.es;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Author: TT. Wu
 * Time: 2017/8/25
 */
public class SearchFAQ {
    /**
     * term query,输入检索的字段范围，返回json格式的字符串
     * @return List<String>
     * @throws UnknownHostException
     */
    public static List<String> query(QueryBuilder queryBuilder, String indexName) throws UnknownHostException {
        TransportClient client = GetClient.getTransportClient();

        List<String> hitContent = new ArrayList<String>();

        SearchResponse scrollResp = client.prepareSearch(indexName)
                .addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC)
                .setScroll(new TimeValue(60000))
                .setQuery(queryBuilder)
                .setSize(10).get();

        while(scrollResp.getHits().getHits().length != 0){
            for(SearchHit hit: scrollResp.getHits().getHits()){
                hitContent.add(hit.getSourceAsString());
            }
            scrollResp = client.prepareSearchScroll(scrollResp.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
        }
        return hitContent;
    }

    public static List<String> termQuery(String field, String term, String indexName) throws UnknownHostException {
        QueryBuilder queryBuilder = QueryBuilders.termQuery(field, term);
        return query(queryBuilder, indexName);
    }

    public static List<String> matchQuery(String question, String indexName) throws UnknownHostException {
        QueryBuilder queryBuilder = QueryBuilders.matchQuery("question", question);
        return query(queryBuilder, indexName);
    }

    public static List<String> fuzzyQuery(String question, String indexName) throws UnknownHostException {
        QueryBuilder queryBuilder = QueryBuilders.matchQuery("question", question);
        return query(queryBuilder, indexName);
    }
}
