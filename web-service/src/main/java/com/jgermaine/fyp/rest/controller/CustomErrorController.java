package com.jgermaine.fyp.rest.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * @author JasonGermaine
 *
 * Handles error handling for RESTful error responses
 *
 */
@RestController
public class CustomErrorController implements ErrorController {

	private static final String PATH = "/error";
	
	@RequestMapping(value = PATH)
	public ResponseEntity<String> error(HttpServletRequest request) {
		HttpStatus status = getStatus(request);
		return new ResponseEntity<String>(status);
	}

	@Override
	public String getErrorPath() {
		return PATH;
	}
	
	private HttpStatus getStatus(HttpServletRequest request) {
		Integer code = (Integer) request.getAttribute("javax.servlet.error.status_code");
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		if (code != null) {
			status = HttpStatus.valueOf(code);
		}
		return status;
	}
}