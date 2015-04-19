package com.jgermaine.fyp.rest.config;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jgermaine.fyp.rest.model.Citizen;
import com.jgermaine.fyp.rest.model.Employee;
import com.jgermaine.fyp.rest.model.Entry;
import com.jgermaine.fyp.rest.model.Report;
import com.jgermaine.fyp.rest.service.impl.CitizenServiceImpl;
import com.jgermaine.fyp.rest.service.impl.CouncilAlertUserDetailsService;
import com.jgermaine.fyp.rest.service.impl.EmployeeServiceImpl;
import com.jgermaine.fyp.rest.service.impl.ReportServiceImpl;

@Component
public class DatabaseInitializer {

	private static final Logger LOGGER = LogManager.getLogger(DatabaseInitializer.class.getName());

	public static final String INIT_USER = "admin@council-alert.com";

	private static final String[] EMP_EMAILS = { "andrea@council-alert.com", "ben@council-alert.com",
			"carol@council-alert.com", "darren@council-alert.com", "eric@council-alert.com",
			"francesca@council-alert.com", "george@council-alert.com", "harry@council-alert.com",
			"irene@council-alert.com", "jason@council-alert.com", "kelly@council-alert.com", "laura@council-alert.com",
			"monica@council-alert.com", "nick@council-alert.com", "oliver@council-alert.com", "paul@council-alert.com",
			"quinn@council-alert.com", "roisin@council-alert.com", "stephen@council-alert.com",
			"tom@council-alert.com", "usher@council-alert.com", "victor@council-alert.com", "wendy@council-alert.com",
			"xang@council-alert.com", "yannick@council-alert.com", "zelda@council-alert.com" };

	private static final String[] FIRST_NAMES = { "Andrea", "Ben", "Carol", "Darren", "Eric", "Francesca", "George",
			"Harry", "Irene", "Jason", "Kelly", "Laura", "Monica", "Nick", "Oliver", "Paul", "Quinn", "Roisin",
			"Stephen", "Tom", "Usher", "Victor", "Wendy", "Xang", "Yannick", "Zelda" };

	private static final String[] LAST_NAMES = { "Byrne", "O'Brien", "Ford", "Murray", "Germaine", "Dunne", "Smith",
			"Johnson", "Woods", "McCarthy", "Murphy", "Thompson", "Bowker", "Taylor", "Reid" };

	private static final String[] PHONE_NUMBERS = { "123456789", "+3531234567", "+441234567", "987654323",
			"0871234567", "343456789", "+353324567", "+441245347", "32445465", "0873434567", };

	private static final Location[] LOCATIONS = { new Location(53.3028383, -6.3498868),
			new Location(53.2908857, -6.3764514), new Location(53.3313158, -6.3756868),
			new Location(53.2803199, -6.3128264), new Location(53.3417427, -6.2905544),
			new Location(53.3505559, -6.292786), new Location(53.3507096, -6.2612862),
			new Location(53.3720174, -6.2605137), new Location(53.3108961, -6.2222152),
			new Location(53.2782173, -6.2622123), new Location(53.3036795, -6.42753), };

	private static final String[] CITIZEN_EMAILS = { "andrea@user.com", "ben@user.com", "carol@user.com",
			"darren@user.com", "eric@user.com", "francesca@user.com", "george@user.com", "harry@user.com",
			"irene@user.com", "jason@user.com", "kelly@user.com", "laura@user.com", "monica@user.com", "nick@user.com",
			"oliver@user.com", "paul@user.com", "quinn@user.com", "roisin@user.com", "stephen@user.com",
			"tom@user.com", "usher@user.com", "victor@user.com", "wendy@user.com", "xang@user.com", "yannick@user.com",
			"zelda@user.com" };

	private static final String[] REPORT_TYPES = { "Lamp Post Repair", "Cats Eye Repair", "Gully Problem",
			"Blocked Drain", "Water Pollution Incident", "Repair Road Sign", "Repair Road Markings", "Weed Control",
			"Grass Maintenance", "Tree Maintenance", "Graffiti", "Abandoned Vehicle Inspection",
			"Abandoned Bike/Trolley", "Repair Damaged Bollard", "Repair Cycle Stand", "Faulty Manhole Cover",
			"Cycle Track", "Footpath", "Road Surface", "Ramps", "Street Name Plate", "Illegal Dumping",
			"Overflowing Skips", "Public Litter Bin Maintenance", "Report Litter Offence", "Sweep Your Street",
			"Noisey neighbour", "Neighbour's dog is barking at me!" };

