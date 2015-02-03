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
	<h1>Index</h1>
	<ul class="list-group">
		<li class="list-group-item"><a href='${pageContext.request.contextPath}/employee/'/>Display Employeess</a></li>
		<li class="list-group-item"><a href='${pageContext.request.contextPath}/employee/new'/>Add New Employee</a></li>
		<li class="list-group-item"><a href='${pageContext.request.contextPath}/employee/slackers'/>Display Unassigned Employees</a></li>
		<li class="list-group-item"><a href='${pageContext.request.contextPath}/employee/task?email=jason@jason.jason&id=6'/>Assign jason@jason.jason report id 6</a></li>
		<li class="list-group-item"><a href='${pageContext.request.contextPath}/report/'/>Display Reports</a></li>
		<li class="list-group-item"><a href='${pageContext.request.contextPath}/report/proximity'/>Display Reports closest to IT Tallaght</a></li>
		<li class="list-group-item"><a href='${pageContext.request.contextPath}/report/map'/>Display Reports On a Map</a></li>
	</ul>
</body>
</html>