package com.shaif.users;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;

@RestController
public class UserResource {
	@Autowired
	UserDaoImpl userDao;
	
	@GetMapping("/users")
	public List<User> retrieveAllUsers()
	{
		return userDao.findALL();
	}
	
//	@GetMapping("/users/{id}")
//	public User retrieveUser(@PathVariable("id")int id) {
//		User user= userDao.findOne(id);
//		
//		if(user==null) {
//			throw new UserNotFoundException("id - "+id);
//		}
//		return user;
//	}
	
	@GetMapping("/users/{id}")
	public EntityModel<User> retrieveUser(@PathVariable int id){
		User user=userDao.findOne(id);
		if(user==null) 
			throw new UserNotFoundException("id -"+id);
	
		EntityModel<User> model=EntityModel.of(user);
		
		WebMvcLinkBuilder linkTo=WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).retrieveAllUsers());
		
		model.add(linkTo.withRel("all-users"));
		return model;
		}

	@PostMapping("/users")
	public ResponseEntity<Object> createUserWithCreatedStatusAndLocation(@Valid @RequestBody User user){
			User savedUser=userDao.save(user);
			URI uri=ServletUriComponentsBuilder.fromCurrentRequest()
					.path("/{id}")
					.buildAndExpand(savedUser.getId())
					.toUri();
			//return ResponseEntity.created(uri).build();
		return ResponseEntity.created(uri).body(savedUser);	
	}
	
	@DeleteMapping("/users/{id}")
	public ResponseEntity deleteUser(@PathVariable int id) {
		User user=userDao.deleteById(id);
		if(user ==null)
			throw new UserNotFoundException("id - "+id);
		return ResponseEntity.status(HttpStatus.OK).body(user);
	}
	
	
}
