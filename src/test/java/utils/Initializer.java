package utils;

import io.restassured.RestAssured;
import models.Constants;

public class Initializer {
    public static boolean isInitialized = false;

    public static void initialize(){
        if (!isInitialized){
            RestAssured.baseURI = Constants.BASE_URL;
        }
    }
}