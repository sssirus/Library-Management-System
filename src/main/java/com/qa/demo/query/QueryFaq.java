package com.qa.demo.query;

import com.qa.demo.algorithm.EditingDistance;
import com.qa.demo.conf.Configuration;
import com.qa.demo.dataStructure.Answer;
import com.qa.demo.dataStructure.DataSource;
import com.qa.demo.dataStructure.Entity;
import com.qa.demo.dataStructure.Question;
import com.qa.demo.utils.es.GetClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.SpanOrQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.qa.demo.conf.Configuration.QUERY_HIT_SIZE;

/**
 * Description: 从常见问答对FAQ中检索相似问题
 * Created by TT. Wu on 2017/9/4.
 */
public class QueryFaq implements DbqaQueryDriver {
    private static Logger LOG = LogManager.getLogger(QueryFaq.class.getName());

    /**
     * 将检索得到的“候选答案”封装至Question对象中，
     * 默认match phrase query
     * @param question
     * @return
     */
    @Override
    public Question search(Question question) {
        return search(question, QueryType.MATCH_PHRASE_QUERY);
    }

    /**
     * 将检索得到的“候选答案”封装至Question对象中
     * @param question
     * @param type
     * @return
     */
    @Override
    public Question search(Question question, QueryType type) {

        return search(question, QueryType.MATCH_PHRASE_QUERY, DataSource.FAQ);
    }

    @Override
    public Question search(Question question, QueryType type, DataSource... dataSources) {
        String questionStr = question.getQuestionString();

        if (question.getQuestionEntity().isEmpty() || question.getQuestionEntity() == null
                || question.getQuestionToken().size()==0 || question.getQuestionToken().isEmpty())
            return question;

        Iterator iterator = question.getQuestionToken().entrySet().iterator();
        //对应问题中的每一个实体，挖去实体后形成的模板各不相同;
        while (iterator.hasNext()) {
            Map.Entry<Entity, ArrayList<String>> entry =
                    (Map.Entry) iterator.next();
            ArrayList<String> tokens = entry.getValue();
            if (tokens.isEmpty() || tokens.size() == 0 || tokens == null)
                return question;
        }

        String[] typeNames = new String[dataSources.length];
        //建立数据源到索引的映射
        for(int i = 0; i < dataSources.length; i++){
            switch (dataSources[i]){
                case FAQ:
                    typeNames[i] = Configuration.ES_TYPE_FAQ;
                    break;
                case FAQ_T:
                    typeNames[i] = Configuration.ES_TYPE_FAQ_T;
                    break;
                case PATTERN:
                    typeNames[i] = Configuration.ES_TYPE_TEMPLATE_T;
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
            LOG.info("[info]候选答案集大小为："+ searchResponse.getHits().totalHits);
            //Scroll until no hits are returned
            do {
                for (SearchHit hit : searchResponse.getHits().getHits()) {
                    //Handle the hit...
//                    Evidence evidence = new Evidence();
                    Answer answer = new Answer();
//                    evidence.setEvidenceString(hit.getField("answer").toString());
                    answer.setAnswerString(hit.getSource().get("answer").toString());
//                    evidence.setEvidenceSource(DataSource.FAQ);
//                    evidence.setEvidenceScore(hit.getScore());
                    String title = hit.getSource().get("question").toString();
                    float score = EditingDistance.getRepetitiveRate(questionStr, title);
                    answer.setAnswerScore(score);
//                    evidenceList.add(evidence);
                    answer.setAnswerSource(hit.getType().toString());
                    answerList.add(answer);
                }

                searchResponse = client.prepareSearchScroll(searchResponse.getScrollId()).setScroll(new TimeValue(60000)).execute().actionGet();
            } while(searchResponse.getHits().getHits().length != 0); // Zero hits mark the end of the scroll and the while loop.

            for(SearchHit hit: searchResponse.getHits().getHits()){

            }
            //保留原有的证据
//            List<Evidence> evidences = question.getQuestionEvidence();
            List<Answer> answers = question.getCandidateAnswer();
//            evidenceList.addAll(evidences);
            answerList.addAll(answers);
            //封装
//            question.setQuestionEvidence(evidenceList);
            question.setCandidateAnswer(answerList);
            LOG.info("[info]已从FAQ中检索相关文本证据");
            return question;

        } catch (UnknownHostException e) {
            e.printStackTrace();
            LOG.error("[error]while query faq");
        }

        return question;
    }

    /**
     * 在es中检索相关问答对，并返回SearchResponse对象
     * @param question
     * @param queryType
     * @return
     */
    private SearchResponse _search(Question question, QueryType queryType, String... typeName) throws UnknownHostException {
        TransportClient client = GetClient.getTransportClient();
        QueryBuilder queryBuilder;

        //判断查询类型
        switch (queryType){
            case TERM_QUERY:
                queryBuilder = _termQuery(question);
                break;
            case MATCH_PHRASE_QUERY:
                queryBuilder = _matchQuery(question);
                break;
            case FUZZY_QUERY:
                queryBuilder = _fuzzyQuery(question);
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
                .setSize(QUERY_HIT_SIZE);

        //执行查询
        SearchResponse searchResponse = searchRequestBuilder.execute().actionGet();

        return searchResponse;
    }

    /**
     * 查询的具体构造方法：term query
     * @param question
     * @return
     */
    //TODO: 根据问题中的关键词、实体、谓语（及其近义词构造查询）
    private QueryBuilder _termQuery(Question question){
        QueryBuilder queryBuilder = QueryBuilders.termQuery("question", "");
        return queryBuilder;
    }

    /**
     * 查询的具体构造方法：match phrase query
     * @param question
     * @return
     */
    private QueryBuilder _matchQuery(Question question){
        QueryBuilder queryBuilder = QueryBuilders.matchPhraseQuery("question", question.getQuestionString());
        return queryBuilder;
    }

    /**
     *  模糊查询
     * @param question
     * @return
     */
    private QueryBuilder _fuzzyQuery(Question question){
        QueryBuilder queryBuilder = QueryBuilders.fuzzyQuery("question", question.getQuestionString());
        return queryBuilder;
    }

    private QueryBuilder _spanOrQuery(Question question){
        //TODO 根据NER得到的不同entity，分词可能有多种结果，这里只对一种分词结果进行操作;
        List<String> questionTokens = question.getQuestionToken().get(0);
        SpanOrQueryBuilder queryBuilder = QueryBuilders.spanOrQuery(
                QueryBuilders.spanTermQuery("question", questionTokens.get(0)));
        for(int i = questionTokens.size(); i > 0; i--){
            queryBuilder.addClause(QueryBuilders.spanTermQuery("question", questionTokens.get(i)));
        }
        return queryBuilder;
    }

    private QueryBuilder _commonTermsQuery(Question question){
        QueryBuilder queryBuilder = QueryBuilders.commonTermsQuery("question", question.getQuestionString());
        return queryBuilder;
    }

    //TODO 根据NER得到的不同entity，分词可能有多种结果，这里只对一种分词结果进行操作;
    private QueryBuilder _termsQuery(Question question){
        List<String> tokenList = question.getQuestionToken().get(0);
        String[] tokens = (String[])tokenList.toArray();
        QueryBuilder queryBuilder = QueryBuilders.termsQuery("question", tokens);
        return queryBuilder;
    }


}
