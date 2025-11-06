package ru.praktikum;


import io.restassured.RestAssured;
import io.restassured.response.Response;
import ru.praktikum.api.OrderApi;
import ru.praktikum.api.UserApi;
import ru.praktikum.models.User;
import static org.hamcrest.Matchers.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.hamcrest.Matchers.equalTo;


public class GetOrdersTest {
    private static UserApi userApi;
    private String accessToken;
    private static OrderApi orderApi;
    private String userId;


    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = "https://stellarburgers.education-services.ru";
        orderApi = new OrderApi();
        userApi = new UserApi();

    }

    @Before
    public void prepareTestData() {
        // Создаем пользователя
        User user = new User("Trexxunique_email@mail.ru", "password123", "John Doe");
        Response response = userApi.createUser(user);
        userId = response.jsonPath().getString("user.id");

        // Логинимся и получаем токен
        User loginUser = new User("Trexxunique_email@mail.ru", "password123", "John Doe");
        Response loginResponse = userApi.loginUser(loginUser);
        accessToken = userApi.extractAndFormatToken(loginResponse);
        System.out.println("Access Token: " + accessToken);

    }

    @Test
    public void testGetOrdersWithAuthorization() {
        Response response = orderApi.getOrders(accessToken);
        response.then()
                .statusCode(200)
                .body("orders", notNullValue());
    }

    @Test
    public void testGetOrdersWithoutAuthorization() {
        Response response = orderApi.getOrders("invalid_token");
        response.then()
                .statusCode(401)
                .body("message", equalTo("You should be authorised"));
    }
}