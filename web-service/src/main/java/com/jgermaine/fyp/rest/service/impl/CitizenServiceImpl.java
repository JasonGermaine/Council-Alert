package com.jgermaine.fyp.rest.service.impl;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jgermaine.fyp.rest.model.Citizen;
import com.jgermaine.fyp.rest.model.Report;
import com.jgermaine.fyp.rest.model.dao.CitizenDao;
import com.jgermaine.fyp.rest.service.CitizenService;

@Service
public class CitizenServiceImpl implements CitizenService {

	@Autowired
	private CitizenDao CitizenDao;

	public Citizen getCitizen() {
		Citizen Citizen = new Citizen();
		Citizen.setEmail("example@example.com");
		Citizen.setPassword("password");
		return Citizen;
	}

	public void removeCitizen(Citizen Citizen) throws Exception {
		CitizenDao.delete(Citizen);
	}

	public List<Citizen> getCitizens(int index) throws Exception {
		return CitizenDao.getAll(index);
	}

	public List<Report> getReportsForCitizen(String email) throws NoResultException, NonUniqueResultException, Exception {
		return getCitizen(email).getReports();
	}
	
	public Citizen getCitizen(String email) throws NoResultException, NonUniqueResultException, Exception {
		return CitizenDao.getByEmail(email);
	}

	public void updateCitizen(Citizen citizen) throws Exception {
		CitizenDao.update(citizen);
	}
}