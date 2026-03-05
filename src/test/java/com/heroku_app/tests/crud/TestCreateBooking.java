package com.heroku_app.tests.crud;

import com.heroku_app.base.BaseTest;
import com.heroku_app.endpoints.APIConstants;
import com.heroku_app.pojos.reponse.BookingResponse;
import com.heroku_app.pojos.request.Booking;

import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import io.restassured.RestAssured;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class TestCreateBooking extends BaseTest {

    @BeforeMethod
    public void setUpCreateBookingAPI() {
       requestSpecification.basePath(APIConstants.CREATE_UPDATE_BOOKING_URL);
    }

    @Test(groups = "reg", priority = 1)
    @Owner("Nhu")
    @Description("TC#1 - Verify booking can be created with minimum valid payload (if mentioned in requirements)")
    @TmsLink("https://example.tms.local/TC-1")
    public void testCreateBookingPOST_RequiredPayloadPositive() {
        
        response = RestAssured.given(requestSpecification)
            .when()
            .body(buildBody(payloadManager.createPayloadBooking()))
            .log().all()
            .post();

        validatableResponse = response.then().log().all();
        validatableResponse.statusCode(200);
        // Can add more assertions here to verify the response body content

        BookingResponse bookingResponse = payloadManager.bookingResponseObject(response.asString());
        int bookingid = bookingResponse.getBookingid();
        assertActions.verifyStringKeyNotNull(bookingid);
        assertActions.verifyTrue(bookingid > 0, "Booking ID should be greater than 0");
        assertActions.verifyStringKey(bookingResponse.getBooking().getFirstname(), "John");
    }

    @Test(groups = "reg", priority = 1)
    @Owner("Nhu")
    @Description("TC#2 - Verify booking can be created with full valid payload")
    @TmsLink("https://example.tms.local/TC-2")
    public void testCreateBookingPOST_FullPositive() {
        Booking payload = payloadManager.createPayloadBooking();
        response = RestAssured.given(requestSpecification)
            .when()
            .body(buildBody(payload))
            .log().all()
            .post();

        validatableResponse = response.then().log().all();
        validatableResponse.statusCode(200);
        // Can add more assertions here to verify the response body content


        BookingResponse bookingResponse = payloadManager.bookingResponseObject(response.asString());
        int bookingid = bookingResponse.getBookingid();
        assertActions.verifyStringKeyNotNull(bookingid);
        assertActions.verifyTrue(bookingid > 0, "Booking ID should be greater than 0");
        assertActions.verifyStringKey(bookingResponse.getBooking().getFirstname(), "John");
    }

    //in this case, just want to demo faker data, in fact, it is the same with TC#02
    // This is not fuzzy testing, because we need to generate a lot of test data to create fuzzy testing, and we also need to have some negative test cases with invalid data to see how the system behaves
    @Test(groups = "reg", priority = 1)
    @Owner("Nhu")
    @Description("TC#3 - Verify booking can be created with random faker data ")
    @TmsLink("https://example.tms.local/TC-3")
    public void testCreateBookingPOST_FakerDataPositive() {
        Booking payload = payloadManager.createPayloadBookingFakerJS();
        response = RestAssured.given(requestSpecification)
            .when()
            .body(buildBody(payload))
            .log().all()
            .post();

        validatableResponse = response.then().log().all();
        validatableResponse.statusCode(200);
        // Can add more assertions here to verify the response body content

        BookingResponse bookingResponse = payloadManager.bookingResponseObject(response.asString());
        int bookingid = bookingResponse.getBookingid();
        assertActions.verifyStringKeyNotNull(bookingid);
        assertActions.verifyTrue(bookingid > 0, "Booking ID should be greater than 0");
        assertActions.verifyStringKey(bookingResponse.getBooking().getFirstname(), payload.getFirstname());
    }

    
    @Test(groups = "reg", priority = 2)
    @Description("TC#4 - Verify booking response schema")
    public void testCreateBookingPOST_SchemaValidation() {
        Booking payload = payloadManager.createPayloadBooking();
        response = RestAssured.given(requestSpecification)
            .when()
            .body(buildBody(payload))
            .post();

        response.then()
            .statusCode(200)
            .body(matchesJsonSchemaInClasspath("schemas/booking-response-schema.json"));
    }

    @Test(groups = "qa", priority = 3)
    @Description("TC#5 - Verify response data integrity with input data")
    public void testCreateBookingPOST_ResponseDataIntegrity() {
        Booking payload = payloadManager.createPayloadBooking();
        response = RestAssured.given(requestSpecification)
            .when()
            .body(buildBody(payload))
            .post();

        validatableResponse = response.then().log().all();
        validatableResponse.statusCode(200);

        BookingResponse bookingResponse = payloadManager.bookingResponseObject(response.asString());
        Booking booking = bookingResponse.getBooking();
        assertActions.verifyStringKeyNotNull(bookingResponse.getBookingid());
        assertActions.verifyResponseBody(booking.getFirstname(), payload.getFirstname());
        assertActions.verifyResponseBody(booking.getLastname(), payload.getLastname());
        assertActions.verifyResponseBody(booking.getTotalprice(), payload.getTotalprice());
        assertActions.verifyResponseBody(booking.getDepositpaid(), payload.getDepositpaid());
        assertActions.verifyResponseBody(booking.getBookingdates().getCheckin(), payload.getBookingdates().getCheckin());
        assertActions.verifyResponseBody(booking.getBookingdates().getCheckout(), payload.getBookingdates().getCheckout());
        assertActions.verifyResponseBody(booking.getAdditionalneeds(), payload.getAdditionalneeds());
    }


}