	private static final String[] REPORT_COMMENTS = {
			"Please can you hurry because I am having a lot of difficulty going about my day to day life.",
			"It is a disgrace that this has been going on for so long",
			"May I request John to come and fix this because my neighbour Betty says he is a lovely man.",
			"Needs urgent fixing!", };

	static {
		random = new Random();
	}

	private static ArrayList<Employee> employees = new ArrayList<Employee>();
	private static ArrayList<Report> reports = new ArrayList<Report>();

	@Autowired
	private CouncilAlertUserDetailsService councilAlertUserService;

	@Autowired
	private EmployeeServiceImpl employeeService;

	@Autowired
	private CitizenServiceImpl citizenService;

	@Autowired
	private ReportServiceImpl reportService;

	private static Random random;

	@PostConstruct
	public void postConstruct() {
		initEmployees();
		initCitizens();
		initReports();
		initAssignments();
	}

	private void initEmployees() {
		insertNewEmployee(new Employee(Secret.MY_EMP_EMAIL, Secret.MY_PASSWORD, "Jason", "Germaine", "12345678910",
				53.2884615, -6.3525261));
		insertNewEmployee(new Employee(INIT_USER, Secret.SECRET_PASSWORD, "Council", "Admin", "12345678910",
				53.2884615, -6.3525261));

		for (int i = 0; i < EMP_EMAILS.length; i++) {
			Location location = LOCATIONS[random.nextInt(LOCATIONS.length)];
			Employee emp = new Employee(EMP_EMAILS[i], Secret.SECRET_GENERIC_PASSWORD, FIRST_NAMES[i],
					LAST_NAMES[random.nextInt(LAST_NAMES.length)], PHONE_NUMBERS[random.nextInt(PHONE_NUMBERS.length)],
					location.getLat(), location.getLon());
			employees.add(emp);
			insertNewEmployee(emp);
		}
	}

	private void initCitizens() {
		insertNewCitizen(new Citizen(Secret.MY_CITZ_EMAIL, Secret.MY_PASSWORD));

		for (String email : CITIZEN_EMAILS) {
			insertNewCitizen(new Citizen(email, Secret.SECRET_GENERIC_PASSWORD));
		}
	}

	private void initReports() {
		Location loc = LOCATIONS[random.nextInt(LOCATIONS.length)];
		Report rpt = new Report(REPORT_TYPES[random.nextInt(REPORT_TYPES.length)], loc.getLat(),
				loc.getLon(), getRandomDate(), true);
		insertNewReport(rpt, false);
		
		for (int i = 0; i < 100; i++) {
			Location location = LOCATIONS[random.nextInt(LOCATIONS.length)];
			Report report = new Report(REPORT_TYPES[random.nextInt(REPORT_TYPES.length)], location.getLat(),
					location.getLon(), getRandomDate(), i % 4 == 0);

			if (i % 4 != 0)
				reports.add(report);

			insertNewReport(report, i % 6 == 0);
		}
	}

	private void initAssignments() {
		for (int i = 0; i < employees.size() / 2; i++) {
			try {
				int reportIndex = random.nextInt(reports.size());
				int employeeIndex = random.nextInt(employees.size());

				Employee emp = employeeService.getEmployee(employees.get(employeeIndex).getEmail());
				Report report = reportService.getReport(reportIndex + 1);

				employees.remove(employeeIndex);
				reports.remove(reportIndex);

				report.setEmployee(emp);
				reportService.updateReport(report);
			} catch (Exception e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}

	private void insertNewEmployee(Employee employee) {
		try {
			//employeeService.addEmployee(employee);
			councilAlertUserService.createNewUser(employee);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	private void insertNewCitizen(Citizen citizen) {
		try {
			//citizenService.addCitizen(citizen);
			councilAlertUserService.createNewUser(citizen);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	private void insertNewReport(Report report, boolean addComment) {
		try {
			Citizen c = citizenService.getCitizen(CITIZEN_EMAILS[random.nextInt(CITIZEN_EMAILS.length)]);
			c.addReport(report);
			report.setCitizen(c);

			if (addComment) {
				Entry entry = new Entry();
				entry.setAuthor(c.getEmail());
				entry.setComment(REPORT_COMMENTS[random.nextInt(REPORT_COMMENTS.length)]);
				entry.setTimestamp(report.getTimestamp());
				report.addEntry(entry);
			}

			reportService.addReport(report);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	private static class Location {
		double lat, lon;

		public Location(double lat, double lon) {
			this.lat = lat;
			this.lon = lon;
		}

		public double getLat() {
			return lat;
		}

		public double getLon() {
			return lon;
		}
	}

	private Date getRandomDate() {
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, (random.nextInt(50)) * -1);
		return cal.getTime();
	}
}
