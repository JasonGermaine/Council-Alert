package com.jgermaine.fyp.rest.service;

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceException;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;

import com.jgermaine.fyp.rest.model.Citizen;

public interface CitizenService {

	public Citizen getCitizen();

	public void addCitizen(Citizen Citizen) throws EntityExistsException, PersistenceException,
		DataIntegrityViolationException, Exception;

	public void removeCitizen(Citizen Citizen) throws Exception;

	public List<Citizen> getCitizens() throws Exception;

	public Citizen getCitizen(String email) throws NoResultException, NonUniqueResultException, Exception;

	public void updateCitizen(Citizen citizen) throws Exception;
}
