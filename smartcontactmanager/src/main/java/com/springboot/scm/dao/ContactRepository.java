package com.springboot.scm.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.springboot.scm.entities.Contact;
import com.springboot.scm.entities.User;

public interface ContactRepository extends JpaRepository<Contact, Integer> {

	@Query("from Contact as c where c.user.id=:userId")
	public Page<Contact> findContactsByUser(@Param("userId") int userId,Pageable pagebale);
	
	@Query("from Contact as c where c.user.id=:userId and c.name=:userName")
	public Page<Contact> findByUserName(@Param("userId") int userId,@Param("userName") String name,Pageable pageable);
	
	//searching 
	public List<Contact> findByNameContainingAndUser(String name,User user);
}
