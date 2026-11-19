package com.heroku_app.tests.crud;

import org.testng.annotations.Test;

import com.heroku_app.base.BaseTest;
import com.heroku_app.endpoints.APIConstants;
import com.heroku_app.pojos.request.Booking;
import com.heroku_app.pojos.response.BookingResponse;

import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.response.Response;

public class TestCreateBookingHeaderPayload extends BaseTest {

    @Test(groups = "header_content_type", priority = 1)
    @Description("TC-HEADER-001 - Create booking with standard application/json Content-Type")
    public void createBooking_withApplicationJsonContentType_shouldSucceed() {
        Booking payload = payloadFactory.createPayloadBookingFakerJS(); // Create a valid booking payload using Faker

        // Use the existing request specification which already has Content-Type set to
        // application/json
        Response response = bookingAPI.createBooking(payload);

        assertActions.verifyResponseBody(response.getStatusCode(), 200); // Expecting 200 OK

        BookingResponse bookingResponse = response.as(BookingResponse.class);
        assertActions.verifyNotNull(bookingResponse.getBookingid());
        assertActions.verifyTrue(bookingResponse.getBookingid() > 0, "Booking ID should be greater than 0");
        
    }

    @Test(groups = "header_content_type", priority = 2)
    @Description("TC-HEADER-002 - Reject booking with invalid Content-Type")
    public void createBooking_withInvalidContentType_shouldBeRejected() {
        Booking payload = payloadFactory.createPayloadBookingFakerJS();
        // Use the existing request specification but override the Content-Type to an invalid value
        Response response = bookingAPI.createBooking(payload, "text/plain"); // Invalid Content-Type

        assertActions.verifyResponseBody(response.getStatusCode(), 418); // Expecting 418 I'm a teapot for invalid Content-Type
    }

    

    @Test(groups = "header_content_type", priority = 2)
    @Description("TC-HEADER-003 - Reject booking when Content-Type is missing")
    public void createBooking_withoutContentType_shouldBeRejected() {
        Booking payload = payloadFactory.createPayloadBookingFakerJS(); // Create a valid booking payload using Faker
        Response response = bookingAPI.createBookingWithoutContentType(payload);

        // Create a new request specification without the Content-Type header
        
        int statusCode = response.getStatusCode();
        assertActions.verifyTrue( response.getStatusCode() == 500,
                "Actual status code: " + statusCode);
    }

    @Test(groups = "header_content_type", priority = 2)
    @Description("TC-HEADER-004 - Reject booking when Accept is not application/json")
    public void createBooking_withNonJsonAccept_shouldBeRejected() {
                        Booking payload = payloadFactory.createPayloadBookingFakerJS(); // Create a valid booking payload using Faker
        // Use the existing request specification but override the Accept header to a non-JSON value
        Response response = bookingAPI.createBooking(payload, "application/json", "text/plain"); //
        assertActions.verifyTrue(response.getStatusCode() == 418, "Expected status code: 418 I'm a teapot for non-JSON Accept header");
    }

    @Test(groups = "header_content_type", priority = 3)
    @Description("TC-HEADER-005 - Create booking with an unexpected custom header")
    public void createBooking_withUnexpectedCustomHeader_shouldBeStable() {
        Booking payload = payloadFactory.createPayloadBookingFakerJS();

        // Use the existing request specification with an unexpected custom header
        Response response = bookingAPI.createBookingWithHeaders(payload, "X-Custom-Header", "UnexpectedValue");
        assertActions.verifyTrue(response.getStatusCode() == 200, "API should return 200 for unexpected header"); // Expecting 200
        
        BookingResponse bookingResponse = response.as(BookingResponse.class);
        assertActions.verifyNotNull(bookingResponse.getBookingid());
        assertActions.verifyTrue(bookingResponse.getBookingid() > 0, "Booking ID should be greater than 0");
    }

