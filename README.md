# F24-CS6010-GuestLecture

### Jaryt Salvo
**Date:** 10-20-2024

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

The project demonstrates the application of regression analysis techniques and model diagnostics using different programming languages, showcasing the strengths and characteristics of each language in the context of data science and machine learning tasks.

## Running the code

To work on this project, you'll need to install Docker and/or DevPod (optional). 

   - [Install Docker](https://docs.docker.com/get-docker/)
   - [Install DevPod](https://devpod.sh/)

> **Option 1 is recommended for easier setup.**

### **Option 1: Remote-Centric** 

1. Install [Docker](https://docs.docker.com/get-docker/) and [DevPod](https://devpod.sh/) and open DevPod.

2. In DevPod, go to `Workspaces`, click `+ Create`, in the `Git Repo` field, enter `https://github.com/adabwana/lr-three-langs`, ensure `Docker` is selected in `Provider`, Name your workspace (e.g. `lr-three-langs`), and click the `Create Workspace` button. The project will open in your browser using the provided `.devcontainer` configuration.

### **Note**: 

VSCode server will popup on `localhost:10800` in your *default* browser. Chrome-based browsers work better for VSCode server. In Firefox, VSCode hotkeys might not work as expected, instead using Firefox's default bindings. 

If VSCode hotkeys are not working in Chrome, viz. `Ctrl + Alt + C` then `Ctrl + Alt + J` to `Calva: Jack-In`, you may need to, on the right-side of the URL bar, click the `^` button to open the "App" view ("PWA"), and then click the `Open Remote Window`. Now try `Calva: Jack-In` again: `Ctrl + Alt + C` then `Ctrl + Alt + J`.


### -------- **OR** --------


### **Option 2: Local-Centric** 

1. Install [Docker](https://docs.docker.com/get-docker/) and open VSCode:

2. Install the VSCode extension [Dev Containers](https://marketplace.visualstudio.com/items?itemName=ms-vscode-remote.remote-containers). Press `Ctrl` `+` `` ` `` to open the terminal in VSCode.

3. In the VSCode terminal, clone the repository to `Documents/projects`, for example:

   ```bash
   cd .\Documents\projects
   git clone https://github.com/adabwana/lr-three-langs.git
   cd lr-three-langs
   code .
   ```

4. When VSCode reopens, you can find a popup on the bottom right saying "Open in Dev Container". If not, press `Ctrl + Shift + P` and type and select `Dev Containers: Open Folder in Container`. Navigate to the folder and open it.

## Getting started with Clojure
To get started with Clojure, use the following commands:

> - `Ctrl + Alt + C` then `Ctrl + Alt + J` to "Jack-in" and connect to the REPL (Read-Eval-Print Loop).
> - Select `deps.edn` in the `Connect Sequence` dropdown menu.
> - Check the `dev` alias in the `Connect Sequence` dropdown menu.
> - Press `OK`.

**Note**: We include `dev` alias in project so that we can use `Clay` for rendering with `Ctrl + Alt + SPACE` then `n` to render the namespace in the browser.

A new window with a connected REPL will open. From there, you can run the code in the `src/assignments` folder. You can run code in the clj files with different levels of evaluation. See [Calva's user guide](https://calva.io/eval-tips/) for examples. However, the most used are:

> - `Alt + Enter` to evaluate the top level form. ;; *i use this most*
> - `Ctrl + Enter` to evaluate the current form.
> - `Ctrl + Alt + C` then `Enter` to evaluate the entire namespace.

Using the `Clay` rendering hotkeys, you can render the workbook with different formats. My two most used `Clay` keybindings are:

> - `Ctrl + Alt + SPACE` then `n` to render the namespace.
> - `Ctrl + Alt + SPACE` then `,` to render current form.

***You can change the hotkey in the `Settings` menu.***