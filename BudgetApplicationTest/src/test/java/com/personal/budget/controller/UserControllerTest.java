package com.personal.budget.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.personal.budget.config.SecurityConfig;
import com.personal.budget.model.User;
import com.personal.budget.repository.UserRepository;
import com.personal.budget.service.UserService;
import com.personal.budget.service.UserServiceImpl;

//Import is needed because webmvctest is sliced testing
//It will ignore the config
@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	private UserController controller;
	
	@MockBean
	private UserService service;
	
	@MockBean
	private UserRepository userRepository;
	
	@BeforeEach
	void setUp() throws Exception {
		controller = new UserController(new UserServiceImpl(userRepository));
	}

	@Test
	void test_RegisterUser_ReturnsCorrectStatusAndResponse_WhenGivenValidUser() throws Exception {

		User user = new User();
		user.setEmail("rsian761@gmail.com");
		user.setPassword("testing");
		user.setUsername("rsian");
		
		User user2 = new User();
		user2.setId(1L);
		user2.setAuthority("USER");
		user2.setEmail("rsian761@gmail.com");
		user2.setPassword("testing");
		user2.setUsername("rsian");
		
	    ObjectMapper mapper = new ObjectMapper();
		
		when(service.save(user)).thenReturn(user2);
	
		mockMvc.perform(post("/user/newuser").contentType(MediaType.APPLICATION_JSON).content(mapper.writer().writeValueAsString(user)))
				.andExpect(status().isOk());
	}
	
	
	@Test
	void test_RegisterUser_GivesBadRequestStatus_WhenNoEmailPresent() throws Exception {

		User user = new User();
		user.setPassword("testing");
		user.setUsername("rsian");
		
	    ObjectMapper mapper = new ObjectMapper();
		
		mockMvc.perform(post("/user/newuser").contentType(MediaType.APPLICATION_JSON).content(mapper.writer().writeValueAsString(user)))
				.andExpect(status().isBadRequest());
	}
	
	@Test
	void test_RegisterUser_GivesBadRequestStatus_WhenNoUsernamePresent() throws Exception {

		User user = new User();
		user.setEmail("rsian761@gmail.com");
		user.setPassword("testing");
		
	    ObjectMapper mapper = new ObjectMapper();
		
		mockMvc.perform(post("/user/newuser").contentType(MediaType.APPLICATION_JSON).content(mapper.writer().writeValueAsString(user)))
				.andExpect(status().isBadRequest());
	}
	
	@Test
	void test_RegisterUser_GivesBadRequestStatus_WhenNoPasswordPresent() throws Exception {

		User user = new User();
		user.setEmail("rsian761@gmail.com");
		user.setUsername("rsian");
		
	    ObjectMapper mapper = new ObjectMapper();
		
		mockMvc.perform(post("/user/newuser").contentType(MediaType.APPLICATION_JSON).content(mapper.writer().writeValueAsString(user)))
				.andExpect(status().isBadRequest());
	}
	
	@Test
	void test_RegisterUser_ReturnsCorrectStatusAndResponse_WhenDuplicateKeyExceptionThrown() throws Exception {

		User user = new User();
		user.setEmail("rsian761@gmail.com");
		user.setPassword("testing");
		user.setUsername("rsian");
		
		User user2 = new User();
		user2.setId(1L);
		user2.setAuthority("USER");
		user2.setEmail("rsian761@gmail.com");
		user2.setPassword("testing");
		user2.setUsername("rsian");
		
	    ObjectMapper mapper = new ObjectMapper();
	    
		when(service.save(any(User.class))).thenThrow(new DuplicateKeyException("duplicate key value violates unique constraint \"unique_username\""));
	
		mockMvc.perform(post("/user/newuser").contentType(MediaType.APPLICATION_JSON).content(mapper.writer().writeValueAsString(user)))
				.andExpect(status().isBadRequest());
	}
}
