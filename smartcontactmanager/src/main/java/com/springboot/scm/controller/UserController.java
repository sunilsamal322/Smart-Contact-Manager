package com.springboot.scm.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.springboot.scm.dao.ContactRepository;
import com.springboot.scm.dao.UserRepository;
import com.springboot.scm.entities.Contact;
import com.springboot.scm.entities.User;
import com.springboot.scm.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	
	//common data for all
	@ModelAttribute
	public void addCommonData(Model model,Principal principal)
	{
		String userName=principal.getName();
		System.out.println(userName);
		
		User user=userRepository.getUserByName(userName);
		
		System.out.println(user);
		
		model.addAttribute("user", user);
	}
	//dashboard home
	@RequestMapping("/index")
	public String dashboard(Model model,Principal principal)
	{
		model.addAttribute("title","User Dashboard");
		return "normal/user_dashboard";
	}
	@RequestMapping("/add-contact")
	public String openAddContactForm(Model model)
	{
		model.addAttribute("title","Add Contact");
		model.addAttribute("conatct", new Contact());
		
		return "normal/add_contact_form";
	}
	
	@PostMapping(path="/process-contact")
	public String processContact(@ModelAttribute("contact") Contact contact,@RequestParam("profileImage") MultipartFile file,Principal principal,HttpSession session)
	{
		try
		{
			String name=principal.getName();
			User user=this.userRepository.getUserByName(name);
			
				
			//processing and uploading file
			if(file.isEmpty())
			{
				contact.setImage("default.jpg");
				System.out.println("file is empty");
			}
			else
			{
				//not empty so update the name to contact
				contact.setImage(file.getOriginalFilename());
				
				File saveFile=new ClassPathResource("/static/image").getFile();
				
				Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				
				Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
				System.out.println("image is uploaded");
			}
			//
			
			contact.setUser(user);
			user.getContacts().add(contact);
			this.userRepository.save(user);
			
			System.out.println("Data added to the database");
			
			session.setAttribute("message", new Message("Successfully Added","success"));
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
			session.setAttribute("message", new Message("Something went wrong,Try again !","danger"));
		}
		
		return "normal/add_contact_form";
	}
	
	@GetMapping("/show-contacts/{page}")
	public String showContactsHandler(@PathVariable("page") Integer page,Model model,Principal principal)
	{
		model.addAttribute("title","Show User Contacts");
		
		String username=principal.getName();
		User user=this.userRepository.getUserByName(username);
		
		
		Pageable pageable=PageRequest.of(page, 5);
		
		
		Page<Contact> contacts=this.contactRepository.findContactsByUser(user.getId(),pageable);
		
		model.addAttribute("contacts",contacts);
		model.addAttribute("currentPage",page);
		model.addAttribute("totalPages", contacts.getTotalPages());
		
		return "normal/show_contacts";
	
	}
	
	@GetMapping("/{cId}/contact")
	public String showContactDetails(@PathVariable("cId") Integer cId,Model model,Principal principal)
	{
		System.out.println(cId);
		
		Optional<Contact> contactOptional=this.contactRepository.findById(cId);
		
		Contact contact=contactOptional.get();
		
		String username=principal.getName();
		User user=this.userRepository.getUserByName(username);
		
		if(user.getId()==contact.getUser().getId())
		{
			model.addAttribute("title",contact.getName());
			model.addAttribute("contact",contact);
		}
		return "normal/contact_details";
	}
	
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid") Integer cid,Principal principal,HttpSession session)
	{
		Contact contact=this.contactRepository.findById(cid).get();
		
		User user=this.userRepository.getUserByName(principal.getName());
		
		this.contactRepository.delete(contact);
		
		session.setAttribute("message", new Message("Deleted Successfully", "success"));
		
		return "redirect:/user/show-contacts/0";
	}
	
	@PostMapping("/update-contact/{cid}")
	public String updateForm(@PathVariable("cid") int cid,Model model)
	{
		model.addAttribute("title","Update Contact");
		
		Contact contact=this.contactRepository.findById(cid).get();
		
		model.addAttribute("contact",contact);
		
		return "normal/update_form";
	}
	@PostMapping("/process-update")
	public String updateProcessForm(@ModelAttribute("contact") Contact contact,@RequestParam("profileImage") MultipartFile file,Model model,HttpSession session,Principal principal)
	{
		try
		{
			Contact oldContact=this.contactRepository.findById(contact.getCid()).get();
			
			if(!file.isEmpty())
			{
				//delete old photo
				File deleteFile=new ClassPathResource("/static/image").getFile();
				
				File file1=new File(deleteFile,oldContact.getImage());
				
				file1.delete();
				
				//update new				
				File saveFile=new ClassPathResource("/static/image").getFile();
				
				Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				
				Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
				
				contact.setImage(file.getOriginalFilename());
			}
			else
			{
				contact.setImage(oldContact.getImage());
			}
			String userName=principal.getName();
			User user=this.userRepository.getUserByName(userName);
			contact.setUser(user);
			this.contactRepository.save(contact);
			session.setAttribute("message", new Message("Successfully Updated", "success"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return "redirect:/user/"+contact.getCid()+"/contact";
	}
	
	@GetMapping("/profile")
	public String showProfile(Model model)
	{
		model.addAttribute("title","Profile");
		return "normal/profile_view";
	}
	@PostMapping("/update-userform/{id}")
	public String userUpdateForm(@PathVariable("id") int id,Model model)
	{
		User user=this.userRepository.findById(id).get();
		
		model.addAttribute("title","Update Profile");
		
		return "normal/update_user_form";
	}
	
	@PostMapping("/profile-process-update")
	public String processUpdateUser(@ModelAttribute("user") User user,@RequestParam("profileImage") MultipartFile file,Model model,Principal princiapl,HttpSession session)
	{
		try
		{
			User oldUser=this.userRepository.findById(user.getId()).get();
			
			if(!file.isEmpty())
			{
				File deleteFile=new ClassPathResource("/static/image").getFile();
				
				File file1=new File(deleteFile,oldUser.getImageUrl());
				
				file1.delete();
				
				//update new				
				File saveFile=new ClassPathResource("/static/image").getFile();
				
				Path path=Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				
				Files.copy(file.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
				
				user.setImageUrl(file.getOriginalFilename());
			}
			else
			{
				user.setImageUrl(oldUser.getImageUrl());
			}
			this.userRepository.save(user);
			session.setAttribute("message", new Message("Successfully your profile updated", "success"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return "redirect:/user/profile";
	}
	
	@GetMapping("/setting")
	public String openSetting()
	{
		return "normal/setting";
	}
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldpassword") String oldpassword,@RequestParam("newpassword") String newpassword,Principal principal,HttpSession session)
	{ 
		User user=this.userRepository.getUserByName(principal.getName());
		
		if(this.bCryptPasswordEncoder.matches(oldpassword, user.getPassword()))
		{
			//change password
			user.setPassword(this.bCryptPasswordEncoder.encode(newpassword));
			this.userRepository.save(user);
			session.setAttribute("message", new Message("Your password successfully changed", "success"));
			return "redirect:/user/index";
		}
		else
		{
			session.setAttribute("message", new Message("Incorrect password,Please enter correct password", "danger"));
			return "redirect:/user/setting";
		}
		
	}
}
