---
name: REST Assured API Automation Framework
description: Production-grade REST API automation framework with REST Assured, POJO serialization using GSON, PayloadManager pattern, E2E integration workflows with TestNG ITestContext, and Allure reporting. Updated to current com.heroku_app structure.
version: 2.0.0
author: thetestingacademy
license: MIT
testingTypes: [api, integration]
frameworks: [rest-assured]
languages: [java]
domains: [api]
agents: [claude-code, cursor, github-copilot, windsurf, codex, aider, continue, cline, zed, bolt]
---

# REST Assured API Automation Framework Skill (V2)

You are an expert QA automation engineer specializing in REST API automation with Java using REST Assured. When the user asks you to build, review, or debug an API test automation framework, follow these instructions aligned with the current project structure (`com.heroku_app.*`) and test execution flow.

## Core Principles

1. **PayloadManager pattern** -- Centralize all request payload creation and response deserialization in `PayloadManager` using GSON.
2. **POJO-driven requests and responses** -- Use Java objects with `@SerializedName` and `@Expose` for type-safe JSON mapping.
3. **RequestSpecBuilder for DRY setup** -- Configure base URI and default headers once in `BaseTest`, reuse across tests.
4. **Custom assertion helpers** -- Keep assertions readable and reusable through `AssertActions`.
5. **E2E integration with ITestContext** -- Share token/bookingId safely between multi-step test methods.
6. **Centralized API constants** -- Store endpoint paths in `APIConstants`, avoid hardcoded paths in tests.
7. **Multiple data strategies** -- Support static payloads, JavaFaker data, malformed payloads, and edge cases.
8. **Allure metadata on tests** -- Use `@Description`, `@Owner`, `@TmsLink` for traceability.

## Project Structure

```text
src/
  main/java/com/heroku_app/
    endpoints/
      APIConstants.java               # Base URL and endpoint paths
    modules/
      PayloadManager.java             # Payload creation and response parsing
    pojos/
      request/
        Auth.java                     # Authentication POJO
        Booking.java                  # Booking request POJO
        Bookingdates.java             # Nested dates POJO
      reponse/
        BookingResponse.java          # Booking response POJO
        TokenResponse.java            # Token response POJO
    commons/
      GlobalConstants.java            # Global paths/constants

  test/java/com/heroku_app/
    base/
      BaseTest.java                   # Setup, teardown, token helper
    asserts/
      AssertActions.java              # Custom assertion methods
    tests/
      crud/
        TestHealthCheck.java
        TestCreateToken.java
        TestCreateBooking.java
      e2e_integration/
        TestIntegrationFlow1.java

  test/resources/
    data.properties
    schemas/
      booking-response-schema.json
    testng.xml                        # Default CRUD suite
    testng_e2e.xml                    # E2E integration suite

pom.xml
```

## Maven Dependencies

```xml
<dependencies>
    <dependency>
        <groupId>io.rest-assured</groupId>
        <artifactId>rest-assured</artifactId>
        <version>5.5.1</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>io.rest-assured</groupId>
        <artifactId>json-schema-validator</artifactId>
        <version>5.5.1</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testng</groupId>
        <artifactId>testng</artifactId>
        <version>7.10.2</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>io.qameta.allure</groupId>
        <artifactId>allure-testng</artifactId>
        <version>2.27.0</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>com.google.code.gson</groupId>
        <artifactId>gson</artifactId>
        <version>2.11.0</version>
    </dependency>
    <dependency>
        <groupId>org.assertj</groupId>
        <artifactId>assertj-core</artifactId>
        <version>3.25.1</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>com.github.javafaker</groupId>
        <artifactId>javafaker</artifactId>
        <version>1.0.2</version>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>io.qameta.allure</groupId>
            <artifactId>allure-maven</artifactId>
            <version>2.12.0</version>
        </plugin>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.1.2</version>
            <configuration>
                <suiteXmlFiles>
                    <suiteXmlFile>${suiteXmlFile}</suiteXmlFile>
                </suiteXmlFiles>
                <systemPropertyVariables>
                    <allure.results.directory>${project.build.directory}/allure-results</allure.results.directory>
                </systemPropertyVariables>
            </configuration>
        </plugin>
    </plugins>
</build>
```

## API Constants

```java
package com.heroku_app.endpoints;

public class APIConstants {
    public static final String BASE_URL = "https://restful-booker.herokuapp.com";
    public static final String CREATE_UPDATE_BOOKING_URL = "/booking";
    public static final String AUTH_URL = "/auth";
    public static final String PING_URL = "/ping";

    private APIConstants() {
    }
}
```

