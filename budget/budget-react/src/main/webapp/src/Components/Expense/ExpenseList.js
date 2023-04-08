import React, { useState } from "react";
import Expense from "./Expense";
import ExpenseTotal from "./ExpenseTotal";
import classes from "./ExpenseList.module.css";

const DUMMY_EXPENSE = [
  {
    id: 1,
    category: "TEST",
    description: "test",
    amount: 50.0,
    purchaseDate: "2023-01-02",
  },
  {
    id: 2,
    category: "TEST",
    description: "test",
    amount: 60.0,
    purchaseDate: "2023-01-02",
  },
];

const ExpenseList = () => {
  const totalOfExpenses = DUMMY_EXPENSE.reduce((a, b) => {
    return a + b.amount;
  }, 0);

  const expensesList = DUMMY_EXPENSE.map((expense) => (
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
