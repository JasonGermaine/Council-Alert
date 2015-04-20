package com.jgermaine.fyp.rest.exception;

/**
 * 
 * @author JasonGermaine
 * 
 *         The class AssignmentException denotes an error when an attempt to
 *         assign a report to an employee occurs whereas one or the other are
 *         already assigned
 *
 */
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
