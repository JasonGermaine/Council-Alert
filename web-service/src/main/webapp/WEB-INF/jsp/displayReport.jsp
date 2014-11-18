<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
<style>
table{
    border-collapse: collapse;
        border-spacing: 0;
	width:100%;
	height:100%;
	margin:0px;padding:0px;
}tr:last-child td:last-child {
	-moz-border-radius-bottomright:0px;
	-webkit-border-bottom-right-radius:0px;
	border-bottom-right-radius:0px;
}
table tr:first-child td:first-child {
	-moz-border-radius-topleft:0px;
	-webkit-border-top-left-radius:0px;
	border-top-left-radius:0px;
}
table tr:first-child td:last-child {
	-moz-border-radius-topright:0px;
	-webkit-border-top-right-radius:0px;
	border-top-right-radius:0px;
}tr:last-child td:first-child{
	-moz-border-radius-bottomleft:0px;
	-webkit-border-bottom-left-radius:0px;
	border-bottom-left-radius:0px;
}tr:hover td{
	
}
tr:nth-child(odd){ background-color:#ffaa56; }
tr:nth-child(even)    { background-color:#ffffff; }td{
	vertical-align:middle;
	
	
	border:1px solid #000000;
	border-width:0px 1px 1px 0px;
	text-align:center;
	padding:7px;
	font-size:16px;
	font-family:Arial;
	font-weight:normal;
	color:#000000;
}tr:last-child td{
	border-width:0px 1px 0px 0px;
}tr td:last-child{
	border-width:0px 0px 1px 0px;
}tr:last-child td:last-child{
	border-width:0px 0px 0px 0px;
}
tr:first-child td{
		background:-o-linear-gradient(bottom, #ff7f00 5%, #bf5f00 100%);	background:-webkit-gradient( linear, left top, left bottom, color-stop(0.05, #ff7f00), color-stop(1, #bf5f00) );
	background:-moz-linear-gradient( center top, #ff7f00 5%, #bf5f00 100% );
	filter:progid:DXImageTransform.Microsoft.gradient(startColorstr="#ff7f00", endColorstr="#bf5f00");	background: -o-linear-gradient(top,#ff7f00,bf5f00);

	background-color:#ff7f00;
	border:0px solid #000000;
	text-align:center;
	border-width:0px 0px 1px 1px;
	font-size:18px;
	font-family:Arial;
	font-weight:bold;
	color:#ffffff;
}
tr:first-child:hover td{
	background:-o-linear-gradient(bottom, #ff7f00 5%, #bf5f00 100%);	background:-webkit-gradient( linear, left top, left bottom, color-stop(0.05, #ff7f00), color-stop(1, #bf5f00) );
	background:-moz-linear-gradient( center top, #ff7f00 5%, #bf5f00 100% );
	filter:progid:DXImageTransform.Microsoft.gradient(startColorstr="#ff7f00", endColorstr="#bf5f00");	background: -o-linear-gradient(top,#ff7f00,bf5f00);

	background-color:#ff7f00;
}
tr:first-child td:first-child{
	border-width:0px 0px 1px 0px;
}
tr:first-child td:last-child{
	border-width:0px 0px 1px 1px;
}
</style>
</head>

<body>
	<h1>
		Reports
	</h1>
		<table>
			<tr>
			<th>ID</th>
			<th>Name</th>
			<th>Longitude</th>
			<th>Latitude</th>
			</tr>
			<c:forEach items="${reports}" var="report">
				<tr>
				<td><c:out value="${report.getId()}" /></td>
				<td><c:out value="${report.getName()}" /></td>
				<td><c:out value="${report.getLongitude()}" /></td>
				<td><c:out value="${report.getLatitude()}" /></td>
				</tr>
			</c:forEach>
		</table>
</body>
</html>