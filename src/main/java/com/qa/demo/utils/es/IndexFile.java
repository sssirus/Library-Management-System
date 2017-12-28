package com.qa.demo.utils.es;

import com.qa.demo.conf.Configuration;
import com.qa.demo.conf.FileConfig;
import com.qa.demo.dataStructure.DataSource;
import com.qa.demo.dataStructure.QuestionTemplate;
import com.qa.demo.templateTraining.TemplateFromTripletsClient;
import com.qa.demo.utils.io.IOTool;
import com.qa.demo.templateTraining.TemplateGeneralization;
import com.qa.demo.utils.trainingcorpus.ExtractQuestionsFromText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.io.IOException;
import java.util.*;

import static com.qa.demo.conf.Configuration.ES_TYPE_TEMPLATE_T;
import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * Description:
 * Author: TT. Wu
 * Time: 2017/8/25
 */
public class IndexFile {
    private static Logger LOG = LogManager.getLogger(IndexFile.class.getName());

    /**
     * 初始化索引结构
     * @param indexName
     * @param type
     * @param filePath
     */
    private static boolean _init(String indexName, String type, String filePath) {
        try {
            TransportClient client = GetClient.getTransportClient();

            //启动系统的时候判断索引是否存在
            IndicesExistsRequest inExistsRequest = new IndicesExistsRequest(indexName);
            //如果已经存在的话就更新数据；如果不存在就创建索引。
            IndicesExistsResponse indicesExistsResponse = client.admin().indices().exists(inExistsRequest).actionGet();
            System.out.println(indexName + " is existed or not: " + indicesExistsResponse.isExists());
            if (indicesExistsResponse.isExists()) {
                //索引index已经存在，则继续检查是不是存在该type
                TypesExistsResponse typesExistsResponse = client.admin().indices().prepareTypesExists(indexName).setTypes(type).get();
                System.out.println(type + " is existed or not: " + typesExistsResponse.isExists());
                //如果type已经存在，直接返回
                if(typesExistsResponse.isExists()){
                    return true;
                }
                else{ //type之前不存在，则定义索引结构
                    PutMappingRequest putMappingRequest = null;
                    if(type.equalsIgnoreCase(Configuration.ES_TYPE_TEMPLATE_T)){
                        putMappingRequest = Requests.putMappingRequest(indexName).type(type).source(_getModifiedMappingForTemplate(type));
                    }
                    else if(type.equalsIgnoreCase(Configuration.ES_TYPE_SYNONYM_T)){
                        putMappingRequest = Requests.putMappingRequest(indexName).type(type).source(_getModifiedMappingForSynonym(type));
                    }
                    else if(type.equalsIgnoreCase(Configuration.ES_TYPE_ENCYCLOPEDIA)){
                        putMappingRequest = Requests.putMappingRequest(indexName).type(type).source(_getModifiedMappingForEncyclopedia(type));
                    }
                    else{
                        putMappingRequest = Requests.putMappingRequest(indexName).type(type).source(_getModifiedMapping(type));
                    }
                    PutMappingResponse putMappingResponse = client.admin().indices().putMapping(putMappingRequest).actionGet();
                    LOG.info("[info]已定义index:"+indexName+"-"+type);
                    System.out.println("已定义index:"+indexName+"-"+type);
                    return false;
                }
            }
            else{
                //定义索引结构
                XContentBuilder mapping;
                if(type.equalsIgnoreCase(Configuration.ES_TYPE_TEMPLATE_T)){
                    mapping = _getDefinitionMappingForTemplate(indexName, type);
                }
                else if(type.equalsIgnoreCase(Configuration.ES_TYPE_SYNONYM_T)){
                    mapping = _getDefinitionMappingForSynonym(indexName, type);
                }
                else if(type.equalsIgnoreCase(Configuration.ES_TYPE_ENCYCLOPEDIA)){
                    mapping = _getDefinitionMappingForEncyclopedia(indexName, type);
                }
                else{
                    mapping = _getDefinitionMapping(indexName, type);
                }
                //创建索引
                CreateIndexRequestBuilder createIndexRequestBuilder = client.admin().indices()
                        .prepareCreate(indexName)
                        .setSource(mapping);
                CreateIndexResponse createIndexResponse = createIndexRequestBuilder.execute().actionGet();
                LOG.info("[info]已定义index:"+indexName+"-"+type);
                System.out.println("已定义index:"+indexName+"-"+type);
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }


    //没有索引，建立索引和增加新的synonym template type;
    private static XContentBuilder
    _getDefinitionMappingForSynonym(String indexName, String typeFaq) throws IOException {
        XContentBuilder mapping = jsonBuilder()
                .startObject()
                .startObject("settings")
                .field("number_of_shards", 1) //设置分片的数量
                .field("number_of_replicas", 1) //设置副本数量
                .endObject()
                .startObject("mappings")
                .startObject(typeFaq) // type名称
                .startObject("_all")
                .field("enabled", "false")
                .endObject()
                .startObject("properties") //下面是设置文档属性
                .startObject("synonyms").field("type", "text").field("analyzer", "index_ansj")
                .field("search_analyzer", "query_ansj").field("store", "yes")
                .endObject()
                .startObject("predicate").field("type", "text").field("analyzer", "index_ansj")
                .field("search_analyzer", "query_ansj").field("store", "yes")
                .endObject()
                .endObject()
                .endObject()
                .endObject()
                .endObject();

        return mapping;
    }

    //已有索引，增加新的synonym template type;
    private static XContentBuilder _getModifiedMappingForSynonym(String typeFaq) throws IOException {
        XContentBuilder mapping = jsonBuilder()
                .startObject()
                .startObject("properties") //下面是设置文档属性
                .startObject("synonyms").field("type", "text").field("analyzer", "index_ansj")
                .field("search_analyzer", "query_ansj").field("store", "yes")
                .endObject()
                .startObject("predicate").field("type", "text").field("analyzer", "index_ansj")
                .field("search_analyzer", "query_ansj").field("store", "yes")
                .endObject()
                .endObject()
                .endObject();
        return mapping;
    }

    //没有索引，建立索引和增加新的template type;
    private static XContentBuilder _getDefinitionMappingForTemplate(String indexName, String typeFaq) throws IOException {
        XContentBuilder mapping = jsonBuilder()
                .startObject()
                .startObject("settings")
                .field("number_of_shards", 1) //设置分片的数量
                .field("number_of_replicas", 1) //设置副本数量
                .endObject()
                .startObject("mappings")
                .startObject(typeFaq) // type名称
                .startObject("_all")
                .field("enabled", "false")
                .endObject()
                .startObject("properties") //下面是设置文档属性
                .startObject("pattern").field("type", "text").field("analyzer", "index_ansj")
                .field("search_analyzer", "query_ansj").field("store", "yes")
                .endObject()
                .startObject("predicatename").field("type", "text").field("analyzer", "index_ansj")
                .field("search_analyzer", "query_ansj").field("store", "yes")
                .endObject()
                .endObject()
                .endObject()
                .endObject()
                .endObject();

        return mapping;
    }

    //已有索引，增加新的template type;
    private static XContentBuilder _getModifiedMappingForTemplate(String typeFaq) throws IOException {
        XContentBuilder mapping = jsonBuilder()
                .startObject()
                .startObject("properties") //下面是设置文档属性
                .startObject("pattern").field("type", "text").field("analyzer", "index_ansj")
                .field("search_analyzer", "query_ansj").field("store", "yes")
                .endObject()
                .startObject("predicatename").field("type", "text").field("analyzer", "index_ansj")
                .field("search_analyzer", "query_ansj").field("store", "yes")
                .endObject()
                .endObject()
                .endObject();
        return mapping;
    }

    //没有索引，建立索引和增加新的encyclopedia type;
    private static XContentBuilder _getDefinitionMappingForEncyclopedia(String indexName, String typeFaq) throws IOException {
        XContentBuilder mapping = jsonBuilder()
                .startObject()
                .startObject("settings")
                .field("number_of_shards", 1) //设置分片的数量
                .field("number_of_replicas", 1) //设置副本数量
                .endObject()
                .startObject("mappings")
                .startObject(typeFaq) // type名称
                .startObject("_all")
                .field("enabled", "false")
                .endObject()
                .startObject("properties") //下面是设置文档属性
                .startObject("title").field("type", "text").field("analyzer", "index_ansj")
                .field("search_analyzer", "query_ansj").field("store", "yes")
                .endObject()
                .startObject("content").field("type", "text").field("analyzer", "index_ansj")
                .field("search_analyzer", "query_ansj").field("store", "yes")
                .endObject()
                .endObject()
                .endObject()
                .endObject()
                .endObject();

        return mapping;
    }

    //已有索引，增加新的encyclopedia type;
    private static XContentBuilder _getModifiedMappingForEncyclopedia(String type) throws IOException {
        XContentBuilder mapping = jsonBuilder()
                .startObject()
                .startObject("properties") //下面是设置文档属性
                .startObject("title").field("type", "text").field("analyzer", "index_ansj")
                .field("search_analyzer", "query_ansj").field("store", "yes")
                .endObject()
                .startObject("content").field("type", "text").field("analyzer", "index_ansj")
                .field("search_analyzer", "query_ansj").field("store", "yes")
                .endObject()
                .endObject()
                .endObject();
        return mapping;
    }

    //没有索引，建立索引和增加新的FAQ type;
    private static XContentBuilder _getDefinitionMapping(String indexName, String typeFaq) throws IOException {
        XContentBuilder mapping = jsonBuilder()
                .startObject()
                .startObject("settings")
                .field("number_of_shards", 1) //设置分片的数量
                .field("number_of_replicas", 1) //设置副本数量
                .endObject()
                .startObject("mappings")
                .startObject(typeFaq) // type名称
                .startObject("_all")
                .field("enabled", "false")
                .endObject()
                .startObject("properties") //下面是设置文档属性
                .startObject("category").field("type", "text").field("analyzer", "index_ansj")
                .field("search_analyzer", "query_ansj").field("store", "yes")
                .endObject()
                .startObject("question").field("type", "text").field("analyzer", "index_ansj")
                .field("search_analyzer", "query_ansj").field("store", "yes")
                .endObject()
                .startObject("answer").field("type", "text").field("analyzer", "index_ansj")
                .field("search_analyzer", "query_ansj").field("store", "yes")
                .endObject()
                .endObject()
                .endObject()
                .endObject()
                .endObject();

        return mapping;
    }

    private static XContentBuilder _getModifiedMapping(String typeFaq) throws IOException {
        XContentBuilder mapping = jsonBuilder()
                .startObject()
                .startObject("properties") //下面是设置文档属性
                .startObject("category").field("type", "text").field("analyzer", "index_ansj")
                .field("search_analyzer", "query_ansj").field("store", "yes")
                .endObject()
                .startObject("question").field("type", "text").field("analyzer", "index_ansj")
                .field("search_analyzer", "query_ansj").field("store", "yes")
                .endObject()
                .startObject("answer").field("type", "text").field("analyzer", "index_ansj")
                .field("search_analyzer", "query_ansj").field("store", "yes")
                .endObject()
                .endObject()
                .endObject();
        return mapping;
    }

    /**
     * 索引faq的默认数据源是网络检索得到的问答对
     * @throws IOException
     */
    public static void indexFaqData() throws IOException {
        indexFaqData(DataSource.FAQ);
    }

    /**
     * 对指定数据源的FAQ数据进行初始化
     * @param dataSources
     * @throws IOException
     */
    public static void indexFaqData(DataSource... dataSources) throws IOException {
        for(DataSource dataSource: dataSources){
            long n = 0;
            //选择数据源
            String indexName = Configuration.ES_INDEX_FAQ;
            String filePath;
            String typeFaq;
            //选择索引的faq来源
            switch (dataSource) {
                case FAQ://网络爬取得常用问答对
                    filePath = FileConfig.FILE_FAQ;
                    typeFaq = Configuration.ES_TYPE_FAQ;
                    break;
                case FAQ_T://由知识图谱生成的问答对
                    filePath = FileConfig.FILE_FAQ_T;
                    typeFaq = Configuration.ES_TYPE_FAQ_T;
                    break;
                case PATTERN://由知识图谱生成的问答对形成的模板
                    filePath = FileConfig.TEMPLATE_REPOSITORY_TRIPLETS;
                    typeFaq = ES_TYPE_TEMPLATE_T;
                    break;
                case SYNONYM://由模板分词形成的同义词集
                    filePath = FileConfig.TEMPLATE_SYNONYM_REPOSITORY;
                    typeFaq = Configuration.ES_TYPE_SYNONYM_T;
                    break;
                default:
                    filePath = FileConfig.FILE_FAQ;
                    typeFaq = Configuration.ES_TYPE_FAQ;
            }

            //索引数据之前首先对es中的索引结构进行初始化
            if(!_init(indexName, typeFaq, filePath)){

                if(typeFaq.equalsIgnoreCase(ES_TYPE_TEMPLATE_T))
                {
                    //取得模板库，为自然问句去掉实体之后的模板;
                    HashSet<QuestionTemplate> qTemplates =
                            TemplateFromTripletsClient.getInstance().getTemplateRepository();
                    TransportClient client = GetClient.getTransportClient();
                    for(QuestionTemplate qtemplate : qTemplates)
                    {
                        String patternname = qtemplate.getTemplateString();
                        String predicatename = qtemplate.getPredicate().getKgPredicateName();
                        IndexResponse indexResponse = client
                                .prepareIndex(indexName, typeFaq)
                                .setSource( //这里可以直接使用json字符串
                                        jsonBuilder()
                                                .startObject()
                                                .field("pattern", patternname)
                                                .field("predicatename", predicatename)
                                                .endObject()
                                ).get();
                        //输出计数
                        n = n+1;
                        if(n%100 == 0){
                            System.out.println(n);
                        }
                    }

                }

                else if(typeFaq.equalsIgnoreCase(Configuration.ES_TYPE_SYNONYM_T))
                {
                    //取得模板库，为谓词和某个模板经过分词之后形成的关键词组合，并不是同义词的组合;
                    HashMap<String, HashSet<String>> predicateTemplatesMap =
                            TemplateGeneralization.getInstance().getPredicateTemplateSegmentationMap();

                    TransportClient client = GetClient.getTransportClient();
                    Iterator it = predicateTemplatesMap.entrySet().iterator();
                    while(it.hasNext())
                    {
                        Map.Entry<String, HashSet<String>> entry = (Map.Entry)(it.next());
                        String predicatename = entry.getKey();
                        HashSet<String> synonyms = entry.getValue();
                        String synonym_string = "";
                        for(String temp : synonyms)
                        {
                            synonym_string = temp;
                            IndexResponse indexResponse = client
                                    .prepareIndex(indexName, typeFaq)
                                    .setSource( //这里可以直接使用json字符串
                                            jsonBuilder()
                                                    .startObject()
                                                    .field("synonyms", synonym_string)
                                                    .field("predicate", predicatename)
                                                    .endObject()
                                    ).get();
                            //输出计数
                            n = n+1;
                            if(n%100 == 0){
                                System.out.println(n);
                            }
                        }
                    }
                }

                else{
                    HashMap<Integer, HashMap<String, String>> data = ExtractQuestionsFromText.getQuestionsFromFile(filePath);
                    TransportClient client = GetClient.getTransportClient();

                    Iterator<Map.Entry<Integer, HashMap<String, String>>> iterator = data.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<Integer, HashMap<String, String>> entry = iterator.next();
                        HashMap<String, String> keyValueMap = new HashMap<String, String>(entry.getValue());

                        IndexResponse indexResponse = client
                                .prepareIndex(indexName, typeFaq)
                                .setSource( //这里可以直接使用json字符串
                                        jsonBuilder()
                                                .startObject()
                                                .field("category", keyValueMap.get("CATEGORY"))
                                                .field("question", keyValueMap.get("QUESTION"))
                                                .field("answer", keyValueMap.get("ANSWER"))
                                                .endObject()
                                ).get();
//                System.out.println("index "+indexResponse.getIndex()+" insert doc id: "+indexResponse.getId());
                        //输出计数
                        n = n+1;
                        if(n%100 == 0){
                            System.out.println(n);
                        }
                    }
                }
            }
        }
    }

    /**
     * 对指定数据源的百科数据进行初始化
     * @param dataSources
     * @throws IOException
     */
    public static void indexEncyclopediaData(DataSource... dataSources) throws IOException {
        for(DataSource dataSource: dataSources){
            long n = 0;
            //选择数据源
            String indexName = Configuration.ES_INDEX_FAQ;
            String filePath;
            String type;
            //选择索引的faq来源
            switch (dataSource) {
                case ENCYCLOPEDIA://网络爬取得常用问答对
                    filePath = FileConfig.ENCYCLOPEDIA;
                    type = Configuration.ES_TYPE_ENCYCLOPEDIA;
                    break;
                default:
                    filePath = FileConfig.ENCYCLOPEDIA;
                    type = Configuration.ES_TYPE_ENCYCLOPEDIA;
            }

            //索引数据之前首先对es中的索引结构进行初始化
            if(!_init(indexName, type, filePath)){
                HashSet<List<String>> documents = (HashSet<List<String>>) IOTool.parseEncyclopedia(FileConfig.ENCYCLOPEDIA);
                TransportClient client = GetClient.getTransportClient();
                for(List<String> list: documents){
                    if(list.size()>=2){
                        String title = list.get(0);
                        String content = list.get(1);
                        IndexResponse indexResponse = client
                                .prepareIndex(indexName, type)
                                .setSource( //这里可以直接使用json字符串
                                        jsonBuilder()
                                                .startObject()
                                                .field("title", title)
                                                .field("content", content)
                                                .endObject()
                                ).get();
                        //输出计数
                        n = n+1;
                        if(n%100 == 0){
                            System.out.println(n);
                        }
                    }
                }
            }
        }
    }
}
