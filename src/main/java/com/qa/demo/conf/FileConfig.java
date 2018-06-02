package com.qa.demo.conf;

/**
 * Description: 统一管理文件资源
 * Author: TT. Wu
 * Time: 2017/8/24
 */
public class FileConfig {
    //常用问答对文件
    public static final String FILE_FAQ = "src/main/resources/data/QAQuestionSamplesUTF8.txt";
    public static final String FILE_FAQ_T = "src/main/resources/data/QAQuestionFromKbUTF8.txt";
    public static final String FILE_TRIPLET_FAQ_SAMPLE = "src/main/resources/data/QAQuestionFromKbSample.txt";
    public static final String QUESTION_FOR_TEST = "src/main/resources/data/QuestionForTest.txt";
    public static final String QUESTION_FOR_TRAIN = "src/main/resources/data/QuestionForTrain.txt";//open

    //整理问答对的原始文件
    public static final String FILE_QUESTION = "src/main/resources/data/QAQuestionSamples.txt";
    public static final String FILE_ANSWER = "src/main/resources/data/QAQuestionSamplesOnlyAnswers.txt";

    //分类文件
    public static final String FILE_CATEGORY = "src/main/resources/data/category.txt";
    //属性文件
    public static final String FILE_ATTRIBUTE = "src/main/resources/data/attribute.txt";

    //文件夹
    public static final String DIC_CATEGORY_PROPERTIES = "src/main/resources/data/CategoryProperties";

    //实体链接训练所用的实体列表文件地址
    public static final String ENTITY_HASH = "src/main/resources/data/EntityLinkingWithLucene/entity_hash.txt";
    //使用Lucene的实体链接，其存放index的文件夹地址
    public static final String ENTITY_LINKING_LUCENE_INDEX = "src/main/resources/data/EntityLinkingWithLucene/QAIndex/";

    //分词用字典地址
    public static final String DICTIONARY_FILE = "src/main/resources/data/dictionary/default.dic";
    //停用词表
    public static final String STOPWORD_FILE = "src/main/resources/data/dictionary/stopwords.dat";

    //datatype property三元组地址
    public static final String DATATYPE_PROPERTY_TRIPLETS_FILE = "src/main/resources/data/kbfile/rdf_datatype.txt";
    //object property三元组地址
    public static final String OBJECT_PROPERTY_TRIPLETS_FILE = "src/main/resources/data/kbfile/rdf_object.txt";
    //zhwiki_infobox三元组地址
    public static final String ZHWIKI_TRIPLETS_FILE = "src/main/resources/data/kbfile/rdf_zhwiki.txt";

    //农业知识图谱实体的来源
    public static final String ENTITY_SOURCE_ONE = "src/main/resources/data/KG/input/Entity/baidu_extra_entities.nt";
    public static final String ENTITY_SOURCE_TWO = "src/main/resources/data/KG/input/Entity/baidubaike_entities.nt";
    public static final String ENTITY_SOURCE_THERE = "src/main/resources/data/KG/input/Entity/hudong_extra_entities.nt";
    public static final String ENTITY_SOURCE_FOUR = "src/main/resources/data/KG/input/Entity/hudongbaike_entities.nt";
    public static final String ENTITY_SOURCE_FIVE = "src/main/resources/data/KG/input/Entity/zhwiki_entities.nt";
    public static final String ENTITY_SOURCE_SIX = "src/main/resources/data/KG/input/Entity/zhwiki_extra_entities.nt";
    public static final String ENTITY_SOURCE_TOTAL = "src/main/resources/data/KG/output/Kg_Entity.txt";
    //农业知识图谱属性的来源
    public static final String PROPERTY_SOURCE_ONE = "src/main/resources/data/KG/input/Property/baidubaike_entitiy_properties.nt";
    public static final String PROPERTY_SOURCE_TWO =  "src/main/resources/data/KG/input/Property/baidubaike_extra_entitiy_properties.nt";
    public static final String PROPERTY_SOURCE_THERE =  "src/main/resources/data/KG/input/Property/hudong_extra_entitiy_properties.nt";
    public static final String PROPERTY_SOURCE_FOUR =  "src/main/resources/data/KG/input/Property/hudongbaike_entitiy_properties.nt";
    public static final String PROPERTY_SOURCE_FIVE =  "src/main/resources/data/KG/input/Property/zhwiki_entitiy_properties.nt";
    public static final String PROPERTY_SOURCE_SIX =  "src/main/resources/data/KG/input/Property/zhwiki_extra_entitiy_properties.nt";
    public static final String PROPERTY_SOURCE_TOTAL = "src/main/resources/data/KG/output/Kg_Property.txt";
    //农业知识图谱输出文件
    //全体实体文件
    public static final String ENTITY_RESULT = "src/main/resources/data/KG/output/Entity.txt";
    //全体属性文件
    public static final String PROPERTY_RESULT = "src/main/resources/data/KG/output/Property.txt";
    //实体链接文件
    public static final String ENTITYLINKING_RESULT = "src/main/resources/data/KG/output/EntityLinking.txt";
    //属性链接文件
    //一个问题对应的属性
    public static final String QUESTIONWITHPROPERTY_RESULT = "src/main/resources/data/KG/output/QuestionWithProperty.txt";
    //一个属性对应的问题
    public static final String PROPERTYWITHQUESTION_RESULT = "src/main/resources/data/KG/output/PropertyWithQuestion.txt";
    //无对应属性的问题
    public static final String QUESTIONWITHOUTPROPERTY_RESULT = "src/main/resources/data/KG/output/QuestionWithoutProperty.txt";
    //无对应问题的属性
    public static final String PROPERTYWITHOUTQUESTION_RESULT = "src/main/resources/data/KG/output/PropertyWithoutQuestion.txt";
    //三元组
    //数值属性三元组
    public static final String RDFDATA_RESULT = "src/main/resources/data/KG/output/Rdf_Data.txt";
    //对象属性三元组
    public static final String RDFOBJ_RESULT = "src/main/resources/data/KG/output/Rdf_Obj.txt";

