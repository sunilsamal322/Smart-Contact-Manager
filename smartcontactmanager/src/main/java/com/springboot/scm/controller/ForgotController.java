package com.springboot.scm.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.springboot.scm.dao.UserRepository;
import com.springboot.scm.entities.User;
import com.springboot.scm.service.EmailService;

@Controller
public class ForgotController {
	
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private EmailService emailService;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	Random random=new Random();
	
	@GetMapping("/forgot")
	public String openEmailForm()
	{
		return "forgot_email_form";
	}
	@PostMapping("/send-otp")
	public String sendOtp(@RequestParam("email") String email,HttpSession session)
	{
		//generating 4 digit otp
		
		
		int otp=random.nextInt(1000,9999);
		
		
		//write code for send otp to email
		String Subject="OTP from SCM";
		String message=""
						+"<div style='border:1px solid #e2e2e2; padding:20px;'>"
						+"<h1>"
						+"OTP is "
						+"<b>"+otp
						+"</b>"
						+"</h1>"
						+"</div>";
		String to=email;
		
		boolean flag=this.emailService.sendEmail(Subject, message,to);
		
		if(flag)
		{
			session.setAttribute("myotp", otp);
			session.setAttribute("email", email);
			return "verify_otp";
		}
		else
		{
			session.setAttribute("message","Check your email id");
			return "forgot_email_form";
		}
	}
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp") int otp,HttpSession session)
	{
		int myotp=(int)session.getAttribute("myotp");
		String email=(String)session.getAttribute("email");
		
		if(myotp==otp)
		{
			User user=this.userRepository.getUserByName(email);
			if(user==null)
			{
				session.setAttribute("message","This email id is not registered");
				return "forgot_email_form";
			}
			else
			{
				return "password_change_form";
			}
		}
		else
		{
			session.setAttribute("message", "You have entered wrong otp");
			return "verify_otp";
		}
	}
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("newPassword") String newPassword,HttpSession session)
	{
		String email=(String)session.getAttribute("email");
		User user=this.userRepository.getUserByName(email);
		user.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
		this.userRepository.save(user);
		return "redirect:/signin?change=password changed successfully";
	}
}
