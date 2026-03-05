package com.heroku_app.tests.crud;

import com.heroku_app.base.BaseTest;
import com.heroku_app.endpoints.APIConstants;

import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import io.restassured.RestAssured;
import org.testng.annotations.Test;

public class TestCreateToken extends BaseTest {

    @Test(groups = "reg", priority = 1)
    @Owner("Pramod")
    @Description("TC#2 - Create token and verify")
    @TmsLink("https://example.tms.local/TC-2")
    public void testTokenPOST() {
        requestSpecification.basePath(APIConstants.AUTH_URL);
        response = RestAssured.given(requestSpecification)
            .when()
            .body(payloadManager.setAuthPayload())
            .post();

        validatableResponse = response.then().log().all();
        validatableResponse.statusCode(200);

        String token = payloadManager.getTokenFromJSON(response.asString());
        assertActions.verifyStringKeyNotNull(token);
    }
}
