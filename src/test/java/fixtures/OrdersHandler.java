package fixtures;

import models.Constants;
import models.orders.create.Request;
import models.orders.create.Response;
import org.apache.http.HttpStatus;

import static io.restassured.RestAssured.given;

public class OrdersHandler {
    public static io.restassured.response.Response createOrderWithAuth(Request request, String token){
        return  given().
                header("Content-type", "application/json").
                header("Authorization", token).
                body(request).
                post(Constants.ORDER_ROUTE);
    }

    public static Response creteOrderWithoutAuth(Request request, int statusCode){
        return  given().
                header("Content-type", "application/json").
                body(request).
                post(Constants.ORDER_ROUTE).
                then().assertThat().statusCode(statusCode).
                extract().as(Response.class);
    }

    public static void creteOrderWithoutAuth(Request request){
        given().
                header("Content-type", "application/json").
                body(request).
                post(Constants.ORDER_ROUTE).
                then().assertThat().statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    public static models.orders.get.Response getOrders(String token, int statusCode){
        return given().
                header("Content-type", "application/json").
                header("Authorization", token).
                get(Constants.ORDER_ROUTE).
                then().assertThat().statusCode(statusCode).
                extract().as(models.orders.get.Response.class);
    }

    public static models.orders.get.Response getOrders(int statusCode){
        return given().
                header("Content-type", "application/json").
                get(Constants.ORDER_ROUTE).
                then().assertThat().statusCode(statusCode).
                extract().as(models.orders.get.Response.class);
    }

}
