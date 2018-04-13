package com.qa.demo.conf;

import java.util.Arrays;
import java.util.HashSet;

public class Configuration {

    //用来处理处理答案-问题对文件的特殊分隔符
    public static final String SPLITSTRING = "_____";

    //所有map键值对中，标识问题的键；
    public static final String QUESTION = "QUESTION";
    //所有map键值对中，标识答案的键；
    public static final String ANSWER = "ANSWER";
    //所有map键值对中，标识问题类型的键；
    public static final String CATEGORY = "CATEGORY";
    //所有map键值对中，标识ES评分的键；
    public static final String ESSCORE = "ESSCORE";

    //表示类的自制知识图谱语义空间前缀，例如:<http://coindb/tupu.zgny/category/泻下药>；
    public static final String CATEGORY_PREFIX = "http://coindb/tupu.zgny/category/";

    //表示类的农科院语义空间前缀，例如:<http://caas.net.cn/agriculture/category/泻下药>；
    public static final String CATEGORY_PREFIX_CAAS = "http://caas.net.cn/agriculture/category/";

    //表示类别关系rdfs的自制知识图谱语义空间前缀，例如:<http://coindb/ontology/category>；
    public static final String CATEGORY_RDFS_PREFIX = "http://coindb/ontology/category";

    //表示类别关系rdfs的语义空间前缀，例如:<http://zhishi.me/ontology/category>；
    public static final String CATEGORY_RDFS_PREFIX_zhishime = "http://zhishi.me/ontology/category";

    //表示类别关系rdfs的农科院语义空间前缀，例如:<http://caas.net.cn/ontology/category>；
    public static final String CATEGORY_RDFS_PREFIX_CAAS = "http://caas.net.cn/ontology/category";

    //表示subclassof关系rdfs的语义空间前缀，例如:<http://www.w3.org/2000/01/rdf-schema#subClassOf>；
    public static final String SUBCLASSOF_PREFIX = "http://www.w3.org/2000/01/rdf-schema#subClassOf";

    //表示实体名称的baidu语义空间前缀，例如:<http://zhishi.me/baidubaike/resource/乔魁多>；
    public static final String ENTITY_PREFIX_BAIDU = "http://zhishi.me/baidubaike/resource/";

    //表示实体名称的自制知识图谱语义空间前缀，例如:<http://coindb/tupu.zgny/resource/巴豆霜>；
    public static final String ENTITY_PREFIX = "http://coindb/tupu.zgny/resource/";

    //表示实体名称的农科院语义空间前缀，例如:<http://caas.net.cn/agriculture/resource/巴豆霜>；
    public static final String ENTITY_PREFIX_CAAS = "http://caas.net.cn/agriculture/resource/";

    //表示实体名称的互动百科语义空间前缀，例如:<http://zhishi.me/hudongbaike/resource/新疆小麦>
    public static final String ENTITY_PREFIX_HUDONG = "http://zhishi.me/hudongbaike/resource/";

    //表示谓词名称的baidu语义空间前缀，例如:<http://zhishi.me/baidubaike/property/中文名>；
    public static final String PREDICATE_PREFIX_BAIDU = "http://zhishi.me/baidubaike/property/";

    //表示谓词名称的hudongbaike语义空间前缀，例如:<http://zhishi.me/hudongbaike/property/中文名>；
    public static final String PREDICATE_PREFIX_HUDONG = "http://zhishi.me/hudongbaike/property/";

    //表示谓词名称的wiki语义空间前缀，例如:<http://zhishi.me/zhwiki/property/中文名>；
    public static final String PREDICATE_PREFIX_WIKI = "http://zhishi.me/zhwiki/property/";

    //表示谓词的自制知识图谱语义空间前缀，例如:<http://coindb/tupu.zgny/property/产地>；
    public static final String PROPERTY_PREFIX = "http://coindb/tupu.zgny/property/";

    //表示谓词名称的农科院语义空间前缀，例如:<http://caas.net.cn/agriculture/property/中文名>；
    public static final String PREDICATE_PREFIX_CAAS = "http://caas.net.cn/agriculture/property/";

    /* ElasticSearch相关设置*/

    //集群名称
    public static final String ES_CLUSTER_NAME = "elasticsearch";
    //集群地址: 默认本地地址
    public static final String ES_HOST = "127.0.0.1";

    //集群名称
//    public static final String ES_CLUSTER_NAME = "my-application";
//  //集群地址: 默认本地地址
//    public static final String ES_HOST = "192.168.3.234";
    //索引名称: 农业项目-FAQ
    public static final String ES_INDEX_FAQ = "kse_agriculture_faq";
    //类型名称：互联网获取的常用问答对
    public static final String ES_TYPE_FAQ = "faq";
    //类型名称：农业项目-FAQ-由知识图谱三元组生成
    public static final String ES_TYPE_FAQ_T = "faq_t";
    //类型名称：农业项目-FAQ-由知识图谱三元组生成的模板库
    public static final String ES_TYPE_TEMPLATE_T = "template_t";
    //类型名称：农业项目-农业百科
    public static final String ES_TYPE_ENCYCLOPEDIA = "encyclopedia_t";
    //类型名称：农业项目-FAQ-由知识图谱三元组生成的模板库
    public static final String ES_TYPE_SYNONYM_T = "template_synonym_t";

    //查询的size大小，即查询ES或者模板库中的最大命中数;
    public static final int QUERY_HIT_SIZE = 50;

    //包含界门纲目科属种族组的属性;
    public static HashSet<String> SPECIAL_WORD_SET = new HashSet<String>(
            Arrays.asList("界", "门", "亚门", "纲", "亚纲", "目", "亚目", "科", "亚科", "属", "亚属",
                    "种", "亚种", "族", "亚族", "组", "亚组"));

    //标点符号表;
    public static HashSet<String> PUNCTUATION_SET = new HashSet<String>(
            Arrays.asList(",", "?", ".", "，", "。", "？", "（", "）"));

    //默认TDB model name；
    public static String TDB_MODEL_NAME = "TDB_agriculture";

    //词向量相似度的过滤阈值，低于该阈值的都记为0.0；
    public static Double W2V_THRESHOLD = 0.40;

    //编辑距离相似度的过滤阈值，低于该阈值的都记为0.0；
    public static Double EDITDISTANCE_THRESHOLD = 0.40;

}