    @Test(groups = "header_content_type", priority = 3)
    @Description("TC-HEADER-006 - Verify response headers for create booking")
    public void createBooking_shouldReturnExpectedResponseHeaders() {
        Booking payload = payloadFactory.createPayloadBookingFakerJS();
        Response response = bookingAPI.createBooking(payload);
        assertActions.verifyTrue(response.getStatusCode() == 200, "Expected status code: 200"   ); // Expecting 200     
        assertActions.verifyContentType(response, "application/json");

          BookingResponse bookingResponse = response.as(BookingResponse.class);
        assertActions.verifyNotNull(bookingResponse.getBookingid());
    }

    @Test(groups = "header_content_type", priority = 2)
    @Description("TC-HEADER-007 - Verify API behavior when Accept header is missing")
    public void createBooking_withoutAcceptHeader_shouldBeHandled() {
        Booking payload = payloadFactory.createPayloadBookingFakerJS();
        Response response = RestAssured.given()
                .baseUri(APIConstants.BASE_URL)
                .contentType("application/json")
                .when()
                .body(payload)
                .post(APIConstants.CREATE_UPDATE_BOOKING_URL);

        assertActions.verifyTrue(
                response.getStatusCode() == 200 || response.getStatusCode() == 406,
                "Expected status code 200 or 406, but was: " + response.getStatusCode());
    }

    @Test(groups = "http_method", priority = 3)
    @Description("TC-HTTP-001 - Reject PUT on the create-booking endpoint")
    public void createBookingEndpoint_withPutMethod_shouldBeRejected() {
        Booking payload = payloadFactory.createPayloadBookingFakerJS();

        // Attempt to send a PUT request to the create booking endpoint
        Response response = bookingAPI.createBookingWithPutMethod(payload); // Assuming you have a method to send PUT requests

        assertActions.verifyTrue(response.getStatusCode() == 405, "Expected status code: 405"); // Expecting 405 Method Not Allowed
        
    }

    @Test(groups = "http_method", priority = 3)
    @Description("TC-HTTP-002 - Reject PATCH on the create-booking endpoint")
    public void createBookingEndpoint_withPatchMethod_shouldBeRejected() {
        Booking payload = payloadFactory.createPayloadBookingFakerJS();
        Response response = RestAssured.given()
                .baseUri(APIConstants.BASE_URL)
                .contentType("application/json")
                .accept("application/json")
                .body(payload)
                .patch(APIConstants.CREATE_UPDATE_BOOKING_URL);

        assertActions.verifyTrue(
                response.getStatusCode() == 405,
                "Expected status code 405, but was: " + response.getStatusCode());
    }

    @Test(groups = "http_method", priority = 3)
    @Description("TC-HTTP-003 - Reject DELETE on the create-booking collection endpoint")
    public void createBookingEndpoint_withDeleteMethod_shouldBeRejected() {
        Response response = RestAssured.given()
                .baseUri(APIConstants.BASE_URL)
                .accept("application/json")
                .delete(APIConstants.CREATE_UPDATE_BOOKING_URL);

        assertActions.verifyTrue(
                response.getStatusCode() == 405,
                "Expected status code 405, but was: " + response.getStatusCode());
    }

    @Test(groups = "payload_integrity", priority = 4)
    @Description("TC-PAYLOAD-001 - Reject an empty payload")
    public void createBooking_withEmptyPayload_shouldBeRejected() {
        Booking payload = new Booking();

        Response response = bookingAPI.createBooking(payload);
        assertActions.verifyTrue(response.getStatusCode() == 500, "Expected status code: 500"); // Expecting 500 Internal Server Error for empty payload
    }

    @Test(groups = "payload_integrity", priority = 4)
    @Description("TC-PAYLOAD-002 - Reject malformed JSON")
    public void createBooking_withMalformedJson_shouldBeRejected() {
        String malformedJson = "{ \"firstname\": \"John\", \"lastname\": \"Doe\" "; // Missing closing brace

        Response response = bookingAPI.createBookingWithRawJson(malformedJson);
        assertActions.verifyTrue(response.getStatusCode() == 400, "Expected status code: 400 for malformed JSON payload"); // Expecting 400 Bad Request for malformed JSON
    }

