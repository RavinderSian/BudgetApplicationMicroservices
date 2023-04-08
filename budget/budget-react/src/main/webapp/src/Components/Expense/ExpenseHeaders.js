import classes from "./ExpenseHeaders.module.css";

const ExpenseHeaders = () => {
  return (
    <div className={classes["budget-list-header"]}>
      <div>
        <p className={classes["sorted-header"]}>Date</p>
        <p className={classes["sorting-arrows"]} data-to-sort="date">
          &#8593;&#8595;
        </p>
      </div>
      <div>
        <p className={classes["sorted-header"]}>Category</p>
        <select id="category-filter-input">
          <option value="All">All</option>
          <option value="DATES">Dates</option>
          <option value="MISC">Misc</option>
          <option value="FUEL">Fuel</option>
        </select>
      </div>
      <div>
        <p className={classes["sorted-header"]}>Cost</p>
        <p className={classes["sorting-arrows"]} data-to-sort="cost">
          &#8593;&#8595;
        </p>
      </div>
      <p>Description</p>
      <p></p>
      <p></p>
      <p>Hide</p>
    </div>
  );
};

export default ExpenseHeaders;
