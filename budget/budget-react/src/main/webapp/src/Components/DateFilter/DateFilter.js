import React from "react";
import classes from "./DateFilter.module.css";
import backArrow from "../../icons/backArrow.svg";
import nextArrow from "../../icons/nextArrow.svg";

const DateFilter = () => {
  return (
    <div className={`${classes["budget-date-filter"]}`}>
      <div className={`${classes["year-grid"]}`}>
        <div className={`${classes["flex-box-year"]}`}>
          <a className={`${classes["anchor-arrow-back"]}`}>
            <img
              className={`${classes["date-traversal-arrow"]}`}
              src={backArrow}
              alt="Back arrow for year"
            />
          </a>
          <h3 className={`${classes["year-text"]}`}>2019</h3>
          <a className={`${classes["anchor-arrow-next"]}`}>
            <img
              className={`${classes["date-traversal-arrow"]}`}
              src={nextArrow}
              alt="Forward arrow for year"
            />
          </a>
        </div>
      </div>
      <div className={`${classes["flex-box-month"]}`}>
        <a className={`${classes["anchor-arrow-back"]}`} href="#">
          <img
            id="month-arrow-back"
            src={backArrow}
            alt="Back arrow for year"
          />
        </a>
        <h4 className={`${classes["month-text"]}`}>January</h4>
        <a className={`${classes["anchor-arrow-next"]}`}>
          <img
            id="month-arrow-next"
            src={nextArrow}
            alt="Forward arrow for year"
          />
        </a>
      </div>
    </div>
  );
};

export default DateFilter;
