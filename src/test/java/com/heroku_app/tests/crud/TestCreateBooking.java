package com.heroku_app.tests.crud;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import java.util.Map;

import org.joda.time.LocalDate;
import org.testng.annotations.Test;

import com.heroku_app.base.BaseTest;
import com.heroku_app.dataproviders.BookingDataProviders;
import com.heroku_app.pojos.request.Booking;
import com.heroku_app.pojos.response.BookingResponse;

import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import io.restassured.response.Response;

public class TestCreateBooking extends BaseTest {
        @Test(groups = "smoke", priority = 1)
        @Owner("Nhu")
        @Description("TC-BOOKING-001 - Create booking with minimum valid payload")
        @TmsLink("https://example.tms.local/TC-BOOKING-001")
        public void createBooking_withMinimumValidPayload_shouldSucceed() {
                Map<String, Object> booking = payloadFactory.createBasePayloadBooking();
                booking.remove("additionalneeds"); // Remove the optional field to create a minimum payload

                // 1. POST: verify what server returns
                Response postResponse = bookingAPI.createBooking(booking);
                assertActions.verifyResponseBody(postResponse.getStatusCode(), 200);

                Map<String, Object> createBody = postResponse.as(Map.class);
                System.out.println("Create Booking Response Body: " + createBody);
                Map<String, Object> bookingData = (Map<String, Object>) createBody.get("booking");
                assertActions.verifyTrue(!bookingData.containsKey("additionalneeds"),
                                "Additional needs should be present in the response for minimum payload");

                int bookingId = ((Number) createBody.get("bookingid")).intValue();
                assertActions.verifyTrue(bookingId > 0, "Booking ID should be greater than 0");

                // 2. GET: verify the booking data is correctly stored in the system
                Response getResponse = bookingAPI.getBookingById(bookingId);
                assertActions.verifyTrue(getResponse.getStatusCode() == 200,
                                "GET request should return status code 200");

                Map<String, Object> getBody = getResponse.as(Map.class);
                System.out.println("Get Booking Response Body: " + getBody);

                assertActions.verifyTrue(!getBody.containsKey("additionalneeds"),
                                "Additional needs should not be present in the retrieved booking data for minimum payload");
                assertActions.verifyTrue(booking.get("firstname").equals(getBody.get("firstname")),
                                "First name should match");
                assertActions.verifyTrue(booking.get("lastname").equals(getBody.get("lastname")),
                                "Last name should match");

        }

        @Test(dataProvider = "validBookingData", dataProviderClass = BookingDataProviders.class, groups = "happy_path", priority = 1)
        @Description("TC-BOOKING-002 - Create booking with full valid payload")
        public void createBooking_withFullValidPayload_shouldSucceed(
                        String testCaseName,
                        String firstname,
                        String lastname,
                        int totalprice,
                        boolean depositpaid,
                        String checkin,
                        String checkout,
                        String additionalneeds) {

                Booking booking = payloadFactory.createPayloadBooking(firstname, lastname, totalprice, depositpaid,
                                checkin, checkout, additionalneeds);
                Response response = bookingAPI.createBooking(booking);

                assertActions.verifyResponseBody(response.getStatusCode(), 200);
                // Can add more assertions here to verify the response body content

                BookingResponse bookingResponse = response.as(BookingResponse.class);
                int bookingId = bookingResponse.getBookingid();

                assertActions.verifyTrue(bookingId > 0, "Booking ID should be greater than 0");
                assertActions.verifyStringEquals(bookingResponse.getBooking().getFirstname(), firstname);
        }

        @Test(groups = "happy_path", priority = 2)
        @Description("TC-BOOKING-003 - Verify create-booking response schema")
        public void createBooking_shouldReturnValidResponseSchema() {
                Map<String, Object> payload = payloadFactory.createBasePayloadBooking();
                Response response = bookingAPI.createBooking(payload);
                response.then()
                                .statusCode(200)
                                .body(matchesJsonSchemaInClasspath("schemas/booking-response-schema.json"));
        }

