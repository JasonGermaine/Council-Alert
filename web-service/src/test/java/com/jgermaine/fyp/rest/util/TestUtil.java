package com.jgermaine.fyp.rest.util;

import java.io.IOException;
import java.util.Date;

import org.springframework.security.authentication.encoding.ShaPasswordEncoder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jgermaine.fyp.rest.config.DatabaseInitializer;
import com.jgermaine.fyp.rest.config.Secret;
import com.jgermaine.fyp.rest.controller.AdminController;
import com.jgermaine.fyp.rest.model.Citizen;
import com.jgermaine.fyp.rest.model.CouncilAlertUser;
import com.jgermaine.fyp.rest.model.Employee;
import com.jgermaine.fyp.rest.model.EmployeeUpdateRequest;
import com.jgermaine.fyp.rest.model.PasswordChangeRequest;
import com.jgermaine.fyp.rest.model.Report;
import com.jgermaine.fyp.rest.model.Role;

public final class TestUtil {

	public static Employee getDefaultEmp() {
		Employee employee = new Employee();
		employee.setEmail(DatabaseInitializer.INIT_USER);
		employee.setPassword(Secret.SECRET_PASSWORD);
		employee.setFirstName("Jason");
		employee.setLastName("Germaine");
		employee.setPhoneNum("12334567");
		employee.setLatitude(53.2884615);
		employee.setLongitude(-6.3525261);
		return employee;
	}

	public static Report getDefaultReport() {
		Report report = new Report();
		report.setName("Report");
		report.setCitizen(getDefaultCitizen());
		report.setLatitude(53.2884615);
		report.setLongitude(-6.3525261);
		report.setTimestamp(new Date());
		report.setId(1);
		report.setEmployee(null);
		report.setStatus(false);
		return report;
	}

	public static EmployeeUpdateRequest getUpdateRequest() {
		EmployeeUpdateRequest request = new EmployeeUpdateRequest();
		request.setPhoneNum("12334567");
		request.setDeviceId(AdminController.DELETED_DEVICE_ID);
		request.setLatitude(53.2884615);
		request.setLongitude(-6.3525261);
		return request;
	}
	
	public static PasswordChangeRequest getPasswordRequest() {
		PasswordChangeRequest request = new PasswordChangeRequest();
		request.setPassword(Secret.SECRET_PASSWORD);
		request.setNewPassword("password");
		return request;
	}

	public static Citizen getDefaultCitizen() {
		Citizen citz = new Citizen();
		citz.setEmail(DatabaseInitializer.INIT_USER);
		citz.setPassword(Secret.SECRET_PASSWORD);
		return citz;
	}

	public static CouncilAlertUser getDefaultCouncilAlertUser() {
		CouncilAlertUser user = new CouncilAlertUser();
		user.setLogin("sample@email.com");	
		user.setSalt("salt");
		user.setPassword(new ShaPasswordEncoder(256).encodePassword(Secret.SECRET_PASSWORD, user.getSalt()));
		user.addRole(new Role("USER", user));
		return user;
	}
	
	public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		return mapper.writeValueAsBytes(object);
	}
}
