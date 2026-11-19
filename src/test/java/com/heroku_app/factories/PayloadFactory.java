package com.heroku_app.factories;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.github.javafaker.Faker;
import com.google.gson.Gson;
import com.heroku_app.pojos.request.Auth;
import com.heroku_app.pojos.request.Booking;
import com.heroku_app.pojos.request.Bookingdates;
import com.heroku_app.pojos.response.BookingResponse;
import com.heroku_app.pojos.response.TokenResponse;

public class PayloadFactory {

    private final Gson gson = new Gson();
    private final Faker faker = new Faker();

    public Booking createPayloadBooking(
            String firstname,
            String lastname,
            int totalprice,
            boolean depositpaid,
            String checkin,
            String checkout,
            String additionalneeds) {
        Bookingdates bookingdates = new Bookingdates();
        bookingdates.setCheckin(checkin);
        bookingdates.setCheckout(checkout);

        Booking booking = new Booking();
        booking.setFirstname(firstname);
        booking.setLastname(lastname);
        booking.setTotalprice(totalprice);
        booking.setDepositpaid(depositpaid);
        booking.setBookingdates(bookingdates);
        booking.setAdditionalneeds(additionalneeds);

        return booking;
    }
    public Booking createMinimumPayloadBooking(
            String firstname,
            String lastname,
            int totalprice,
            boolean depositpaid,
            String checkin,
            String checkout
            ) {
        Bookingdates bookingdates = new Bookingdates();
        bookingdates.setCheckin(checkin);
        bookingdates.setCheckout(checkout);

        Booking booking = new Booking();
        booking.setFirstname(firstname);
        booking.setLastname(lastname);
        booking.setTotalprice(totalprice);
        booking.setDepositpaid(depositpaid);
        booking.setBookingdates(bookingdates);
        

        return booking;
    }

    public Map<String, Object> createPayloadBookingAsMap(
            String firstname,
            String lastname,
            int totalprice,
            boolean depositpaid,
            String checkin,
            String checkout,
            String additionalneeds) {
        Map<String, Object> bookingdates = new LinkedHashMap<>();
        bookingdates.put("checkin", checkin);
        bookingdates.put("checkout", checkout);

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("firstname", firstname);
        payload.put("lastname", lastname);
        payload.put("totalprice", totalprice);
        payload.put("depositpaid", depositpaid);
        payload.put("bookingdates", bookingdates);
        payload.put("additionalneeds", additionalneeds);

        return payload;
    }

    public Map<String, Object> createBasePayloadBooking(){
        String checkinDate = java.time.LocalDate.now().plusDays(7).toString();
        String checkoutDate = java.time.LocalDate.now().plusDays(14).toString();

        Map<String, Object> bookingdates = new LinkedHashMap<>();
        bookingdates.put("checkin", checkinDate);
        bookingdates.put("checkout", checkoutDate);

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("firstname", "John");
        payload.put("lastname", "Smith");
        payload.put("totalprice", 500);
        payload.put("depositpaid", true);
        payload.put("bookingdates", bookingdates);
        payload.put("additionalneeds", "Breakfast");

        return payload;
    }

    public Map<String, Object> createPayloadBookingWithFields(String fieldPath, Object value){
        
        Map<String, Object> payload = new LinkedHashMap<>();
        String[] fields = fieldPath.split("\\.");
        Map<String, Object> currentMap = payload;

        for (int i = 0; i < fields.length - 1; i++) {
            String field = fields[i];
            Map<String, Object> nestedMap = new LinkedHashMap<>();
            currentMap.put(field, nestedMap);
            currentMap = nestedMap;
        }

        currentMap.put(fields[fields.length - 1], value);
        return payload;
    }

    // Method to create a partial payload for booking with some single field
    private static final Set<String> ALLOWED_PARTIAL_FIELDS = Set.of(
            "firstname",
            "lastname",
            "totalprice",
            "depositpaid",
            "bookingdates",
            "additionalneeds"
    );
    public Map<String, Object> createPartialPayloadBooking(Map<String, Object> fieldsToUpdate) {
       
        Map<String, Object> partialPayload = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : fieldsToUpdate.entrySet()) {
            
            String fieldName = entry.getKey();
            if (fieldName == null || !ALLOWED_PARTIAL_FIELDS.contains(fieldName)) {
                throw new IllegalArgumentException("Field '" + fieldName + "' is not allowed in the partial payload.");
            }
            partialPayload.put(fieldName, entry.getValue());

        }
        return partialPayload;
    }

    public Booking createPayloadBookingFakerJS() {
        Bookingdates bookingdates = new Bookingdates();
        bookingdates.setCheckin("2025-02-01");
        bookingdates.setCheckout("2025-02-10");

        Booking booking = new Booking();
        booking.setFirstname(faker.name().firstName());
        booking.setLastname(faker.name().lastName());
        booking.setTotalprice(faker.random().nextInt(1, 1000));
        booking.setDepositpaid(faker.random().nextBoolean());
        booking.setBookingdates(bookingdates);
        booking.setAdditionalneeds("Lunch");

        return booking;
    }

    // Method to create authentication payload as JSON string
    public Auth createAuthPayload(String username, String password) {
        Auth auth = new Auth();
        auth.setUsername(username);
        auth.setPassword(password);
        return auth;
    }

    /** Method to convert JSON response to BookingResponse object **/
    // public BookingResponse bookingResponseObject(String bookingResponseString) {
    //     return gson.fromJson(bookingResponseString, BookingResponse.class);
    // }

    // Method to extract token from JSON response
    // public String getTokenFromJSON(String tokenResponse) {
    //     TokenResponse response = gson.fromJson(tokenResponse, TokenResponse.class);
    //     return response.getToken();
    // }
}