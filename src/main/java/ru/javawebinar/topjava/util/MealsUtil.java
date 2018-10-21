package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExceed;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;

public class MealsUtil {
    public static Map<Integer, UserMeal> meals = new ConcurrentHashMap<>();

    static {
        UserMeal meal = new UserMeal(LocalDateTime.of(2015, Month.MAY, 30, 10, 0), "Завтрак", 500);
        meals.put(meal.getId(), meal);
        meal = new UserMeal(LocalDateTime.of(2015, Month.MAY, 30, 13, 0), "Обед", 1000);
        meals.put(meal.getId(), meal);
        meal = new UserMeal(LocalDateTime.of(2015, Month.MAY, 30, 20, 0), "Ужин", 500);
        meals.put(meal.getId(), meal);
        meal = new UserMeal(LocalDateTime.of(2015, Month.MAY, 31, 10, 0), "Завтрак", 1000);
        meals.put(meal.getId(), meal);
        meal = new UserMeal(LocalDateTime.of(2015, Month.MAY, 31, 13, 0), "Обед", 500);
        meals.put(meal.getId(), meal);
        meal = new UserMeal(LocalDateTime.of(2015, Month.MAY, 31, 20, 0), "Ужин", 510);
        meals.put(meal.getId(), meal);
    }

    public static final int CALORIES_PERDAY = 2000;

    public static void main(String[] args) {
        List<UserMealWithExceed> mealsWithExceeded = getFilteredWithExceeded(new ArrayList<>(meals.values()), LocalTime.of(7, 0), LocalTime.of(12, 0), CALORIES_PERDAY);
        mealsWithExceeded.forEach(System.out::println);

        System.out.println(getFilteredWithExceededByCycle(new ArrayList<>(meals.values()), LocalTime.of(7, 0), LocalTime.of(12, 0), CALORIES_PERDAY));
        System.out.println(getFilteredWithExceededInOnePass(new ArrayList<>(meals.values()), LocalTime.of(7, 0), LocalTime.of(12, 0), CALORIES_PERDAY));
        System.out.println(getFilteredWithExceededInOnePass2(new ArrayList<>(meals.values()), LocalTime.of(7, 0), LocalTime.of(12, 0), CALORIES_PERDAY));
    }

    public static List<UserMealWithExceed> getFilteredWithExceeded(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesSumByDate = meals.stream()
                .collect(
                        Collectors.groupingBy(UserMeal::getDate, Collectors.summingInt(UserMeal::getCalories))
//                      Collectors.toMap(UserMeal::getDate, UserMeal::getCalories, Integer::sum)
                );

        return meals.stream()
                .filter(meal -> TimeUtil.isBetween(meal.getTime(), startTime, endTime))
                .map(meal -> createWithExceed(meal, caloriesSumByDate.get(meal.getDate()) > caloriesPerDay))
                .collect(toList());
    }

    public static List<UserMealWithExceed> getFilteredWithExceededByCycle(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {

        final Map<LocalDate, Integer> caloriesSumByDate = new HashMap<>();
        meals.forEach(meal -> caloriesSumByDate.merge(meal.getDate(), meal.getCalories(), Integer::sum));

        final List<UserMealWithExceed> mealsWithExceeded = new ArrayList<>();
        meals.forEach(meal -> {
            if (TimeUtil.isBetween(meal.getTime(), startTime, endTime)) {
                mealsWithExceeded.add(createWithExceed(meal, caloriesSumByDate.get(meal.getDate()) > caloriesPerDay));
            }
        });
        return mealsWithExceeded;
    }

    public static List<UserMealWithExceed> getFilteredWithExceededInOnePass(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        Collection<List<UserMeal>> list = meals.stream()
                .collect(Collectors.groupingBy(UserMeal::getDate)).values();

        return list.stream().flatMap(dayMeals -> {
            boolean exceed = dayMeals.stream().mapToInt(UserMeal::getCalories).sum() > caloriesPerDay;
            return dayMeals.stream().filter(meal ->
                    TimeUtil.isBetween(meal.getTime(), startTime, endTime))
                    .map(meal -> createWithExceed(meal, exceed));
        }).collect(toList());
    }

    public static List<UserMealWithExceed> getFilteredWithExceededInOnePass2(List<UserMeal> meals, LocalTime startTime, LocalTime endTime, int caloriesPerDay) {
        final class Aggregate {
            private final List<UserMeal> dailyMeals = new ArrayList<>();
            private int dailySumOfCalories;

            private void accumulate(UserMeal meal) {
                dailySumOfCalories += meal.getCalories();
                if (TimeUtil.isBetween(meal.getDateTime().toLocalTime(), startTime, endTime)) {
                    dailyMeals.add(meal);
                }
            }

            // never invoked if the upstream is sequential
            private Aggregate combine(Aggregate that) {
                this.dailySumOfCalories += that.dailySumOfCalories;
                this.dailyMeals.addAll(that.dailyMeals);
                return this;
            }

            private Stream<UserMealWithExceed> finisher() {
                final boolean exceed = dailySumOfCalories > caloriesPerDay;
                return dailyMeals.stream().map(meal -> createWithExceed(meal, exceed));
            }
        }

        Collection<Stream<UserMealWithExceed>> values = meals.stream()
                .collect(Collectors.groupingBy(UserMeal::getDate,
                        Collector.of(Aggregate::new, Aggregate::accumulate, Aggregate::combine, Aggregate::finisher))
                ).values();

        return values.stream().flatMap(identity()).collect(toList());
    }

    public static UserMealWithExceed createWithExceed(UserMeal meal, boolean exceeded) {
        return new UserMealWithExceed(meal.getId(), meal.getDateTime(), meal.getDescription(), meal.getCalories(), exceeded);
    }

    public static List<UserMealWithExceed> convertWithExceeded(List<UserMeal> meals, int caloriesPerDay) {
        Map<LocalDate, Integer> caloriesSumByDate = meals.stream()
                .collect(
                        Collectors.groupingBy(UserMeal::getDate, Collectors.summingInt(UserMeal::getCalories))
//                      Collectors.toMap(UserMeal::getDate, UserMeal::getCalories, Integer::sum)
                );

        return meals.stream()
                .map(meal -> createWithExceed(meal, caloriesSumByDate.get(meal.getDate()) > caloriesPerDay))
                .collect(toList());
    }
}