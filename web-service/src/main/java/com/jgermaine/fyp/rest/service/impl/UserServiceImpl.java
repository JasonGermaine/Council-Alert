package com.jgermaine.fyp.rest.service.impl;

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
	public User getUser(String email) {
		return UserDao.getUser(email);
	}

}
