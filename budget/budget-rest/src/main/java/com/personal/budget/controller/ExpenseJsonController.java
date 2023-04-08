package com.personal.budget.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.personal.budget.model.Expense;
import com.personal.budget.model.User;
import com.personal.budget.service.ExpenseService;
import com.personal.budget.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/expenses")
public class ExpenseJsonController {
	
	private final ExpenseService service;
	private final UserService userService;
	
	public ExpenseJsonController(ExpenseService service, UserService userService) {
		this.service = service;
		this.userService = userService;
	}
	
	@GetMapping("/delete/{id}")
	public ResponseEntity<?> deleteExpense(@PathVariable Long id, HttpServletRequest request,
			 RedirectAttributes redirectAttributes) {
		
		service.deleteById(id);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PostMapping("/search")
	public ResponseEntity<?> search(@RequestBody String searchString ,HttpServletRequest request) {
		
		Long userId = userService.findByUsername(request.getUserPrincipal().getName()).get().getId();

		List<Expense> results = service.getSearchResults(userId, searchString);
		
		return new ResponseEntity<>(results, HttpStatus.OK);
	}
	
	@PostMapping("/addexpensejson")
	public ResponseEntity<?> addExpenseJSON(@RequestBody @Valid Expense newExpense, BindingResult bindingResult,
			HttpServletRequest request) {
		
		if (bindingResult.hasFieldErrors()) {
			
			Map<String, String> errorMap = new HashMap<>();
			bindingResult.getFieldErrors().forEach(error -> errorMap.put(error.getField(), error.getDefaultMessage()));
			
			return new ResponseEntity<>(errorMap.toString(), HttpStatus.BAD_REQUEST);
		}
		
		String loggedInUsername = request.getUserPrincipal().getName();
		
		newExpense.setUserId(userService.findByUsername(loggedInUsername).get().getId());
		
		try {
			service.save(newExpense);
		}
		catch(Exception exception) {
			return new ResponseEntity<>("Currently down due to maintenance", HttpStatus.SERVICE_UNAVAILABLE);
		}
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@GetMapping("/{username}")
	public ResponseEntity<?> getExpensesForUser(@PathVariable String username, HttpServletRequest request,
			 RedirectAttributes redirectAttributes) {
		
		Optional<User> userOptional = userService.findByUsername(username);
		
		if (userOptional.isEmpty()) {
			log.error(String.format("User %s not found", username));
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		List<Expense> expenses = service.findByUserId(userOptional.get().getId());
		
		if (expenses.isEmpty()) 
				log.info(String.format("No expenses for user: %s", username));
		
		return new ResponseEntity<>(expenses, HttpStatus.OK);
	}
	
	@GetMapping("/{username}/{year}")
	public ResponseEntity<?> getExpensesForUserAndYear(@PathVariable String username, @PathVariable String year, HttpServletRequest request,
			 RedirectAttributes redirectAttributes) {
		
		Optional<User> userOptional = userService.findByUsername(username);
		
		if (userOptional.isEmpty()) {
			log.error(String.format("User %s not found", username));
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		List<Expense> expenses = service.findExpensesByYearForUser(Integer.valueOf(year) ,userOptional.get().getId());
		
		if (expenses.isEmpty()) 
				log.info(String.format("No expenses for user: %s year: %s", username, year));
		
		return new ResponseEntity<>(expenses, HttpStatus.OK);
	}

}
