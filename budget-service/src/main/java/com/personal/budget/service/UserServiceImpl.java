package com.personal.budget.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.personal.budget.model.User;
import com.personal.budget.model.UserPrincipal;
import com.personal.budget.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService, UserDetailsService {

	private final UserRepository repository;
	
	public UserServiceImpl(UserRepository repository) {
		this.repository = repository;
	}

	@Override
	public User save(User user) {
		return repository.save(user); 
	}

	@Override
	public void deleteById(Long id) {
		repository.deleteById(id);
	}

	@Override
	public Optional<User> findByUsername(String username) {
		return repository.findByUsername(username);
	}

	@Override
	public Optional<User> findById(Long id) {
		return repository.findById(id);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = repository.findByUsername(username).get();
		
		if (user == null) {
			throw new UsernameNotFoundException("Failed login");
		}
		
        List<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<>(Arrays.asList(new SimpleGrantedAuthority(user.getAuthority())));
		return new UserPrincipal(user, grantedAuthorities);
	}

}
