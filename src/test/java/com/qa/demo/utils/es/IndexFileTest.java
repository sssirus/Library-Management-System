package com.qa.demo.utils.es;

import com.qa.demo.conf.Configuration;
import com.qa.demo.dataStructure.DataSource;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.engine.Engine;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;

import java.io.IOException;


import java.io.IOException;

import static org.elasticsearch.index.query.QueryBuilders.termQuery;

public class IndexFileTest {
    @Test
    public void init() throws Exception {
        TransportClient client = GetClient.getTransportClient();
        TypesExistsResponse typesExistsResponse = client.admin().indices().prepareTypesExists(Configuration.ES_INDEX_FAQ).setTypes(Configuration.ES_TYPE_FAQ).get();
        System.out.println(typesExistsResponse.isExists());
    }

    @Test
    public void indexData() throws Exception {
        TransportClient client = GetClient.getTransportClient();

//        GetResponse response = client.prepareGet(Configuration.ES_INDEX_FAQ, IndexFile.type, "AV4-At0VG44tWoFkZac2").get();
//        System.out.println(response.getSource());

//        IndexFile.initFaq();
//        IndexFile.indexFaqData();

        QueryBuilder qb = termQuery("answer", "印度尼西亚");
        SearchResponse searchResponse = client.prepareSearch(Configuration.ES_INDEX_FAQ)
                .addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC)
//                .setScroll(new TimeValue(6000))
                .setQuery(qb)
                .setSize(10).get();

        System.out.println(searchResponse.getHits().totalHits);

        for(SearchHit hit: searchResponse.getHits().getHits()){
            System.out.println(hit.getSource().get("answer"));
        }
    }

    @Test
    public void indexEncyclopediaData() throws IOException {
        IndexFile.indexEncyclopediaData(DataSource.ENCYCLOPEDIA);
    }
}