package com.personal.budget.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.personal.budget.model.Expense;

public class ExpenseRowMapper implements RowMapper<Expense> {

	@Override
	public Expense mapRow(ResultSet rs, int rowNum) throws SQLException {
		Expense expense = new Expense();
		expense.setId(rs.getLong("id"));
		expense.setUserId(rs.getLong("user_id"));
		expense.setAmount(rs.getBigDecimal("amount"));
		expense.setCategory(rs.getString("category"));
		expense.setDescription(rs.getString("description"));
		expense.setPurchaseDate(rs.getTimestamp(("purchase_date")).toLocalDateTime().toLocalDate());
		
		return expense;
	}

}
