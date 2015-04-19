package com.jgermaine.fyp.rest.service;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import com.jgermaine.fyp.rest.model.Citizen;
import com.jgermaine.fyp.rest.model.Report;

public interface CitizenService {

	Citizen getCitizen();

	void removeCitizen(Citizen Citizen) throws Exception;

	List<Citizen> getCitizens() throws Exception;

	Citizen getCitizen(String email) throws NoResultException, NonUniqueResultException, Exception;

	void updateCitizen(Citizen citizen) throws Exception;
	
	List<Report> getReportsForCitizen(String email) throws NoResultException, NonUniqueResultException, Exception;
}