        @Test(groups = "happy_path", priority = 2)
        @Description("TC-BOOKING-006 - Retrieve created booking and verify stored data")
        public void createBooking_shouldStoreRetrievableBookingData() {
                Map<String, Object> payload = payloadFactory.createBasePayloadBooking();
                Response createResponse = bookingAPI.createBooking(payload);
                assertActions.verifyResponseBody(createResponse.getStatusCode(), 200);

                Map<String, Object> createBody = createResponse.as(Map.class);
                int bookingid = ((Number) createBody.get("bookingid")).intValue();
                assertActions.verifyTrue(bookingid > 0, "Booking ID should be greater than 0");

                // Retrieve the created booking by ID
                Response getResponse = bookingAPI.getBookingById(bookingid);
                assertActions.verifyResponseBody(getResponse.getStatusCode(), 200);
                Map<String, Object> retrievedBooking = getResponse.as(Map.class);

                // Verify that the retrieved booking matches the original payload
                payload.forEach((key, expected) -> {
                        assertActions.verifyTrue(retrievedBooking.get(key).equals(expected),
                                        "Field '" + key + "' in retrieved booking should match the input payload. Expected: "
                                                        + expected + ", Actual: " + retrievedBooking.get(key));
                });
        }

        @Test(dataProvider = "missingBookingFieldData", dataProviderClass = BookingDataProviders.class, groups = "required_fields", priority = 1)
        @Description("TC-BOOKING-007 - Reject booking payload with missing required fields")
        public void createBooking_withMissingRequiredField_shouldBeRejected(
                        String testCaseName,
                        Map<String, Object> fields) {
                Response response;

                Map<String, Object> payload = payloadFactory.createPartialPayloadBooking(fields);
                response = bookingAPI.createBooking(payload);

                assertActions.verifyResponseBody(response.getStatusCode(), 500); // Current API returns Internal Server
                                                                                 // Error for a missing

        }

        @Test(dataProvider = "validBoundaryBookingData", dataProviderClass = BookingDataProviders.class, groups = "boundary_values", priority = 1)
        @Description("TC-BOOKING-008 - Create booking with valid boundary values")
        public void createBooking_withValidBoundaryValues_shouldSucceed(
                        String testCaseName,
                        String firstname,
                        String lastname,
                        int totalprice,
                        boolean depositpaid,
                        String checkin,
                        String checkout,
                        String additionalneeds) {
                Booking payload = payloadFactory.createPayloadBooking(firstname, lastname, totalprice, depositpaid,
                                checkin, checkout, additionalneeds);
                Response response = bookingAPI.createBooking(payload);
                assertActions.verifyResponseBody(response.getStatusCode(), 200);
        }

        @Test(dataProvider = "invalidBookingFirstname", dataProviderClass = BookingDataProviders.class, groups = "field_validation", priority = 4)
        @Description("TC-BOOKING-009 - Reject booking with invalid firstname")
        public void createBooking_withInvalidFirstname_shouldBeRejected(
                        String testCaseName,
                        Object firstname) {
                Map<String, Object> payload = payloadFactory.createBasePayloadBooking();
                payload.put("firstname", firstname); // Assuming the Booking class has a method to set the firstname
                Response response = bookingAPI.createBooking(payload);

                assertActions.verifyResponseBody(response.getStatusCode(), 500);

                // Verify error message
                // Verify no booking is created in the system (if API returns error message, we
                // can verify that the booking is not created by checking the response or by
                // making a GET request to verify the booking does not exist)
        }

        @Test(dataProvider = "invalidBookingLastname", dataProviderClass = BookingDataProviders.class, groups = "field_validation", priority = 4)
        @Description("TC-BOOKING-010 - Reject booking with invalid lastname")
        public void createBooking_withInvalidLastname_shouldBeRejected(
                        String testCaseName,
                        Object lastname) {
                Map<String, Object> payload = payloadFactory.createBasePayloadBooking();
                payload.put("lastname", lastname); // Assuming the Booking class has a method to set the

                Response response = bookingAPI.createBooking(payload);

                assertActions.verifyResponseBody(response.getStatusCode(), 500);
                // Verify error message
                // Verify no booking is created in the system (if API returns error message, we
                // can verify that the booking is not created by checking the response or by
                // making a GET request to verify the booking does not exist)
        }

