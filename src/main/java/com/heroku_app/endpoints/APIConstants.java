package com.heroku_app.endpoints;

import com.heroku_app.config.PropertiesConfig;

public class APIConstants {
    public static final String BASE_URL = PropertiesConfig.getRequiredProperty("base.url");
    public static final String CREATE_UPDATE_BOOKING_URL = "/booking";
    public static final String GET_BOOKING_IDS = "/booking";
    public static final String AUTH_URL = "/auth";
    public static final String PING_URL = "/ping";

    private APIConstants() {
    }
}
