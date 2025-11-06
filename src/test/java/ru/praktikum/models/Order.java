package ru.praktikum.models;

import java.util.List;

public class Order {
    private List<String> ingredients;

    public Order(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    // Геттеры
    public List<String> getIngredients() {
        return ingredients;
    }

    // Сеттеры
    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }
}
