package com.heroku_app.client;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.*;

import java.util.Map;

import com.heroku_app.endpoints.APIConstants;
import com.heroku_app.utils.BuildBodyJSON;

public class RestClient {

    private final RequestSpecification requestSpecification;

    public RestClient(RequestSpecification requestSpecification) {
        this.requestSpecification = requestSpecification;
    }

    public RequestSpecification createRequest() {
        return given().spec(requestSpecification);
    }

    public Response sendGetRequest(String url) {
        return createRequest().when().get(url);
    }

    // Post with default Content-Type and Accept headers
    public Response sendPostRequest(String url, Object payload) {
        return createRequest().when().body(BuildBodyJSON.buildBody(payload)).post(url);
    }

    // Post with custom Content-Type and Accept headers
    public Response sendPostRequest(String url, Object payload, String contentType, String accept) {
        return createRequest().contentType(contentType).accept(accept).when().body(BuildBodyJSON.buildBody(payload)).post(url);
    }

    // Post with custom Content-Type 
    public Response sendPostRequest(String url, Object payload, String contentType) {
        return createRequest().contentType(contentType).when().body(BuildBodyJSON.buildBody(payload)).post(url);
    }

    // Post without Content-Type
    public Response sendPostRequestWithoutContentType(String url, Object payload) {
        return RestAssured.given()
        .baseUri(APIConstants.BASE_URL)
        .accept("application/json")
        .when()
        .body(BuildBodyJSON.buildBody(payload))
        .post(url);
    }

    // Post with custom header
    public Response sendPostRequestWithHeaders(String url, Object payload, String headerKey, String headerValue) {
        return createRequest().header(headerKey, headerValue).when().body(BuildBodyJSON.buildBody(payload)).post(url);
    }

    // Post with raw Json String
    public Response sendPostRequestRawJson(String url, String jsonString) {
        return createRequest().when().body(jsonString).post(url);
    }

    public Response sendPutRequest(String url, Object payload, Map<String, String> cookies) {
        if (cookies != null && !cookies.isEmpty()) {
            return createRequest().cookies(cookies).when().body(BuildBodyJSON.buildBody(payload)).put(url);
        }
        return createRequest().when().body(BuildBodyJSON.buildBody(payload)).put(url);
    }

    
    public Response sendDeleteRequest(String url, Map<String, String> cookies) {
        if (cookies != null && !cookies.isEmpty()) {
            return createRequest().cookies(cookies).when().delete(url);
        }
        
        return createRequest().when().delete(url);
    }



}
