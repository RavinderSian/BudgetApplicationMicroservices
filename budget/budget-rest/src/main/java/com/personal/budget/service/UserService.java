package com.personal.budget.service;

import java.util.Optional;

import com.personal.budget.model.User;

public interface UserService {
	
	User save(User user);
	void deleteById(Long id);
	Optional<User> findByUsername(String username);
	Optional<User> findById(Long id);

}
