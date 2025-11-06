package ru.praktikum;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.*;
import ru.praktikum.api.OrderApi;
import ru.praktikum.api.UserApi;
import ru.praktikum.models.Order;
import ru.praktikum.models.User;

import java.util.Arrays;
import java.util.List;
import static org.hamcrest.Matchers.*;
import java.util.ArrayList;

public class CreateOrderTest {
    private static OrderApi orderApi;
    private static UserApi userApi;
    private String accessToken;
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
        User user = new User("Texxunique_email@mail.ru", "password123", "John Doe");
        Response response = userApi.createUser(user);
        userId = response.jsonPath().getString("user.id");

        // Логинимся и получаем токен
        User loginUser = new User("Texxunique_email@mail.ru", "password123", "John Doe");
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

    /**
     * Тестирует создание заказа без авторизации.
     * Ожидаемый результат: ошибка авторизации (код 401).
     * НО ПРИ ТЕСТИРОВАНИИ ЧЕРЕЗ  Postman ТАК ЖЕ 200, ПЕПЕДАЕТСЯ БЕЗ ЗАГОЛОВКА И БЕЗ ТОКЕНА
     * ДЕФФЕКТ!!!
     */

    @Test
    public void testCreateOrderWithoutAuthorization() {
        User user = new User("TPQSSSRRRRunique_email_test@mail.ru", "password123", "John Doe");
        Response registerResponse = userApi.createUser(user);
        String userId = registerResponse.jsonPath().getString("user.id");
        List<String> ingredients = Arrays.asList("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6f");
        Order order = new Order(ingredients);
        Response response = orderApi.createOrder(order, "J");
        response.then()
                .statusCode(401)
                .body("message", equalTo("You should be authorised"));
    }

    /**
     * Тестирует создание заказа с ингредиентами.
     * Ожидаемый результат: успешное создание заказа (код 200).
     */
    @Test
    public void testCreateOrderWithIngredients() {
        List<String> ingredients = Arrays.asList("61c0c5a71d1f82001bdaaa6d", "61c0c5a71d1f82001bdaaa6f");
        Order order = new Order(ingredients);
        Response response = orderApi.createOrder(order, accessToken);
        response.then()
                .statusCode(200)
                .body("success", equalTo(true));
    }

    /**
     * Тестирует создание заказа без ингредиентов.
     * Ожидаемый результат: ошибка (код 400), так как ингредиенты обязательны.
     */
    @Test
    public void testCreateOrderWithoutIngredients() {
        Order order = new Order(new ArrayList<>());
        Response response = orderApi.createOrder(order, accessToken);
        response.then()
                .statusCode(400)
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    /**
     * Тестирует создание заказа с некорректным хешем ингредиента.
     * Ожидаемый результат: внутренняя ошибка сервера (код 500).
     */
    @Test
    public void testCreateOrderWithInvalidIngredientsHash() {
        List<String> ingredients = Arrays.asList("*");
        Order order = new Order(ingredients);
        Response response = orderApi.createOrder(order, accessToken);
        response.then()
                .statusCode(500)
                .body(containsString("Internal Server Error"));
    }
}
