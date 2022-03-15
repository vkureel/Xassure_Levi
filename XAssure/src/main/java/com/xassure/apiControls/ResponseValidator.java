package com.xassure.apiControls;

import io.restassured.response.Response;
import org.hamcrest.Matchers;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;


public class ResponseValidator {


    public void responseStringValueCompare(String pathToField, Response response, String key, String value) {
        if (value.equalsIgnoreCase("null")) {
            response.then().
                    body((pathToField.concat(key)), is(nullValue()));
        } else {
            response.then().
                    body((pathToField.concat(key)).trim(), is(value));
        }
    }


    public void responseBooleanValueCompare(String pathToField, Response response, String key, Boolean value) {
        response.then().
                body((pathToField.concat(key)).trim(), is(value));

    }


    public void responseIntValueCompare(String pathToField, Response response, String key, int value) {
        response.then().
                body((pathToField.concat(key)), is(value));
    }


    public void responseStringValueContains(Response response, String value) {
        response.body().asString().contains(value);
    }


    public void responseNullValueCompare(String pathToField, Response response, String key) {
        response.then().
                body((pathToField.concat(key)), is(nullValue()));
    }


    public void responseEmpty(Response response) {
        response.then().
                body("isEmpty()", Matchers.is(true));
    }


    public Boolean verifySuccess(Response response) {
        int code = response.getStatusCode();
        if (code == 200) {
            return true;
        } else {
            return false;
        }


    }


}
