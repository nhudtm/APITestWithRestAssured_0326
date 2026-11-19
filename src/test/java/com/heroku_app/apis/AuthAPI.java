package com.heroku_app.apis;

import com.heroku_app.client.RestClient;
import com.heroku_app.endpoints.APIConstants;
import com.heroku_app.factories.PayloadFactory;
import com.heroku_app.pojos.request.Auth;
import com.heroku_app.pojos.response.TokenResponse;

import io.restassured.response.Response;

public class AuthAPI {
    private final RestClient restclient;
    private final PayloadFactory payloadFactory;

    public AuthAPI(RestClient restclient, PayloadFactory payloadFactory) {
        this.restclient = restclient;
        this.payloadFactory = payloadFactory;
    }      
    
    public Response getTokenRequest(String username, String password) {
        Auth authPayload = payloadFactory.createAuthPayload(username, password);
        return restclient.sendPostRequest(APIConstants.AUTH_URL, authPayload);
    }

    public String getToken(String username, String password) {
        
        // 1. Create the authentication payload and send the POST request to get the token
        Response response = getTokenRequest(username, password);

        // 2. Validate authen response
        if (response.getStatusCode() != 200) {
            throw new RuntimeException("Failed to authenticate. Status code: " + response.getStatusCode());
        }

        // 3. Deserialize response
        TokenResponse tokenResponse = response.as(TokenResponse.class);

        // 4. Return the token
        return tokenResponse.getToken();
    }
}
