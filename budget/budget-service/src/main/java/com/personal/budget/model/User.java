package com.personal.budget.model;

import java.io.Serializable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class User implements Serializable {
	
	
	private Long id;
	
	@NotBlank
	private String username;
	
	@NotBlank
	private String password;
	
	private String authority;
	
	@Email
	@NotBlank
	private String email;

}
