"use strict";
import { MONTHS } from './config.js';
import { displayExpenses, monthArrows, calculateTotalExpenses } from './helpers.js';
import { sendDeleteRequest, searchRequest } from './requests.js';

const navBar = document.querySelector('.nav-box')
const currentMonth = document.querySelector('.month-text');
const dateBanner = document.querySelector('.budget-date-filter');
let expensesOnPage = document.querySelectorAll('.budget-list');
const expenseForm = document.querySelector('.add-expense-form');
const expenseHeaders = document.querySelector('.budget-list-header');
const total = document.querySelector('.total');
const totalBar = document.querySelector('.budget-list-total');
const searchBar = document.querySelector('.search-bar');
const categoryFilter = document.querySelector('#category-filter-input');
const sortingArrows = document.querySelector('.sorting-arrows');

categoryFilter.onchange = function() {
	displayExpensesBasedOnCategory(categoryFilter.value.toLowerCase());
}

const displayExpensesBasedOnCategory = function(category){
	
	if (category.toLowerCase() === 'all'){
		displayExpensesBasedOnMonth();
	} else {
		const filteredExpenses = expenses.filter(expense => parseInt(expense.purchaseDate.split('-')[1]) === MONTHS.indexOf(currentMonth.textContent)+1)
			.filter(expense => expense.category.toLowerCase() === category.toLowerCase());
		expensesOnPage.forEach(expense => expense.parentNode.removeChild(expense));
		displayExpenses(filteredExpenses, expenseHeaders);
		expensesOnPage = document.querySelectorAll('.budget-list');
		total.innerHTML = `Total: £${parseFloat(calculateTotalExpenses(filteredExpenses)).toFixed(2)}`;

	}
}

navBar.addEventListener('input', async (e) => {
	
	if(!e.target.classList.contains('search-bar')) return;
	if(searchBar.value.length === 0){ 
		expenseForm.classList.remove('hidden-opacity-collapse');
		dateBanner.classList.remove('hidden-opacity-collapse');
		totalBar.classList.remove('hidden-opacity-collapse');
		displayExpensesBasedOnMonth();
	} else {
	
		expenseForm.classList.add('hidden-opacity-collapse');
		dateBanner.classList.add('hidden-opacity-collapse');
		totalBar.classList.add('hidden-opacity-collapse');
	
		const result = await searchRequest(searchBar.value);
		
		expensesOnPage.forEach(expense => expense.parentNode.removeChild(expense));
	
		displayExpenses(result, expenseHeaders);
		expensesOnPage = document.querySelectorAll('.budget-list');
	}
})

//Listens for ignore checkbox
document.addEventListener("change", (e) => {
	
	if (e.target.id !== 'ignore-expense-checkbox') return;
	
	//Best way to get parent
	const expenseElement = e.target.parentElement.parentElement;
	
	const currentTotal = parseFloat(total.innerHTML.substring(total.innerHTML.indexOf('£') + 1));
	
	if (e.target.checked) {
		expenseElement.style.opacity = '0.5';
		const blurredExpenseAmount = +parseFloat(expenseElement.children[2].innerHTML.replace('£', ''));
		
		total.innerHTML = `Total: £${(currentTotal - blurredExpenseAmount).toFixed(2)}`;
	} else {
		const blurredExpenseAmount = +parseFloat(expenseElement.children[2].innerHTML.replace('£', ''));
		expenseElement.style.opacity = '1';
		total.innerHTML = `Total: £${(currentTotal + blurredExpenseAmount).toFixed(2)}`;
	}
	
})


const displayExpensesBasedOnMonth = (function displayMonthlyExpenses() {
	
	if (!currentMonth) return;
	
	let expenseTotal;

	const filteredExpenses = expenses.filter(expense => parseInt(expense.purchaseDate.split('-')[1]) === 
		MONTHS.indexOf(currentMonth.textContent)+1);
	
	//Below removes each expense element currently displayed on the page
	expensesOnPage.forEach(expense => expense.parentNode.removeChild(expense));
	
	if (currentMonth.textContent === 'ALL') {
	displayExpenses(expenses, expenseHeaders);
	expenseTotal = calculateTotalExpenses(expenses);

	} else {
	displayExpenses(filteredExpenses, expenseHeaders);
	expenseTotal = calculateTotalExpenses(filteredExpenses);
	}
	
	total.innerHTML = `Total: £${parseFloat(expenseTotal).toFixed(2)}`;
	//This is needed so the expensesOnPage is the new set of expenses and not the old
	expensesOnPage = document.querySelectorAll('.budget-list');
	return displayMonthlyExpenses;
})();

