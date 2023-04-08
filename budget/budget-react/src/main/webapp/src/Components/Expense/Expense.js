import React, { useState } from "react";
import EditExpenseForm from "./EditExpenseForm";
import classes from "./Expense.module.css";

const Expense = (props) => {
  const [isEditing, setIsEditing] = useState(false);
  const [isHiding, setIsHiding] = useState(false);

  const toggleHideHandler = () => {
    setIsHiding((isHiding) => !isHiding);
  };

  const toggleEditHandler = () => {
    setIsEditing((isEditing) => !isEditing);
  };

  return (
    <React.Fragment>
      <div
        className={`${classes["budget-list"]} ${isHiding && classes["hide"]}`}
      >
        <p>{props.purchaseDate}</p>
        <p>{props.category}</p>
        <p>{`${String.fromCharCode(163)}${props.amount.toFixed(2)}`}</p>
        <p>{props.description}</p>
        <a className={classes["edit-expense-link"]}>
          <button
            onClick={toggleEditHandler}
            className={classes["edit-expense-btn"]}
          >
            Edit
          </button>
        </a>
        <a className={classes["delete-expense-link"]} href="/delete/expense.id">
          <button className={classes["delete-expense-btn"]}>Delete</button>
        </a>
        <div>
          <input
            onChange={toggleHideHandler}
            type="checkbox"
            id="ignore-expense-checkbox"
          />
        </div>
      </div>
      {isEditing && (
        <EditExpenseForm key={props.id} {...props.expense}></EditExpenseForm>
      )}
    </React.Fragment>
  );
};

export default Expense;
