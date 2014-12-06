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

<style>
	.image {
		height: 300px;
		width: 300px;
	}
</style>
</head>

<body>
	<h1>
		Reports
	</h1>
		<table class="table" style="margin-top: 50px;">
			<tr>
			<th>ID</th>
			<th>Name</th>
			<th>Description</th>
			<th>Comment</th>
			<th>Complete</th>
			<th>Image - Before</th>
			<th>Image - After</th>
			<th>Longitude</th>
			<th>Latitude</th>
			<th>Timestamp</th>
			</tr>
			<c:forEach items="${reports}" var="report">
				<tr>
				<td><c:out value="${report.getId()}" /></td>
				<td><c:out value="${report.getName()}" /></td>
				<td><c:out value="${report.getDescription()}" /></td>
				<td><c:out value="${report.getComment()}" /></td>
				<td><c:out value="${report.getStatus()}" /></td>
				<c:choose>
					<c:when test="${empty report.getImageBefore()}">
						<td>None</td>
					</c:when>
					<c:otherwise>
						<td><img class="image" src="${report.getImageBeforeUrl()}"></img></td>
					</c:otherwise>
				</c:choose>
				<c:choose>
					<c:when test="${empty report.getImageAfter()}">
						<td>None</td>
					</c:when>
					<c:otherwise>
						<td><img class="image" src="${report.getImageAfterUrl()}"></img></td>
					</c:otherwise>
				</c:choose>				
				<td><c:out value="${report.getLongitude()}" /></td>
				<td><c:out value="${report.getLatitude()}" /></td>
				<td><c:out value="${report.getTimestamp()}" /></td>
				</tr>
			</c:forEach>				
		</table>
</body>
</html>