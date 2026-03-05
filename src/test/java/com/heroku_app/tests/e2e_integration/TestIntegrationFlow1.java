package com.heroku_app.tests.e2e_integration;

import com.google.gson.Gson;
import com.heroku_app.base.BaseTest;
import com.heroku_app.endpoints.APIConstants;
import com.heroku_app.pojos.reponse.BookingResponse;
import com.heroku_app.pojos.request.Booking;

import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.restassured.RestAssured;
import org.testng.ITestContext;
import org.testng.annotations.Test;

public class TestIntegrationFlow1 extends BaseTest {

    @Test(priority = 1)
    @Owner("Nhu")
    @Description("E2E Step 1 - Create booking")
    public void testCreateBooking(ITestContext iTestContext) {
        requestSpecification.basePath(APIConstants.CREATE_UPDATE_BOOKING_URL);
        response = RestAssured.given(requestSpecification)
            .when()
            .body(payloadManager.createPayloadBooking())
            .post();

        validatableResponse = response.then().log().all();
        validatableResponse.statusCode(200);

        BookingResponse bookingResponse = payloadManager.bookingResponseObject(response.asString());
        iTestContext.setAttribute("bookingid", bookingResponse.getBookingid());
        assertActions.verifyStringKeyNotNull(bookingResponse.getBookingid());
    }

    @Test(priority = 2, dependsOnMethods = "testCreateBooking")
    @Description("E2E Step 2 - Verify booking exists")
    public void testVerifyBookingId(ITestContext iTestContext) {
        Integer bookingid = (Integer) iTestContext.getAttribute("bookingid");
        String basePathGET = APIConstants.CREATE_UPDATE_BOOKING_URL + "/" + bookingid;
        requestSpecification.basePath(basePathGET);

        response = RestAssured.given(requestSpecification)
            .when()
            .get();

        validatableResponse = response.then().log().all();
        validatableResponse.statusCode(200);

        Booking booking = new Gson().fromJson(response.asString(), Booking.class);
        assertActions.verifyStringKey(booking.getFirstname(), "John");
    }

    @Test(priority = 3, dependsOnMethods = "testVerifyBookingId")
    @Description("E2E Step 3 - Update booking")
    public void testUpdateBookingByID(ITestContext iTestContext) {
        Integer bookingid = (Integer) iTestContext.getAttribute("bookingid");
        String token = getToken();
        iTestContext.setAttribute("token", token);

        String basePathPUT = APIConstants.CREATE_UPDATE_BOOKING_URL + "/" + bookingid;
        requestSpecification.basePath(basePathPUT);

        response = RestAssured.given(requestSpecification)
            .cookie("token", token)
            .when()
            .body(payloadManager.fullUpdatePayload())
            .put();

        validatableResponse = response.then().log().all();
        validatableResponse.statusCode(200);
    }

    @Test(priority = 4, dependsOnMethods = "testUpdateBookingByID")
    @Description("E2E Step 4 - Delete booking")
    public void testDeleteBookingById(ITestContext iTestContext) {
        Integer bookingid = (Integer) iTestContext.getAttribute("bookingid");
        String token = (String) iTestContext.getAttribute("token");

        String basePathDELETE = APIConstants.CREATE_UPDATE_BOOKING_URL + "/" + bookingid;
        requestSpecification.basePath(basePathDELETE);

        response = RestAssured.given(requestSpecification)
            .cookie("token", token)
            .when()
            .delete();

        validatableResponse = response.then().log().all();
        validatableResponse.statusCode(201);
    }
}
