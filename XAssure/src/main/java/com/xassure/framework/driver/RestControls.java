package com.xassure.framework.driver;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.response.ResponseBodyExtractionOptions;

import java.util.Map;

public interface RestControls {

    ResponseBodyExtractionOptions GetRequestWithOauth(String url);

    ResponseBodyExtractionOptions GetRequestWithGraphQL(String url, String query);

    public ResponseBodyExtractionOptions GetRequestWithGraphQL(String url, String query, Map<String, ?> headers);

    String makeGetRequestAndGetBody(String url);

    Response makeGetRequestAndGetResponse(String url);

    int makeGetRequestAndGetStatusCode(String url);

    String makeGetRequestAndGetContentType(String url);

    String makeGetRequestAndGetHeaders(String url, String headerName);

}
