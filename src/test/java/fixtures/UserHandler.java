package fixtures;

import models.Constants;
import models.user.create.Request;
import org.apache.http.HttpStatus;

import static io.restassured.RestAssured.given;

public class UserHandler {
    public static io.restassured.response.Response createUserHandler(Request userData){
        return  given().
                header("Content-type", "application/json").
                body(userData).
                post(Constants.USER_CREATE_ROUTE);
    }

    public static models.user.login.Response loginCourier(models.user.login.Request request){
        return given().
                header("Content-type", "application/json").
                body(request).
                post(Constants.USER_LOGIN_ROUTE).body().as(models.user.login.Response.class);
    }

    public static void deleteCourier(String authToken){
        given().
                header("Content-type", "application/json").
                header("Authorization", authToken).
                delete(Constants.USER_ROUTE).then().statusCode(HttpStatus.SC_ACCEPTED);
    }
}