## POJO Models -- Request

### Auth.java

```java
package com.heroku_app.pojos.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Auth {
    @SerializedName("username")
    @Expose
    private String username;

    @SerializedName("password")
    @Expose
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
```

### Booking.java

```java
package com.heroku_app.pojos.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Booking {
    @SerializedName("firstname") @Expose private String firstname;
    @SerializedName("lastname") @Expose private String lastname;
    @SerializedName("totalprice") @Expose private Integer totalprice;
    @SerializedName("depositpaid") @Expose private Boolean depositpaid;
    @SerializedName("bookingdates") @Expose private Bookingdates bookingdates;
    @SerializedName("additionalneeds") @Expose private String additionalneeds;

    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }
    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }
    public Integer getTotalprice() { return totalprice; }
    public void setTotalprice(Integer totalprice) { this.totalprice = totalprice; }
    public Boolean getDepositpaid() { return depositpaid; }
    public void setDepositpaid(Boolean depositpaid) { this.depositpaid = depositpaid; }
    public Bookingdates getBookingdates() { return bookingdates; }
    public void setBookingdates(Bookingdates bookingdates) { this.bookingdates = bookingdates; }
    public String getAdditionalneeds() { return additionalneeds; }
    public void setAdditionalneeds(String additionalneeds) { this.additionalneeds = additionalneeds; }
}
```

### Bookingdates.java

```java
package com.heroku_app.pojos.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Bookingdates {
    @SerializedName("checkin") @Expose private String checkin;
    @SerializedName("checkout") @Expose private String checkout;

    public String getCheckin() { return checkin; }
    public void setCheckin(String checkin) { this.checkin = checkin; }
    public String getCheckout() { return checkout; }
    public void setCheckout(String checkout) { this.checkout = checkout; }
}
```

## POJO Models -- Response

### TokenResponse.java

```java
package com.heroku_app.pojos.reponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TokenResponse {
    @SerializedName("token")
    @Expose
    private String token;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
```

### BookingResponse.java

```java
package com.heroku_app.pojos.reponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.heroku_app.pojos.request.Booking;

public class BookingResponse {
    @SerializedName("bookingid")
    @Expose
    private Integer bookingid;

    @SerializedName("booking")
    @Expose
    private Booking booking;

    public Integer getBookingid() { return bookingid; }
    public void setBookingid(Integer bookingid) { this.bookingid = bookingid; }
    public Booking getBooking() { return booking; }
    public void setBooking(Booking booking) { this.booking = booking; }
}
```

## PayloadManager Pattern

Centralize payload creation and response parsing to keep tests focused on assertions.

```java
package com.heroku_app.modules;

import com.github.javafaker.Faker;
import com.google.gson.Gson;
import com.heroku_app.pojos.reponse.BookingResponse;
import com.heroku_app.pojos.reponse.TokenResponse;
import com.heroku_app.pojos.request.Auth;
import com.heroku_app.pojos.request.Booking;
import com.heroku_app.pojos.request.Bookingdates;

public class PayloadManager {
    Gson gson;
    Faker faker;

    public Booking createPayloadBooking() { /* build static payload */ return new Booking(); }
    public Booking createPayloadBookingFakerJS() { /* random payload */ return new Booking(); }
    public Booking createPayloadBookingWithoutFirstname() { /* edge payload */ return new Booking(); }

    public String setAuthPayload() {
        Auth auth = new Auth();
        auth.setUsername("admin");
        auth.setPassword("password123");
        gson = new Gson();
        return gson.toJson(auth);
    }

    public BookingResponse bookingResponseObject(String responseString) {
        gson = new Gson();
        return gson.fromJson(responseString, BookingResponse.class);
    }

    public String getTokenFromJSON(String tokenResponse) {
        gson = new Gson();
        TokenResponse response = gson.fromJson(tokenResponse, TokenResponse.class);
        return response.getToken();
    }
}
```

## Base Test Class

