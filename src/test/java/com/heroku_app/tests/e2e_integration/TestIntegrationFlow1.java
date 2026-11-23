package com.heroku_app.tests.e2e_integration;

import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.heroku_app.base.BaseTest;
import com.heroku_app.pojos.request.Booking;
import com.heroku_app.pojos.response.BookingResponse;

import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.restassured.response.Response;

public class TestIntegrationFlow1 extends BaseTest {
    private Integer bookingId;
    private Booking bookingPayload;

    @Test
    @Owner("Nhu")
    @Description("E2E Step 1 - Create booking")
    public void testCreateBooking() {
         bookingPayload = payloadFactory.createPayloadBookingFakerJS();
        Response response = bookingAPI.createBooking(bookingPayload);

        assertActions.verifyResponseBody(response.getStatusCode(), 200);
       
        BookingResponse bookingResponse = response.as(BookingResponse.class);
        Integer id = bookingResponse.getBookingid();
        assertActions.verifyTrue(id > 0, "Booking ID should be greater than 0");
        assertActions.verifyNotNull(id);
        bookingId = id;
    }

    @Test(dependsOnMethods = "testCreateBooking")
    @Description("E2E Step 2 - Verify booking exists")
    public void testVerifyBookingId() {
        Response response = bookingAPI.getBookingById(bookingId);
        assertActions.verifyResponseBody(response.getStatusCode(), 200);

        Booking booking = response.as(Booking.class);
        assertActions.verifyStringEquals(booking.getFirstname(), bookingPayload.getFirstname());
        assertActions.verifyStringEquals(booking.getLastname(), bookingPayload.getLastname());
        assertActions.verifyIntEquals(booking.getTotalprice(), bookingPayload.getTotalprice());
        assertActions.verifyStringEquals(booking.getBookingdates().getCheckin(), bookingPayload.getBookingdates().getCheckin());
        assertActions.verifyStringEquals(booking.getBookingdates().getCheckout(), bookingPayload.getBookingdates().getCheckout());
    }

    @Test( dependsOnMethods = "testVerifyBookingId")
    @Description("E2E Step 3 - Update booking")
    public void testUpdateBookingByID() {
        getAuthToken();
        Booking updatePayload = payloadFactory.createPayloadBookingFakerJS();
        Response response = bookingAPI.updateBooking(bookingId, updatePayload, token);

        assertActions.verifyResponseBody(response.getStatusCode(), 200);

        Booking updatedBooking = bookingAPI.getBookingById(bookingId).as(Booking.class);
        assertActions.verifyStringEquals(updatedBooking.getFirstname(), updatePayload.getFirstname());
        assertActions.verifyStringEquals(updatedBooking.getLastname(), updatePayload.getLastname());
    }

    @Test( dependsOnMethods = "testUpdateBookingByID")
    @Description("E2E Step 4 - Delete booking")
    public void testDeleteBookingById() {
        
        Response response = bookingAPI.deleteBooking(bookingId, token);
        assertActions.verifyResponseBody(response.getStatusCode(), 201);

        // Verify the booking is deleted
        Response getDeletedBookingResponse = bookingAPI.getBookingById(bookingId);
        assertActions.verifyResponseBody(getDeletedBookingResponse.getStatusCode(), 404);
    }

    @AfterClass (alwaysRun = true)
    public void cleanUpBooking() {
        // Clean up resources if necessary
        if (bookingId !=null && token != null) {
            Response response = bookingAPI.deleteBooking(bookingId, token);
            if (response.getStatusCode() != 201) {
                System.out.println("Failed to delete booking with ID: " + bookingId);
            }
        }
    }
}