    //从三元组形成问题，将问题中的实体抠掉，形成模板，并存于文件中；
    public static final String TEMPLATE_REPOSITORY_TRIPLETS = "src/main/resources/data/templateRepository/triplet_template_repository.txt";
    //分词之后的模板库，包含了谓词、模板、分词之后的模板;
    public static final String TEMPLATE_SEGMENTATION_REPOSITORY = "src/main/resources/data/templateRepository/template_segmentation_repository.txt";
    //分词之后的模板库，包含了谓词、分词之后的模板;
    public static final String TEMPLATE_SEGMENTATION_KEYWORDS_REPOSITORY = "src/main/resources/data/templateRepository/template_segmentation_keywords_repository.txt";
    //分词、去掉停用词之后的谓词-tokens同义词集合库；
    public static final String TEMPLATE_SYNONYM_REPOSITORY = "src/main/resources/data/templateRepository/template_synonym_repository.txt";

    //KBQA使用模板回答问题的结果文件；
    public static final String KBQA_TEMPLATE_RESULT = "src/main/resources/data/kbqa_template_result.txt";

    //结合FAQ、三元组生成问答对、模板检索得到的结果；
    public static final String QA_SYSTEM_RESULT = "src/main/resources/data/qa_system_result.txt";

    //农科院提供的19000条百科数据；
    public static final String ENCYCLOPEDIA = "src/main/resources/data/encyclopedia.txt";
    //农科院提供的19000条百科数据,抽取出的文本数据；
    public static final String ENCYCLOPEDIA_CONTENT = "src/main/resources/data/encyclopedia_content.txt";

    //补充的知识图谱属性文件；
    public static final String COMPLEMENTKB = "src/main/resources/data/kbfile/addition_properties";
    //补充的知识图谱三元组文件；
    public static final String COMPLEMENT_KB_TRIPLETS = "src/main/resources/data/kbfile/addition_properties_triplets.txt";
    //NT格式的三元组文件；
    public static final String NT_TRIPLETS = "src/main/resources/data/kbfile/NT_triplets.nt";

    //设置log文件的参数文件所在的位置；
    public static final String LOG_PROPERTY = "src/main/resources/log4j.properties";

    //设置TDB文件的位置；
    public static final String TDB = "src/main/resources/data/kbfile/TDB";
    //供TDB读取RDF的文件位置；
    public static final String TDB_RDF = "src/main/resources/data/kbfile/NT_triplets.nt";
    //农业词向量文件地址；
    public static final String W2V_vector = "src/main/resources/data/WordVector/wiki.zh.text.vector";
    //DL4J DLL文件；
    public static final String W2V_file = "src/main/resources/data/WordVector/depends.exe";

    //topological pattern的子树文件；
    public static final String TP_SUBTREE = "src/main/resources/data/topologicalPattern/TPattern_Subtree.txt";
    //topological pattern的谓词指称文件；
    public static final String TP_PREDICATE_MENTION = "src/main/resources/data/topologicalPattern/TPattern_PredicateMention.txt";
    //topological pattern中分析句法树的模型文件；
    public static final String TP_CNN_MODEL = "src/main/resources/data/topologicalPattern/chinesePCFG.ser.gz";

    //别名词典；
    public static final String ALIAS_DICTIONARY = "src/main/resources/data/templateRepository/aliasDict.txt";
    //字向量相关；
    public static String normalwordsVector = "src/main/resources/data/WordVector/subWord2Vec/library/normalwordsVector";
    public static String subwordsVector = "src/main/resources/data/WordVector/subWord2Vec/library/subwordsVector";
    public static String wiki5000seg = "src/main/resources/data/WordVector/subWord2Vec/json/wiki5000.seg.txt.json";
    public static String wiki5000segbpe = "src/main/resources/data/WordVector/subWord2Vec/json/wiki5000.seg.bpe.txt.json";

}
