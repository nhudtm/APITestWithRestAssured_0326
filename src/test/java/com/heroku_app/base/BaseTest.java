package com.heroku_app.base;

import com.heroku_app.apis.AuthAPI;
import com.heroku_app.apis.BookingAPI;
import com.heroku_app.asserts.AssertActions;
import com.heroku_app.client.RestClient;
import com.heroku_app.commons.GlobalConstants;
import com.heroku_app.config.PropertiesConfig;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.io.File;
import com.heroku_app.endpoints.APIConstants;
import com.heroku_app.factories.PayloadFactory;
import com.heroku_app.spec.RequestSpecFactory;

public class BaseTest {

    public AssertActions assertActions;
    public PayloadFactory payloadFactory;
    public RestClient restClient;
    public BookingAPI bookingAPI;
    public AuthAPI authAPI;
  


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
        RequestSpecification requestSpec = RequestSpecFactory.createDefaultSpec();
        restClient = new RestClient(requestSpec);
        assertActions = new AssertActions();
        payloadFactory = new PayloadFactory();
        bookingAPI = new BookingAPI(restClient);
        authAPI = new AuthAPI(restClient, payloadFactory);

    }

    public String getToken() {
        return authAPI.getToken(PropertiesConfig.getRequiredProperty("username"), PropertiesConfig.getRequiredProperty("password"));
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
