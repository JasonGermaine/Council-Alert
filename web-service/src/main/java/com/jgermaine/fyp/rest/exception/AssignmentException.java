package com.jgermaine.fyp.rest.exception;

public class AssignmentException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AssignmentException() {

	}

	public AssignmentException(ExceptionType type) {
		super(type.getType());
	}
	
	public AssignmentException(String message) {
		super(message);
	}

	public AssignmentException(Throwable cause) {
		super(cause);
	}

	public AssignmentException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public enum ExceptionType {
		REPORT, EMPLOYEE;
		
		public String getType() {
			if (this == REPORT) {
				return "Report";
			} else {
				return "Employee";
			}
		}
	}
}
