package fixtures;

import models.Constants;
import models.user.create.Request;
import models.user.update.Response;
import org.apache.http.HttpStatus;

import static io.restassured.RestAssured.given;

public class UserHandler {
    public static io.restassured.response.Response createUserHandler(Request userData){
        return  given().
                header("Content-type", "application/json").
                body(userData).
                post(Constants.USER_CREATE_ROUTE);
    }

    public static models.user.login.Response loginUser(models.user.login.Request request){
        return given().
                header("Content-type", "application/json").
                body(request).
                post(Constants.USER_LOGIN_ROUTE).body().as(models.user.login.Response.class);
    }

    public static models.user.login.Response loginUser(models.user.login.Request request, int statusCode){
        return given().
                header("Content-type", "application/json").
                body(request).
                post(Constants.USER_LOGIN_ROUTE).
                then().assertThat().statusCode(statusCode).
                extract().as(models.user.login.Response.class);
    }

    public static void deleteUser(String authToken){
        given().
                header("Content-type", "application/json").
                header("Authorization", authToken).
                delete(Constants.USER_ROUTE).then().statusCode(HttpStatus.SC_ACCEPTED);
    }

    public static Response updateUser(models.user.update.Request request, int statusCode){
        return given().
                header("Content-type", "application/json").
                body(request).patch(Constants.USER_ROUTE).then().statusCode(statusCode).extract().as(Response.class);
    }

    public static Response updateUser(models.user.update.Request request, String authToken, int statusCode){
        return given().
                header("Content-type", "application/json").
                header("Authorization", authToken).
                body(request).patch(Constants.USER_ROUTE).then().statusCode(statusCode).extract().as(Response.class);
    }
}
