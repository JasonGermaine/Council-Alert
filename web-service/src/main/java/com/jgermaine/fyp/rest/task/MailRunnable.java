package com.jgermaine.fyp.rest.task;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jgermaine.fyp.rest.model.Employee;

public class MailRunnable implements Runnable {

	private static final Logger LOGGER = LogManager
			.getLogger(MailRunnable.class.getName());

	private String reportId;
	private Employee emp;

	public MailRunnable(String id, Employee emp) {
		this.reportId = id;
		this.emp = emp;
	}

	@Override
	public void run() {

		String to = emp.getEmail();

		String from = "tasks.council.alert@gmail.com";
		final String username = "tasks.council.alert";
		final String password = "X00090307";

		String host = "smtp.gmail.com";

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(username, password);
					}
				});

		try {
			Message message = new MimeMessage(session);

			message.setFrom(new InternetAddress(from));

			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(to));

			message.setSubject("New Task Assignment");

			message.setText(getMessage());

			Transport.send(message);

		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	private String getMessage() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("Hello " + emp.getFirstName() + " " + emp.getLastName());
		sb.append("\n\nYou have received a new task assignment.");
		sb.append("\nYou have been assigned Report: " + reportId);
		sb.append("\n\nLog into the CouncilAlert Android application "
				+ "for further details on this report");
		sb.append("\n\nPlease do not reply to this email");
		sb.append("\n\n\nRegards");
		sb.append("\nCouncil Alert Task Management");
		
		return sb.toString();
	}
}
