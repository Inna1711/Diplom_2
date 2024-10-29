package models;

public class Constants {
    public static final String BASE_URL = "https://stellarburgers.nomoreparties.site";

    public static final String USER_CREATE_ROUTE = "api/auth/register";
    public static final String USER_LOGIN_ROUTE = "api/auth/login";
    public static final String USER_ROUTE = "api/auth/user";

    public static final String TEST_USERNAME = "Test Username";
    public static final String TEST_EMAIL = "testemail@example.ru";
    public static final String TEST_PASSWORD = "123456789";

    public static final String NOT_ENOUGH_DATA_ERROR = "Email, password and name are required fields";
    public static final String DUPLICATE_ERROR = "User already exists";
}
