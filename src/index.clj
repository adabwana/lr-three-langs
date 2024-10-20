^:kindly/hide-code
(ns index
  (:require
    [utils.formatting :refer [md]])
  (:import [java.time LocalDate]
           [java.time.format DateTimeFormatter]))

(let [formatter (DateTimeFormatter/ofPattern "M/d/yy")
      current-date (str (.format (LocalDate/now) formatter))]
  (md (str "

### Jaryt Salvo
**Date:** **" current-date "**

**Fall 2024 | CS6010 Data Science Programming**

*************

This repository contains a comparative analysis of linear regression implementations using three programming languages: R, Clojure, and Python. The project focuses on analyzing the relationship between horsepower and miles per gallon (MPG) in the Auto MPG dataset. Key features include:

1. Data Loading and Visualization: Loading the Auto MPG dataset and creating initial scatter plots to visualize the relationship between horsepower and MPG.

2. Simple Linear Regression: Implementing and evaluating a simple linear regression model in each language.

3. Polynomial Regression: Extending the analysis to polynomial regression models of different degrees (2 and 3/5) to capture non-linear relationships.

4. Model Diagnostics: Performing comprehensive model diagnostics including:
   - Linearity and independence checks
   - Normality of residuals (QQ plots)
   - Homoscedasticity tests
   - Independence of residuals
   - Observed vs. Predicted value plots

5. Visualization: Creating various plots to illustrate model performance and diagnostic results.

6. Cross-Language Comparison: Providing a platform to compare implementation details, syntax, and performance across Clojure, R, and Python.

The project demonstrates the application of regression analysis techniques and model diagnostics using different programming languages, showcasing the strengths and characteristics of each language in the context of data science and machine learning tasks.")))
