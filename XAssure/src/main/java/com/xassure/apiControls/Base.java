package com.xassure.apiControls;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xassure.utilities.XMLtoJson;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

import static io.restassured.RestAssured.given;


public class Base {

    static Future<Response> retInt;
    static Map<Future<Response>, Long> l = new HashMap<Future<Response>, Long>();
    Response a;
    private HTTP_METHOD httpMethod;
    private RequestSpecBuilder specBuilder = new RequestSpecBuilder();

    public enum HTTP_METHOD {POST, GET, PUT, DELETE, PATCH}

    public RequestSpecBuilder getSpecBuilder() {

        return specBuilder;
    }

    public HTTP_METHOD getHttpMethod() {
        return httpMethod;
    }

    protected void setHttpMethod(HTTP_METHOD method) {
        httpMethod = method;
    }


    public Response execute() {
        RequestSpecification specification = specBuilder.build();
        switch (httpMethod) {
            case POST:
                return given().spec(specification).when().post();
            case GET:
                return given().spec(specification).when().get();
            case PUT:
                return given().spec(specification).when().put();
            case DELETE:
                return given().spec(specification).when().delete();
            case PATCH:
                return given().spec(specification).when().patch();
            default:
                return null;
        }
    }


    public String getBaseUri(Boolean isHttpsRequired, String host, String port) {
        String completeUri;
        if (isHttpsRequired) {
            completeUri = "https://".concat(host);
        } else {
            completeUri = "http://".concat(host);
        }
        if (!(port.equals(null) | port.equalsIgnoreCase("null"))) {
            return completeUri.concat(":").concat(port);
        }

        return completeUri;
    }


    public Map<Future<Response>, Long> scalabilityChk(int threadCount, long timeInSec, final RequestSpecBuilder requestSpecBuilder) throws ExecutionException, InterruptedException {
        Long timeInMiliSeconds = timeInSec * 1000;
        long time = System.currentTimeMillis();
        final long executionTime = time + timeInMiliSeconds;
        final ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        while (System.currentTimeMillis() < executionTime) {
            Long inTime = System.currentTimeMillis();
            retInt = executor.submit(new Callable<Response>() {
                public synchronized Response call() throws Exception {
                    a = execute();
                    return a;
                }
            });
            l.put(retInt, inTime);
        }
        System.out.println("Finished all threads");
        Map<Future<Response>, Long> lnew = new HashMap<Future<Response>, Long>();
        for (Map.Entry<Future<Response>, Long> entry : l.entrySet()) {
            try {
                if (entry.getKey().isDone())
                    lnew.put(entry.getKey(), entry.getValue());
                else entry.getKey().cancel(true);

            } catch (Exception e) {
                System.out.println("Interrupted");
            }
        }
        executor.shutdownNow();
        return lnew;
    }


    public <E> E deserialize(Response response, Class<E> cls) throws IOException {
        String json;
        if (response.getContentType().contains("application/xml")) {
            XMLtoJson xmLtoJson = new XMLtoJson();
            json = xmLtoJson.convert(response.asString());
        } else {
            json = response.asString();
        }
        ObjectMapper objectMapper = new ObjectMapper();
        E e = objectMapper.readValue(json, cls);
        return e;
    }

}

