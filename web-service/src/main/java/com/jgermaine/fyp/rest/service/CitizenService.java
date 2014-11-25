package com.jgermaine.fyp.rest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jgermaine.fyp.rest.model.Citizen;
import com.jgermaine.fyp.rest.model.CitizenDao;

@Service
public class CitizenService implements ICitizenService {

	@Autowired
	private CitizenDao CitizenDao;
	
	
	public Citizen getCitizen(){
		Citizen Citizen = new Citizen();
		Citizen.setEmail("example@example.com");
		Citizen.setPassword("password");
		return Citizen;
	}
	
	public void addCitizen(Citizen Citizen) {
		CitizenDao.create(Citizen);
	}
	
	public void removeCitizen(Citizen Citizen) {
		CitizenDao.delete(Citizen);;
	}
	
	public List<Citizen> getCitizens() {
		return CitizenDao.getAll();
	}
	
	public Citizen getCitizen(String email) {
		return CitizenDao.getByEmail(email);
	}
	
} 