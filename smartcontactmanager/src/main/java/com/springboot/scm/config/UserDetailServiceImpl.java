package com.springboot.scm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.springboot.scm.dao.UserRepository;
import com.springboot.scm.entities.User;

public class UserDetailServiceImpl implements UserDetailsService{

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		User user=userRepository.getUserByName(username);
		
		if(user==null)
		{
			throw new UsernameNotFoundException("could not find user !");
		}
			
		CustomUserDetails customUserDetails=new CustomUserDetails(user);
		
		return customUserDetails;
	}
	
}
