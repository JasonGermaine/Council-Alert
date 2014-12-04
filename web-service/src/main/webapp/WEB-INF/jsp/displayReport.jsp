<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css">
<!-- Optional theme -->
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap-theme.min.css">
<!-- Latest compiled and minified JavaScript -->
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/js/bootstrap.min.js"></script>
<script src="https://code.jquery.com/jquery-1.11.0.min.js"></script>
<script src="https://code.jquery.com/jquery-migrate-1.2.1.min.js"></script>

</head>

<body>
	<h1>
		Reports
	</h1>
		<table class="table" style="margin-top: 50px;">
			<tr>
			<th>ID</th>
			<th>Name</th>
			<th>Longitude</th>
			<th>Latitude</th>
			<th>Timestamp</th>
			<th>Complete</th>
			</tr>
			<c:forEach items="${reports}" var="report">
				<tr>
				<td><c:out value="${report.getId()}" /></td>
				<td><c:out value="${report.getName()}" /></td>
				<td><c:out value="${report.getLongitude()}" /></td>
				<td><c:out value="${report.getLatitude()}" /></td>
				<td><c:out value="${report.getTimestamp()}" /></td>
				<td><c:out value="${report.getStatus()}" /></td>
				</tr>
			</c:forEach>
		</table>
</body>
</html>