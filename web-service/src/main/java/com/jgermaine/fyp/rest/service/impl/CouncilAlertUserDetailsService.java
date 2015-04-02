package com.jgermaine.fyp.rest.service.impl;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;

import com.jgermaine.fyp.rest.model.CouncilAlertUser;
import com.jgermaine.fyp.rest.model.Employee;
import com.jgermaine.fyp.rest.model.Role;
import com.jgermaine.fyp.rest.model.User;
import com.jgermaine.fyp.rest.model.dao.CouncilAlertUserDao;

@Service
public class CouncilAlertUserDetailsService implements UserDetailsService {

	@Autowired
	private CouncilAlertUserDao councilAlertUserDao;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		CouncilAlertUser user = councilAlertUserDao.getByEmail(username);
		
		if (user == null) {
			throw new UsernameNotFoundException(String.format("User %s does not exist!", username));
		}
		return new UserRepositoryUserDetails(user);
	}
	
	public CouncilAlertUser getUser(String email) {
		return councilAlertUserDao.getByEmail(email);
	}

	public void createNewUser(User newUser) {
		CouncilAlertUser user = new CouncilAlertUser();
		user.setLogin(newUser.getEmail());	
		user.setSalt(KeyGenerators.string().generateKey());
		user.setPassword(new ShaPasswordEncoder(256).encodePassword(newUser.getPassword(), user.getSalt()));
		user.addRole(new Role("USER", user));
		
		if (newUser instanceof Employee)
			user.addRole(new Role("ADMIN", user));
		
		councilAlertUserDao.createUser(user);
	}
	
	public void removeUser(User user) {		
		councilAlertUserDao.remove(councilAlertUserDao.getByEmail(user.getEmail()));
	}
	
	public void updateUser(CouncilAlertUser user) {
		councilAlertUserDao.update(user);
	}
	
	private final static class UserRepositoryUserDetails extends CouncilAlertUser implements UserDetails {

		private static final long serialVersionUID = 1L;

		private UserRepositoryUserDetails(CouncilAlertUser user) {
			super(user);
		}

		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
			return getRoles();
		}

		@Override
		public String getUsername() {
			return getEmail();
		}

		@Override
		public boolean isAccountNonExpired() {
			return true;
		}

		@Override
		public boolean isAccountNonLocked() {
			return true;
		}

		@Override
		public boolean isCredentialsNonExpired() {
			return true;
		}

		@Override
		public boolean isEnabled() {
			return true;
		}

	}

}
