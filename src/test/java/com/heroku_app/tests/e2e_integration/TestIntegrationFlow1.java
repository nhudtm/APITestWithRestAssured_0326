package com.heroku_app.tests.e2e_integration;

import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.heroku_app.base.BaseTest;
import com.heroku_app.pojos.request.Booking;
import com.heroku_app.pojos.response.BookingResponse;

import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.restassured.response.Response;

public class TestIntegrationFlow1 extends BaseTest {

    @Test
    @Owner("Nhu")
    @Description("E2E Step 1 - Create booking")
    public void testCreateBooking(ITestContext iTestContext) {
        Booking payload = payloadFactory.createPayloadBooking("John", "Doe", 150, true, "2023-01-01", "2023-01-10", "Breakfast");   
        Response response = bookingAPI.createBooking(payload);

        assertActions.verifyResponseBody(response.getStatusCode(), 200);
       
        BookingResponse bookingResponse = response.as(BookingResponse.class);
        iTestContext.setAttribute("bookingid", bookingResponse.getBookingid());
        assertActions.verifyNotNull(bookingResponse.getBookingid());
    }

    @Test( dependsOnMethods = "testCreateBooking")
    @Description("E2E Step 2 - Verify booking exists")
    public void testVerifyBookingId(ITestContext iTestContext) {
        Integer bookingid = (Integer) iTestContext.getAttribute("bookingid");
        Response response = bookingAPI.getBookingById(bookingid);

        assertActions.verifyResponseBody(response.getStatusCode(), 200);

        Booking booking = response.as(Booking.class);
        assertActions.verifyStringEquals(booking.getFirstname(), "John");
    }

    @Test( dependsOnMethods = "testVerifyBookingId")
    @Description("E2E Step 3 - Update booking")
    public void testUpdateBookingByID(ITestContext iTestContext) {
        Integer bookingid = (Integer) iTestContext.getAttribute("bookingid");
        String token = getToken();
        iTestContext.setAttribute("token", token);

        Booking payload = payloadFactory.createPayloadBooking("Jane", "Doe", 200, false, "2023-01-01", "2023-01-10", "Lunch");
        Response response = bookingAPI.updateBooking(bookingid, payload, token);

        assertActions.verifyResponseBody(response.getStatusCode(), 200);

        Booking updatedBooking = response.as(Booking.class);
        assertActions.verifyStringEquals(updatedBooking.getFirstname(), "Jane");
    }

    @Test( dependsOnMethods = "testUpdateBookingByID")
    @Description("E2E Step 4 - Delete booking")
    public void testDeleteBookingById(ITestContext iTestContext) {
        Integer bookingid = (Integer) iTestContext.getAttribute("bookingid");
        String token = (String) iTestContext.getAttribute("token");

        Response response = bookingAPI.deleteBooking(bookingid, token);
        assertActions.verifyResponseBody(response.getStatusCode(), 201);

        // Verify the booking is deleted
        Response getDeletedBookingResponse = bookingAPI.getBookingById(bookingid);
        assertActions.verifyResponseBody(getDeletedBookingResponse.getStatusCode(), 404);
    }
}
