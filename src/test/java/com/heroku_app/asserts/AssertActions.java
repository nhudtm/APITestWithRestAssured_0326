package com.heroku_app.asserts;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;

import io.restassured.response.Response;

public class AssertActions {

    public void verifyResponseBody(String actual, String expected) {
        assertEquals(actual, expected);
    }

    public void verifyResponseBody(int actual, int expected) {
        assertEquals(actual, expected);
    }

    public void verifyResponseBody(boolean actual, boolean expected) {
        assertEquals(actual, expected);
    }

    public void verifyStringEquals(String actual, String expected) {
        assertThat(actual).isNotNull();
        assertThat(actual).isNotBlank();
        assertEquals(actual, expected);
    }

    public void verifyIntEquals(int actual, int expected) {
        assertEquals(actual, expected);
    }

    public void verifyBooleanEquals(boolean actual, boolean expected) {
        assertEquals(actual, expected);
    }
    public void verifyNotNull(Integer keyExpect) {
        assertThat(keyExpect).isNotNull();
    }

    public void verifyStringKeyNotNull(String keyExpect) {
        assertThat(keyExpect).isNotNull();
    }

    public void verifyResponseTime(Response response, long maxMillis) {
        assertEquals(response.getTime(), maxMillis);
    }

    public void verifyContentType(Response response, String expectedContentType) {
        assertEquals(response.getContentType(), expectedContentType);
    }

    public void verifyTrue(boolean condition, String description) {
        assertEquals(condition, true, description);
    }
}
