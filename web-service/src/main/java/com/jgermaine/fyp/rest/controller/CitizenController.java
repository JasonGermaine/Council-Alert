package com.jgermaine.fyp.rest.controller;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.jgermaine.fyp.rest.model.Citizen;
import com.jgermaine.fyp.rest.service.CitizenService;

@RestController
@RequestMapping("/citizen")
public class CitizenController {

	private static final Logger LOGGER = LogManager
			.getLogger(ReportController.class.getName());

	@Autowired
	private CitizenService citizenService;

	@RequestMapping("/post")
	public Citizen postReportDetails(@RequestBody Citizen citizen) {
		LOGGER.info(String.format("Citizen email: %s, password: %s",
				citizen.getEmail(), citizen.getPassword()));
		citizenService.addCitizen(citizen);
		return citizen;
	}
	
	@RequestMapping("/display")
	public ModelAndView getListUsersView() {	
		 ModelAndView model = new ModelAndView();
		 model.setViewName("displayCitizen"); 
		 model.addObject("citizens", citizenService.getCitizens()); 
		 return model;
	}

}