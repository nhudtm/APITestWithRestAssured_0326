package com.heroku_app.asserts;

import io.restassured.response.Response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;

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

    public void verifyStatusCode(Response response, Integer expected) {
        assertEquals(response.getStatusCode(), (int) expected);
    }

    public void verifyStringKey(String keyExpect, String keyActual) {
        assertThat(keyExpect).isNotNull();
        assertThat(keyExpect).isNotBlank();
        assertThat(keyExpect).isEqualTo(keyActual);
    }

    public void verifyStringKeyNotNull(Integer keyExpect) {
        assertThat(keyExpect).isNotNull();
    }

    public void verifyStringKeyNotNull(String keyExpect) {
        assertThat(keyExpect).isNotNull();
    }

    public void verifyResponseTime(Response response, long maxMillis) {
        assertThat(response.getTime()).isLessThan(maxMillis);
    }

    public void verifyContentType(Response response, String expectedContentType) {
        assertThat(response.getContentType()).contains(expectedContentType);
    }

    public void verifyTrue(boolean condition, String description) {
        assertThat(condition).as(description).isTrue();
    }
}
