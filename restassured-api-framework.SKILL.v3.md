---
name: REST Assured API Automation Framework
description: REST API automation framework using Java, REST Assured, TestNG, GSON, JavaFaker, Allure, API service objects, PayloadFactory, and TestNG-dependent E2E workflows.
version: 3.0.0
author: thetestingacademy
license: MIT
testingTypes: [api, integration]
frameworks: [rest-assured, testng, allure]
languages: [java]
domains: [api]
agents: [claude-code, cursor, github-copilot, windsurf, codex, aider, continue, cline, zed, bolt]
---

# REST Assured API Automation Framework Skill (V3)

You are an expert QA automation engineer building, reviewing, debugging, and extending a Java REST API framework. Follow the current `com.heroku_app.*` structure exactly.

## Mandatory Architecture

```text
src/main/java/com/heroku_app/
├─ commons/
│  └─ GlobalConstants.java
├─ config/
│  └─ PropertiesConfig.java
├─ endpoints/
│  └─ APIConstants.java
├─ pojos/
│  ├─ request/
│  │  ├─ Auth.java
│  │  ├─ Booking.java
│  │  └─ Bookingdates.java
│  └─ response/
│     ├─ BookingResponse.java
│     └─ TokenResponse.java
└─ utils/
   └─ BuildBodyJSON.java

src/test/java/com/heroku_app/
├─ apis/
│  ├─ AuthAPI.java
│  └─ BookingAPI.java
├─ asserts/
│  └─ AssertActions.java
├─ base/
│  └─ BaseTest.java
├─ client/
│  └─ RestClient.java
├─ dataproviders/
│  └─ BookingDataProviders.java
├─ factories/
│  └─ PayloadFactory.java
├─ spec/
│  └─ RequestSpecFactory.java
└─ tests/
   ├─ crud/
   │  ├─ TestCreateToken.java
   │  ├─ TestCreateBooking.java
   │  └─ TestCreateBookingHeaderPayload.java
   └─ e2e_integration/
      └─ TestIntegrationFlow1.java

src/test/resources/
├─ config.properties
├─ test.xml
├─ testng_e2e.xml
└─ schemas/
   └─ booking-response-schema.json
```

Do not introduce `PayloadManager`, `modules/PayloadManager.java`, `ITestContext` state, `data.properties`, or the misspelled `pojos/reponse` package in new code. Use the V3 structure above.

## Core Responsibilities

1. `RequestSpecFactory` creates the default base URI, content type, accept header, and request logging.
2. `RestClient` sends HTTP methods through the shared `RequestSpecification`.
3. `AuthAPI` and `BookingAPI` contain endpoint-specific request operations.
4. `PayloadFactory` creates POJO and `Map` payloads for valid, invalid, boundary, and Faker-based scenarios.
5. `BuildBodyJSON` serializes POJO or `Map` objects with GSON.
6. `AssertActions` centralizes repeated assertions.
7. `BaseTest` initializes framework components and optionally obtains auth tokens.
8. Test classes focus on arranging test data, invoking API objects, and asserting outcomes.

## Maven Configuration

Use Java 17 and the following framework versions:

- REST Assured 5.5.1
- TestNG 7.10.2
- Allure TestNG 2.27.0
- Allure Maven 2.12.0
- Surefire 3.1.2
- GSON 2.11.0
- AssertJ 3.25.1
- JavaFaker 1.0.2
- Log4j Core 2.24.0

The POM selects suites dynamically:

```xml
<suiteXmlFile>src/test/resources/${xml}.xml</suiteXmlFile>
```

The default Maven profile sets:

```xml
<properties>
    <xml>test</xml>
</properties>
```

## Configuration

Default configuration:

```properties
base.url=https://restful-booker.herokuapp.com
username=admin
password=password123
```

`PropertiesConfig` resolves a key in this order:

1. Java system property
2. Environment variable using the original key
3. Normalized environment variable, such as `BASE_URL`
4. `config.properties`

Use `PropertiesConfig.getRequiredProperty(...)` for required values. Do not read `config.properties` directly inside tests.

## API Constants

Keep endpoint paths in `APIConstants`:

```java
package com.heroku_app.endpoints;

import com.heroku_app.config.PropertiesConfig;

public class APIConstants {
    public static final String BASE_URL =
        PropertiesConfig.getRequiredProperty("base.url");
    public static final String CREATE_UPDATE_BOOKING_URL = "/booking";
    public static final String GET_BOOKING_IDS = "/booking";
    public static final String AUTH_URL = "/auth";
    public static final String PING_URL = "/ping";

    private APIConstants() {
    }
}
```

## Request Specification

Create the shared request specification in `RequestSpecFactory`:

```java
public class RequestSpecFactory {
    public static RequestSpecification createDefaultSpec() {
        return new RequestSpecBuilder()
            .setBaseUri(APIConstants.BASE_URL)
            .setContentType(ContentType.JSON)
            .setAccept("application/json")
            .build()
            .log()
            .all();
    }
}
```

