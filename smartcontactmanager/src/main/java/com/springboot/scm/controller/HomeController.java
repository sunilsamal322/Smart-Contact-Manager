package com.springboot.scm.controller;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.springboot.scm.dao.UserRepository;
import com.springboot.scm.entities.User;
import com.springboot.scm.helper.Message;

@Controller
public class HomeController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@RequestMapping("/")
	public String home(Model m)
	{
		m.addAttribute("title", "Smart Contact Manager");
		return "home";
	}
	@RequestMapping("/about")
	public String about(Model m)
	{
		m.addAttribute("title", "Smart Contact Manager");
		return "about";
	}
	@RequestMapping("/signup")
	public String signup(Model m)
	{
		m.addAttribute("title", "Smart Contact Manager");
		m.addAttribute("user",new User());
		return "signup";
	}
	@PostMapping("/register")
	public String registerUser(@Valid@ModelAttribute("user") User user,BindingResult bindingResult,@RequestParam(value="agreement",defaultValue = "false") boolean agreement,Model m,HttpSession session)
	{
		try
		{
			if(!agreement)
			{
				System.out.println("You have not agreed the terms and condition");
				throw new Exception("You have not agreed the terms and condition");
			}
			if(bindingResult.hasErrors())
			{
				System.out.println("Error "+bindingResult.toString());
				m.addAttribute("user",user);
				return "signup";
			}
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.jpg");
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			
			System.out.println("agreement"+agreement);
			System.out.println("User"+user);
			
			User result=this.userRepository.save(user);
			
			m.addAttribute("user",new User());
			
			session.setAttribute("message",new Message("Successfully registered !" , "alert-success"));
			
			return "signup";
		}
		catch(Exception e)
		{
			e.printStackTrace();
			m.addAttribute("user",user);
			session.setAttribute("message",new Message("something went wrong !"+e.getMessage(), "alert-danger"));
			return "signup";
		}
	}
	@GetMapping("/signin")
	public String customLogin(Model m)
	{
		m.addAttribute("title","Login Page");
		return "login";
	}
}
