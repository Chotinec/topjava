<%--
  Created by IntelliJ IDEA.
  User: Art_PC
  Date: 20.10.2018
  Time: 12:10
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>Meal</title>
</head>
<body>

<h3><a href="meals">Meals</a></h3>
<h2>Meal</h2>
<form method="POST" action='meals' name="frmAddMeal">
    Meal ID : <input type="text" readonly="readonly" name="mealId" value="<c:out value="${meal.id}" />" /> <br />
    DateTime : <input type="text" name="dateTime" value="<c:out value="${meal.dateTime}" />" /><br/>
    Description : <input type="text" name="description" value="<c:out value="${meal.description}" />" /> <br />
    Calories : <input type="text" name="calories" value="<c:out value="${meal.calories}" />" /> <br />
    <input type="submit" value="Submit" />
</form>
</body>
</html>
