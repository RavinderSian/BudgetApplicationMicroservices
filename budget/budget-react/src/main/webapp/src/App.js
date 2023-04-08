import React from "react";
import "./App.css";
import NavigationHeader from "./Components/NavigationHeader/NavigationHeader";
import DateFilter from "./Components/DateFilter/DateFilter";
import AppBody from "./Components/AppBody/AppBody";
import ExpenseHeaders from "./Components/Expense/ExpenseHeaders";
import ExpenseList from "./Components/Expense/ExpenseList";
import ExpenseTotal from "./Components/Expense/ExpenseTotal";
import AddExpenseForm from "./Components/Expense/AddExpenseForm";

function App() {
  return (
    <React.Fragment>
      <NavigationHeader />
      <AppBody>
        <DateFilter />
        <ExpenseHeaders></ExpenseHeaders>
        <ExpenseList></ExpenseList>
      </AppBody>
    </React.Fragment>
  );
}

export default App;
