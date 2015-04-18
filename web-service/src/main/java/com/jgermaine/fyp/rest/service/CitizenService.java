package com.jgermaine.fyp.rest.service;

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceException;

	import org.springframework.dao.DataIntegrityViolationException;

import com.jgermaine.fyp.rest.model.Citizen;
import com.jgermaine.fyp.rest.model.Report;

public interface CitizenService {

	Citizen getCitizen();

	void addCitizen(Citizen Citizen) throws EntityExistsException, PersistenceException,
		DataIntegrityViolationException, Exception;

	void removeCitizen(Citizen Citizen) throws Exception;

	List<Citizen> getCitizens() throws Exception;

	Citizen getCitizen(String email) throws NoResultException, NonUniqueResultException, Exception;

	void updateCitizen(Citizen citizen) throws Exception;
	
	List<Report> getReportsForCitizen(String email) throws NoResultException, NonUniqueResultException, Exception;
}
