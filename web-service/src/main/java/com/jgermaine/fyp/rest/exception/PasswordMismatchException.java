package com.jgermaine.fyp.rest.exception;

import com.jgermaine.fyp.rest.util.ResponseMessageUtil;

/**
 * 
 * @author JasonGermaine
 * 
 * The PasswordMistmatchException denotes an error where a password comparison fails
 *
 */
public class PasswordMismatchException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PasswordMismatchException() {
		super(ResponseMessageUtil.ERROR_PASSWORD_MISMATCH);
	}
	
	public PasswordMismatchException(String message) {
		super(message);
	}

	public PasswordMismatchException(Throwable cause) {
		super(cause);
	}

	public PasswordMismatchException(String message, Throwable cause) {
		super(message, cause);
	}
}
