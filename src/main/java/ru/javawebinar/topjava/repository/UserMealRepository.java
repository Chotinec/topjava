package ru.javawebinar.topjava.repository;

import ru.javawebinar.topjava.model.UserMeal;

import java.util.List;

public interface UserMealRepository {
    UserMeal save(UserMeal meal);
    void delete(int id);
    UserMeal getById(int id);
    List<UserMeal> getAll();
}
