package com.jgermaine.fyp.rest.task;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.jgermaine.fyp.rest.config.Secret;
import com.jgermaine.fyp.rest.model.Employee;

public class MailRunnable implements Runnable {

	private String reportId;
	private Employee emp;

	public MailRunnable(String id, Employee emp) {
		this.reportId = id;
		this.emp = emp;
	}

	@Override
	public void run() {

		String to = emp.getEmail();

		// Sender details
		String from = "tasks.council.alert@gmail.com";
		final String username = "tasks.council.alert";
		final String password = Secret.EMAIL_PASSWORD;

		Properties props = getEmailProperties();

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

	/**
	 * Retrieve properties required for email setup
	 * @return
	 */
	private Properties getEmailProperties() {

		Properties props = new Properties();
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		
		return props;
	}

	/**
	 * Retrieve the message to be sent in the email
	 * @return
	 */
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
