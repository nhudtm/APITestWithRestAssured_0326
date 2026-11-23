package com.heroku_app.dataproviders;

import java.time.LocalDate;
import java.util.Map;

import org.testng.annotations.DataProvider;

public class BookingDataProviders {
        String checkinDate = LocalDate.now().plusDays(7).toString();
        String checkoutDate = LocalDate.now().plusDays(14).toString();

        @DataProvider(name = "validBookingData")
        public Object[][] validBookingData() {
                return new Object[][] {
                                { "Valid 1", "John", "Doe", 100, true, checkinDate, checkoutDate, "Breakfast" },
                                { "Valid 2", "Jane", "Smith", 200, false, checkinDate, checkoutDate, "Lunch" },
                                { "Valid 3", "Alice", "Johnson", 150, true, checkinDate, checkoutDate, "Dinner" }
                };
        }

        // Used for testing partial updates of booking data
        @DataProvider(name = "partialBookingData")
        public Object[][] partialBookingData() {
                return new Object[][] {
                                // Update one field: firstname
                                {
                                                "Update FirstName",
                                                Map.of(
                                                                "firstname", "Michael")
                                },

                                // Update two fields: firstname + lastname
                                {
                                                "Update FirstName and LastName",
                                                Map.of(
                                                                "firstname", "Michael",
                                                                "lastname", "Brown")
                                },

                                // Update two fields: totalprice + depositpaid
                                {
                                                "Update TotalPrice and DepositPaid",
                                                Map.of(
                                                                "totalprice", 800,
                                                                "depositpaid", false)
                                },

                                // Update multiple fields
                                {
                                                "Update Multiple Fields",
                                                Map.of(
                                                                "firstname", "David",
                                                                "lastname", "Wilson",
                                                                "additionalneeds", "Lunch")
                                },

                                // Update bookingdates
                                {
                                                "Update BookingDates",
                                                Map.of(
                                                                "bookingdates",
                                                                Map.of(
                                                                                "checkin", checkinDate,
                                                                                "checkout", checkoutDate))
                                }
                };
        }

        @DataProvider(name = "missingBookingFieldData")
        public Object[][] missingBookingFieldData() {

                return new Object[][] {

                                // Missing FirstName
                                {
                                                "Missing FirstName",
                                                Map.of(
                                                                "lastname", "Smith",
                                                                "totalprice", 500,
                                                                "depositpaid", true,
                                                                "bookingdates", Map.of(
                                                                                "checkin", checkinDate,
                                                                                "checkout", checkoutDate),
                                                                "additionalneeds", "Breakfast")
                                },

                                // Missing LastName
                                {
                                                "Missing LastName",
                                                Map.of(
                                                                "firstname", "John",
                                                                "totalprice", 500,
                                                                "depositpaid", true,
                                                                "bookingdates", Map.of(
                                                                                "checkin", checkinDate,
                                                                                "checkout", checkoutDate),
                                                                "additionalneeds", "Breakfast")
                                },

                                // Missing TotalPrice
                                {
                                                "Missing TotalPrice",
                                                Map.of(
                                                                "firstname", "John",
                                                                "lastname", "Smith",
                                                                "depositpaid", true,
                                                                "bookingdates", Map.of(
                                                                                "checkin", checkinDate,
                                                                                "checkout", checkoutDate),
                                                                "additionalneeds", "Breakfast")
                                },

                                // Missing DepositPaid
                                {
                                                "Missing DepositPaid",
                                                Map.of(
                                                                "firstname", "John",
                                                                "lastname", "Smith",
                                                                "totalprice", 500,
                                                                "bookingdates", Map.of(
                                                                                "checkin", checkinDate,
                                                                                "checkout", checkoutDate),
                                                                "additionalneeds", "Breakfast")
                                },

                                // Missing Bookingdates
                                {
                                                "Missing Bookingdates",
                                                Map.of(
                                                                "firstname", "John",
                                                                "lastname", "Smith",
                                                                "totalprice", 500,
                                                                "depositpaid", true,
                                                                "additionalneeds", "Breakfast")
                                }
                };
        }

        @DataProvider(name = "validBoundaryBookingData")
        public Object[][] validBoundaryBookingData() {
                return new Object[][] {
                                { "Minimum Length FirstName", "A", "Doe", 0, true, checkinDate, checkoutDate,
                                                "Breakfast" }, // Minimum
                                                               // length
                                                               // for
                                                               // firstname
                                { "Minimum Length LastName", "John", "B", 0, true, checkinDate, checkoutDate,
                                                "Breakfast" }, // Minimum
                                                               // length
                                                               // for
                                                               // lastname
                                { "Maximum Length FirstName",
                                                "ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ",
                                                "Doe",
                                                1000000, false, checkinDate, checkoutDate, "Lunch" }, // Maximum length
                                                                                                      // for firstname
                                { "Maximum Length LastName", "John",
                                                "ABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ",
                                                1000000, false, checkinDate, checkoutDate, "Lunch" }, // Maximum length
                                                                                                      // for lastname
                                { "Minimum Value TotalPrice", "John", "Doe", 0, true, checkinDate, checkoutDate,
                                                "Breakfast" }, // Minimum
                                                               // value
                                                               // for
                                                               // totalprice
                                { "Maximum Value TotalPrice", "John", "Doe", 1000000, true, checkinDate, checkoutDate,
                                                "Breakfast" } // Maximum
                                                              // value
                                                              // for
                                                              // totalprice

                };
        }

