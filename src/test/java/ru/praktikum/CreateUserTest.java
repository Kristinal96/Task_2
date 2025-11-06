package ru.praktikum;


import io.restassured.RestAssured;
import io.restassured.response.Response;
import ru.praktikum.api.UserApi;
import ru.praktikum.models.User;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.Matchers.equalTo;


public class CreateUserTest {
    private static UserApi userApi;
    private String userId;
    private String accessToken;

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = "https://stellarburgers.education-services.ru";
        userApi = new UserApi();
    }

    @Before
    public void prepareTestData() {
        // Создаем пользователя
        User user = new User("TdPunique_email@mail.ru", "password123", "John Doe");
        Response response = userApi.createUser(user);
        userId = response.jsonPath().getString("user.id");

        // Логинимся и получаем токен
        User loginUser = new User("TdPunique_email@mail.ru", "password123", "John Doe");
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

    @Test
    public void testCreateUniqueUser() {
        User uniqueUser = new User("TqdPiiiiiiiunique_email@mail.ru", "password123", "John Doe");
        Response response = userApi.createUser(uniqueUser);
        System.out.println("Create Unique User Status Code: " + response.statusCode());
        response.then()
                .statusCode(200)
                .body("success", equalTo(true));
        userId = response.jsonPath().getString("user.id");
    }

    @Test
    public void testCreateExistingUser() {
        User existingUser = new User("TyqdPiiiiiiiunique_email@mail.ru", "password123", "John Doe");

        // Создаем пользователя первый раз
        Response firstResponse = userApi.createUser(existingUser);
        System.out.println("First Create User Status Code: " + firstResponse.statusCode());
        firstResponse.then()
                .statusCode(200); // Ожидаем успешное создание

        // Повторяем попытку создать существующего пользователя
        Response secondResponse = userApi.createUser(existingUser);
        System.out.println("Second Create User Status Code: " + secondResponse.statusCode());
        secondResponse.then()
                .statusCode(403)
                .body("message", equalTo("User already exists"));
    }

    @Test
    public void testCreateUserWithMissingField() {
        User incompleteUser = new User("TPiiiiincomplete_email@mail.ru", "", "John Doe");
        Response response = userApi.createUser(incompleteUser);
        System.out.println("Create User With Missing Field Status Code: " + response.statusCode());
        response.then()
                .statusCode(403)
                .body("message", equalTo("Email, password and name are required fields"));
    }
}