package com.personal.budget.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.personal.budget.mappers.ExpenseRowMapper;
import com.personal.budget.model.Expense;

@Repository
public class ExpenseRepositoryImpl implements ExpenseRepository {

	private final JdbcTemplate jdbcTemplate;
	
	public ExpenseRepositoryImpl(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public Expense save(Expense expense) {
		
		KeyHolder holder = new GeneratedKeyHolder();
		
		jdbcTemplate.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				PreparedStatement ps = connection.prepareStatement("INSERT INTO expenses (category, amount, user_id, description, purchase_date) VALUES (?, ?, ?, ?, ?)",
						Statement.RETURN_GENERATED_KEYS);
				
				ps.setString(1, expense.getCategory());
				ps.setBigDecimal(2, expense.getAmount());
				ps.setLong(3, expense.getUserId());
				ps.setString(4, expense.getDescription());
				ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.of(expense.getPurchaseDate(), LocalTime.MIDNIGHT)));
				
				return ps;
			}
		}, holder);
		
		Number newId = (Long) holder.getKeys().get("id");
		
		expense.setId(newId.longValue());
		return expense;
		
	}

	@Override
	public void deleteById(Long id) {
		jdbcTemplate.update("DELETE FROM expenses WHERE id=?", id);
	}
	
	@Override
	public Optional<Expense> findById(Long id) {
		try {
		return Optional.of(jdbcTemplate.queryForObject("SELECT * FROM expenses WHERE id=?", 
				new ExpenseRowMapper(), id));
		}
		catch (EmptyResultDataAccessException e) {
			return Optional.empty();
		}
	}
	
	@Override
	public List<Expense> findByUserId(Long userId) {
			return jdbcTemplate.query("SELECT * FROM expenses WHERE user_id=?", 
					new ExpenseRowMapper(), userId);
	}

	@Override
	public List<Expense> findAll() {
		return jdbcTemplate.query("SELECT * FROM expenses", new ExpenseRowMapper());
	}
	
	@Override
	public List<Expense> findExpensesByYearForUser(Integer year, Long userId) {
		return jdbcTemplate.query("SELECT * FROM expenses WHERE purchase_date > ? AND purchase_date < ? AND user_id = ?", 
				new ExpenseRowMapper(), LocalDate.of(year-1, 12, 31), LocalDate.of(year+1, 1, 1), userId);
	}
	
	@Override
	public void updateExpense (Expense expense) {
		jdbcTemplate.update("UPDATE expenses SET category = ?, amount = ?, description = ?, purchase_date = ? WHERE id = ?;", 
				expense.getCategory(), expense.getAmount(), expense.getDescription(), expense.getPurchaseDate(), expense.getId());
		
	}

	@Override
	public List<Expense> findExpensesByMatchingDescriptionForUser(Long userId, String searchString) {
		String comparisonString = "%".concat(searchString.concat("%"));
		return jdbcTemplate.query("SELECT * FROM expenses WHERE user_id = ? AND LOWER(description) LIKE ?;", 
				new ExpenseRowMapper(), userId, comparisonString);
	}
	
}
