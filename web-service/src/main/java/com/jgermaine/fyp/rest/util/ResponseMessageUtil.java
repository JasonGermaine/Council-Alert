package com.jgermaine.fyp.rest.util;

public final class ResponseMessageUtil {
	
	
	public static final String SUCCESS_USER_CREATION = "User was created successfully.";
	public static final String SUCCESS_EMPLOYEE_CREATION = "Employee was created successfully.";
	public static final String SUCCESS_REPORT_CREATION = "Report was created successfully.";
	
	public static final String SUCCESS_EMPLOYEE_UPDATE = "Employee was updated successfully.";
	public static final String SUCCESS_REPORT_UPDATE = "Report was updated successfully.";
	public static final String SUCCESS_PASSWORD_UPDATE = "Password was updated successfully.";
	
	public static final String SUCCESS_EMPLOYEE_REMOVAL = "Employee was removed successfully.";
	
	public static final String SUCCESS_ASSIGNMENT = "Report was successfully assigned to Employee.";
	
	public static final String ERROR_USER_NOT_EXIST = "User does not exist.";	
	public static final String ERROR_EMPLOYEE_NOT_EXIST = "Employee does not exist.";
	public static final String ERROR_REPORT_NOT_EXIST = "Report does not exist.";
	public static final String ERROR_GENERIC_NOT_EXIST = "Data for id entered does not exist.";
	
	public static final String ERROR_USER_EXIST = "A User is already registered for that email.";
	public static final String ERROR_EMPLOYEE_EXIST = "An Employee is already registered for that email.";
	public static final String ERROR_REPORT_EXIST = "A Report for that id has already been created.";
	
	public static final String ERROR_ASSIGNMENT = " currently has a different assignment.";
	
	public static final String ERROR_PASSWORD_MISMATCH = "Password does not match.";
	
	public static final String ERROR_INVALID_STATUS = "Invalid report status entered.";
	
	public static final String ERROR_INVALID_DATA = "invalid fields submitted";
	
	public static final String ERROR_UNAUTHORIZED_USER = "User does not have authorized access.";
		
	public static final String ERROR_UNEXPECTED = "An unexpected error has occurred.";
}
