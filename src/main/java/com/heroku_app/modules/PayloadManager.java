package com.heroku_app.modules;

import com.github.javafaker.Faker;
import com.google.gson.Gson;
import com.heroku_app.pojos.reponse.BookingResponse;
import com.heroku_app.pojos.reponse.TokenResponse;
import com.heroku_app.pojos.request.Auth;
import com.heroku_app.pojos.request.Booking;
import com.heroku_app.pojos.request.Bookingdates;

public class PayloadManager {

    private final Gson gson = new Gson();
    private final Faker faker = new Faker();

    public Booking createPayloadBooking() {
        Booking booking = new Booking();
        booking.setFirstname("John");
        booking.setLastname("Henry");
        booking.setTotalprice(112);
        booking.setDepositpaid(true);

        Bookingdates bookingdates = new Bookingdates();
        bookingdates.setCheckin("2025-02-01");
        bookingdates.setCheckout("2025-02-10");
        booking.setBookingdates(bookingdates);
        booking.setAdditionalneeds("Breakfast");

        return booking;
    }

    public Booking createPayloadBookingFakerJS() {
        Booking booking = new Booking();
        booking.setFirstname(faker.name().firstName());
        booking.setLastname(faker.name().lastName());
        booking.setTotalprice(faker.random().nextInt(1, 1000));
        booking.setDepositpaid(faker.random().nextBoolean());

        Bookingdates bookingdates = new Bookingdates();
        bookingdates.setCheckin("2025-02-01");
        bookingdates.setCheckout("2025-02-10");
        booking.setBookingdates(bookingdates);
        booking.setAdditionalneeds("Lunch");

        return booking;
    }

    public Booking createPayloadBookingWrongBody() {
        Booking booking = new Booking();
        booking.setFirstname("Lazy: Liza");
        booking.setLastname("Test");
        booking.setTotalprice(112);
        booking.setDepositpaid(true);

        Bookingdates bookingdates = new Bookingdates();
        bookingdates.setCheckin("5025-02-01");
        bookingdates.setCheckout("5025-02-10");
        booking.setBookingdates(bookingdates);
        booking.setAdditionalneeds("Breakfast");

        return booking;
    }

    public Booking fullUpdatePayload() {
        Booking booking = new Booking();
        booking.setFirstname("UpdatedFirstName");
        booking.setLastname("UpdatedLastName");
        booking.setTotalprice(500);
        booking.setDepositpaid(false);

        Bookingdates bookingdates = new Bookingdates();
        bookingdates.setCheckin("2025-03-01");
        bookingdates.setCheckout("2025-03-15");
        booking.setBookingdates(bookingdates);
        booking.setAdditionalneeds("Dinner");

        return booking;
    }

    // Method to create authentication payload as JSON string
    public String setAuthPayload() {
        Auth auth = new Auth();
        auth.setUsername("admin");
        auth.setPassword("password123");
        return gson.toJson(auth);
    }

    /**Method to convert JSON response to BookingResponse object**/
    public BookingResponse bookingResponseObject(String bookingResponseString) {
        return gson.fromJson(bookingResponseString, BookingResponse.class);
    }

    // Method to extract token from JSON response
    public String getTokenFromJSON(String tokenResponse) {
        TokenResponse response = gson.fromJson(tokenResponse, TokenResponse.class);
        return response.getToken();
    }
}