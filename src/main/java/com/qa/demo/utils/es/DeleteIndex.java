package com.qa.demo.utils.es;

import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.client.transport.TransportClient;

import java.net.UnknownHostException;

/**
 * Description:
 * Created by TT. Wu on 2017/9/6.
 */
public class DeleteIndex {
    public static void deleteIndex(String indexName) throws UnknownHostException {
        TransportClient client = GetClient.getTransportClient();
        //启动系统的时候判断索引是否存在
        IndicesExistsRequest inExistsRequest = new IndicesExistsRequest(indexName);
        //如果已经存在的话就把索引删除，然后重新创建索引（待完善）
        IndicesExistsResponse indicesExistsResponse = client.admin().indices().exists(inExistsRequest).actionGet();
        System.out.println(indicesExistsResponse.isExists());
        if (indicesExistsResponse.isExists()) {
            DeleteIndexResponse deleteIndexResponse = client.admin().indices().prepareDelete(indexName).execute().actionGet();
            System.out.println(deleteIndexResponse.isAcknowledged());
        }
    }

    public static void deleteType(String indexName, String type) throws UnknownHostException {
        TransportClient client = GetClient.getTransportClient();
        //启动系统的时候判断索引是否存在
        IndicesExistsRequest inExistsRequest = new IndicesExistsRequest(indexName);
        //如果已经存在的话就把索引删除
        IndicesExistsResponse indicesExistsResponse = client.admin().indices().exists(inExistsRequest).actionGet();
        System.out.println(indicesExistsResponse.isExists());
        if (indicesExistsResponse.isExists()) {
            TypesExistsResponse typesExistsResponse = client.admin().indices().prepareTypesExists(type).get();
            if(typesExistsResponse.isExists()){
                DeleteResponse response = client.prepareDelete().setType("").get();
                System.out.println(response.status());
            }
        }
    }
}
