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

import static fixtures.UserHandler.*;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;
import static utils.Initializer.initialize;


public class TestLoginUser {

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
        deleteUser(token);
    }

    @After
    @Step("Delete user")
    public void cleanup(){
        cleanupUser(Constants.TEST_EMAIL, Constants.TEST_PASSWORD);
    }

    @Test
    @DisplayName("try to create a user with correct params!")
    @Description("Test create with correct params")
    public void testLoginWithCorrectParams(){
        models.user.login.Request userLoginData = new models.user.login.Request(Constants.TEST_EMAIL, Constants.TEST_PASSWORD);
        models.user.login.Response response = loginUser(userLoginData);
        assertTrue("User is not logged in!", response.isSuccess());
        assertNull("Message should be empty!", response.getMessage());
        assertNotNull("Access token is null!", response.getAccessToken());
        assertNotNull("Refresh token is null!", response.getRefreshToken());
    }

    @Test
    @DisplayName("try to login a user with less params than expected!")
    @Description("Test login with less params")
    public void failLoginWithLessParams(){
        models.user.login.Request userLoginData = new models.user.login.Request(Constants.TEST_EMAIL);
        models.user.login.Response response = loginUser(userLoginData, HttpStatus.SC_UNAUTHORIZED);
        assertEquals("Error message is not correct!", Constants.INCORRECT_CREDENTIALS, response.getMessage());
        assertFalse("Courier shouldn't be created!", response.isSuccess());
    }

    @Test
    @DisplayName("try to login a user with wrong password")
    @Description("Test login a user with wrong password")
    public void failLoginWithWrongParams(){
        models.user.login.Request userLoginData = new models.user.login.Request(Constants.TEST_EMAIL, Constants.TEST_PASSWORD + "Salt");
        models.user.login.Response response = loginUser(userLoginData, HttpStatus.SC_UNAUTHORIZED);
        assertEquals("Error message is not correct!", Constants.INCORRECT_CREDENTIALS, response.getMessage());
        assertFalse("Courier shouldn't be created!", response.isSuccess());
    }
}
