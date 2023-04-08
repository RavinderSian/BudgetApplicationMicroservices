import React from "react";
import classes from "./EditExpenseForm.module.css";

const EditExpenseForm = (props) => {
  return (
    <form
      className={classes["budget-list-edit-form"]}
      action="/editexpense"
      id="expense"
      method="post"
    >
      <input
        className={classes["edit-expense-input"]}
        type="hidden"
        name="id"
        value={props.id}
      />
      <input
        className={classes["edit-expense-input"]}
        type="date"
        name="purchaseDate"
        value={props.purchaseDate}
        placeholder={props.purchaseDate}
      />
      <select
        className={classes["edit-expense-input"]}
        name="category"
        value={props.category}
        placeholder={props.category}
      >
        <option value="" disabled selected>
          Select something...
        </option>
        <option value="DATES">Dates</option>
        <option value="MISC">Misc</option>
        <option value="FUEL">Fuel</option>
      </select>
      <input
        className={classes["edit-expense-input"]}
        type="text"
        name="amount"
        value={props.amount}
        placeholder={props.amount}
      />
      <input
        className={classes["edit-expense-input"]}
        type="text"
        name="description"
        value={props.description}
        placeholder={props.description}
      />
      <input
        className={classes["edit-expense-input"]}
        name="submit-login"
        type="submit"
        value="submit"
      />
    </form>
  );
};

export default EditExpenseForm;
