"use strict";
import { consumeRegister } from './register.js';
import { MONTHS } from './config.js';
import { displayExpenses, monthArrows, calculateTotalExpenses } from './helpers.js';
import { sendDeleteRequest, searchRequest } from './requests.js';

const navBar = document.querySelector('.nav-box')
const registerBox = document.querySelector(".register-box");
const registerSubmitBtn = document.querySelector("#btn-register-submit");
const currentMonth = document.querySelector('.month-text');

let expensesOnPage = document.querySelectorAll('.budget-list');
const expenseHeaders = document.querySelector('.budget-list-header');
const total = document.querySelector('.total');

const totalBar = document.querySelector('.budget-list-total');
const searchBar = document.querySelector('.search-bar');
const expenseForm = document.querySelector('.add-expense-form');
const dateBanner = document.querySelector('.budget-date-filter');
const categoryFilter = document.querySelector('#category-filter-input');
const sortingArrows = document.querySelector('.sorting-arrows');

if (categoryFilter) {
	categoryFilter.onchange = function() {
		displayExpensesBasedOnCategory(categoryFilter.value.toLowerCase());
	}
}

const displayExpensesBasedOnCategory = function(category) {

	if (category.toLowerCase() === 'all') {
		displayExpensesBasedOnMonth();
	} else {
		const filteredExpenses = expenses.filter(expense => parseInt(expense.purchaseDate.split('-')[1]) === MONTHS.indexOf(currentMonth.textContent) + 1)
			.filter(expense => expense.category.toLowerCase() === category.toLowerCase());
		expensesOnPage.forEach(expense => expense.parentNode.removeChild(expense));
		displayExpenses(filteredExpenses, expenseHeaders);
		expensesOnPage = document.querySelectorAll('.budget-list');
		total.innerHTML = `Total: £${parseFloat(calculateTotalExpenses(filteredExpenses)).toFixed(2)}`;
	}
}

