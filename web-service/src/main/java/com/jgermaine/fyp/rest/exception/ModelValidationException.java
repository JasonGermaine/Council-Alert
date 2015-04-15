package com.jgermaine.fyp.rest.exception;

import com.jgermaine.fyp.rest.util.ResponseMessageUtil;

public class ModelValidationException extends Exception {

	/**
		 * 
		 */
	private static final long serialVersionUID = 1L;
	
	public ModelValidationException() {
		super();
	}

	public ModelValidationException(int errorCount) {
		super(errorCount + " " + ResponseMessageUtil.ERROR_INVALID_DATA);
	}

	public ModelValidationException(String message) {
		super(message);
	}

	public ModelValidationException(Throwable cause) {
		super(cause);
	}

	public ModelValidationException(String message, Throwable cause) {
		super(message, cause);
	}
}
