package com.personal.budget.repository;

import java.util.Optional;

import com.personal.budget.model.User;

public interface UserRepository {

	User save(User user);
	void deleteById(Long id);
	Optional<User> findByUsername(String username);
	Optional<User> findById(Long id);
}
 