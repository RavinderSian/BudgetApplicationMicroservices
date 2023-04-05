package com.personal.budget.repository;

import java.util.List;
import java.util.Optional;

import com.personal.budget.model.Expense;

public interface ExpenseRepository {

	Expense save(Expense expense);
	void deleteById(Long id);
	Optional<Expense> findById(Long id);
	List<Expense> findAll();
	List<Expense> findByUserId(Long userId);
	List<Expense> findExpensesByYearForUser(Integer year, Long userId);
	void updateExpense(Expense expense);
	List<Expense> findExpensesByMatchingDescriptionForUser(Long userId, String searchString);
	
}