```java
package com.heroku_app.base;

import com.google.gson.Gson;
import com.heroku_app.asserts.AssertActions;
import com.heroku_app.endpoints.APIConstants;
import com.heroku_app.modules.PayloadManager;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeMethod;

public class BaseTest {
    public RequestSpecification requestSpecification;
    public AssertActions assertActions;
    public PayloadManager payloadManager;
    public Response response;
    public ValidatableResponse validatableResponse;

    @BeforeMethod
    public void setUp() {
        payloadManager = new PayloadManager();
        assertActions = new AssertActions();

        requestSpecification = new RequestSpecBuilder()
            .setBaseUri(APIConstants.BASE_URL)
            .addHeader("Content-Type", "application/json")
            .build().log().all();
    }

    public String buildBody(Object payload) {
        if (payload instanceof String) return (String) payload;
        return new Gson().toJson(payload);
    }

    public String getToken() {
        requestSpecification = RestAssured.given();
        requestSpecification.baseUri(APIConstants.BASE_URL).basePath(APIConstants.AUTH_URL);
        response = requestSpecification.contentType(ContentType.JSON)
            .body(payloadManager.setAuthPayload())
            .when().post();
        return payloadManager.getTokenFromJSON(response.asString());
    }
}
```

## Custom Assertion Helpers

```java
package com.heroku_app.asserts;

import io.restassured.response.Response;
import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;

public class AssertActions {
    public void verifyResponseBody(String actual, String expected) { assertEquals(actual, expected); }
    public void verifyResponseBody(int actual, int expected) { assertEquals(actual, expected); }
    public void verifyResponseBody(boolean actual, boolean expected) { assertEquals(actual, expected); }
    public void verifyStatusCode(Response response, Integer expected) { assertEquals(response.getStatusCode(), (int) expected); }
    public void verifyStringKey(String keyExpect, String keyActual) { assertThat(keyExpect).isNotNull().isNotBlank().isEqualTo(keyActual); }
    public void verifyStringKeyNotNull(Integer keyExpect) { assertThat(keyExpect).isNotNull(); }
    public void verifyStringKeyNotNull(String keyExpect) { assertThat(keyExpect).isNotNull(); }
    public void verifyResponseTime(Response response, long maxMillis) { assertThat(response.getTime()).isLessThan(maxMillis); }
    public void verifyContentType(Response response, String expectedContentType) { assertThat(response.getContentType()).contains(expectedContentType); }
    public void verifyTrue(boolean condition, String description) { assertThat(condition).as(description).isTrue(); }
}
```

## CRUD Test Patterns

### Health Check

```java
@Test(groups = "reg", priority = 1)
public void testHealthCheckGET() {
    requestSpecification.basePath(APIConstants.PING_URL);
    response = RestAssured.given(requestSpecification).when().get();
    response.then().statusCode(201);
}
```

### Token Creation

```java
@Test(groups = "reg", priority = 1)
@Description("Create token and verify")
public void testTokenPOST() {
    requestSpecification.basePath(APIConstants.AUTH_URL);
    response = RestAssured.given(requestSpecification)
        .when().body(payloadManager.setAuthPayload()).post();
    response.then().statusCode(200);
    assertActions.verifyStringKeyNotNull(payloadManager.getTokenFromJSON(response.asString()));
}
```

### Booking CRUD

```java
package com.heroku_app.tests.crud;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.heroku_app.base.BaseTest;
import com.heroku_app.endpoints.APIConstants;
import com.heroku_app.pojos.reponse.BookingResponse;
import com.heroku_app.pojos.request.Booking;

import io.qameta.allure.Description;
import io.qameta.allure.Owner;
import io.qameta.allure.TmsLink;
import io.restassured.RestAssured;

public class TestCreateBooking extends BaseTest {

    @BeforeMethod
    public void setUpCreateBookingAPI() {
        requestSpecification.basePath(APIConstants.CREATE_UPDATE_BOOKING_URL);
    }

    @Test(groups = "1_HappyPath", priority = 1)
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

    @Test(groups = "HappyPath", priority = 1)
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

    @Test(groups = "1_HappyPath", priority = 1)
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

    @Test(groups = "1_HappyPath", priority = 2)
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

    @Test(groups = "1_HappyPath", priority = 3)
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
        assertActions.verifyResponseBody(booking.getBookingdates().getCheckin(),
                payload.getBookingdates().getCheckin());
        assertActions.verifyResponseBody(booking.getBookingdates().getCheckout(),
                payload.getBookingdates().getCheckout());
        assertActions.verifyResponseBody(booking.getAdditionalneeds(), payload.getAdditionalneeds());
    }

    @Test(groups = "2_RequiredFields", priority = 1)
    @Description("TC#7 - Verify booking cannot be created with blank FirstName fields")
    public void testCreateBookingPOST_Blank_FirstName() {
        Booking payload = payloadManager.createPayloadBooking();
        payload.setFirstname("");

        response = RestAssured.given(requestSpecification)
                .when()
                .body(buildBody(payload))
                .post();

        validatableResponse = response.then().log().all();
        validatableResponse.statusCode(400); 
    }
```

