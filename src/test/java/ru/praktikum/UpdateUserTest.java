package ru.praktikum;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.*;
import ru.praktikum.api.UserApi;
import ru.praktikum.models.User;
import static org.hamcrest.Matchers.*;


public class UpdateUserTest {
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
        User user = new User("TWUPZxunique_email@mail.ru", "password123", "John Doe");
        Response response = userApi.createUser(user);
        userId = response.jsonPath().getString("user.id");

        // Логинимся и получаем токен
        User loginUser = new User("TWUPZxunique_email@mail.ru", "password123", "John Doe");
        Response loginResponse = userApi.loginUser(loginUser);
        accessToken = userApi.extractAndFormatToken(loginResponse);
        System.out.println("Access Token: " + accessToken);

    }


    @After
    public void cleanUp() {
        if (userId != null) {
            userApi.deleteUser(userId);
        }
    }

    @Test
    public void testUpdateUserWithAuthorization() {
        User updatedUser = new User("TWUPZxunique_email@mail.ru", "password123", "John DoeO");
        System.out.println("Access Token: " + accessToken);
        Response response = userApi.updateUser(updatedUser, accessToken);
        response.then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    @Test
    public void testUpdateUserWithoutAuthorization() {
        User updatedUser = new User("xunique_email@mail.ru", "password123", "John Doe");
        System.out.println("Access Token: " + accessToken);
        Response response = userApi.updateUser(updatedUser, "invalid_token");
        response.then()
                .statusCode(401)
                .body("message", equalTo("You should be authorised"));
    }

}