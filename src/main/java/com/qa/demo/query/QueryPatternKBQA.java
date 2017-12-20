package com.qa.demo.query;

import com.qa.demo.algorithm.EditingDistance;
import com.qa.demo.conf.Configuration;
import com.qa.demo.dataStructure.*;
import com.qa.demo.questionAnalysis.QuestionAnalysisDriver;
import com.qa.demo.questionAnalysis.QuestionAnalysisDriverImpl;
import com.qa.demo.utils.es.GetClient;
import org.elasticsearch.action.search.SearchRequestBuilder;
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

import static com.qa.demo.conf.Configuration.QUERY_HIT_SIZE;

/**
 *  Created time: 2017_09_09
 *  Author: Devin Hua
 *  Function description:
 *  To match templates which are generated from triplets and to return the candidate answers.
 */

public class QueryPatternKBQA implements KbqaQueryDriver {


    //对问题进行基于KB的查询，返回候选答案集等相关信息，放在question数据结构中；
    //该查询中，将模板（从自然问句中将实体去掉后的模板）库放在ES索引中，通过ES查询得到谓词；
    @Override
    public Question kbQueryAnswers(Question q) {

        //取得问题分析器驱动；
        QuestionAnalysisDriver qAnalysisDriver = new QuestionAnalysisDriverImpl();
        q = qAnalysisDriver.nerQuestion(q);
        q = this.patternExtractQuestion(q);
        q = GetCandidateAnswers.getCandidateAnswers(q, DataSource.PATTERN);
        return q;
    }

    //模板到谓词的映射;
    private Question patternExtractQuestion(Question q){

        ArrayList<QueryTuple> tuples = this.patternMatch(q);
        q.setQueryTuples(tuples);
        return q;
    }

    private ArrayList<QueryTuple> patternMatch(Question q) {

        ArrayList<QueryTuple> tuples = new ArrayList<>();

        if (q.getQuestionEntity().isEmpty() || q.getQuestionEntity() == null
                || q.getQuestionToken().size()==0 || q.getQuestionToken().isEmpty())
            return tuples;
        //从问题中将实体删掉后，去匹配模板;
        for (Entity e : q.getQuestionEntity()) {
            String sentence =
                    q.getQuestionString().replace(e.getKgEntityName(), Configuration.SPLITSTRING);
            for (String punctuation : Configuration.PUNCTUATION_SET) {
                sentence = sentence.replace(punctuation, "");
            }
            sentence = sentence.trim();
            if(sentence.equalsIgnoreCase(Configuration.SPLITSTRING)||sentence.equals(""))
                continue;
            try {
                SearchResponse searchResponse = _searchTemplate(sentence, QueryType.MATCH_PHRASE_QUERY, Configuration.ES_TYPE_TEMPLATE_T);
                TransportClient client = GetClient.getTransportClient();
                do {
                    for (SearchHit hit : searchResponse.getHits().getHits()) {
                        String predicatename =
                                hit.getSource().get("predicatename").toString();
                        String patternname =
                                hit.getSource().get("pattern").toString();
                        Predicate p = new Predicate();
                        p.setKgPredicateName(predicatename);
                        QueryTuple tuple = new QueryTuple();
                        QuestionTemplate qTemplate = new QuestionTemplate();
                        qTemplate.setPredicate(p);
                        qTemplate.setTemplateString(patternname);
                        tuple.setTemplate(qTemplate);
                        tuple.setSubjectEntity(e);
                        tuple.setPredicate(qTemplate.getPredicate());
                        try {
                            float score = hit.getScore();
                            if (score > 0) {
                                Double d = Double.parseDouble(String.valueOf(score));
                                tuple.setTupleScore(d);
                            } else {
                                float score1 = EditingDistance.getRepetitiveRate(sentence, patternname);
                                Double d = Double.parseDouble(String.valueOf(score1));
                                tuple.setTupleScore(d);
                            }
                        }catch(Exception exception){
                            tuple.setTupleScore(0.0);
                        }
                        tuples.add(tuple);
                    }
                    searchResponse = client.prepareSearchScroll(searchResponse.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
                } while(searchResponse.getHits().getHits().length != 0); // Zero hits mark the end of the scroll and the while loop.
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            }
        }
        return tuples;
    }



    private SearchResponse _searchTemplate(String qtemplatestring, KbqaQueryDriver.QueryType queryType, String typeName) throws UnknownHostException {
        TransportClient client = GetClient.getTransportClient();
        QueryBuilder queryBuilder;

        //判断查询类型
        switch (queryType){
            case TERM_QUERY:
                queryBuilder = _termQueryTemplate(qtemplatestring);
                break;
            case MATCH_PHRASE_QUERY:
                queryBuilder = _matchQueryTemplate(qtemplatestring);
                break;
            case FUZZY_QUERY:
                queryBuilder = _fuzzyQueryTemplate(qtemplatestring);
                break;
            default:
                queryBuilder = _matchQueryTemplate(qtemplatestring);
                break;
        }

        //构造es查询请求
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(Configuration.ES_INDEX_FAQ)
                .setTypes(typeName) // 从指定的类型中进行查询
                .addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC)
                .setScroll(new TimeValue(60000))
                .setQuery(queryBuilder)
                .setSize(QUERY_HIT_SIZE);

        //执行查询
        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();

        return searchResponse;
    }

    /**
     * 查询的具体构造方法：term query
     * @param questionstring
     * @return
     */
    //TODO: 根据问题中的关键词、实体、谓语（及其近义词构造查询）
    private QueryBuilder _termQueryTemplate(String questionstring){
        QueryBuilder queryBuilder = QueryBuilders.termQuery("pattern", questionstring);
        return queryBuilder;
    }

    /**
     * 查询的具体构造方法：match phrase query
     * @param questionstring
     * @return
     */
    private QueryBuilder _matchQueryTemplate(String questionstring){
        QueryBuilder queryBuilder = QueryBuilders.matchPhraseQuery("pattern", questionstring);
        return queryBuilder;
    }

    /**
     *  模糊查询
     * @param questionstring
     * @return
     */
    private QueryBuilder _fuzzyQueryTemplate(String questionstring){
        QueryBuilder queryBuilder = QueryBuilders.fuzzyQuery("pattern", questionstring);
        return queryBuilder;
    }


}
