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
import static fixtures.OrdersHandler.getOrders;
import static fixtures.UserHandler.*;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;
import static utils.Initializer.initialize;


@RunWith(Parameterized.class)
public class TestGetOrder {
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

    public TestGetOrder(boolean isAuthorized){
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

    @Before
    @Step("Create order")
    public void createOrder(){
        String token = getAuthToken(new models.user.login.Request(Constants.TEST_EMAIL, Constants.TEST_PASSWORD));
        models.orders.create.Request order = new models.orders.create.Request(new String[]{Constants.TEST_CORRECT_INGREDIENT_HASH});
        createOrderWithAuth(order, token);
    }

    @Step("Get orders")
    public void getUserOrders(){
        if (!isAuthorized){
            models.orders.get.Response response = getOrders(HttpStatus.SC_UNAUTHORIZED);
            assertEquals("Message should be correct!", Constants.UNAUTHORIZED_ERROR, response.getMessage());
            assertFalse("Response shouldn't be correct", response.isSuccess());
        } else {
            String token = getAuthToken(new models.user.login.Request(Constants.TEST_EMAIL, Constants.TEST_PASSWORD));
            models.orders.get.Response response = getOrders(token, HttpStatus.SC_OK);
            assertTrue("Response should be successful", response.isSuccess());
            assertEquals("Total created orders is not correct", 1, response.getTotal());
        }
    }

    @Test
    @DisplayName("try to create order with ingredients")
    @Description("Test create order with ingredients")
    public void testGetOrders(){
        getUserOrders();
    }
}
