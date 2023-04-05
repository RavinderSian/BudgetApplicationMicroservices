package com.personal.budget.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.personal.budget.model.Expense;
import com.personal.budget.model.ExpenseDTO;
import com.personal.budget.model.User;
import com.personal.budget.service.ExpenseService;
import com.personal.budget.service.UserService;

@Controller
public class ExpenseController {
	
	private final ExpenseService service;
	private final UserService userService;
	
	public ExpenseController(ExpenseService service, UserService userService) {
		this.service = service;
		this.userService = userService;
	}
	
	@GetMapping("/budget")
	public String budget(Model model, HttpServletRequest request) {
		
		if (request.getUserPrincipal() == null) {
			model.addAttribute("expenses", new ArrayList<>());
		} else {
			Long userId = userService.findByUsername(request.getUserPrincipal().getName()).get().getId();
			model.addAttribute("expenses", service.findExpensesByYearForUser(LocalDate.now().getYear(), userId));
		}
		
		model.addAttribute("currentYear", LocalDate.now().getYear());
		model.addAttribute("previousYear", LocalDate.now().getYear() - 1);
		model.addAttribute("nextYear", LocalDate.now().getYear() + 1);
		
		model.addAttribute("currentMonth", LocalDate.now().getMonth().toString());
		
		model.addAttribute("expense", new Expense());
		model.addAttribute("expenseToEdit", new ExpenseDTO());


		model.addAttribute("user", new User());
		
		return "budget";
	}
	
	@PreAuthorize("hasAuthority('USER')")
	@GetMapping("/budget/{year}")
	public String budgetForYear(@PathVariable Integer year, Model model, HttpServletRequest request) {
		
		Long userId = userService.findByUsername(request.getUserPrincipal().getName()).get().getId();
		model.addAttribute("expenses", service.findExpensesByYearForUser(year, userId));
		
		model.addAttribute("expense", new Expense());
		model.addAttribute("currentYear", year);
		model.addAttribute("previousYear", year - 1);
		model.addAttribute("nextYear", year + 1);
		model.addAttribute("currentMonth", LocalDate.now().getMonth().toString());
		
		return "budget-year";
	}
	
	//Save incase we need this again
//	@PreAuthorize("hasAuthority('USER')")
//	@PostMapping("/addexpense")
//	public String addExpense(@ModelAttribute @Valid Expense newExpense, Model model, BindingResult bindingResult,
//			HttpServletRequest request,
//			 RedirectAttributes redirectAttributes) {
//		
//		if (bindingResult.hasErrors()) {
//			
//		}
//		
//		String loggedInUsername = request.getUserPrincipal().getName();
//		
//		newExpense.setUserId(userService.findByUsername(loggedInUsername).get().getId());
//		
//		service.save(newExpense);
//
//		return "redirect:/budget";
//	}
	
	@PostMapping("/editexpense")
	public String editExpense(Model model, @ModelAttribute ExpenseDTO expenseDTO, HttpServletRequest request) {
		
		service.updateExpense(expenseDTOToExpense(expenseDTO));
		return "redirect:/budget";
	}
	
	private Expense expenseDTOToExpense(ExpenseDTO expenseDTO) {
		
		Expense expense = new Expense();
		
		String[] splitDate = expenseDTO.getPurchaseDate().split("-");
		
		expense.setId(Long.valueOf(expenseDTO.getId()));
		expense.setCategory(expenseDTO.getCategory());
		expense.setDescription(expenseDTO.getDescription());
		expense.setAmount(new BigDecimal(expenseDTO.getAmount()));
		expense.setPurchaseDate(LocalDate.of(Integer.valueOf(splitDate[0]), Integer.valueOf(splitDate[1]), Integer.valueOf(splitDate[2])));
		
		return expense;
		
	}
	
}