## E2E Integration Flow with ITestContext

Use `ITestContext` to share state in multi-step flows.


This pattern demonstrates a complete CRUD workflow where test methods share state via TestNG's `ITestContext`. Each step depends on the previous one.

```java
package com.thetestingacademy.tests.e2e_integration;

import com.thetestingacademy.base.BaseTest;
import com.thetestingacademy.endpoints.APIConstants;
import com.thetestingacademy.pojos.reponse.BookingResponse;
import io.restassured.RestAssured;
import org.testng.ITestContext;
import org.testng.annotations.Test;

public class TestIntegrationFlow1 extends BaseTest {

    // Step 1: Create a booking
    @Test(priority = 1)
    public void testCreateBooking(ITestContext iTestContext) {
        requestSpecification.basePath(APIConstants.CREATE_UPDATE_BOOKING_URL);
        response = RestAssured.given(requestSpecification)
            .when()
            .body(payloadManager.createPayloadBookingAsString())
            .post();

        validatableResponse = response.then().log().all();
        validatableResponse.statusCode(200);

        BookingResponse bookingResponse = payloadManager.bookingResponseJava(response.asString());

        // Store booking ID for subsequent steps
        iTestContext.setAttribute("bookingid", bookingResponse.getBookingid());

        assertActions.verifyStringKeyNotNull(bookingResponse.getBookingid());
    }

    // Step 2: Verify the booking exists
    @Test(priority = 2)
    public void testVerifyBookingId(ITestContext iTestContext) {
        Integer bookingid = (Integer) iTestContext.getAttribute("bookingid");

        String basePathGET = APIConstants.CREATE_UPDATE_BOOKING_URL + "/" + bookingid;
        requestSpecification.basePath(basePathGET);

        response = RestAssured.given(requestSpecification)
            .when()
            .get();

        validatableResponse = response.then().log().all();
        validatableResponse.statusCode(200);

        // Verify the returned booking matches what we created
        BookingResponse bookingResponse = payloadManager.bookingResponseJava(response.asString());
        assertActions.verifyStringKey(bookingResponse.getBooking().getFirstname(), "Pramod");
    }

    // Step 3: Update the booking (requires auth token)
    @Test(priority = 3)
    public void testUpdateBookingByID(ITestContext iTestContext) {
        Integer bookingid = (Integer) iTestContext.getAttribute("bookingid");
        String token = getToken();

        // Store token for the delete step
        iTestContext.setAttribute("token", token);

        String basePathPUT = APIConstants.CREATE_UPDATE_BOOKING_URL + "/" + bookingid;
        requestSpecification.basePath(basePathPUT);

        response = RestAssured.given(requestSpecification)
            .cookie("token", token)
            .when()
            .body(payloadManager.fullUpdatePayloadAsString())
            .put();

        validatableResponse = response.then().log().all();
        validatableResponse.statusCode(200);
    }

    // Step 4: Verify the update
    @Test(priority = 4)
    public void testVerifyUpdatedBooking(ITestContext iTestContext) {
        Integer bookingid = (Integer) iTestContext.getAttribute("bookingid");

        String basePathGET = APIConstants.CREATE_UPDATE_BOOKING_URL + "/" + bookingid;
        requestSpecification.basePath(basePathGET);

        response = RestAssured.given(requestSpecification)
            .when()
            .get();

        validatableResponse = response.then().log().all();
        validatableResponse.statusCode(200);

        BookingResponse bookingResponse = payloadManager.bookingResponseJava(response.asString());
        assertActions.verifyStringKey(bookingResponse.getBooking().getFirstname(), "UpdatedFirstName");
    }

    // Step 5: Delete the booking
    @Test(priority = 5)
    public void testDeleteBookingById(ITestContext iTestContext) {
        Integer bookingid = (Integer) iTestContext.getAttribute("bookingid");
        String token = (String) iTestContext.getAttribute("token");

        String basePathDELETE = APIConstants.CREATE_UPDATE_BOOKING_URL + "/" + bookingid;
        requestSpecification.basePath(basePathDELETE);

        response = RestAssured.given().spec(requestSpecification)
            .cookie("token", token)
            .when()
            .delete();

        validatableResponse = response.then().log().all();
        validatableResponse.statusCode(201);
    }
}
```

