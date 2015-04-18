package com.jgermaine.fyp.rest.service;

import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;

import com.jgermaine.fyp.rest.model.User;


public interface UserService {

	User getUser(String email) throws NoResultException, NonUniqueResultException, Exception;

}
