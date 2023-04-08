import React from "react";
import classes from "./NavigationHeader.module.css";

const NavigationHeader = () => {
  return (
    <nav className={`${classes["nav-box"]}`}>
      <h1>Budget Application</h1>
      <input
        className={`${classes["search-bar"]}`}
        type="search"
        placeholder="search"
      ></input>
    </nav>
  );
};

export default NavigationHeader;