        @DataProvider(name = "invalidBookingFirstname")
        public Object[][] invalidBookingFirstname() {
                return new Object[][] {
                                { "Empty FirstName", "" },
                                { "Null FirstName", (String) null },
                                { "Blank FirstName", "   " },
                                { "Numeric FirstName", 123 },
                                { "Boolean FirstName", true },
                                { "Too Long FirstName", "A".repeat(256) } // firstname > max length

                };
        }

        @DataProvider(name = "invalidBookingLastname")
        public Object[][] invalidBookingLastname() {
                return new Object[][] {
                                { "Empty LastName", "" },
                                { "Null LastName", (String) null },
                                { "Blank LastName", "   " },
                                { "Numeric LastName", 123 },
                                { "Boolean LastName", true },
                                { "Too Long LastName", "A".repeat(256) } // lastname > max length
                };
        }

        @DataProvider(name = "invalidBookingDepositPaid")
        public Object[][] invalidBookingDepositPaid() {
                return new Object[][] {
                                { "String DepositPaid", "true" },
                                { "Integer DepositPaid", 1 },
                                { "Null DepositPaid", null },
                                { "Empty DepositPaid", "" },
                                { "Blank DepositPaid", "   " },

                };
        }

        @DataProvider(name = "invalidBookingTotalPrice")
        public Object[][] invalidBookingTotalPrice() {
                return new Object[][] {
                                { "String TotalPrice", "100" },
                                { "Boolean TotalPrice", true },
                                { "Null TotalPrice", null },
                                { "Negative TotalPrice", -100 },
                                { "Exceeding Max TotalPrice", 1000001 },
                                { "Zero TotalPrice", 0 } // Assuming 0 is valid, but if not, you can remove this case
                };

        }

        @DataProvider(name = "invalidBookingCheckinDates")
        public Object[][] invalidBookingCheckinDates() {
                return new Object[][] {
                                { "Null Checkin Date", null },
                                { "Empty Checkin Date", "" },
                                { "Blank Checkin Date", "   " },
                                { "Invalid Format Checkin Date", "01-01-2023" },
                                { "Inexistent Checkin Date", "2026-02-30" },
                                { "Past Checkin Date", LocalDate.now().minusDays(1).toString() }, // Assuming check-in
                                                                                                  // date cannot be in
                                                                                                  // the past
                                { "Checking Date equal to Checkout Date", checkoutDate }, // checkout = today plus 14
                                                                                          // days
                };
        }

        @DataProvider(name = "invalidBookingCheckoutDates")
        public Object[][] invalidBookingCheckoutDates() {
                return new Object[][] {
                                { "Null Checkout Date", null },
                                { "Empty Checkout Date", "" },
                                { "Blank Checkout Date", "   " },
                                { "Invalid Format Checkout Date", "01-01-2023" },
                                { "Inexistent Checkout Date", "2026-02-30" },
                                { "Past Checkout Date", LocalDate.now().minusDays(1).toString() }, // Assuming check-out
                                                                                                   // date cannot be in
                                                                                                   // the past

                };
        }

        @DataProvider(name = "invalidBookingAdditionalNeeds")
        public Object[][] invalidAdditionalNeeds() {
                return new Object[][] {
                                { "Null AdditionalNeeds", null },
                                { "Too Long AdditionalNeeds", "A".repeat(256) }, // additionalneeds > max length
                                { "Boolean AdditionalNeeds", true },
                                { "Numeric AdditionalNeeds", 123 },

                };
        }

        @DataProvider(name = "invalidBookingDates")
        public Object[][] invalidBookingDates() {
                return new Object[][] {
                                { "Null BookingDates", null },
                                { "Blank BookingDates", "   " },
                                { "Empty String BookingDates", "" },
                                { "Empty BookingDates", Map.of() },
                                { "String BookingDates", checkinDate },
                                { "Boolean BookingDates", true },
                                { "Array BookingDates", new String[] { checkinDate, checkoutDate } },
                                { "Array of Objects BookingDates", new Object[] { checkinDate, checkoutDate } },
                                { "Object with unknown keys BookingDates", Map.of("unknownKey", "value") }

                };
        }
}