package ru.praktikum.api;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import ru.praktikum.models.User;


public class UserApi {
    @Step("Создание пользователя")
    public Response createUser(User user) {
        return RestAssured.given()
                .contentType("application/json")
                .body(user)
                .when()
                .post("/api/auth/register");
    }

    @Step("Авторизация пользователя")
    public Response loginUser(User user) {
        return RestAssured.given()
                .contentType("application/json")
                .body(user)
                .when()
                .post("/api/auth/login")
                .then()
                .extract()
                .response();
    }

    @Step("Изминение пользователя")
    public Response updateUser(User user, String accessToken) {
        return RestAssured.given()
                .log().everything()
                .contentType("application/json")
                .header("Authorization", accessToken)
                .body(user)
                .when()
                .patch("/api/auth/user");

    }

    public String refreshToken(String refreshToken) {
        return RestAssured.given()
                .contentType("application/json")
                .body(refreshToken)
                .when()
                .post("/api/auth/token")
                .then()
                .extract()
                .response()
                .jsonPath()
                .getString("accessToken");
    }

    @Step("Удаление пользователя")
    public Response deleteUser(String userId) {
        return RestAssured.delete("/api/auth/user/" + userId);
    }

    public String extractAndFormatToken(Response response) {
        if (response == null || response.jsonPath().get("accessToken") == null) {
            throw new RuntimeException("Access token is missing in the response");
        }
        String rawToken = response.jsonPath().getString("accessToken");
        if (!rawToken.contains("Bearer")) {
            return "Bearer " + rawToken;
        }
        return rawToken;
    }
}
