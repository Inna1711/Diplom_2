import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import models.Constants;
import models.user.create.Request;
import models.user.create.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static fixtures.OrdersHandler.createOrderWithAuth;
import static fixtures.OrdersHandler.creteOrderWithoutAuth;
import static fixtures.UserHandler.*;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;
import static utils.Initializer.initialize;


@RunWith(Parameterized.class)
public class TestCreateOrder {
    Boolean isAuthorized;

    @BeforeClass
    public static void setUp(){
        initialize();
    }

    @Before
    @Step("Create user")
    public void setUpUser(){
        Request request = new Request(Constants.TEST_USERNAME, Constants.TEST_EMAIL, Constants.TEST_PASSWORD);
        io.restassured.response.Response apiResponse = createUserHandler(request);
        Response response = apiResponse.then().assertThat().statusCode(HttpStatus.SC_OK).extract().as(Response.class);
        MatcherAssert.assertThat(response, notNullValue());
    }

    @Step("Get auth token!")
    private String getAuthToken(models.user.login.Request request){
        models.user.login.Response response = loginUser(request);
        return response.getAccessToken();
    }

    private void cleanupUser(String email, String password){
        models.user.login.Request request = new models.user.login.Request(email, password);
        String token = getAuthToken(request);
        if (token != null){
            deleteUser(token);
        }

    }

    @After
    @Step("Cleanup user")
    public void cleanup(){
        cleanupUser(Constants.TEST_EMAIL, Constants.TEST_PASSWORD);
    }

    public TestCreateOrder(boolean isAuthorized){
        this.isAuthorized = isAuthorized;
    }

    @Parameterized.Parameters
    public static Boolean[][] getAuthorizationInit(){
        return new Boolean[][]{
                {
                        true
                },
                {
                        false
                }
        };
    }

    @Step("Create correct order")
    private void createCorrectOrder(models.orders.create.Request request, String token, Integer statusCode){
        if (!isAuthorized){
            var response = creteOrderWithoutAuth(request, HttpStatus.SC_FORBIDDEN);
            assertNull("Name should be null", response.getName());
            assertFalse("Response shouldn't be positive", response.isSuccess());
            assertNotNull("Message shouldn't be null", response.getMessage());
        } else {
            io.restassured.response.Response responseRaw = createOrderWithAuth(request, token);
            var response = responseRaw.then().statusCode(statusCode).extract().as(models.orders.create.Response.class);
            assertNotNull("Name shouldn't be null", response.getName());
            assertTrue("Response should be positive", response.isSuccess());
            assertNull("Message should be null", response.getMessage());
        }
    }

    @Step("Create incorrect order")
    private void createIncorrectOrder(models.orders.create.Request request, String token, int statusCode, String message){
        if (!isAuthorized){
            var response = creteOrderWithoutAuth(request, HttpStatus.SC_FORBIDDEN);
            assertNull("Name should be null", response.getName());
            assertFalse("Response shouldn't be positive", response.isSuccess());
            assertEquals("Message should be correct", Constants.UNAUTHORIZED_ERROR, response.getMessage());
        } else {
            if (statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR){
                creteOrderWithoutAuth(request);
                return;
            }
            io.restassured.response.Response responseRaw = createOrderWithAuth(request, token);
            var response = responseRaw.then().statusCode(statusCode).extract().as(models.orders.create.Response.class);
            assertEquals("Message should be correct", message, response.getMessage());
            assertFalse("Response shouldn't be positive", response.isSuccess());
            assertNull("Name should be null", response.getName());
        }
    }

    @Test
    @DisplayName("try to create order with ingredients")
    @Description("Test create order with ingredients")
    public void correctCreateOrderWithIngredients(){
        models.orders.create.Request request = new models.orders.create.Request(new String[]{
                Constants.TEST_CORRECT_INGREDIENT_HASH
        });
        models.user.login.Request loginRequest = new models.user.login.Request(Constants.TEST_EMAIL, Constants.TEST_PASSWORD);
        String token = getAuthToken(loginRequest);
        createCorrectOrder(request, token, HttpStatus.SC_OK);
    }

    @Test
    @DisplayName("try to create order without ingredients")
    @Description("Test create order without ingredients")
    public void incorrectCreateOrderWithoutIngredients(){
        models.orders.create.Request request = new models.orders.create.Request(new String[]{});
        models.user.login.Request loginRequest = new models.user.login.Request(Constants.TEST_EMAIL, Constants.TEST_PASSWORD);
        String token = getAuthToken(loginRequest);
        createIncorrectOrder(request, token, HttpStatus.SC_BAD_REQUEST, Constants.NOT_ENOUGH_INGREDIENTS_ERROR);
    }

    @Test
    @DisplayName("try to create order with wrong ingredients")
    @Description("Test create order with wrong ingredients")
    public void incorrectCreateOrderWithWrongIngredients(){
        models.orders.create.Request request = new models.orders.create.Request(new String[]{Constants.TEST_INCORRECT_INGREDIENT_HASH});
        models.user.login.Request loginRequest = new models.user.login.Request(Constants.TEST_EMAIL, Constants.TEST_PASSWORD);
        String token = getAuthToken(loginRequest);
        createIncorrectOrder(request, token, HttpStatus.SC_INTERNAL_SERVER_ERROR, Constants.WRONG_INGREDIENTS_ERROR);
    }


}
