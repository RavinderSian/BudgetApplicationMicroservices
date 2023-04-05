package com.personal.budget.repository;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import com.personal.budget.model.User;

@SpringBootTest
@AutoConfigureTestDatabase
class UserRepositoryImplTest {
	
	@Autowired
    JdbcTemplate jdbcTemplate;
	
    @Autowired
	UserRepository repository;

    @BeforeEach
    void createTable() {
    	jdbcTemplate.execute("CREATE TABLE USERS ( ID bigint NOT NULL PRIMARY KEY AUTO_INCREMENT, "
    			+ "EMAIL varchar(50) NOT NULL UNIQUE, USERNAME varchar(50) NOT NULL UNIQUE, "
    			+ "PASSWORD varchar(50) NOT NULL, "
    			+ "AUTHORITY varchar(50) NOT NULL);");
    }
	
    @AfterEach
    void deleteTable() {
    	jdbcTemplate.execute("DROP TABLE IF EXISTS USERS");
    }

	@Test
	void test_Save_SavesEntityCorrectly_WhenGivenValidEntity() {
		
		User user = new User();
		user.setId(1L);
		user.setUsername("test");
		user.setPassword("test");
		user.setEmail("test@gmail.com");
		user.setAuthority("USER");
		
		User saved = repository.save(user);
		Optional<User> result = repository.findById(saved.getId());
		
		assertThat(result.get().getId(), equalTo(saved.getId()));
		assertThat(result.get().getUsername(), equalTo(saved.getUsername()));
		assertThat(result.get().getPassword(), equalTo(saved.getPassword()));
		assertThat(result.get().getEmail(), equalTo(saved.getEmail()));

	}
	
	@Test
	void test_Save_ThrowsJdbcSQLIntegrityConstraintViolationException_WhenGivenInvalidEntity() {
		
		User user = new User();
		user.setId(1L);
		user.setUsername("test");
		user.setEmail("test@gmail.com");
		user.setAuthority("USER");
		
		Exception thrown = Assertions.assertThrows(
				Exception.class,
	           () -> {repository.save(user);
	           });
	
		assertThat(thrown.getMessage().contains("NULL not allowed for column"), equalTo(true));
	}
	
	@Test
	void test_Save_ThrowsJdbcSQLIntegrityConstraintViolationException_WhenGivenNonUniqueUsername() {
		
		User user = new User();
		user.setUsername("test");
		user.setPassword("test");
		user.setEmail("test@gmail.com");
		user.setAuthority("USER");
		
		User user2 = new User();
		user2.setUsername("test");
		user2.setPassword("test");
		user2.setEmail("testing@gmail.com");
		user2.setAuthority("USER");
		
		repository.save(user);
		Exception thrown = Assertions.assertThrows(
				Exception.class,
	           () -> {repository.save(user2);
	           });
		
		assertThat(thrown.getMessage().contains("Unique index or primary key violation"), equalTo(true));
	}
	
	@Test
	void test_Save_ThrowsJdbcSQLIntegrityConstraintViolationException_WhenGivenNonUniqueEmail() {
		
		User user = new User();
		user.setUsername("test");
		user.setPassword("test");
		user.setEmail("test@gmail.com");
		user.setAuthority("USER");
		
		User user2 = new User();
		user2.setUsername("testing");
		user2.setPassword("test");
		user2.setEmail("test@gmail.com");
		user2.setAuthority("USER");
		
		repository.save(user);
		Exception thrown = Assertions.assertThrows(
				Exception.class,
	           () -> {repository.save(user2);
	           });
		
		assertThat(thrown.getMessage().contains("Unique index or primary key violation"), equalTo(true));
	}
	
	@Test
	void test_FindById_FindsCorrectEntity_WhenEntityExistsWithGivenId() {
		
		User user = new User();
		user.setUsername("test");
		user.setPassword("test");
		user.setEmail("test@gmail.com");
		user.setAuthority("USER");
		
		User saved = repository.save(user);
		Optional<User> result = repository.findById(saved.getId());
		
		assertThat(result.get().getId(), equalTo(saved.getId()));
		assertThat(result.get().getUsername(), equalTo(saved.getUsername()));
		assertThat(result.get().getPassword(), equalTo(saved.getPassword()));
		assertThat(result.get().getEmail(), equalTo(saved.getEmail()));

	}
	
	@Test
	void test_FindById_ReturnsEmptyOptional_WhenEntityDoesNotWithGivenId() {
		
		assertThat(repository.findById(1L).isEmpty(), equalTo(true));
	}
	
	@Test
	void test_FindByUsername_FindsCorrectEntity_WhenEntityExistsWithGivenId() {
		
		User user = new User();
		user.setUsername("test");
		user.setPassword("testing");
		user.setEmail("test@gmail.com");
		user.setAuthority("USER");
		
		User saved = repository.save(user);
		Optional<User> result = repository.findByUsername("test");
		
		assertThat(result.get().getId(), equalTo(saved.getId()));
		assertThat(result.get().getUsername(), equalTo(saved.getUsername()));
		assertThat(result.get().getPassword(), equalTo(saved.getPassword()));
		assertThat(result.get().getEmail(), equalTo(saved.getEmail()));

	}
	
	@Test
	void test_FindByUsername_ReturnsEmptyOptional_WhenEntityDoesNotWithGivenId() {
		
		assertThat(repository.findByUsername("test").isEmpty(), equalTo(true));
	}

	@Test
	void test_DeleteById_FindsCorrectEntity_WhenEntityExistsWithGivenId() {
		
		User user = new User();
		user.setUsername("test");
		user.setPassword("test");
		user.setEmail("test@gmail.com");
		user.setAuthority("USER");
		
		User saved = repository.save(user);
		repository.deleteById(saved.getId());
		
		assertThat(repository.findById(saved.getId()).isEmpty(), equalTo(true));

	}
}