        @Test(dataProvider = "invalidBookingTotalPrice", dataProviderClass = BookingDataProviders.class, groups = "field_validation", priority = 2)
        @Description("TC-BOOKING-011 - Reject booking with invalid total price")
        public void createBooking_withInvalidTotalPrice_shouldBeRejected(
                        String testCaseName,
                        Object totalprice) {
                Map<String, Object> payload = payloadFactory.createBasePayloadBooking();
                payload.put("totalprice", totalprice);
                Response response = bookingAPI.createBooking(payload);

                assertActions.verifyResponseBody(response.getStatusCode(), 500);
        }

        @Test(dataProvider = "invalidBookingCheckinDates", dataProviderClass = BookingDataProviders.class, groups = "field_validation", priority = 2)
        @Description("TC-BOOKING-012 - Reject booking with invalid check-in date")
        public void createBooking_withInvalidCheckinDate_shouldBeRejected(
                        String testCaseName,
                        Object checkin) {
                Map<String, Object> payload = payloadFactory.createBasePayloadBooking();
                Map<String, Object> bookingdates = (Map<String, Object>) payload.get("bookingdates");
                bookingdates.put("checkin", checkin);
                payload.put("bookingdates", bookingdates);
                Response response = bookingAPI.createBooking(payload);
                assertActions.verifyResponseBody(response.getStatusCode(), 500);
        }

        @Test(dataProvider = "invalidBookingCheckoutDates", dataProviderClass = BookingDataProviders.class, groups = "field_validation", priority = 2)
        @Description("TC-BOOKING-013 - Reject booking with invalid check-out date")
        public void createBooking_withInvalidCheckoutDate_shouldBeRejected(
                        String testCaseName,
                        Object checkout) {
                Map<String, Object> payload = payloadFactory.createBasePayloadBooking();
                Map<String, Object> bookingdates = (Map<String, Object>) payload.get("bookingdates");
                bookingdates.put("checkout", checkout);
                payload.put("bookingdates", bookingdates);
                Response response = bookingAPI.createBooking(payload);
                assertActions.verifyResponseBody(response.getStatusCode(), 500);
        }

        @Test(dataProvider = "invalidBookingDates", dataProviderClass = BookingDataProviders.class, groups = "field_validation", priority = 2)
        @Description("TC-BOOKING-014 - Reject booking with invalid booking-dates structure")
        public void createBooking_withInvalidBookingDates_shouldBeRejected(
                        String testCaseName,
                        Object bookingdates) {
                Map<String, Object> payload = payloadFactory.createBasePayloadBooking();
                payload.put("bookingdates", bookingdates);

                Response response = bookingAPI.createBooking(payload);
                assertActions.verifyResponseBody(response.getStatusCode(), 500);
        }

        @Test(groups = "field_validation", priority = 2)
        @Description("TC-BOOKING-015 - Reject check-in date after check-out date")
        public void createBooking_withCheckinAfterCheckout_shouldBeRejected() {
                String checkin = LocalDate.now().plusDays(5).toString();
                String checkout = LocalDate.now().plusDays(3).toString();
                Map<String, Object> payload = payloadFactory.createBasePayloadBooking();
                Map<String, Object> bookingdates = (Map<String, Object>) payload.get("bookingdates");
                bookingdates.put("checkout", checkin);
                bookingdates.put("checkin", checkout);
                payload.put("bookingdates", bookingdates);
                Response response = bookingAPI.createBooking(payload);
                assertActions.verifyResponseBody(response.getStatusCode(), 500);
        }