Negative header tests should be handled by `RestClient` or per-request overrides. Do not mutate the shared specification across test methods.

## POJO Packages

Request POJOs belong in `com.heroku_app.pojos.request`.

```java
@SerializedName("firstname")
@Expose
private String firstname;
```

Response POJOs belong in `com.heroku_app.pojos.response`, not `pojos.reponse`.

```java
package com.heroku_app.pojos.response;

public class BookingResponse {
    @SerializedName("bookingid")
    @Expose
    private Integer bookingid;

    @SerializedName("booking")
    @Expose
    private Booking booking;
}
```

Implement matching getters and setters for every mapped field.

## Payload Factory

`PayloadFactory` centralizes payload construction. Use it for:

- Standard full payloads
- Minimum valid payloads
- Faker payloads
- `Map` payloads
- Partial and nested field maps
- Authentication payloads
- Allowed and invalid field combinations

Example:

```java
Booking booking = payloadFactory.createPayloadBooking(
    "John",
    "Smith",
    500,
    true,
    "2026-01-01",
    "2026-01-05",
    "Breakfast"
);
```

Do not duplicate payload construction inside test classes.

## REST Client

`RestClient` owns low-level REST Assured execution:

- `sendGetRequest(...)`
- `sendPostRequest(...)`
- `sendPostRequestRawJson(...)`
- `sendPostRequestWithoutContentType(...)`
- `sendPostRequestWithHeaders(...)`
- `sendPutRequest(...)`
- `sendDeleteRequest(...)`

`RestClient` creates requests from the shared specification and uses `BuildBodyJSON` for object payloads.

Use `String` payloads for malformed JSON and JSON arrays. Use `Object` or `Map` payloads for normal business-data scenarios.

## API Service Objects

`AuthAPI` handles authentication:

```java
Response response = authAPI.getTokenRequest(username, password);
String token = authAPI.getToken(username, password);
```

`BookingAPI` handles booking operations and protected token cookies:

```java
Response response = bookingAPI.createBooking(payload);
Response response = bookingAPI.getBookingById(bookingId);
Response response = bookingAPI.updateBooking(bookingId, payload, token);
Response response = bookingAPI.deleteBooking(bookingId, token);
```

Keep endpoint URLs and cookie construction out of test methods.

## Base Test

`BaseTest` initializes:

- `AssertActions`
- `PayloadFactory`
- `RestClient`
- `BookingAPI`
- `AuthAPI`
- `String token`

Call `getAuthToken()` only in tests that need protected operations.

```java
@BeforeMethod
public void setUp() {
    RequestSpecification requestSpec =
        RequestSpecFactory.createDefaultSpec();

    restClient = new RestClient(requestSpec);
    assertActions = new AssertActions();
    payloadFactory = new PayloadFactory();
    bookingAPI = new BookingAPI(restClient);
    authAPI = new AuthAPI(restClient, payloadFactory);
}

public void getAuthToken() {
    String username =
        PropertiesConfig.getRequiredProperty("username");
    String password =
        PropertiesConfig.getRequiredProperty("password");

    token = authAPI.getToken(username, password);
}
```

Use `@AfterMethod(alwaysRun = true)` and `@AfterClass(alwaysRun = true)` where cleanup must run after failures.

## CRUD Test Pattern

```java
package com.heroku_app.tests.crud;

import org.testng.annotations.Test;
import com.heroku_app.base.BaseTest;

public class TestCreateBooking extends BaseTest {
    @Test(groups = "smoke", priority = 1)
    public void createBooking_withMinimumValidPayload_shouldSucceed() {
        Map<String, Object> payload =
            payloadFactory.createBasePayloadBooking();
        payload.remove("additionalneeds");

        Response response = bookingAPI.createBooking(payload);

        assertActions.verifyResponseBody(response.getStatusCode(), 200);
    }
}
```

Use descriptive method names. Include the tested condition and expected outcome.

## E2E Test Pattern

Use `dependsOnMethods` and class fields for state in `TestIntegrationFlow1`. Do not use `ITestContext` for new E2E flows because the current implementation uses class fields.

```java
public class TestIntegrationFlow1 extends BaseTest {
    private Integer bookingId;
    private Booking bookingPayload;

    @Test
    public void testCreateBooking() {
        bookingPayload = payloadFactory.createPayloadBookingFakerJS();
        Response response = bookingAPI.createBooking(bookingPayload);
        BookingResponse result = response.as(BookingResponse.class);

        bookingId = result.getBookingid();
    }

    @Test(dependsOnMethods = "testCreateBooking")
    public void testVerifyBookingId() {
        Response response = bookingAPI.getBookingById(bookingId);
    }

    @Test(dependsOnMethods = "testVerifyBookingId")
    public void testUpdateBookingByID() {
        getAuthToken();
        Booking updatePayload =
            payloadFactory.createPayloadBookingFakerJS();

        bookingAPI.updateBooking(bookingId, updatePayload, token);
    }

    @Test(dependsOnMethods = "testUpdateBookingByID")
    public void testDeleteBookingById() {
        bookingAPI.deleteBooking(bookingId, token);
        Response response = bookingAPI.getBookingById(bookingId);
        assertActions.verifyResponseBody(response.getStatusCode(), 404);
    }
}
```

