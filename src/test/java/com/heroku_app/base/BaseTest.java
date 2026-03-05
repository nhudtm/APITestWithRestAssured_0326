package com.heroku_app.base;

import com.heroku_app.asserts.AssertActions;
import com.heroku_app.commons.GlobalConstants;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;


import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.io.File;
import com.google.gson.Gson;
import com.heroku_app.endpoints.APIConstants;
import com.heroku_app.modules.PayloadManager;

public class BaseTest {
    public RequestSpecification requestSpecification;
    public AssertActions assertActions;
    public PayloadManager payloadManager;
    public JsonPath jsonPath;
    public Response response;
    public ValidatableResponse validatableResponse;

    @BeforeSuite
    public void deleteFileInReport() {
           // Remove all file in Allure attachment (json file)
        deleteAllFileInFolder("allure-results");
    }

    public void deleteAllFileInFolder(String folderName) {
        try {
            String pathFolderDownload = GlobalConstants.PROJECT_PATH + GlobalConstants.FILE_SEPARATOR + folderName;
            File file = new File(pathFolderDownload);
            File[] listOfFiles = file.listFiles();
            if (listOfFiles.length != 0) {
                for (int i = 0; i < listOfFiles.length; i++) {
                    if (listOfFiles[i].isFile() && !listOfFiles[i].getName().equals("environment.properties")) {
                        new File(listOfFiles[i].toString()).delete();
                    }
                }
            }
        } catch (Exception e) {
            System.out.print(e.getMessage());
        }
    }

    @BeforeMethod
    public void setUp() {
        payloadManager = new PayloadManager();
        assertActions = new AssertActions();

        requestSpecification = new RequestSpecBuilder()
                .setBaseUri(APIConstants.BASE_URL)
                .addHeader("Content-Type", "application/json")
                .build()
                .log()
                .all();
    }

    public String getToken() {
        // Set up the request specification for authentication
        requestSpecification = RestAssured.given();
        requestSpecification.baseUri(APIConstants.BASE_URL)
                .basePath(APIConstants.AUTH_URL);

        // Create the authentication payload and send the POST request to get the token
        String payload = payloadManager.setAuthPayload();
        response = requestSpecification.contentType(ContentType.JSON)
                .body(payload)
                .when()
                .post();

        // Extract and return the token from the response
        return payloadManager.getTokenFromJSON(response.asString());
    }

    public String buildBody(Object payload) {
        if (payload == null) {
            throw new IllegalArgumentException("Payload object cannot be null");
        }
        if (payload instanceof String) {
            return (String) payload;
        }
        return new Gson().toJson(payload);
    }

    @AfterMethod
    public void tearDown() {
        System.out.println("Finished the Test!");
    }

    // Additional _ Clean up resources after all tests are done
    @AfterClass
    public void cleanUp() {
        System.out.println("All tests completed. Cleaning up resources if necessary.");
        RestAssured.reset();
    }
}
