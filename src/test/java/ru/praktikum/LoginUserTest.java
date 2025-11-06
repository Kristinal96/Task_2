package ru.praktikum;


import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.*;
import ru.praktikum.api.UserApi;
import ru.praktikum.models.User;
import static org.hamcrest.Matchers.*;

public class LoginUserTest {
    private static UserApi userApi;
    private String userId;



    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = "https://stellarburgers.education-services.ru";
        userApi = new UserApi();
    }

    @Before
    public void prepareTestData() {
        // Создаем пользователя
        User user = new User("Tunique_email@mail.ru", "password123", "John Doe");
        Response response = userApi.createUser(user);
        userId = response.jsonPath().getString("user.id");

        // Логинимся и получаем токен
        User loginUser = new User("Tunique_email@mail.ru", "password123", "John Doe");
        Response loginResponse = userApi.loginUser(loginUser);
        String accessToken = userApi.extractAndFormatToken(loginResponse);
        System.out.println("Access Token: " + accessToken);
    }

    @After
    public void cleanUp() {
        if (userId != null) {
            userApi.deleteUser(userId);
        }
    }

    // Тест №1: Успешная авторизация с правильными данными
    @Test
    public void testSuccessfulLogin() {
        User validUser = new User("Tunique_email@mail.ru", "password123", "John Doe");
        Response response = userApi.loginUser(validUser);
        response.then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    // Тест №2: Неправильный логин
    @Test
    public void testFailedLoginWithInvalidEmail() {
        User invalidUser = new User("invalid%email@example.com", "valid_password", "");
        Response response = userApi.loginUser(invalidUser);
        response.then()
                .statusCode(401)
                .body("message", equalTo("email or password are incorrect"));
    }

    // Тест №3: Правильная почта, но неправильный пароль
    @Test
    public void testFailedLoginWithInvalidPassword() {
        User invalidUser = new User("valid_user@example.com", "wrong_password", "");
        Response response = userApi.loginUser(invalidUser);
        response.then()
                .statusCode(401)
                .body("message", equalTo("email or password are incorrect"));
    }
}