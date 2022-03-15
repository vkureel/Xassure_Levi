package com.xassure.apiControls;

import io.restassured.response.Response;
import org.testng.Assert;



public class StatusCodeValidator {

    public static void validate200(Response response){Assert.assertEquals(response.getStatusCode(),200); }

    public static void validate201(Response response){
        Assert.assertEquals(response.getStatusCode(),201);
    }

    public static void validate204(Response response){
        Assert.assertEquals(response.getStatusCode(),204);
    }

    public static void validate401(Response response){
        Assert.assertEquals(response.getStatusCode(),401);
    }

    public static void validate409(Response response){
        Assert.assertEquals(response.getStatusCode(),409);
    }

    public static void validate500(Response response){
        Assert.assertEquals(response.getStatusCode(),500);
    }

    public static void validate400(Response response){
        Assert.assertEquals(response.getStatusCode(),400);
    }

    public static void validate404(Response response){
        Assert.assertEquals(response.getStatusCode(),404);
    }



}