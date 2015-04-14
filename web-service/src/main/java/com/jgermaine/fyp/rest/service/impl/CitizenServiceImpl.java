package com.jgermaine.fyp.rest.service.impl;

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceException;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.jgermaine.fyp.rest.model.Citizen;
import com.jgermaine.fyp.rest.model.dao.CitizenDao;
import com.jgermaine.fyp.rest.model.dao.ReportDao;
import com.jgermaine.fyp.rest.service.CitizenService;

@Service
public class CitizenServiceImpl implements CitizenService {

	@Autowired
	private CitizenDao CitizenDao;

	@Autowired
	private ReportDao reportDao;

	public Citizen getCitizen() {
		Citizen Citizen = new Citizen();
		Citizen.setEmail("example@example.com");
		Citizen.setPassword("password");
		return Citizen;
	}

	public void addCitizen(Citizen Citizen) throws EntityExistsException, PersistenceException,
		DataIntegrityViolationException, Exception {
		CitizenDao.create(Citizen);
	}

	public void removeCitizen(Citizen Citizen) throws Exception {
		CitizenDao.delete(Citizen);
		;
	}

	public List<Citizen> getCitizens() throws Exception {
		return CitizenDao.getAll();
	}

	public Citizen getCitizen(String email) throws NoResultException, NonUniqueResultException, Exception {
		return CitizenDao.getByEmail(email);
	}

	public void updateCitizen(Citizen citizen) throws Exception {
		CitizenDao.update(citizen);
	}
}