const displayCorrectExpensesForMonth = function(e) {
	if (!e.target.id.includes('month-arrow')) return;

	e.preventDefault();

	const newMonth = monthArrows(e.target.id, currentMonth);
	currentMonth.textContent = newMonth;
	
	displayExpensesBasedOnMonth();
}

//We are listening on document because some elements do not exist at certain points
//This means we need event delegation to listen for them
document.addEventListener("click", (e) => {
	displayCorrectExpensesForMonth(e);
	
	if (e.target.classList.contains('sorting-arrows')){
		
		let filteredExpenses = expenses.filter(expense => parseInt(expense.purchaseDate.split('-')[1]) === 
			MONTHS.indexOf(currentMonth.textContent)+1);
		
		if (currentMonth.textContent === 'ALL') {
			filteredExpenses = expenses;
		}
		
		if (sortingArrows.textContent === '↓') {
			sortingArrows.innerHTML = '↑';
			const asc = filteredExpenses.slice().sort((a, b) => a.amount - b.amount);
			expensesOnPage.forEach(expense => expense.parentNode.removeChild(expense));
			displayExpenses(asc, expenseHeaders);
			expensesOnPage = document.querySelectorAll('.budget-list');
		} else if (sortingArrows.textContent === '↑') {
			sortingArrows.innerHTML = '↑↓';
			expensesOnPage.forEach(expense => expense.parentNode.removeChild(expense));
			displayExpenses(filteredExpenses, expenseHeaders);
			expensesOnPage = document.querySelectorAll('.budget-list');
		} else if (sortingArrows.textContent === '↑↓') {
			sortingArrows.innerHTML = '↓';
			const desc = filteredExpenses.slice().sort((a, b) => b.amount - a.amount);
			
			expensesOnPage.forEach(expense => expense.parentNode.removeChild(expense));
			displayExpenses(desc, expenseHeaders);
			expensesOnPage = document.querySelectorAll('.budget-list');
		}
		
	}

	if (e.target.classList.contains('delete-expense-btn')) {
		e.preventDefault();
		const confirm = window.confirm('Are you sure you want to delete this? This operation CANNOT be undone');

		if (confirm) {
			sendDeleteRequest(e.target.closest('a').href);
			window.location.reload();
		}
	}

	if (e.target.classList.contains('edit-expense-btn')) {
		e.preventDefault();

		const expenseToEdit = e.target.closest('.budget-list');
		
		if (expenseToEdit.dataset.editing) return;
		
		expenseToEdit.dataset.editing = 'true';
		
		const currentExpenseId = expenseToEdit.querySelector('.delete-expense-link').attributes.item(3).name;
		const currentExpenseContent = Array.from(expenseToEdit.children).filter(child => child.nodeName === 'P').map(paragraph => paragraph.textContent);
		
		expenseToEdit.insertAdjacentHTML('afterend', `<form class = "budget-list-edit-form" action="/editexpense" id=expense method="post">
					<input class = "edit-expense-input" type = "hidden" name = "id" value = ${currentExpenseId}>
					<input class = "edit-expense-input" type = "date" name = "purchaseDate" value = ${currentExpenseContent[0]} placeholder=${currentExpenseContent[0]}>
					<select class = "edit-expense-input" name="category" value = ${currentExpenseContent[1]} placeholder = ${currentExpenseContent[1]}>
				        <option value="DATES">Dates</option>
				        <option value="MISC">Misc</option>
				 		<option value="FUEL">Fuel</option>
				        <option value="DATES">MISC</option>
			     	</select>
					<input class = "edit-expense-input" type = "text" name = "amount" value = ${currentExpenseContent[2].replace('£', '')} placeholder = ${currentExpenseContent[2]}>
					<input class = "edit-expense-input" type = "text" name = "description" value = ${currentExpenseContent[3]} placeholder = ${currentExpenseContent[3]}>
					<input class = "edit-expense-input" name="submit-login" type="submit" value="submit" />
				</form>`);
	}
});
