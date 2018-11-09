package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))
public class MealServiceTest {

    static {
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService mealService;

    @Test
    public void get() {
        Meal meal = mealService.get(MEAL1.getId(), USER_ID);
        assertMatch(meal, MEAL1);
    }

    @Test
    public void delete() {
        mealService.delete(MEAL1.getId(), USER_ID);
        List<Meal> meals = mealService.getAll(USER_ID);
        assertMatch(meals, MEAL3, MEAL2);
    }

    @Test
    public void getBetweenDateTimes() {
        List<Meal> meals = mealService.getBetweenDateTimes(LocalDateTime.of(2015, Month.MAY, 30, 11, 0),
                LocalDateTime.of(2015, Month.MAY, 30, 16, 0),
                USER_ID);
        assertMatch(meals, MEAL2);
    }

    @Test
    public void getAll() {
        List<Meal> meals = mealService.getAll(USER_ID);
        assertMatch(meals, MEAL3, MEAL2, MEAL1);
    }

    @Test
    public void update() {
        Meal updated = new Meal(MEAL1);
        updated.setCalories(510);
        updated.setDescription("updatedDescription");
        mealService.update(updated, USER_ID);
        assertMatch(updated, mealService.get(MEAL1.getId(), USER_ID));
    }

    @Test
    public void create() {
        Meal meal = new Meal(LocalDateTime.of(2015, Month.MAY, 30, 10, 0), "Завтрак", 500);
        Meal created = mealService.create(meal, USER_ID);
        meal.setId(created.getId());
        assertMatch(created, mealService.get(meal.getId(), USER_ID));
    }

    @Test(expected = NotFoundException.class)
    public void notFoundDelete() throws Exception {
        mealService.delete(1, USER_ID);
    }

    @Test(expected = NotFoundException.class)
    public void notFoundGet() throws Exception {
        mealService.get(1, USER_ID);
    }

    @Test(expected = Exception.class)
    public void notFoundUpdate() throws Exception {
        mealService.update(MEAL1, 2);
    }
}