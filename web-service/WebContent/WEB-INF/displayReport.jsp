<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<body>
	<h1>
		Reports
	</h1>
		<table>
			<th>ID</th>
			<th>Name</th>
			<th>Longitude</th>
			<th>Latitude</th>
			<c:forEach items="${reports}" var="report">
				<td><c:out value="${report.getId()}" /></td>
				<td><c:out value="${report.getName()}" /></td>
				<td><c:out value="${report.getLongitude()}" /></td>
				<td><c:out value="${report.getLatitude()}" /></td>
			</c:forEach>
		</table>
</body>
</html>