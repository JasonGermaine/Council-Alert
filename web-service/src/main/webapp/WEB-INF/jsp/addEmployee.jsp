<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">

<title>Add Employee</title>

<link rel="shortcut icon" href="<c:url value="council-alert.ico"/> " />

<!-- Latest compiled and minified CSS -->
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap.min.css">

<!-- Optional theme -->
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/css/bootstrap-theme.min.css">

<!-- Latest compiled and minified JavaScript -->
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.1/js/bootstrap.min.js"></script>
<script src="http://code.jquery.com/jquery-1.11.0.min.js"></script>
<script src="http://code.jquery.com/jquery-migrate-1.2.1.min.js"></script>

<style type="text/css">
.error {
	color: red;
	font-weight: bold;
}
</style>
</head>
<body>
	<div class="container">
		<h2 class="col-lg-offset-2 col-md-offset-2"
			style="padding-bottom: 50px">Register New Employee</h2>
		<form:form class="form-horizontal" role="form" method="POST"
			action="new" commandName="employeeForm" modelattribute="employee">
			<div class="form-group">
				<form:label path="firstName"
					class="control-label col-lg-2 col-lg-offset-2 col-md-2 col-md-offset-2 col-sm-12"
					for="fname">First Name:</form:label>
				<div class="col-lg-6 col-md-6 col-sm-12">
					<form:input path="firstName" class="form-control" id="fname"
						placeholder="First Name" />
					<form:errors path="firstName" />
				</div>
			</div>
			<div class="form-group">
				<form:label path="lastName"
					class="control-label col-lg-2 col-lg-offset-2 col-md-2 col-md-offset-2 col-sm-12"
					for="lname">Last Name:</form:label>
				<div class="col-lg-6 col-md-6 col-sm-12">
					<form:input path="lastName" class="form-control" id="lname"
						placeholder="Last Name" />
					<form:errors path="lastName" />
				</div>
			</div>
			<div class="form-group">
				<form:label path="email"
					class="control-label col-lg-2 col-lg-offset-2 col-md-2 col-md-offset-2 col-sm-12"
					for="email">Email:</form:label>
				<div class="col-lg-6 col-md-6 col-sm-12">
					<form:input path="email" type="email" class="form-control"
						id="email" placeholder="Email" />
					<form:errors path="email" cssClass="error" />
				</div>
			</div>
			<div class="form-group">
				<form:label path="phoneNum"
					class="control-label col-lg-2 col-lg-offset-2 col-md-2 col-md-offset-2 col-sm-12"
					for="phoneNum">Phone Number:</form:label>
				<div class="col-lg-6 col-md-6 col-sm-12">
					<form:input path="phoneNum" class="form-control" id="phoneNum"
						placeholder="Phone Number" />
					<form:errors path="phoneNum" />
				</div>
			</div>
			<div class="form-group">
				<form:label path="password"
					class="control-label col-lg-2 col-lg-offset-2 col-md-2 col-md-offset-2 col-sm-12"
					for="pwd">Password:</form:label>
				<div class="col-lg-6 col-md-6 col-sm-12 controls">
					<form:input path="password" type="password" class="form-control"
						id="pwd" placeholder="Password" />
					<form:errors path="password" />
				</div>
			</div>
			<div class="form-group">
				<label
					class="control-label col-lg-2 col-lg-offset-2 col-md-2 col-md-offset-2 col-sm-12"
					for="pwdConf">Confirm Password:</label>
				<div class="col-lg-6 col-md-6 col-sm-12">
					<input type="password" class="form-control" id="pwdConf"
						placeholder="Confirm password" />
				</div>
			</div>
			<div class="form-group form-actions">
				<div
					class="col-lg-offset-4 col-lg-1 col-md-offset-4 col-md-1 col-sm-1">
					<button type="submit" class="btn btn-primary">Register</button>
				</div>
				<div class="col-lg-1 col-md-1 col-sm-1">
					<button type="reset" class="btn btn-primary">Clear</button>
				</div>
			</div>
			<form:hidden path="assigned" value="false" />
		</form:form>
	</div>
</body>
</html>
