package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExceed;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

public class UserMealsUtil {
    public static void main(String[] args) {
        List<UserMeal> mealList = Arrays.asList(
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30,10,0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30,13,0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30,20,0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31,10,0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31,13,0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31,20,0), "Ужин", 510)
        );

        List<UserMealWithExceed> filteredUserMealWithExceeded = getFilteredWithExceeded(mealList, LocalTime.of(7, 0), LocalTime.of(12,0), 2000);
        filteredUserMealWithExceeded.forEach(item -> System.out.println(item.toString()));
    }

    public static List<UserMealWithExceed>  getFilteredWithExceededWithCycle(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> sumCaloriesPerDay = new HashMap<>();
        for (UserMeal userMeal : mealList) {
            LocalDate localDate = userMeal.getDateTime().toLocalDate();
            int calories;
            if (sumCaloriesPerDay.containsKey(localDate)) {
               calories = sumCaloriesPerDay.get(userMeal.getDateTime().toLocalDate()) + userMeal.getCalories();
            } else {
                calories = userMeal.getCalories();
            }
            sumCaloriesPerDay.put(localDate, calories);
        }

        List<UserMealWithExceed> filteredUserMealWithExceeded = new ArrayList<>();
        for (UserMeal userMeal : mealList) {
            if (TimeUtil.isBetween(userMeal.getDateTime().toLocalTime(), startTime, endTime)) {

                filteredUserMealWithExceeded.add(new UserMealWithExceed(userMeal.getDateTime(),
                        userMeal.getDescription(),
                        userMeal.getCalories(),
                        sumCaloriesPerDay.get(userMeal.getDateTime().toLocalDate()) > caloriesPerDay));
            }
        }

        return filteredUserMealWithExceeded;
    }

    public static List<UserMealWithExceed>  getFilteredWithExceeded(List<UserMeal> mealList, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesSumByDate =  mealList.stream().collect(Collectors.groupingBy(um->um.getDateTime().toLocalDate(),
                Collectors.summingInt(UserMeal::getCalories)));

        return mealList.stream().
                        filter(um->TimeUtil.
                        isBetween(um.getDateTime().toLocalTime(),startTime, endTime)).
                        map(um->new UserMealWithExceed(um.getDateTime(), um.getDescription(), um.getCalories(), caloriesSumByDate.get(um.getDateTime().toLocalDate()) > caloriesPerDay)).
                        collect(Collectors.toList());
    }
}
