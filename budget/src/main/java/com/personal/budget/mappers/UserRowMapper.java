package com.personal.budget.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.personal.budget.model.User;

public class UserRowMapper implements RowMapper<User> {

	@Override
	public User mapRow(ResultSet rs, int rowNum) throws SQLException {
		User user = new User();
		user.setId(rs.getLong("id"));
		user.setUsername(rs.getString(("username")));
		user.setPassword(rs.getString("password"));
		user.setEmail(rs.getString("email"));
		user.setAuthority(rs.getString("authority"));
		
		return user;
	}
	
}
