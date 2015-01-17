package com.jgermaine.fyp.rest.controller;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.jgermaine.fyp.rest.model.Citizen;
import com.jgermaine.fyp.rest.service.impl.CitizenServiceImpl;

@RestController
@RequestMapping("/citizen")
public class CitizenController {

	private static final Logger LOGGER = LogManager
			.getLogger(CitizenController.class.getName());

	@Autowired
	private CitizenServiceImpl citizenService;

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

	@RequestMapping("/login")
	public Boolean getPage(@RequestParam(value="email", required=true) String email, 
		    @RequestParam(value="password", required =true) String password) {
		Citizen citizen = citizenService.getCitizen(email);	
		if (citizen != null && password.equals(citizen.getPassword())) {
				return true;
		} else {
			return false;
		}
	}
}