Add `@AfterClass(alwaysRun = true)` cleanup when resources may remain after a failed step.

## TestNG Suites

Default CRUD suite: `src/test/resources/test.xml`

```xml
<suite name="API Test Suite">
    <test verbose="2" preserve-order="true" name="CRUD Tests">
        <classes>
            <class name="com.heroku_app.tests.crud.TestCreateToken"/>
            <class name="com.heroku_app.tests.crud.TestCreateBooking"/>
            <class name="com.heroku_app.tests.crud.TestCreateBookingHeaderPayload"/>
        </classes>
    </test>
</suite>
```

E2E suite: `src/test/resources/testng_e2e.xml`

```xml
<suite name="E2E Integration Suite">
    <test verbose="2" preserve-order="true" name="Integration Flow 1">
        <classes>
            <class name="com.heroku_app.tests.e2e_integration.TestIntegrationFlow1"/>
        </classes>
    </test>
</suite>
```

Keep suite class names synchronized with package names after refactors.

## TestNG Groups and Priorities

Use a group for each test intent:

- `reg`
- `smoke`
- `happy_path`
- `required_fields`
- `boundary_values`
- `field_validation`
- `data_integrity`
- `header_content_type`
- `http_method`
- `payload_integrity`
- `security_input`

Use priority when sequence within a class is meaningful. E2E flows must use `dependsOnMethods` instead of priority alone.

## Assertion Strategy

Verify more than HTTP status:

- Response status code
- Response content type
- Required response fields
- Business data integrity
- Booking ID greater than zero
- JSON schema
- Response time when relevant
- GET 404 after deletion

Example JSON schema validation:

```java
response.then()
    .statusCode(200)
    .body(matchesJsonSchemaInClasspath(
        "schemas/booking-response-schema.json"));
```

For tolerated backend behavior, document why and assert the accepted status range explicitly.

## Allure

Use Allure annotations for traceability:

```java
@Description("TC-BOOKING-001 - Create booking")
@Owner("Nhu")
@TmsLink("https://example.tms.local/TC-BOOKING-001")
@Severity(SeverityLevel.CRITICAL)
```

Allure results are written to `target/allure-results`.

## Maven Commands

```bash
# Default CRUD suite
mvn clean test

# E2E suite
mvn clean test -Dxml=testng_e2e

# Direct Surefire suite
mvn clean test -DsuiteXmlFile=src/test/resources/testng_e2e.xml

# One test class
mvn clean test -Dtest=TestCreateBooking

# Generate Allure report
mvn allure:report

# Serve Allure report
mvn allure:serve
```

## Best Practices

1. Centralize HTTP calls in `RestClient`.
2. Centralize endpoint operations in `AuthAPI` and `BookingAPI`.
3. Centralize payload creation in `PayloadFactory`.
4. Keep test classes focused on behavior and assertions.
5. Use POJOs for normal requests and maps or raw strings only when testing malformed structures.
6. Use `PropertiesConfig` and `APIConstants` for environment and endpoint configuration.
7. Obtain auth tokens only for tests that require update or delete operations.
8. Use class fields and `dependsOnMethods` for dependent E2E steps.
9. Validate response fields, headers, and JSON contracts, not only status codes.
10. Cover happy path, required fields, boundaries, invalid fields, headers, HTTP methods, malformed payloads, and security-like inputs.
11. Use JavaFaker for dynamic valid data.
12. Add cleanup for every resource created by an E2E flow.
13. Keep TestNG suite files and POM suite selection synchronized.
14. Add Allure metadata for test ownership and requirement traceability.

## Anti-Patterns

1. Do not hardcode the base URL or endpoint paths in tests.
2. Do not concatenate JSON manually for normal POJO payloads.
3. Do not add a new `modules/PayloadManager` layer.
4. Do not use `ITestContext` as shared state in new E2E tests.
5. Do not shadow `BaseTest.token` in subclass fields.
6. Do not create auth tokens in every unrelated test.
7. Do not mutate a shared `RequestSpecification` in one test and leak state to another.
8. Do not assert only status code for response-contract-heavy endpoints.
9. Do not name response packages `pojos.reponse`.
10. Do not leave suite XML references to deleted test classes.
11. Do not leave test resources referencing deleted `data.properties`.
12. Do not claim E2E success unless create, verify, update, delete, and cleanup behavior pass.
