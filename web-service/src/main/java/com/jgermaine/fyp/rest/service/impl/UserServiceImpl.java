package com.jgermaine.fyp.rest.service.impl;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jgermaine.fyp.rest.model.User;
import com.jgermaine.fyp.rest.model.dao.UserDao;
import com.jgermaine.fyp.rest.service.UserService;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserDao UserDao;
	
	@Override
	public User getUser(String email) throws NoResultException, NonUniqueResultException, Exception {
		return UserDao.getUser(email);
	}

}
