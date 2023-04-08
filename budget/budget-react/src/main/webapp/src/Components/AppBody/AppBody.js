import React from "react";
import classes from "./AppBody.module.css";
import AddExpenseForm from "../Expense/AddExpenseForm";

const AppBody = (props) => {
  return (
    <div className={classes["body-app"]}>
      <div className={classes["body-app-child"]}>{props.children}</div>
      <AddExpenseForm />
    </div>
  );
};

export default AppBody;