navBar.addEventListener('input', async (e) => {

	if (!e.target.classList.contains('search-bar')) return;
	if (searchBar.value.length === 0) {
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

document.addEventListener("submit", async (e) => {

	if (e.target.name === 'addExpenseForm') {
		e.preventDefault();

		const addExpenseInputFields = ['amount', 'purchaseDate', 'description'];

		const response = await addExpense();

		if (response.ok) {
			window.location.reload();
		} else if (response.status === 503) {
			document.querySelector('#add-expense-error-maintenance').classList.toggle('hidden');
		} else {

			document.querySelector('#add-expense-error').classList.remove('hidden');

			const responseText = await response.text();

			const invalidFields = addExpenseInputFields.filter(field => responseText.includes(field));
			
			addExpenseInputFields.filter(field => !invalidFields.includes(field)).forEach(validField => {
				if (validField === 'purchaseDate') {
						document.querySelector('#add-expense-purchase-date').style.backgroundColor = 'white';
					} else {
						document.querySelector(`#add-expense-${validField}`).style.backgroundColor = 'white';
					}
			})

			invalidFields.forEach(invalidField => {

					if (invalidField === 'purchaseDate') {
						document.querySelector('#add-expense-purchase-date').style.backgroundColor = 'red';
					} else {
						document.querySelector(`#add-expense-${invalidField}`).style.backgroundColor = 'red';
					}

				});

		}
	}


}
)


const addExpense = function() {
	return fetch($('form[name="addExpenseForm"]').attr("action"), {
		method: "POST",
		headers: {
			"Content-Type": "application/json",
		},
		body: JSON.stringify({
			purchaseDate: document.querySelector("#add-expense-purchase-date").value,
			amount: document.querySelector("#add-expense-amount").value,
			category: document.querySelector("#add-expense-category").value,
			description: document.querySelector("#add-expense-description").value,
		}),
	});
};


navBar.addEventListener("click", function(e) {
	if (e.target.classList.contains('btn-register')) {
		registerBox.classList.toggle("hidden");
	}
});

//This self executing function displays the current (default) months expenses
//On the page on load 
const displayExpensesBasedOnMonth = (function displayMonthlyExpenses() {

	if (!currentMonth) return;

	let expenseTotal;

	const filteredExpenses = expenses.filter(expense => parseInt(expense.purchaseDate.split('-')[1]) ===
		MONTHS.indexOf(currentMonth.textContent) + 1);

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

	if (e.target.id === null || !e.target.id.includes('month-arrow')) return;

	e.preventDefault();
	const newMonth = monthArrows(e.target.id, currentMonth);
	currentMonth.textContent = newMonth;

	displayExpensesBasedOnMonth();
}

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

//We are listening on document because some elements do not exist at certain points
//This means we need event delegation to listen for them
document.addEventListener("click", (e) => {

	displayCorrectExpensesForMonth(e);

	if (e.target.classList.contains('sorting-arrows')) {
		
		console.log(e.target.textContent);

		let filteredExpenses = expenses.filter(expense => parseInt(expense.purchaseDate.split('-')[1]) ===
			MONTHS.indexOf(currentMonth.textContent) + 1);
			

		if (currentMonth.textContent === 'ALL') {
			filteredExpenses = expenses;
		}

		if (e.target.textContent === '↓') {

			let asc;

			e.target.innerHTML = '↑';
			
			if (e.target.dataset.toSort === 'cost'){
				asc = filteredExpenses.slice().sort((a, b) => a.amount - b.amount);
			}
			
			if (e.target.dataset.toSort === 'date'){
				asc = filteredExpenses.sort((a,b) => 
					parseInt(a.purchaseDate.split('-')[2]) - parseInt(b.purchaseDate.split('-')[2]));
			}
			
			expensesOnPage.forEach(expense => expense.parentNode.removeChild(expense));
			displayExpenses(asc, expenseHeaders);
			expensesOnPage = document.querySelectorAll('.budget-list');
		} else if (e.target.textContent === '↑') {
			e.target.innerHTML = '↑↓';
			expensesOnPage.forEach(expense => expense.parentNode.removeChild(expense));
			displayExpenses(filteredExpenses, expenseHeaders);
			expensesOnPage = document.querySelectorAll('.budget-list');
		} else if (e.target.textContent === '↑↓') {
			let desc;
			e.target.innerHTML = '↓';
			
			if (e.target.dataset.toSort === 'cost'){
				desc = filteredExpenses.slice().sort((a, b) => b.amount - a.amount);
			}
			
			if (e.target.dataset.toSort === 'date'){
				desc = filteredExpenses.sort((a,b) => 
					parseInt(b.purchaseDate.split('-')[2]) - parseInt(a.purchaseDate.split('-')[2]));
			}
			
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

	//If the login button also triggers the hidden class to be added the box never appears
	//So a second condition is needed to ensure that does not happen
	const isClickInside =
		registerBox.contains(e.target) || e.target.classList.contains('btn-register');

	//If we are outside the box when it appears, hide it again 
	if (!isClickInside) {
		registerBox.classList.add("hidden");
	}

	if (e.target.classList.contains('edit-expense-btn')) {

		e.preventDefault();

		const expenseToEdit = e.target.closest('.budget-list');

		if (expenseToEdit.dataset.editing) return;

		expenseToEdit.dataset.editing = 'true';

		const splitURL = expenseToEdit.querySelector('.delete-expense-link').href.split('/');
		const currentExpenseId = splitURL[splitURL.length - 1];
		const currentExpenseContent = Array.from(expenseToEdit.children).filter(child => child.nodeName === 'P').map(paragraph => paragraph.textContent);

		expenseToEdit.insertAdjacentHTML('afterend',
			`<form class = "budget-list-edit-form" action="/editexpense" id=expense method="post">
					<input class = "edit-expense-input" type = "hidden" name = "id" value = ${currentExpenseId}>
					<input class = "edit-expense-input" type = "date" name = "purchaseDate" value = ${currentExpenseContent[0]} placeholder=${currentExpenseContent[0]}>
					<select class = "edit-expense-input" name="category" value = ${currentExpenseContent[1]} placeholder = ${currentExpenseContent[1]}>
				        <option value="" disabled selected>Select something...</option>
				        <option value="DATES">Dates</option>
				        <option value="MISC">Misc</option>
				 		<option value="FUEL">Fuel</option>
			     	</select>
					<input class = "edit-expense-input" type = "text" name = "amount" value = ${currentExpenseContent[2].replace('£', '')} placeholder = ${currentExpenseContent[2]}>
					<input class = "edit-expense-input" type = "text" name = "description" value = "${currentExpenseContent[3]}" placeholder = "${currentExpenseContent[3]}">
					<input class = "edit-expense-input" name="submit-login" type="submit" value="submit" />
			 </form>`);
	}

});

registerSubmitBtn.addEventListener("click", function(e) {
	e.preventDefault();
	consumeRegister();
});

registerBox.addEventListener("keydown", function(e) {
	if (e.key === "Enter") {
		e.preventDefault();
		consumeRegister();
	}
});