    @Test(groups = "payload_integrity", priority = 4)
    @Description("TC-PAYLOAD-003 - Reject JSON with wrong field structure")
    public void createBooking_withWrongJsonStructure_shouldBeRejected() {
        String wrongJsonStructure = "{ \"name\": \"John Doe\", \"price\": 100 }"; // Wrong field names
        Response response = bookingAPI.createBookingWithRawJson(wrongJsonStructure);
        assertActions.verifyTrue(response.getStatusCode() == 500, "Expected status code: 500 for wrong JSON structure"); // Expecting 500 Internal Server Error for wrong JSON structure
    }

    @Test(groups = "payload_integrity", priority = 4)
    @Description("TC-PAYLOAD-004 - Reject JSON with unexpected fields")
    public void createBooking_withExtraUnexpectedFields_shouldBeRejected() {
        String payloadWithExtraFields = "{"
                + "\"firstname\": \"Sally\","
                + "\"lastname\": \"Brown\","
                + "\"totalprice\": 111,"
                + "\"depositpaid\": true,"
                + "\"bookingdates\": {"
                + "\"checkin\": \"2013-02-23\","
                + "\"checkout\": \"2014-10-23\""
                + "},"
                + "\"additionalneeds\": \"Breakfast\","
                + "\"extrafield\": \"unexpected\""
                + "}";
        Response response = bookingAPI.createBookingWithRawJson(payloadWithExtraFields);
        assertActions.verifyTrue(response.getStatusCode() == 500, "Expected status code: 500 for extra unexpected fields"); // Expecting 500 Internal Server Error for extra unexpected fields
    }

    @Test(groups = "payload_integrity", priority = 4)
    @Description("TC-PAYLOAD-005 - Reject a JSON array instead of a booking object")
    public void createBooking_withJsonArray_shouldBeRejected() {
        String payloadWithArray = "["
                + "{\"firstname\":\"Sally\",\"lastname\":\"Brown\"},"
                + "{\"firstname\":\"John\",\"lastname\":\"Doe\"}"
                + "]";

        Response response = bookingAPI.createBookingWithRawJson(payloadWithArray);
        assertActions.verifyTrue(
                response.getStatusCode() != 200,
                "A JSON array should not be accepted as a booking payload");
    }

    @Test(groups = "payload_integrity", priority = 4)
    @Description("TC-PAYLOAD-006 - Reject JSON null payload")
    public void createBooking_withNullJsonPayload_shouldBeRejected() {
        Response response = bookingAPI.createBookingWithRawJson("null");
        assertActions.verifyTrue(
                response.getStatusCode() != 200,
                "A null JSON payload should not be accepted as a booking");
    }

    @Test(groups = "security_input", priority = 4)
    @Description("TC-SECURITY-001 - Verify handling of SQL and XSS-like strings")
    public void createBooking_withSqlAndXssStrings_shouldBeHandledSafely() {
        Booking payload = payloadFactory.createPayloadBookingFakerJS(); // Create a valid booking payload using Faker
        payload.setFirstname("' OR '1'='1' -- <script>alert('xss')</script>");
        payload.setLastname("Robert'); DROP TABLE booking; --");
        payload.setAdditionalneeds("<img src=x onerror=alert('xss')>");

        Response response = bookingAPI.createBooking(payload);
        int statusCode = response.getStatusCode();

       
        assertActions.verifyTrue(statusCode == 200 || statusCode == 400,
                "Expected 200 or 400 for SQL/XSS-like input handling, but was: " + statusCode);

        if (statusCode == 200) {
            BookingResponse bookingResponse = response.as(BookingResponse.class);
            assertActions.verifyNotNull(bookingResponse.getBookingid());
        }
    }
}
