package ru.praktikum.api;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import ru.praktikum.models.Order;

import java.util.List;

public class OrderApi {

    @Step("Создание заказа")
    public Response createOrder(Order order, String accessToken) {
        return RestAssured.given()
                .log().everything()
                .contentType("application/json")
                .header("Authorization", accessToken)
                .body(order)
                .when()
                .post("/api/orders");
    }

    @Step("Создание заказа без авторизации")
    public Response createOrderOrderWithoutAuthorization(Order order, String accessToken) {
        return RestAssured.given()
                .log().everything()
                .contentType("application/json")
                .header("Authorization", accessToken != null ? accessToken : "")
                .body(order)
                .when()
                .post("/api/orders");
    }

    @Step("Получение заказа")
    public Response getOrders(String accessToken) {
        return RestAssured.given()
                .header("Authorization", accessToken)
                .when()
                .log().everything()
                .get("/api/orders");
    }
    public List<String> fetchValidIngredients() {
        return RestAssured.given()
                .when()
                .get("/api/ingredients")
                .then()
                .extract()
                .jsonPath()
                .getList("_id", String.class);
    }
}