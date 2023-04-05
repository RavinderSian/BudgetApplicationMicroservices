package com.personal.budget.controller;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.personal.budget.config.SecurityConfig;
import com.personal.budget.model.Expense;
import com.personal.budget.model.ExpenseDTO;
import com.personal.budget.model.User;
import com.personal.budget.repository.ExpenseRepository;
import com.personal.budget.repository.UserRepository;
import com.personal.budget.service.ExpenseService;
import com.personal.budget.service.ExpenseServiceImpl;
import com.personal.budget.service.UserService;
import com.personal.budget.service.UserServiceImpl;

@WebMvcTest(ExpenseController.class)
@Import(SecurityConfig.class)
class ExpenseControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	private ExpenseController controller;
	
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
		controller = new ExpenseController(new ExpenseServiceImpl(expenseRepository), new UserServiceImpl(userRepository));
	}
	
	@Test
	void test_Budget_DisplaysPageAsExpected_WhenHitWithNoLogin() throws Exception {
		
		mockMvc.perform(get("/budget"))
		.andExpect(status().isOk())
		.andExpect(view().name("budget"))
		.andExpect(model().attribute("expenses", hasSize(0)))
		.andExpect(model().attribute("currentYear", equalTo(LocalDate.now().getYear())))
		.andExpect(model().attribute("previousYear", equalTo(LocalDate.now().getYear()-1)))
		.andExpect(model().attribute("nextYear", equalTo(LocalDate.now().getYear()+1)))
		.andExpect(model().attribute("currentMonth", LocalDate.now().getMonth().toString()))
		.andExpect(model().attribute("expense", equalTo(new Expense())))
		.andExpect(model().attribute("expenseToEdit", equalTo(new ExpenseDTO())))
		.andExpect(model().attribute("user", equalTo(new User())));
		
	}
	
	@Test
	@WithMockUser(username = "rsian", password = "pw", authorities = "USER")
	void test_Budget_DisplaysPageAsExpected_WhenHitWithLoggedInUser() throws Exception {
		
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
		
		User user = new User();
		user.setId(1L);
		user.setAuthority("USER");
		user.setEmail("rsian@gmail.com");
		user.setPassword("test");
		user.setUsername("testing");
		
		when(userService.findByUsername("rsian")).thenReturn(Optional.of(user));
		
		when(service.findExpensesByYearForUser(LocalDate.now().getYear(), 1L))
			.thenReturn(Arrays.asList(expense, expense2));
		
		mockMvc.perform(get("/budget"))
		.andExpect(status().isOk())
		.andExpect(view().name("budget"))
		.andExpect(model().attribute("expenses", hasSize(2)))
		.andExpect(model().attribute("currentYear", equalTo(LocalDate.now().getYear())))
		.andExpect(model().attribute("previousYear", equalTo(LocalDate.now().getYear()-1)))
		.andExpect(model().attribute("nextYear", equalTo(LocalDate.now().getYear()+1)))
		.andExpect(model().attribute("currentMonth", LocalDate.now().getMonth().toString()))
		.andExpect(model().attribute("expense", equalTo(new Expense())))
		.andExpect(model().attribute("expenseToEdit", equalTo(new ExpenseDTO())))
		.andExpect(model().attribute("user", equalTo(new User())));
		
		verify(service, times(1)).findExpensesByYearForUser(LocalDate.now().getYear(), 1L);
	}
	
	@Test
	@WithMockUser(username = "rsian", password = "pw", authorities = "USER")
	void test_BudgetYear_DisplaysPageAsExpected_WhenGivenCurrentYear() throws Exception {
		
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
		
		User user = new User();
		user.setId(1L);
		user.setAuthority("USER");
		user.setEmail("rsian@gmail.com");
		user.setPassword("test");
		user.setUsername("testing");
		
		when(userService.findByUsername("rsian")).thenReturn(Optional.of(user));
		
		when(service.findExpensesByYearForUser(LocalDate.now().getYear(), 1L))
			.thenReturn(Arrays.asList(expense, expense2));
		
		mockMvc.perform(get("/budget/" + LocalDate.now().getYear()))
		.andExpect(status().isOk())
		.andExpect(view().name("budget-year"))
		.andExpect(model().attribute("expenses", hasSize(2)))
		.andExpect(model().attribute("currentYear", equalTo(LocalDate.now().getYear())))
		.andExpect(model().attribute("previousYear", equalTo(LocalDate.now().getYear()-1)))
		.andExpect(model().attribute("nextYear", equalTo(LocalDate.now().getYear()+1)))
		.andExpect(model().attribute("currentMonth", LocalDate.now().getMonth().toString()))
		.andExpect(model().attribute("expense", equalTo(new Expense())));
		
		verify(service, times(1)).findExpensesByYearForUser(LocalDate.now().getYear(), 1L);
	}
	
	@Test
	void test_BudgetYear_ReturnsHTTPStatus401_WhenNoUserLoggedIn() throws Exception {
		
		mockMvc.perform(get("/budget/" + LocalDate.now().getYear()))
		.andExpect(status().isUnauthorized());
		
	}
	
	//Keep incase we add add expense endpoint again
