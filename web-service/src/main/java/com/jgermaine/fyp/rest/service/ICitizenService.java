package com.jgermaine.fyp.rest.service;

import java.util.List;

import com.jgermaine.fyp.rest.model.Citizen;

public interface ICitizenService {

	public Citizen getCitizen();
	
	public void addCitizen(Citizen Citizen);
	
	public void removeCitizen(Citizen Citizen);
	
	public List<Citizen> getCitizens();
	
	public Citizen getCitizen(String email);
}
