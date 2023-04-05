package com.personal.budget.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.personal.budget.model.User;
import com.personal.budget.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
	
	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/newuser")
	public ResponseEntity<?> registerUser(@RequestBody @Valid User user, BindingResult bindingResult, 
			HttpServletResponse httpServletResponse, HttpServletRequest httpServletRequest){
		
		if (bindingResult.hasFieldErrors()) {
			
			Map<String, String> errorMap = new HashMap<>();
			
			bindingResult.getFieldErrors().forEach(error -> errorMap.put(error.getField(), error.getDefaultMessage()));
			
			return new ResponseEntity<>(errorMap.toString(), HttpStatus.BAD_REQUEST);
		}
		
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		user.setPassword(encoder.encode(user.getPassword()));
		user.setAuthority("USER");
		
		try {
			userService.save(user);
		} catch(DuplicateKeyException exception) {
			
			String exceptionMessage = exception.getMessage();
			System.out.println(exceptionMessage);
			String field = exceptionMessage.substring(exceptionMessage.indexOf("\"")+1, 
					exceptionMessage.indexOf("\"", exceptionMessage.indexOf("\"")+1)).split("_")[1];
			
			
			return new ResponseEntity<>(field.concat(" has already been taken"), HttpStatus.BAD_REQUEST);
		}
		catch(DataAccessException exception) {
			return new ResponseEntity<>("Currently down due to maintenance", HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
}