//	@Test
//	@WithMockUser(username = "rsian", password = "pw", authorities = "USER")
//	void test_AddExpense_ReturnsCorrectStatusAndResponse_WhenGivenValidExpense() throws Exception {
//
//		User user = new User();
//		user.setId(1L);
//		user.setAuthority("USER");
//		user.setEmail("rsian761@gmail.com");
//		user.setPassword("testing");
//		user.setUsername("rsian");
//		
//		Expense expense = new Expense();
//		expense.setUserId(1L);
//		expense.setAmount(BigDecimal.valueOf(10));
//		expense.setCategory("Dates");
//		expense.setDescription("car");
//		expense.setPurchaseDate(LocalDate.now());
//		
//	    ObjectMapper mapper = new ObjectMapper();
//	    mapper.registerModule(new JavaTimeModule()); 
//	    
//		when(userService.findByUsername("rsian")).thenReturn(Optional.of(user));
//		
//		mockMvc.perform(post("/addexpense").contentType(MediaType.APPLICATION_JSON)
//				.content(mapper.writer().writeValueAsString(expense)))
//				.andExpect(status().isBadRequest());
//	}
	
	//Keep incase we add add expense endpoint again
//	@Test
//	@WithMockUser(username = "rsian", password = "pw", authorities = "USER")
//	void test_AddExpense_ReturnsCorrectStatusAndResponse_WhenGivenInValidExpense() throws Exception {
//
//		User user = new User();
//		user.setId(1L);
//		user.setAuthority("USER");
//		user.setEmail("rsian761@gmail.com");
//		user.setPassword("testing");
//		user.setUsername("rsian");
//		
//		Expense expense = new Expense();
//		expense.setUserId(1L);
//		expense.setAmount(BigDecimal.valueOf(10));
//		expense.setCategory("Dates");
//		expense.setPurchaseDate(LocalDate.now());
//		
//	    ObjectMapper mapper = new ObjectMapper();
//	    mapper.registerModule(new JavaTimeModule()); 
//	    
//		when(userService.findByUsername("rsian")).thenReturn(Optional.of(user));
//		
//		mockMvc.perform(post("/addexpense").contentType(MediaType.APPLICATION_JSON)
//				.content(mapper.writer().writeValueAsString(expense)))
//				.andExpect(status().isBadRequest());
//	}
	
	@Test
	@WithMockUser(username = "rsian", password = "pw", authorities = "USER")
	void test_EditExpense_ReturnsCorrectStatusAndResponse_WhenGivenValidExpenseDTO() throws Exception {

		User user = new User();
		user.setId(1L);
		user.setAuthority("USER");
		user.setEmail("rsian761@gmail.com");
		user.setPassword("testing");
		user.setUsername("rsian");
		
		when(userService.findByUsername("rsian")).thenReturn(Optional.of(user));
		
		mockMvc.perform(post("/editexpense").contentType(MediaType.APPLICATION_JSON)
				.param("amount", "10")
				.param("id", "1")
				.param("userId", "1")
				.param("description", "car")
				.param("category", "Dates")
				.param("purchaseDate", "2023-02-03"))
				.andExpect(status().isFound());
	}


}
