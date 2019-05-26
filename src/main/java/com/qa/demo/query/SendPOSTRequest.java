package com.qa.demo.query;

import com.qa.demo.dataStructure.ReturnedResults;
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
    public static ReturnedResults sendPostRequest(String url, MultiValueMap<String, String> params){
        RestTemplate client = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpMethod method = HttpMethod.POST;
        // 以表单的方式提交
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        //将请求头部和参数合成一个请求
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);
        //执行HTTP请求，将返回的结构使用ResultVO类格式化
        ResponseEntity<ReturnedResults> response = client.exchange(url, method, requestEntity, ReturnedResults.class);

        return response.getBody();
    }
public static ReturnedResults getPredicateFromFlaskServer(String question) {

    String authorizeUrl = "http://0.0.0.0:6006/predict";

    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("question", question);


    //发送Post数据并返回数据.
    ReturnedResults resultVo =sendPostRequest(authorizeUrl, params);

    return resultVo;
}



}
