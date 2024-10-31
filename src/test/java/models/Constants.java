package models;

public class Constants {
    public static final String BASE_URL = "https://stellarburgers.nomoreparties.site";

    public static final String USER_CREATE_ROUTE = "api/auth/register";
    public static final String USER_LOGIN_ROUTE = "api/auth/login";
    public static final String USER_ROUTE = "api/auth/user";
    public static final String ORDER_ROUTE = " api/orders";

    public static final String TEST_USERNAME = "Test Username";
    public static final String TEST_EMAIL = "testemail@example.ru";
    public static final String TEST_PASSWORD = "123456789";

    public static final String UPDATED_USERNAME = "Updated Test Username";
    public static final String UPDATED_TEST_EMAIL = "updated-testemail@example.ru";
    public static final String UPDATED_TEST_PASSWORD = "updated_123456789";

    public static final String TEST_CORRECT_INGREDIENT_HASH = "61c0c5a71d1f82001bdaaa6d";
    public static final String TEST_INCORRECT_INGREDIENT_HASH = "incorrect-hash";

    public static final String NOT_ENOUGH_DATA_ERROR = "Email, password and name are required fields";
    public static final String DUPLICATE_ERROR = "User already exists";
    public static final String INCORRECT_CREDENTIALS = "email or password are incorrect";
    public static final String UNAUTHORIZED_ERROR = "You should be authorised";
    public static final String NOT_ENOUGH_INGREDIENTS_ERROR = "Ingredient ids must be provided";
    public static final String WRONG_INGREDIENTS_ERROR = "";
}
