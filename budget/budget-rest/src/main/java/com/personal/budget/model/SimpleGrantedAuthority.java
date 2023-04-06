package com.personal.budget.model;

import org.springframework.security.core.GrantedAuthority;

public class SimpleGrantedAuthority implements GrantedAuthority {

	private final String authority;
	
	public SimpleGrantedAuthority(String role) {
		this.authority = "ROLE_" + role;
	}

	@Override
	public String getAuthority() {
		return authority;
	}

}