        @Test(dataProvider = "invalidBookingDepositPaid", dataProviderClass = BookingDataProviders.class, groups = "field_validation", priority = 2)
        @Description("TC-BOOKING-016 - Reject booking with invalid deposit-paid value")
        public void createBooking_withInvalidDepositPaid_shouldBeRejected(
                        String testCaseName,
                        Object depositpaid) {
                Map<String, Object> payload = payloadFactory.createBasePayloadBooking();
                payload.put("depositpaid", depositpaid);
                Response response = bookingAPI.createBooking(payload);

                assertActions.verifyResponseBody(response.getStatusCode(), 500);
        }

        @Test(dataProvider = "invalidBookingAdditionalNeeds", dataProviderClass = BookingDataProviders.class, groups = "field_validation", priority = 2)
        @Description("TC-BOOKING-017 - Reject booking with invalid additional-needs value")
        public void createBooking_withInvalidAdditionalNeeds_shouldBeRejected(
                        String testCaseName,
                        Object additionalneeds) {
                Map<String, Object> payload = payloadFactory.createBasePayloadBooking();
                payload.put("additionalneeds", additionalneeds);
                Response response = bookingAPI.createBooking(payload);

                assertActions.verifyResponseBody(response.getStatusCode(), 500);
        }

        @Test(groups = "data_integrity", priority = 3)
        @Description("TC-BOOKING-004 - Verify create-booking response matches input payload")
        public void createBooking_shouldReturnInputData() {
                Map<String, Object> payload = payloadFactory.createBasePayloadBooking();

                // 1. POST: Create booking and verify response data integrity (payload vs Post
                // response data)
                Response response = bookingAPI.createBooking(payload);
                assertActions.verifyResponseBody(response.getStatusCode(), 200);
                Map<String, Object> bookingResponse = response.as(Map.class);

                Object rawId = bookingResponse.get("bookingid");
                assertActions.verifyTrue(((Number) rawId).intValue() > 0, "Booking ID should be greater than 0");

                // Compare 2 maps: payload vs response booking data
                Map<String, Object> booking = (Map<String, Object>) bookingResponse.get("booking");
                // assertActions.verifyTrue(payload.equals(booking), "Response booking data
                // should match the input payload");

                payload.forEach((key, expected) -> {
                        assertActions.verifyTrue(booking.get(key).equals(expected),
                                        "Field '" + key + "' in response should match the input payload. Expected: "
                                                        + expected + ", Actual: " + booking.get(key));
                });

        }

        @Test(groups = "data_integrity", priority = 2)
        @Description("TC-BOOKING-005 - Create the same payload twice and verify unique booking IDs")
        public void createBooking_withSamePayload_shouldCreateDifferentBookings() {
                Map<String, Object> payload = payloadFactory.createBasePayloadBooking();
                Response response1 = bookingAPI.createBooking(payload);
                assertActions.verifyResponseBody(response1.getStatusCode(), 200);
                BookingResponse bookingResponse1 = response1.as(BookingResponse.class);
                int bookingId1 = bookingResponse1.getBookingid();
                assertActions.verifyTrue(bookingId1 > 0, "Booking ID should be greater than 0");

                Response response2 = bookingAPI.createBooking(payload);
                assertActions.verifyResponseBody(response2.getStatusCode(), 200);
                BookingResponse bookingResponse2 = response2.as(BookingResponse.class);
                int bookingId2 = bookingResponse2.getBookingid();
                assertActions.verifyTrue(bookingId2 > 0, "Booking ID should be greater than 0");
                assertActions.verifyTrue(bookingId1 != bookingId2,
                                "Booking IDs should be different for the same payload");

        }

