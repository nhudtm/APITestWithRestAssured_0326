package com.heroku_app.tests.crud;

import com.heroku_app.base.BaseTest;
import com.heroku_app.endpoints.APIConstants;

import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.restassured.RestAssured;
import org.testng.annotations.Test;

public class TestHealthCheck extends BaseTest {

    @Test(groups = "reg", priority = 1)
    @Owner("Nhu")
    @Description("TC#1 - Verify health check endpoint")
    public void testHealthCheckGET() {
        requestSpecification.basePath(APIConstants.PING_URL);
        response = RestAssured.given(requestSpecification)
            .when()
            .get();

        validatableResponse = response.then().log().all();
        validatableResponse.statusCode(201);
    }
}
