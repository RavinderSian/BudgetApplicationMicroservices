import React from "react";

import classes from "./AddExpenseForm.module.css";

const AddExpenseForm = () => {
  return (
    <div className={classes["add-expense-form"]}>
      <form
        name="addExpenseForm"
        action="/addexpensejson"
        object="expense"
        method="post"
      >
        <h3>Add expense</h3>
        <input
          id="add-expense-purchase-date"
          className={classes["expense-input"]}
          type="date"
          field="purchaseDate"
          placeholder="10-10-2022"
        />
        <input
          id="add-expense-amount"
          className={classes["expense-input"]}
          type="number"
          step="0.01"
          field="amount"
          placeholder="Amount"
        />
        <select id="add-expense-category" name="categories">
          <option value="DATES">Dates</option>
          <option value="MISC">Misc</option>
          <option value="FUEL">Fuel</option>
        </select>
        <input
          id="add-expense-description"
          className={classes["expense-input"]}
          field="description"
          placeholder="Description"
        />
        <input name="submit-login" type="submit" value="submit" />
      </form>
      <label
        id="add-expense-error"
        className={(classes["error"], classes["hidden"])}
      >
        Please amend the highlighted fields
      </label>
      <label
        id="add-expense-error-maintenance"
        className={(classes["error"], classes["hidden"])}
      >
        Down due to maintenance
      </label>
    </div>
  );
};

export default AddExpenseForm;