        @Test(groups = "data_integrity", priority = 2)
        @Description("TC-BOOKING-018 - Ignore client-provided booking ID and generate a new ID")
        public void createBooking_withClientBookingId_shouldIgnoreItAndGenerateNewId() {
                final int clientBookingId = 999_999_999;

                // 1. POST: Create a booking with a bookingid in the payload -> server should
                // ignore the bookingid and generate a new one
                Map<String, Object> payload = payloadFactory.createBasePayloadBooking();
                payload.put("bookingid", clientBookingId); // Add bookingid to the payload

                Response response = bookingAPI.createBooking(payload);
                assertActions.verifyResponseBody(response.getStatusCode(), 200);

                Map<String, Object> responseMap = response.as(Map.class);
                int serverBookingId = ((Number) responseMap.get("bookingid")).intValue();
                assertActions.verifyTrue(serverBookingId != clientBookingId,
                                "Server-generated booking ID should be different from client-provided booking ID");

                // 2. GET: Retrieve the booking by the server-generated booking ID
                Response getResponseByServerId = bookingAPI.getBookingById(serverBookingId);
                Map<String, Object> retrievedBooking = getResponseByServerId.as(Map.class);

                assertActions.verifyTrue(getResponseByServerId.getStatusCode() == 200,
                                "GET request should return status code 200");
                assertActions.verifyStringEquals(retrievedBooking.get("firstname").toString(),
                                payload.get("firstname").toString());

                // 3. GET: Attempt to retrieve the booking by the client-provided booking ID
                // (should not exist)
                Response getResponseByClientId = bookingAPI.getBookingById(clientBookingId);
                assertActions.verifyTrue(getResponseByClientId.getStatusCode() == 404,
                                "GET by client-provided booking ID should return status code 404 but got "
                                                + getResponseByClientId.getStatusCode());

        }

        @Test(groups = "data_integrity", priority = 2)
        @Description("TC-BOOKING-019 - Do not overwrite existing booking when payload contains its booking ID")
        public void createBooking_withExistingBookingId_shouldNotOverwriteExistingBooking() {
                // 1. Create a booking to get a valid booking ID
                Map<String, Object> initialPayload = payloadFactory.createBasePayloadBooking();
                Response initialResponse = bookingAPI.createBooking(initialPayload);
                assertActions.verifyResponseBody(initialResponse.getStatusCode(), 200);
                int existingBookingId = ((Number) initialResponse.as(Map.class).get("bookingid")).intValue();

                // 2. Attempt to create a new booking with the same booking ID in the payload
                Map<String, Object> newPayload = payloadFactory.createBasePayloadBooking();
                newPayload.put("firstname", "NewFirstName"); // Change some fields to differentiate the new booking
                newPayload.put("lastname", "NewLastName"); // Change some fields to differentiate the new booking
                newPayload.put("totalprice", 200); // Change some fields to differentiate the new booking
                newPayload.put("depositpaid", true); // Change some fields to differentiate the new booking
                newPayload.put("additionalneeds", "NewAdditionalNeeds"); // Change some fields to differentiate the new
                                                                         // booking
                newPayload.put("bookingid", existingBookingId); // Add existing booking ID to the new payload

                Response newResponse = bookingAPI.createBooking(newPayload);
                assertActions.verifyResponseBody(newResponse.getStatusCode(), 200);

                int newServerBookingId = ((Number) newResponse.as(Map.class).get("bookingid")).intValue();
                assertActions.verifyTrue(newServerBookingId != existingBookingId,
                                "Server-generated booking ID should be different from existing booking ID");

                // 3. Verify that the original booking is still intact
                Response getOriginalBookingResponse = bookingAPI.getBookingById(existingBookingId);
                assertActions.verifyTrue(getOriginalBookingResponse.getStatusCode() == 200,
                                "GET request for original booking should return status code 200");
                Map<String, Object> originalBooking = getOriginalBookingResponse.as(Map.class);
                assertActions.verifyStringEquals(originalBooking.get("firstname").toString(),
                                initialPayload.get("firstname").toString());
                assertActions.verifyStringEquals(originalBooking.get("lastname").toString(),
                                initialPayload.get("lastname").toString());
                assertActions.verifyIntEquals((int) originalBooking.get("totalprice"),
                                (int) initialPayload.get("totalprice"));
                assertActions.verifyBooleanEquals((boolean) originalBooking.get("depositpaid"),
                                ((boolean) initialPayload.get("depositpaid")));
                assertActions.verifyStringEquals(originalBooking.get("additionalneeds").toString(),
                                initialPayload.get("additionalneeds").toString());

        }



}