## Authentication Handling

```java
// Token-based authentication flow:

// 1. Generate token via /auth endpoint
public String getToken() {
    requestSpecification = RestAssured.given();
    requestSpecification.baseUri(APIConstants.BASE_URL)
        .basePath(APIConstants.AUTH_URL);

    String payload = payloadManager.setAuthPayload();
    response = requestSpecification.contentType(ContentType.JSON)
        .body(payload).when().post();

    return payloadManager.getTokenFromJSON(response.asString());
}

// 2. Use token as cookie in protected requests
String token = getToken();
response = RestAssured.given(requestSpecification)
    .cookie("token", token)
    .when()
    .body(payloadManager.fullUpdatePayloadAsString())
    .put();

// 3. Bearer token alternative
response = RestAssured.given(requestSpecification)
    .header("Authorization", "Bearer " + token)
    .when()
    .get();
```

## TestNG XML Configuration

### Default CRUD suite (`src/test/resources/testng.xml`)

```xml
<suite name="API Test Suite">
    <test name="CRUD Tests">
        <classes>
            <class name="com.heroku_app.tests.crud.TestHealthCheck"/>
            <class name="com.heroku_app.tests.crud.TestCreateToken"/>
            <class name="com.heroku_app.tests.crud.TestCreateBooking"/>
        </classes>
    </test>
</suite>
```

### E2E suite (`src/test/resources/testng_e2e.xml`)

```xml
<suite name="E2E Integration Suite">
    <test name="Integration Flow 1">
        <classes>
            <class name="com.heroku_app.tests.e2e_integration.TestIntegrationFlow1"/>
        </classes>
    </test>
</suite>
```

### Parallel Execution Suite

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="Parallel API Suite" parallel="methods" thread-count="3">
    <test verbose="2" name="Regression">
        <groups>
            <run>
                <include name="reg"/>
            </run>
        </groups>
        <classes>
            <class name="com.thetestingacademy.tests.crud.TestHealthCheck"/>
            <class name="com.thetestingacademy.tests.crud.TestCreateToken"/>
            <class name="com.thetestingacademy.tests.crud.TestCreateBooking"/>
        </classes>
    </test>
</suite>
```

## Allure Reporting

- Results directory: `target/allure-results`
- HTML report: `target/site/allure-maven-plugin`

### Generate and Open Report
```bash
# Run default suite
mvn clean test

# Run specific suite
mvn clean test -DsuiteXmlFile=src/test/resources/testng_e2e.xml

# Generate Allure report
mvn allure:report

# Open report (macOS)
open target/site/allure-maven-plugin/index.html

# Serve report
mvn allure:serve
```
### Annotations

```java
import io.qameta.allure.*;

@Test(groups = "reg", priority = 1)
@TmsLink("https://bugz.atlassian.net/browse/TS-1")
@Owner("Promode")
@Description("TC#1 - Verify that the Booking can be Created")
@Severity(SeverityLevel.CRITICAL)
@Story("Booking CRUD")
@Feature("Booking API")
public void testCreateBookingPOST() {
    Allure.step("Set base path to booking endpoint");
    requestSpecification.basePath(APIConstants.CREATE_UPDATE_BOOKING_URL);

    Allure.step("Send POST request with booking payload");
    response = RestAssured.given(requestSpecification)
        .when()
        .body(payloadManager.createPayloadBookingAsString())
        .post();

    Allure.step("Verify response status code is 200");
    validatableResponse = response.then().statusCode(200);

    Allure.step("Verify booking ID is not null");
    BookingResponse bookingResponse = payloadManager.bookingResponseJava(response.asString());
    assertActions.verifyStringKeyNotNull(bookingResponse.getBookingid());

    Allure.addAttachment("Response Body", "application/json", response.asString(), "json");
}
```

## Response Extraction Patterns

```java

// Extract as POJO with GSON
BookingResponse br = payloadManager.bookingResponseObject(response.asString());
Integer bookingId = br.getBookingid();

I// Extract single value with JsonPath
String firstname = response.jsonPath().getString("booking.firstname");
Integer bookingId = response.jsonPath().getInt("bookingid");
List<Integer> allIds = response.jsonPath().getList(".", Integer.class);

// Extract with ValidatableResponse
validatableResponse = response.then()
    .body("booking.firstname", equalTo("Pramod"))
    .body("booking.totalprice", greaterThan(0))
    .body("bookingid", notNullValue());

