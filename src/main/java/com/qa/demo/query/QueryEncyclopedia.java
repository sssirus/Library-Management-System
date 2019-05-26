package com.qa.demo.query;

import com.qa.demo.algorithm.EditingDistance;
import com.qa.demo.conf.Configuration;
import com.qa.demo.dataStructure.Answer;
import com.qa.demo.dataStructure.DataSource;
import com.qa.demo.dataStructure.Evidence;
import com.qa.demo.dataStructure.Question;
import com.qa.demo.questionAnalysis.QuestionAnalysisDriver;
import com.qa.demo.questionAnalysis.QuestionAnalysisDriverImpl;
import com.qa.demo.utils.es.GetClient;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.List;

/**
 * Description:
 * Created by TT. Wu on 2017/10/10.
 */
public class QueryEncyclopedia implements DbqaQueryDriver{
    private static final Logger logger = LoggerFactory.getLogger(QueryEncyclopedia.class.getName());
    @Override
    public Question search(Question question) throws UnknownHostException {
        return search(question, QueryType.MATCH_PHRASE_QUERY);
    }

    @Override
    public Question search(Question question, QueryType type) {
        return search(question, QueryType.MATCH_PHRASE_QUERY, DataSource.ENCYCLOPEDIA);
    }

    @Override
    public Question search(Question question, QueryType type, DataSource... dataSources) {
        String questionStr = question.getQuestionString();
        String[] typeNames = new String[dataSources.length];

        //建立数据源到索引的映射
        for(int i = 0; i < dataSources.length; i++){
            switch (dataSources[i]){
                case ENCYCLOPEDIA:
                    typeNames[i] = Configuration.ES_TYPE_ENCYCLOPEDIA;
                    break;
            }
        }

        try {
            //获取查询内容
            SearchResponse searchResponse = this._search(question, type, typeNames);
            TransportClient client = GetClient.getTransportClient();
            //解析
//            List<Evidence> evidenceList = new ArrayList<>();
            List<Answer> answerList = new ArrayList<>();
            logger.info("[info]候选答案集大小为："+ searchResponse.getHits().totalHits);

            //Scroll until no hits are returned
            do {
                for (SearchHit hit : searchResponse.getHits().getHits()) {
                    //Handle the hit...
//                    Evidence evidence = new Evidence();
                    Answer answer = new Answer();
//                evidence.setEvidenceString(hit.getField("content").toString());

                    StringBuffer sb = new StringBuffer();
                    String[] strs = hit.getSource().get("content").toString().split("。");
                    for(int x = 0; x<strs.length && x<3; x++){
                        sb.append(strs[x]+"。");
                    }
                    String answerString = sb.toString();
                    answerString = answerString.substring(1);

                    String title = hit.getSource().get("title").toString();
                    title = title.substring(1, title.length()-1);

                    if(!title.equalsIgnoreCase(question.getQuestionString()))
                        continue;
//                    {
//                        answerString = "对不起，“" + question.getQuestionString()
//                                + "”这个问题我暂时没学会，" + "需要了解“" + title
//                                + "”的相关信息吗？\n“"
//                                + title + "”是：" + answerString;
//                    }

                    answer.setAnswerString(answerString);
//                evidence.setEvidenceSource(DataSource.FAQ);
//                evidence.setEvidenceScore(hit.getScore());

                    float score = EditingDistance.getRepetitiveRate(questionStr, title);
                    answer.setAnswerScore(score);
//                evidenceList.add(evidence);
                    answer.setAnswerSource(hit.getType().toString());
                    answerList.add(answer);
//                    System.out.println(answer.getAnswerScore()+": "+title+": "+answer.getAnswerString());
                }

                searchResponse = client.prepareSearchScroll(searchResponse.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
            } while(searchResponse.getHits().getHits().length != 0); // Zero hits mark the end of the scroll and the while loop.

            //保留原有的证据
//            List<Evidence> evidences = question.getQuestionEvidence();
            List<Answer> answers = question.getCandidateAnswer();
//            evidenceList.addAll(evidences);
            answerList.addAll(answers);
            //封装
//            question.setQuestionEvidence(evidenceList);
            question.setCandidateAnswer(answerList);
            logger.info("[info]已从FAQ中检索相关文本证据");
            return question;

        } catch (UnknownHostException e) {
            e.printStackTrace();
            logger.error("[error]while query faq");
        }

        return question;
    }

    private SearchResponse _search(Question question, QueryType queryType, String... typeName) throws UnknownHostException {
        TransportClient client = GetClient.getTransportClient();
        QueryBuilder queryBuilder;

        //判断查询类型
        switch (queryType){
            case TERM_QUERY:
                queryBuilder = _termQuery(question);
                break;
            default:
                queryBuilder = _matchQuery(question);
                break;
        }

        //构造es查询请求
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(Configuration.ES_INDEX_FAQ)
                .setTypes(typeName) // 从指定的类型中进行查询
                .addSort(FieldSortBuilder.DOC_FIELD_NAME, SortOrder.ASC)
                .setScroll(new TimeValue(60000))
                .setQuery(queryBuilder)
                .setSize(Configuration.QUERY_HIT_SIZE);

        //执行查询
        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();

        return searchResponse;
    }

    private QueryBuilder _termQuery(Question question){
        QueryBuilder queryBuilder = QueryBuilders.termQuery("title", question.getQuestionString());
        return queryBuilder;
    }

    private QueryBuilder _matchQuery(Question question){
        QueryBuilder queryBuilder = QueryBuilders.matchPhraseQuery("title", question.getQuestionString());
        return queryBuilder;
    }
}
