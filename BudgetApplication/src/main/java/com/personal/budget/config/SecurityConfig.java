package com.personal.budget.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.thymeleaf.extras.springsecurity5.dialect.SpringSecurityDialect;

import com.personal.budget.model.BudgetAuthenticationFailureHandler;
import com.personal.budget.repository.UserRepository;
import com.personal.budget.service.UserServiceImpl;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) //allows us to use @preAuthorize
public class SecurityConfig {
	
	@Autowired
	UserRepository userRepository;
	
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        
    	http
        .csrf().disable()
		.formLogin() //no custom page so default is /login
		.loginProcessingUrl("/performlogin") //url to submit the username and pw to
		.defaultSuccessUrl("/budget") //landing page after login
		.failureUrl("/failedlogin") //for when login fails
		.and()
		.logout()
		.logoutUrl("/logout") //url for default logout page
		.logoutSuccessUrl("/") //url for successful logout 
		.deleteCookies("JSESSIONID") 
		.and()
		.httpBasic();
    	
        return http.build();
    }
	
	@Bean
	public UserDetailsService userDetailsService() {
		return new UserServiceImpl(userRepository);
	}
	
	@Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	@Bean
    BudgetAuthenticationFailureHandler authenticationFailureHandler(){
        return new BudgetAuthenticationFailureHandler();
    }
	
	  @Bean
	  public DaoAuthenticationProvider authenticationProvider() {
	      DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
	       
	      authProvider.setUserDetailsService(userDetailsService());
	      authProvider.setPasswordEncoder(bCryptPasswordEncoder());
	   
	      return authProvider;
	  }
	
    @Bean
    public SpringSecurityDialect springSecurityDialect(){
        return new SpringSecurityDialect();
    }
	
}

