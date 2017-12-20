package com.qa.demo.utils.es;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequestBuilder;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.TimeValue;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

public class Ansj {
    @Test
    public void runAnsj() {
        Ansj ansj = new Ansj();
        try {
            TransportClient client = GetClient.getTransportClient();
            //启动系统的时候判断索引是否存在
            IndicesExistsRequest inExistsRequest = new IndicesExistsRequest("test_index");
            //如果已经存在的话就把索引删除，然后重新创建索引（待完善）
            IndicesExistsResponse indicesExistsResponse = client.admin().indices().exists(inExistsRequest).actionGet();
            System.out.println(indicesExistsResponse.isExists());
            if (indicesExistsResponse.isExists()) {
                DeleteIndexResponse deleteIndexResponse = client.admin().indices().prepareDelete("test_index")
                        .execute().actionGet();
                System.out.println("是否已经存在该索引: " + deleteIndexResponse.isAcknowledged());
            }

            //定义索引结构
            XContentBuilder mapping = jsonBuilder()
                    .startObject()
                        .startObject("settings")
                            .field("number_of_shards", 1) //设置分片的数量
                            .field("number_of_replicas", 0) //设置副本数量
                        .endObject()
                        .startObject("mappings")
                            .startObject("test_type") // type名称
                                .startObject("_all")
                                    .field("enabled", "false")
                                .endObject()
                                .startObject("properties") //下面是设置文档属性
                                    .startObject("name")
                                        .field("type", "text")
                                        .field("analyzer", "index_ansj")
                                        .field("search_analyzer", "query_ansj")
                                        .field("store", "yes")
                                    .endObject()
                                .endObject()
                            .endObject()
                        .endObject()
                    .endObject();

            //定义索引结构
            CreateIndexRequestBuilder createIndexRequestBuilder = client.admin().indices()
                    .prepareCreate("test_index")
                    .setSource(mapping);
            CreateIndexResponse createIndexResponse = createIndexRequestBuilder.get();
            System.out.println("是否成功创建索引：" + createIndexResponse.toString());

            IndexRequestBuilder indexRequestBuilder = client
                    .prepareIndex("test_index", "test_type")
                    .setSource(XContentFactory.jsonBuilder()
                            .startObject()
                            .field("name", "中国人民万岁")
                            .field("post_date", "2009-11-15T14:12:12")
                            .field("message", "trying on ElasticSearch")
                            .endObject()
                    );

            IndexResponse indexResponse = indexRequestBuilder.get();
            System.out.println("索引名称：" + indexResponse.getIndex() + " 结果：" + indexResponse.getResult() + " id: " + indexResponse.getId());

            //手动刷新
            RefreshResponse refreshResponse = client.admin().indices().refresh(new RefreshRequest("test_index")).get();
            System.out.println(refreshResponse.getSuccessfulShards());

            //检索
            QueryBuilder queryBuilder = QueryBuilders.termQuery("name", "中国");

            SearchResponse resp = client.prepareSearch("test_index")
                    .setQuery(queryBuilder)
                    .setSize(10)
                    .execute().actionGet();

            System.out.println(resp.getHits().totalHits);

            for(SearchHit hit: resp.getHits().getHits()){
                System.out.println(hit.getSourceAsString());
                System.out.println(hit.getScore());
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
