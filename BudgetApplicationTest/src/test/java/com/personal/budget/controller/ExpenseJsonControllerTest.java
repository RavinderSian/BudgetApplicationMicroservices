package com.personal.budget.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.personal.budget.config.SecurityConfig;
import com.personal.budget.model.Expense;
import com.personal.budget.model.User;
import com.personal.budget.repository.ExpenseRepository;
import com.personal.budget.repository.UserRepository;
import com.personal.budget.service.ExpenseService;
import com.personal.budget.service.ExpenseServiceImpl;
import com.personal.budget.service.UserService;
import com.personal.budget.service.UserServiceImpl;

@WebMvcTest(ExpenseJsonController.class)
@Import(SecurityConfig.class)
class ExpenseJsonControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	private ExpenseJsonController controller;
	
	@MockBean
	private ExpenseService service;
	
	@MockBean
	private ExpenseRepository expenseRepository;
	
	@MockBean
	private UserService userService;
	
	@MockBean
	private UserRepository userRepository;
	
	@BeforeEach
	void setUp() throws Exception {
		controller = new ExpenseJsonController(new ExpenseServiceImpl(expenseRepository), new UserServiceImpl(userRepository));
	}
	
	@WithMockUser(username = "test", password = "testing", authorities = "USER")
	@Test
	void test_DeleteRequest_DeletesEntity_WhenGivenValidEntity() throws Exception {
		
		mockMvc.perform(get("/delete/1"))
		.andExpect(status().isOk());
	}
	
	@WithMockUser(username = "rsian", password = "pw", authorities = "USER")
	@Test
	void test_Search_ReturnsEmptyResultsArray_WhenGivenSearchStringWithNoResults() throws Exception {
		
		User user = new User();
		user.setId(1L);
		user.setAuthority("USER");
		user.setEmail("rsian@gmail.com");
		user.setPassword("test");
		user.setUsername("testing");
		
		when(userService.findByUsername("rsian")).thenReturn(Optional.of(user));
		
		mockMvc.perform(post("/search").content("car"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(0)));
	}
	
	@WithMockUser(username = "rsian", password = "pw", authorities = "USER")
	@Test
	void test_Search_ReturnsResultsAsExpected_WhenGivenSearchStringWithMultipleResults() throws Exception {
		
		User user = new User();
		user.setId(1L);
		user.setAuthority("USER");
		user.setEmail("rsian@gmail.com");
		user.setPassword("test");
		user.setUsername("testing");
		
		Expense expense = new Expense();
		expense.setUserId(1L);
		expense.setAmount(BigDecimal.valueOf(10));
		expense.setCategory("Dates");
		expense.setDescription("car");
		expense.setPurchaseDate(LocalDate.now());
		
		Expense expense2 = new Expense();
		expense2.setUserId(1L);
		expense2.setAmount(BigDecimal.valueOf(10));
		expense2.setCategory("Dates");
		expense2.setDescription("cars");
		expense2.setPurchaseDate(LocalDate.now());

		when(userService.findByUsername("rsian")).thenReturn(Optional.of(user));
		when(service.getSearchResults(1L, "car")).thenReturn(Arrays.asList(expense, expense2));
		
		mockMvc.perform(post("/search").content("car"))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$", hasSize(2)))
		.andExpect(jsonPath("$[0]['description']", containsString("car")))
		.andExpect(jsonPath("$[1]['description']", containsString("cars")));
	}
	
	@Test
	@WithMockUser(username = "rsian", password = "pw", authorities = "USER")
	void test_AddExpense_ReturnsCorrectStatusAndResponse_WhenGivenValidExpense() throws Exception {

		User user = new User();
		user.setId(1L);
		user.setAuthority("USER");
		user.setEmail("rsian761@gmail.com");
		user.setPassword("testing");
		user.setUsername("rsian");
		
		Expense expense = new Expense();
		expense.setUserId(1L);
		expense.setAmount(BigDecimal.valueOf(10));
		expense.setCategory("Dates");
		expense.setDescription("car");
		expense.setPurchaseDate(LocalDate.now());
		
	    ObjectMapper mapper = new ObjectMapper();
	    mapper.registerModule(new JavaTimeModule()); 
	    
		when(userService.findByUsername("rsian")).thenReturn(Optional.of(user));
		
		mockMvc.perform(post("/addexpensejson").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writer().writeValueAsString(expense)))
				.andExpect(status().isOk());
	}
	
	@Test
	@WithMockUser(username = "rsian", password = "pw", authorities = "USER")
	void test_AddExpense_ReturnsCorrectStatusAndResponse_WhenGivenExpenseWithInvalidAmountZERO() throws Exception {

		User user = new User();
		user.setId(1L);
		user.setAuthority("USER");
		user.setEmail("rsian761@gmail.com");
		user.setPassword("testing");
		user.setUsername("rsian");
		
		Expense expense = new Expense();
		expense.setUserId(1L);
		expense.setAmount(BigDecimal.ZERO);
		expense.setCategory("Dates");
		expense.setDescription("car");
		expense.setPurchaseDate(LocalDate.now());
		
	    ObjectMapper mapper = new ObjectMapper();
	    mapper.registerModule(new JavaTimeModule()); 
	    
		when(userService.findByUsername("rsian")).thenReturn(Optional.of(user));
		
		mockMvc.perform(post("/addexpensejson").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writer().writeValueAsString(expense)))
		.andDo(print())
				.andExpect(status().isBadRequest());
	}
	
	@Test
	@WithMockUser(username = "rsian", password = "pw", authorities = "USER")
	void test_AddExpense_ReturnsCorrectStatusAndResponse_WhenDataAccessExceptionThrown() throws Exception {

		User user = new User();
		user.setId(1L);
		user.setAuthority("USER");
		user.setEmail("rsian761@gmail.com");
		user.setPassword("testing");
		user.setUsername("rsian");
		
		Expense expense = new Expense();
		expense.setUserId(1L);
		expense.setAmount(BigDecimal.TEN);
		expense.setCategory("Dates");
		expense.setDescription("car");
		expense.setPurchaseDate(LocalDate.now());
		
	    ObjectMapper mapper = new ObjectMapper();
	    mapper.registerModule(new JavaTimeModule()); 
	    
		when(userService.findByUsername("rsian")).thenReturn(Optional.of(user));
		
		when(service.save(any(Expense.class))).thenThrow(new DuplicateKeyException("duplicate key value violates unique constraint \"unique_username\""));
		
		mockMvc.perform(post("/addexpensejson").contentType(MediaType.APPLICATION_JSON)
				.content(mapper.writer().writeValueAsString(expense)))
		.andDo(print())
				.andExpect(status().isServiceUnavailable());
	}
	
}
