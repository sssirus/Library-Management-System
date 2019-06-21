package com.qa.demo.query;

import com.qa.demo.dataStructure.entityReturnedResults;
import com.qa.demo.dataStructure.predicateReturnedResults;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class SendPOSTRequest {
    /**
     * 向目的URL发送post请求
     * @param url       目的url
     * @param params    发送的参数
     * @return  ResultVO
     */
    public static predicateReturnedResults sendPredicatePostRequest(String url, MultiValueMap<String, String> params){
        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpMethod method = HttpMethod.POST;
        // 以表单的方式提交
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //将请求头部和参数合成一个请求
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        //执行HTTP请求，将返回的结构使用ResultVO类格式化
        ResponseEntity<predicateReturnedResults> response = client.exchange(url, method, requestEntity, predicateReturnedResults.class);

        return response.getBody();
    }
    public static entityReturnedResults sendEntityPostRequest(String url, MultiValueMap<String, String> params){
        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpMethod method = HttpMethod.POST;
        // 以表单的方式提交
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //将请求头部和参数合成一个请求
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        //执行HTTP请求，将返回的结构使用ResultVO类格式化
        ResponseEntity<entityReturnedResults> response = client.exchange(url, method, requestEntity, entityReturnedResults.class);

        return response.getBody();
    }
    public static predicateReturnedResults getPredicateFromFlaskServer(String question, String cadidatePredicateList) {

        String authorizeUrl = "http://0.0.0.0:6008/predict";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("question", question);

        params.add("cadidate", cadidatePredicateList);
        //发送Post数据并返回数据.
        predicateReturnedResults resultVo =sendPredicatePostRequest(authorizeUrl, params);

        return resultVo;
    }

    public static entityReturnedResults getEntityFromFlaskServer(String question) {

        String authorizeUrl = "http://0.0.0.0:6008/entity";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("question", question);


        //发送Post数据并返回数据.
        entityReturnedResults resultVo =sendEntityPostRequest(authorizeUrl, params);

        return resultVo;
    }

}
