package com.heroku_app.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class BuildBodyJSON {

    private static final Gson gson = new GsonBuilder()
            .serializeNulls()
            .create();

    public static String buildBody(Object payload) {
        if (payload == null) {
            throw new IllegalArgumentException("Payload cannot be null");
        }

        if (payload instanceof String) {
            return (String) payload;
        }

        return gson.toJson(payload);
    }
}
