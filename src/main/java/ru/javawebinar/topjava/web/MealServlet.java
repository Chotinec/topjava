package ru.javawebinar.topjava.web;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.repository.UserMealRepository;
import ru.javawebinar.topjava.repository.InMemoryUserMealRepository;
import ru.javawebinar.topjava.util.IdCounter;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

public class MealServlet extends HttpServlet {
    private UserMealRepository mealDao;

    private static String INSERT_OR_EDIT = "/meal.jsp";
    private static String LIST_MEAL = "/meals.jsp";

    public MealServlet() {
        super();
        mealDao = new InMemoryUserMealRepository();
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String forward="";
        String action = request.getParameter("action");

        if (action == null) action = "allMeal";

        int mealId;
        switch (action) {
            case "delete":
                mealId = Integer.parseInt(request.getParameter("mealId"));
                mealDao.delete(mealId);
                forward = LIST_MEAL;
                request.setAttribute("meals",  MealsUtil.convertWithExceeded(mealDao.getAll(), MealsUtil.CALORIES_PERDAY));
                break;
            case "edit":
                forward = INSERT_OR_EDIT;
                mealId = Integer.parseInt(request.getParameter("mealId"));
                UserMeal meal = mealDao.getById(mealId);
                request.setAttribute("meal", meal);
                break;
            case "allMeal":
                forward = LIST_MEAL;
                request.setAttribute("meals", MealsUtil.convertWithExceeded(mealDao.getAll(), MealsUtil.CALORIES_PERDAY));
                break;
                default:
                    forward = INSERT_OR_EDIT;
                    break;
        }

        RequestDispatcher view = request.getRequestDispatcher(forward);
        view.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        LocalDateTime dateTime = LocalDateTime.parse(request.getParameter("dateTime"));
        String description = request.getParameter("description");
        int calories = Integer.parseInt(request.getParameter("calories"));

        String mealId = request.getParameter("mealId");
        UserMeal meal = new UserMeal(dateTime, description, calories);

        if(mealId != "") {
            meal.setId(Integer.parseInt(mealId));
        }

        mealDao.save(meal);
        RequestDispatcher view = request.getRequestDispatcher(LIST_MEAL);
        request.setAttribute("meals", MealsUtil.convertWithExceeded(mealDao.getAll(), MealsUtil.CALORIES_PERDAY));
        view.forward(request, response);
    }
}
