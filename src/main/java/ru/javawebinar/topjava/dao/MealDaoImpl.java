package ru.javawebinar.topjava.dao;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.ArrayList;
import java.util.List;

public class MealDaoImpl implements MealDao {
    @Override
    public void add(Meal meal) {
        MealsUtil.meals.put(meal.getId(), meal);
    }

    @Override
    public void delete(int id) {
        MealsUtil.meals.remove(id);
    }

    @Override
    public void update(Meal meal) {
        MealsUtil.meals.replace(meal.getId(), meal);
    }

    @Override
    public Meal getById(int id) {
        return MealsUtil.meals.get(id);
    }

    @Override
    public List<Meal> getAll() {
        return new ArrayList<>(MealsUtil.meals.values());
    }
}
