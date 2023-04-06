'use strict'
import { MONTHS } from './config.js';


export const displayExpenses = function(expenseToShow, expenseHeaders){
	expenseToShow
	.forEach(expense => {
				expenseHeaders.insertAdjacentHTML('afterend' ,
					`<div class = "budget-list">
					<p>${expense.purchaseDate}</p>
			  		<p>${expense.category}</p>
			  		<p>&pound${expense.amount}</p>
			  		<p>${expense.description}</p>
			  		<a class = "edit-expense-link"><button class = "edit-expense-btn">Edit</button></a>
			  		<a class = "delete-expense-link" href = /delete/${expense.id}><button class = "delete-expense-btn">Delete</button></a>
		  			<div>
			  			<input type="checkbox" id="ignore-expense-checkbox">
			  		</div>
		  	</div>`);
		});
}

export const monthArrows = function(id, currentMonth) {
	const indexOfMonth = MONTHS.indexOf(currentMonth.textContent);
	
	let result;
	
	if (id.includes('month-arrow-next')) {
		result = indexOfMonth === 12 ? 'JANUARY' 
		: MONTHS[indexOfMonth+1];
	} else{
		result = indexOfMonth === 0 ? 'ALL' 
		: MONTHS[indexOfMonth-1];
	}
	
	return result;
}

export const calculateTotalExpenses = function(filteredExpenses) {
	return filteredExpenses.reduce((acc, cur) => acc + cur.amount, 0);
}