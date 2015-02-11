package com.jgermaine.fyp.rest.config;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;



@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

//TODO: Implement
	
	/*
	private static final Logger LOGGER = Logger.getLogger(SecurityConfig.class);

	@Override
	protected void configure(AuthenticationManagerBuilder auth)
			throws Exception {
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.authorizeRequests().antMatchers("")
				.permitAll().antMatchers("").permitAll()
				.antMatchers("").permitAll()
				.antMatchers(HttpMethod.POST, "/user").permitAll().anyRequest()
				.authenticated().and().formLogin()
				.defaultSuccessUrl("/")
				.loginProcessingUrl("/authenticate")
				.usernameParameter("username").passwordParameter("password")
				.loginPage("/").and().httpBasic()
				.and().logout().logoutUrl("/")
				.logoutSuccessUrl("").permitAll();

		if ("true".equals(System.getProperty("httpsOnly"))) {
			LOGGER.info("launching the application in HTTPS-only mode");
			http.requiresChannel().anyRequest().requiresSecure();
		}
	}
	*/
}