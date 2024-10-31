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

import static fixtures.UserHandler.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static utils.Initializer.initialize;


@RunWith(Parameterized.class)
public class TestEditUser {
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
        cleanupUser(Constants.UPDATED_TEST_EMAIL, Constants.TEST_PASSWORD);
        cleanupUser(Constants.TEST_EMAIL, Constants.UPDATED_TEST_PASSWORD);
    }

    public TestEditUser(boolean isAuthorized){
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

    @Test
    @DisplayName("try to update user name")
    @Description("Test update user name")
    public void updateName(){
        models.user.login.Request loginRequest = new models.user.login.Request(Constants.TEST_EMAIL, Constants.TEST_PASSWORD);
        String token = getAuthToken(loginRequest);
        models.user.update.Request request = new models.user.update.Request();
        request.setName(Constants.UPDATED_USERNAME);
        if (isAuthorized){
            models.user.update.Response response = updateUser(request, token, HttpStatus.SC_OK);
            assertTrue("Response should be success", response.isSuccess());
            assertEquals("Username should be updated", response.getUser().getName(), Constants.UPDATED_USERNAME);
            assertEquals("Email shouldn't be updated", response.getUser().getEmail(), Constants.TEST_EMAIL);
        } else {
            models.user.update.Response response = updateUser(request, HttpStatus.SC_UNAUTHORIZED);
            assertFalse("Response should not be success", response.isSuccess());
            assertEquals("Error message should be correct", response.getMessage(), Constants.UNAUTHORIZED_ERROR);
        }
    }

    @Step("Request token with credentials")
    private String getTokenWithCredentials(models.user.login.Request request){
        return getAuthToken(request);
    }

    @Test
    @DisplayName("try to update user email")
    @Description("Test update user email")
    public void updateEmail(){
        models.user.login.Request loginRequest = new models.user.login.Request(Constants.TEST_EMAIL, Constants.TEST_PASSWORD);
        String token = getAuthToken(loginRequest);
        models.user.update.Request request = new models.user.update.Request();
        request.setEmail(Constants.UPDATED_TEST_EMAIL);
        if (isAuthorized){
            models.user.update.Response response = updateUser(request, token, HttpStatus.SC_OK);
            assertTrue("Response should be success", response.isSuccess());
            assertEquals("Username shouldn't be updated", response.getUser().getName(), Constants.TEST_USERNAME);
            assertEquals("Email should be updated", response.getUser().getEmail(), Constants.UPDATED_TEST_EMAIL);

            models.user.login.Request oldCredLoginRequest = new models.user.login.Request(Constants.TEST_EMAIL, Constants.TEST_PASSWORD);
            String oldCredToken = getTokenWithCredentials(oldCredLoginRequest);

            assertNull("Token must be null", oldCredToken);

            models.user.login.Request newCredLoginRequest = new models.user.login.Request(Constants.UPDATED_TEST_EMAIL, Constants.TEST_PASSWORD);
            String newCredToken = getTokenWithCredentials(newCredLoginRequest);
            assertNotNull("Token should appear!", newCredToken);
        } else {
            models.user.update.Response response = updateUser(request, HttpStatus.SC_UNAUTHORIZED);
            assertFalse("Response should not be success", response.isSuccess());
            assertEquals("Error message should be correct", response.getMessage(), Constants.UNAUTHORIZED_ERROR);
        }
    }

    @Test
    @DisplayName("try to update user password")
    @Description("Test update user password")
    public void updatePassword(){
        models.user.login.Request loginRequest = new models.user.login.Request(Constants.TEST_EMAIL, Constants.TEST_PASSWORD);
        String token = getAuthToken(loginRequest);
        models.user.update.Request request = new models.user.update.Request();
        request.setPassword(Constants.UPDATED_TEST_PASSWORD);
        if (isAuthorized){
            models.user.update.Response response = updateUser(request, token, HttpStatus.SC_OK);
            assertTrue("Response should be success", response.isSuccess());
            assertEquals("Username shouldn't be updated", response.getUser().getName(), Constants.TEST_USERNAME);
            assertEquals("Email shouldn't be updated", response.getUser().getEmail(), Constants.TEST_EMAIL);

            models.user.login.Request oldCredLoginRequest = new models.user.login.Request(Constants.TEST_EMAIL, Constants.TEST_PASSWORD);
            String oldCredToken = getTokenWithCredentials(oldCredLoginRequest);

            assertNull("Token must be null", oldCredToken);

            models.user.login.Request newCredLoginRequest = new models.user.login.Request(Constants.TEST_EMAIL, Constants.UPDATED_TEST_PASSWORD);
            String newCredToken = getTokenWithCredentials(newCredLoginRequest);
            assertNotNull("Token should appear", newCredToken);
        } else {
            models.user.update.Response response = updateUser(request, HttpStatus.SC_UNAUTHORIZED);
            assertFalse("Response should not be success", response.isSuccess());
            assertEquals("Error message should be correct", response.getMessage(), Constants.UNAUTHORIZED_ERROR);
        }
    }

}
