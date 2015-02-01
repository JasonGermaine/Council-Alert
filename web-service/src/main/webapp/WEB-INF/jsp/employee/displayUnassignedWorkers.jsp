<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css">
<!-- Optional theme -->
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap-theme.min.css">
<!-- Latest compiled and minified JavaScript -->
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/js/bootstrap.min.js"></script>
<script src="https://code.jquery.com/jquery-1.11.0.min.js"></script>
<script src="https://code.jquery.com/jquery-migrate-1.2.1.min.js"></script>

</head>

<body>
	<h1>Unassigned Employees</h1>
	<table class="table" style="margin-top: 50px;">
		<tr>
			<th>First Name</th>
			<th>Last Name</th>
			<th>Email</th>
		</tr>
		<c:forEach items="${employees}" var="employee">
		<c:set var="email"><c:out value="${xyz}" /></c:set>
			<tr>
				<td><c:out value="${employee.getFirstName()}" /></td>
				<td><c:out value="${employee.getLastName()}" /></td>
				<td><c:out value="${employee.getEmail()}"/></td>
				<td><a href='${pageContext.request.contextPath}/employee/assign?email=<c:out value="${employee.getEmail()}"/>'/>Assign Task</a></td>
			</tr>
		</c:forEach>
	</table>
</body>
</html>