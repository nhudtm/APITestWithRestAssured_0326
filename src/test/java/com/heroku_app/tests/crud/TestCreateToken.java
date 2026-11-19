package com.heroku_app.tests.crud;

import com.heroku_app.base.BaseTest;
import com.heroku_app.config.PropertiesConfig;
import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import io.restassured.response.Response;

import org.testng.annotations.Test;

public class TestCreateToken extends BaseTest {

    @Test(groups = "reg", priority = 1)
    @Owner("Pramod")
    @Description("TC#2 - Create token and verify")
    @TmsLink("https://example.tms.local/TC-2")
    public void testTokenPOST() {
        // 1. Create the authentication payload using the PayloadFactory
        String username = PropertiesConfig.getRequiredProperty("username");
        String password = PropertiesConfig.getRequiredProperty("password");
        Response response = authAPI.getTokenRequest(username, password);
        assertActions.verifyResponseBody(response.getStatusCode(), 200);

        String token = authAPI.getToken(username, password);
        assertActions.verifyStringKeyNotNull(token);
    }
}
