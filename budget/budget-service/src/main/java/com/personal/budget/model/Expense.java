package com.personal.budget.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class Expense implements Serializable {

	private Long id;
	private Long userId;
	@NotBlank
	private String category;
	@NotNull
    @DecimalMin(value = "0.0", inclusive = false)
	private BigDecimal amount;
	@NotBlank
	private String description;
	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate purchaseDate;
	
}
