import React, { useState, useEffect, useCallback } from "react";
import Expense from "./Expense";
import ExpenseTotal from "./ExpenseTotal";

// const DUMMY_EXPENSE = [
//   {
//     id: 1,
//     category: "TEST",
//     description: "test",
//     amount: 50.0,
//     purchaseDate: "2023-01-02",
//   },
//   {
//     id: 2,
//     category: "TEST",
//     description: "test",
//     amount: 60.0,
//     purchaseDate: "2023-01-02",
//   },
// ];

const ExpenseList = () => {
  const fetchExpenses = useCallback(async function () {
    const response = await fetch("http://localhost:8080/expenses/rsian", {
      mode: "cors",
    });
    const data = await response.json();
    const transformedExpenses = data.map((expenseData) => {
      return {
        id: expenseData.id,
        userId: expenseData.userId,
        category: expenseData.category,
        amount: expenseData.amount,
        description: expenseData.description,
        purchaseDate: expenseData.purchaseDate,
      };
    });
    setExpensesListFromDB(transformedExpenses);
    console.log(transformedExpenses);
  }, []);

  const [expensesListFromDB, setExpensesListFromDB] = useState([]);

  const totalOfExpenses = expensesListFromDB.reduce((a, b) => {
    return a + b.amount;
  }, 0);

  useEffect(() => {
    fetchExpenses();
  }, [fetchExpenses]);
  const expensesList = expensesListFromDB.map((expense) => (
    <Expense key={expense.id} {...expense}></Expense>
  ));
  return (
    <React.Fragment>
      {expensesList}
      <ExpenseTotal total={totalOfExpenses}></ExpenseTotal>
    </React.Fragment>
  );
};
export default ExpenseList;
