import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import models.Constants;
import models.user.create.Request;
import models.user.create.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.MatcherAssert;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import static fixtures.UserHandler.*;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static utils.Initializer.initialize;


public class TestCreateUser {

    @BeforeClass
    public static void setUp(){
        initialize();
    }

    @Step("Send request to user create")
    private Response createUser(Request request, int statusCode){
        io.restassured.response.Response apiResponse = createUserHandler(request);
        Response response = apiResponse.then().assertThat().statusCode(statusCode).extract().as(Response.class);
        MatcherAssert.assertThat(response, notNullValue());
        return response;
    }

    @Step("Login with test credentials")
    private String getAuthToken(models.user.login.Request request){
        models.user.login.Response response = loginUser(request);
        return response.getAccessToken();
    }

    @Step("Delete user")
    private void cleanupUser(String email, String password){
        models.user.login.Request request = new models.user.login.Request(email, password);
        String token = getAuthToken(request);
        if (token != null){
            deleteUser(token);
        }
    }

    @After
    public void cleanup(){
        cleanupUser(Constants.TEST_EMAIL, Constants.TEST_PASSWORD);
    }

    @Test
    @DisplayName("try to create a user with correct params!")
    @Description("Test create with correct params")
    public void testCreateCorrectUser(){
        Request userCreateData = new Request(Constants.TEST_USERNAME, Constants.TEST_EMAIL, Constants.TEST_PASSWORD);
        Response response = createUser(userCreateData, HttpStatus.SC_OK);
        assertTrue("Courier is not created!", response.isSuccess());
        assertNull("Message should be empty!", response.getMessage());
    }

    @Test
    @DisplayName("try to create a user with less params than expected!")
    @Description("Test create with less params")
    public void failCreateCourier(){
        Request userCreateData = new Request(Constants.TEST_USERNAME, Constants.TEST_EMAIL);
        Response response = createUser(userCreateData, HttpStatus.SC_FORBIDDEN);
        assertEquals("Error message is not correct!", Constants.NOT_ENOUGH_DATA_ERROR, response.getMessage());
        assertFalse("Courier shouldn't be created!", response.isSuccess());
    }

    @Test
    @DisplayName("try to create a user duplicate")
    @Description("Test create user for second time")
    public void createUserTwice(){
        Request userCreateData = new Request(Constants.TEST_USERNAME, Constants.TEST_EMAIL, Constants.TEST_PASSWORD);
        Response response = createUser(userCreateData, HttpStatus.SC_OK);
        assertTrue("User is not created!", response.isSuccess());
        assertNull("Message should be empty!", response.getMessage());
        Response failResponse = createUser(userCreateData, HttpStatus.SC_FORBIDDEN);
        assertFalse("User shouldn't be created!", failResponse.isSuccess());
        assertEquals("Error message is not valid!", Constants.DUPLICATE_ERROR, failResponse.getMessage());
    }
}
