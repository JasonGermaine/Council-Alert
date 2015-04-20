package com.jgermaine.fyp.rest.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.dao.ReflectionSaltSource;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import com.jgermaine.fyp.rest.service.impl.CouncilAlertUserDetailsService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

	@Autowired
	private CouncilAlertUserDetailsService userDetailsService;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		// Apply password hash and salt to authentication process using custom
		// user table in database
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
		provider.setUserDetailsService(userDetailsService);
		provider.setPasswordEncoder(passwordEncoder());
		provider.setSaltSource(saltSource());
		auth.authenticationProvider(provider);
	}

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public ShaPasswordEncoder passwordEncoder() {
		ShaPasswordEncoder encoder = new ShaPasswordEncoder(256);
		return encoder;
	}

	/**
	 * Return the source of the salt to apply to password
	 * 
	 * @return
	 */
	public ReflectionSaltSource saltSource() {
		ReflectionSaltSource source = new ReflectionSaltSource();

		// Maps to the getter method for the 'salt' field
		source.setUserPropertyToUse("getSalt");
		return source;
	}
}