// Chain extraction
String token = response.then()
    .statusCode(200)
    .extract()
    .path("token");
```

## JSON Schema Validation

```java
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

@Test(groups = "1_HappyPath", priority = 2)
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
```
### booking-response-schema.json

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "type": "object",
  "required": ["bookingid", "booking"],
  "properties": {
    "bookingid": { "type": "integer" },
    "booking": {
      "type": "object",
      "required": ["firstname", "lastname", "totalprice", "depositpaid", "bookingdates"],
      "properties": {
        "firstname": { "type": "string" },
        "lastname": { "type": "string" },
        "totalprice": { "type": "integer" },
        "depositpaid": { "type": "boolean" },
        "bookingdates": {
          "type": "object",
          "required": ["checkin", "checkout"],
          "properties": {
            "checkin": { "type": "string" },
            "checkout": { "type": "string" }
          }
        },
        "additionalneeds": { "type": "string" }
      }
    }
  }
}
```
## Best Practices

1. **Use PayloadManager for all payloads** -- Do not duplicate payload construction logic across tests; centralize it for maintainability and consistency.
2. **Separate request and response POJOs** -- Keep request models and response models distinct because response contracts often contain extra fields (e.g., `bookingid`).
3. **Store shared flow state in ITestContext** -- For E2E chains, use `ITestContext` for token/bookingId handoff instead of static/shared mutable fields.
4. **Use meaningful groups and priorities** -- Organize tests by intent (`HappyPath`, `RequiredFields`, `Boundary`, `Header`, `Payload`, `Security`) and set priority only when execution order matters.
5. **Validate more than status code** -- Assert key response fields, content type, and business data integrity, not just HTTP status.
6. **Cover edge and negative cases explicitly** -- Include malformed JSON, boundary values, invalid headers/methods, and security-like inputs (SQL/XSS-like strings).
7. **Generate dynamic data when appropriate** -- Use JavaFaker to reduce false confidence from static-only test data.
8. **Log requests/responses for debuggability** -- Keep `.log().all()` (or focused logging) on critical flows to speed up root-cause analysis.
9. **Centralize endpoint and env settings** -- Keep paths in `APIConstants` and run suite via `src/test/resources/testng*.xml` to avoid drift after refactors.
10. **Use Allure annotations consistently** -- Apply `@Description`, `@Owner`, `@TmsLink` where possible for report traceability.
11. **Keep shared RequestSpecification stable** -- For negative header tests, override per request (`.contentType(...)`, `.accept(...)`, `.header(...)`) instead of mutating global spec state.
12. **Align assertions with real API behavior** -- If API behavior is tolerant (e.g., `200` or `400`), encode intentional assertion ranges explicitly and document why.
13. **Use schema validation on critical contracts** -- Validate response schema for key endpoints to detect contract regressions early.
14. **Keep Allure generation in Maven flow** -- Prefer `allure-maven` with `target/allure-results` for CI-friendly and reproducible reporting.

## Anti-Patterns to Avoid

1. **Hardcoded URLs or endpoint paths in tests** -- Always use `APIConstants`; hardcoded values break quickly across env/path changes.
2. **Manual JSON string concatenation for normal payloads** -- Prefer POJO + GSON serialization to avoid malformed/duplicated payload code.
3. **Shared mutable state across tests** -- Avoid static/global mutable data; use `ITestContext` for controlled sharing.
4. **Tests without meaningful assertions** -- Calling an endpoint without verifying response content/headers/behavior is not a reliable test.
5. **Mutating global RequestSpecification carelessly** -- Do not alter shared spec in one test and leak side effects into others.
6. **Generating auth token redundantly in every unrelated test** -- Reuse/setup tokens wisely to reduce runtime and flakiness.
7. **Ignoring response-time checks where relevant** -- Performance-sensitive endpoints should include response-time guardrails.
8. **Skipping schema validation on contract-heavy endpoints** -- This misses breaking changes that status-code checks won’t catch.
9. **Testing only happy path** -- Excluding negative, malformed, boundary, and security-like inputs leaves major risk uncovered.
10. **Monolithic test classes without clear grouping** -- Keep tests organized by feature area and intent to improve maintainability.
11. **Stale suite/package references after refactor** -- Keep TestNG XML and class package names synchronized (`com.heroku_app.*`, `src/test/resources/testng*.xml`).
12. **Forcing one status code for tolerant APIs** -- When backend behavior intentionally varies, avoid brittle assertions that create false failures.
