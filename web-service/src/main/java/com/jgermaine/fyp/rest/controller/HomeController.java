package com.jgermaine.fyp.rest.controller;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class HomeController {
	
	private static final String PREFIX = "home";
	private static final Logger LOGGER = LogManager
			.getLogger(HomeController.class.getName());

	@RequestMapping("/")
	public String getMapView(Model model) {
		LOGGER.debug("Returning Default Page");
		return PREFIX + "/index";
	}
	
}