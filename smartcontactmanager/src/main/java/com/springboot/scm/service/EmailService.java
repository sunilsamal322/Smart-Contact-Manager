package com.springboot.scm.service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

	public boolean sendEmail(String subject,String message,String to)
	{
		boolean flag=false;
		
		//gmail host
		String host="smtp.gmail.com";
		
		//get the System properties
		Properties properties=System.getProperties();
		
		//sending important information to the properties
		//host set
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");
		
		//Step1 get the Session object
		
		Session session=Session.getInstance(properties, new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				
				return new PasswordAuthentication("samalsunil643@gmail.com","Sunil@1996");
			}
			
			
		});
		
		session.setDebug(true);
		
		//step2 compose the mail(text,multimedia)
		MimeMessage m=new MimeMessage(session);
		try 
		{	
			
			//adding recipient
			m.addRecipient(Message.RecipientType.TO ,new InternetAddress(to));
			
			//adding subject to message
			m.setSubject(subject);
			
			//adding text to message
			//m.setText(message);
			m.setContent(message, "text/html");
			
			
			//step3 send the msg using Transport class
			Transport.send(m);
			flag=true;
			System.out.println("Send successfully");
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return flag;	
	}
}
