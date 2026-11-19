package com.heroku_app.spec;

import com.heroku_app.endpoints.APIConstants;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

public class RequestSpecFactory {

    public static RequestSpecification createDefaultSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(APIConstants.BASE_URL)
                .setContentType(ContentType.JSON)
                .setAccept("application/json")
                .build()
                .log()
                .all();
    }
    
}
