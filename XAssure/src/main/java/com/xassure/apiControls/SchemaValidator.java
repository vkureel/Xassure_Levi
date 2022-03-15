package com.xassure.apiControls;

import io.restassured.response.Response;
import org.hamcrest.MatcherAssert;

import static io.restassured.matcher.RestAssuredMatchers.matchesXsdInClasspath;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;


public class SchemaValidator {

    public void validate(Response response, String FilePath) {
        String json = response.getBody().asString();
        if (response.getContentType().contains("application/json")) {
            MatcherAssert.assertThat(json, matchesJsonSchemaInClasspath(FilePath));
        } else if (response.getContentType().contains("application/xml")) {
            MatcherAssert.assertThat(json, matchesXsdInClasspath(FilePath));
        } else {
            System.out.println("invalid content type");
        }
    }
}
