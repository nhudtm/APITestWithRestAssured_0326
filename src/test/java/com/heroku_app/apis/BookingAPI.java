package com.heroku_app.apis;

import java.util.HashMap;
import java.util.Map;

import com.heroku_app.client.RestClient;
import com.heroku_app.endpoints.APIConstants;
import com.heroku_app.pojos.request.Booking;

import io.restassured.response.Response;

public class BookingAPI {

    private final RestClient restClient;

    public BookingAPI(RestClient restClient) {
        this.restClient = restClient;
    }

    // GET /booking
    public Response getAllBookingIds() {
        return restClient.sendGetRequest(APIConstants.GET_BOOKING_IDS);
    }

    // GET /booking/{id}
    public Response getBookingById(int bookingId) {
        String url = APIConstants.GET_BOOKING_IDS + "/" + bookingId;
        return restClient.sendGetRequest(url);
    }
    // POST /booking with default Content-Type and Accept headers
    public Response createBooking(Object payload) {
        return restClient.sendPostRequest(APIConstants.CREATE_UPDATE_BOOKING_URL, payload);
    }

    // POST /booking with custom Content-Type and Accept headers
    public Response createBooking(Object payload, String contentType, String accept) {
        return restClient.sendPostRequest(APIConstants.CREATE_UPDATE_BOOKING_URL, payload, contentType, accept);
    }
    
    // POST /booking with custom Content-Type
    public Response createBooking(Object payload, String contentType) {
        return restClient.sendPostRequest(APIConstants.CREATE_UPDATE_BOOKING_URL, payload, contentType);
    }

    // POST /booking without Content-Type
    public Response createBookingWithoutContentType(Object payload) {
        return restClient.sendPostRequestWithoutContentType(APIConstants.CREATE_UPDATE_BOOKING_URL, payload);
    }

    // POST /booking with custom header
    public Response createBookingWithHeaders(Object payload, String headerKey, String headerValue) {
        return restClient.sendPostRequestWithHeaders(APIConstants.CREATE_UPDATE_BOOKING_URL, payload, headerKey, headerValue);
    }

    // POST /booking with raw Json String
    public Response createBookingWithRawJson(String jsonString) {
        return restClient.sendPostRequestRawJson(APIConstants.CREATE_UPDATE_BOOKING_URL, jsonString);
    }

    // PUT /booking/{id} 
    // Need Cookies for authentication, so we will pass the token as a parameter
    public Response updateBooking(int bookingId, Object payload, String token) {
        String url = APIConstants.CREATE_UPDATE_BOOKING_URL + "/" + bookingId;
        return restClient.sendPutRequest(url, payload, createAuthCookies(token));
    }

    // DELETE /booking/{id}
    // Need Cookies for authentication, so we will pass the token as a parameter
    public Response deleteBooking(int bookingId, String token) {
        String url = APIConstants.CREATE_UPDATE_BOOKING_URL + "/" + bookingId;
        return restClient.sendDeleteRequest(url, createAuthCookies (token));
    }

    private Map<String, String> createAuthCookies(String token) {
        if (token == null || token.isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }

        Map <String, String> cookies = new HashMap<>();
        cookies.put("token", token);
        return cookies;
    }

    public Response createBookingWithPutMethod(Booking payload) {
        String url = APIConstants.CREATE_UPDATE_BOOKING_URL;
        return restClient.sendPutRequest(url, payload, null);
    }
}
