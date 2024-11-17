# MessagingApp Setup Guide

## About
This app is a direct messaging application. The frontend is handled entirely by JavaFX, while the backend and database operations are managed by Django. We use SQLite for our database. Our user model, `myUser`, extends the `User` class provided by `django.contrib.auth`.

## Class Documentation:

Authenticator
The Authenticator class is responsible for handling user authentication.
We use the authentication methods provided by Django’s REST framework. 
It takes in a username and a password, calls the database to check if those are correct, and then returns a boolean. 

Client\n
The Client class is responsible for managing the connection to the server. (In the future this will get merged with GUI).
We are utilizing the ‘newClientCommand()’ method to take a string input and send that to the server. This string input will reflect potential buttons that the user will be able to click on in the GUI. For now, you can manually code in the string being sent to the server as long as it follows the proper formatting of ThreadedServer’s switch case. 

MessageManager\n
The MessageManager class is responsible for handling message operations.
It provides methods to create messages as well as to manage message storage and retrieval success/unsuccess response from the database.

UserManager
The `UserManager` class is responsible for managing user data and interactions within the server. It provides methods to create, update, delete, and retrieve user information, as well as adding/removing friends, and blocking/unblocking users. 

- `createUser`: Creates a new user with the specified username and password and other info like email and bio.
- `editUser`: Updates the information of an existing user, however, it doesn’t allow to change the username or password of that user.
- `deleteUser`: Deletes the user with the specified user name.
- `getUser`: Retrieves the user information for the specified user name.
- `addFriend`: Adds a friend to the user's friend list.
- `unFriend`: Remove a friend from the user’s friend list.
- `block`: Blocks the specified user for the given user.
- `unblock`: Unblocks the specified user for the given user.

ThreadedServer
This server will act as a mediator between the database and client, reading the input from the client and calling methods that are in UserManager.java. The server then retrieves the response from the database and sends a SUCCESS/FAILED message back to the client. 

Python_backend
This is a Django project. Inside of it is an application called messaging, which contains most of the logic used, including the serializer, views.py, models.py, and url routing. 

## IMPORTANT
Please make sure .gitignore exists in your project's root directory before making any commits to Github. Otherwise, it will create conflicts for everyone. 

## Setup Instructions

**Note:** These setup instructions are specified for IntelliJ. VSCode-specific instructions are below, and requires a bit more work(or less in another person in the team's opinion). Please do not use any other IDE to run this program. 

1. Clone and Pull
   - Make sure this is the first time you are cloning the repository. If you have previously cloned it, delete the instance of the repository on your local machine before re-cloning.

2. Navigate to python_backend/
   - Run `cd python_backend/`
   - Remain in this directory. ALL terminal commands in this guide are run from this directory. 

3. Configure the Python Module and Create a Virtual Environment
   - Note: Do not reuse an already existing virtual environment.
   - Note: Do not use `python -m venv venv` to create the venv.
    1. First, navigate to **File -> Project Structure -> Modules**. Verify that a module named `python_backend` already exists. Click on it.
    2. Go to **Dependencies**. You will need to set the <mark>Module SDK</mark>. Click on it and select `Add Python SDK from disk...`.
    3. Make sure **New Environment** is selected.
    4. Set **Location** to `/<your_project_directory>/messagingApp/python_backend/venv`
        - Here is my location, for reference: `/Users/erict/messagingApp/python_backend/venv`. Your **Location** should look similar to this.
    5. For the **base interpreter**, set it equal to `/usr/local/bin/python3.12`.
        - Please use python3.12 to run this project to ensure compatibility. Do not use other, earlier versions of Python.
    6. Do not select/toggle anything else that is not explicitly mentioned above.
    7. Click **Apply**, then **OK**.
    8. Your <mark>Module SDK</mark> should be named <mark>Python 3.12 (messagingApp)</mark>. If it's not, navigate to **File -> Project Structure -> SDKs**. Select your newly created SDK and rename it appropriately. Click **Apply**, then **OK**.
    9. Before moving on, navigate to **python_backend/**. Verify that inside of the directory is a **venv/** directory. If yes, you can move on!

4. Activate the Virtual Environment
   - Make sure your terminal is in the **python_backend** directory before running this step. `cd python_backend/`
   1. For macOS/Linux: `source venv/bin/activate`
   2. For Windows: `venv\Scripts\activate`
   3. You should see a small <mark>(venv)</mark> tag next to your terminal's command prompt. If it's there, you have successfully activated your virtual environment!
  
5. Install Requirements and Apply Migrations
   1. Run `pip install -r requirements.txt`
   2. Run `python manage.py makemigrations`
   3. Run `python manage.py migrate`
   4. You can move on after running all commands. 

6. Add the JavaFX library
   1. Download the version of JavaFX that is most compatible with your computer.
       - Note: Please ensure that that the JavaFX version you choose is compatible with the version of Java that is running on your project. 
   3. Navigate to **File -> Project Structure -> Modules**. Verify that a module named 'messagingApp' already exists. Click on it.
   4. Under the Module SDK dropdown menu, you should see a small **+**. Click on it. Then, select **2. Library...**, and open the necessary JavaFX files.
      1. Only add files in `lib/` that end in `.jar`. There should be 8 of these. 
      2. Your JavaFX files should be located in a directory with a similar name to `javafx-sdk-23.0.1/`. Inside of that directory should be a directory named `lib/`.
   5. Before clicking **OK**, you should be brought to a window where you can name your library and set its level.
      1. For **Name:**, make sure you name your library `javafx-swt`.
      2. For **Level:**, keep the library on the `Project Library`
   5. Click **OK**.
   6. You will be brought back to the messagingApp module. Click **Apply**, then **OK** to apply your library. 
   7. You should see a new library named `javafx-swt` inside of the messagingApp module. You just imported the JavaFX libraries!

7. Configure the JavaFX Application
   1. Navigate to **Run -> Edit Configurations**.
   2. Click on **+**. Add an **Application**.
   3. Click on **Modify options**. Find 'Add VM options' and make sure it's toggled on.
   4. Fill out the fields as such:
      1. **Name**: "JavaFXApp"
      2. **Run on**: Local Machine
      3. **module not specified**: java 21 SDK of 'messagingApp'
         - Note: Your Java Program may not run "java 21". As long as the selected module is the "SDK of 'messagingApp'", it should be fine.
         - Note: If you select messagingApp as the -cp (do the next step first), it should automatically fill this field with the correct information. 
      5. **-cp <no module>**: messagingApp
      6. **VM options**: `--module-path "path/to/your/javafx/lib" --add-modules javafx.controls,javafx.fxml,javafx.graphics`
         - Note: replace `path/to/your/javafx/lib` with the complete path on your computer where you saved your javaFX `lib/` files.
      7. **Main class**: java_files.mainGUI
   5. Do not modify any other fields. Click **Apply**, then **OK**.
   6. Your JavaFX Application is set up!

8. Run the Program
   1. In the top right corner, select **JavaFXApp** instead of **Current File**.
   2. Go to Terminal.
        1. Navigate to the python_backend/ directory.
        2. Run `python manage.py runserver`. Once you see the terminal print a URL `http://127.0.0.1:8000/`, you are ready to start. 
   4. Run it!
  
9. There will still be errors with JUnit, since it's not imported yet. We assume that we don't need to provide instructions for this setup, and that you are able to configure JUnit to work. 

---

All done! 

## Note for VS Code users
Since VS Code doesn't have the whole **File -> Project Structure** thing, they mostly will work with the terminal. 
### Setting up python virtual environment
- After finish cloning, open the terminal with the project path, then start running these command line by line:
```
python -m venv venv
venv\Scripts\activate
pip install -r python_backend/requirements.txt
python python_backend/manage.py makemigrations
python python_backend/manage.py migrate
python python_backend/manage.py runserver
```
- The first line create the virtual environment, second line activate it, third line install python packages to run properly, fourth and fifth line for migrations and sixth line to run the server.
- That's it, the server is up.

### Add javafx .jar files to Referenced Libraries
- Download the version of JavaFX that is most compatible with your computer, we recommend 23.0.1, which can be downloaded here: `https://gluonhq.com/products/javafx/`, then extracted the downloaded file to some location.
- Press Ctrl + Shift + P, type Java: Open Project Settings, go to Libraries and add all of the .jar files of javafx to it, you can find does .jar files under `javafx-sdk-23.0.1\lib`(the `javafx-sdk-23.0.1` change depends on the version of javafx you have)

### Modify launch.json
- First you may need to create the launch.json file, go to run and debug on the left, find `create a launch.json file`, press on it, then a pop up will show up in the middle top of the screen, choose java and vs code will create one for you, then start modifying it.
- Add the following line to `launch.json`, in the configurations section, between `request` and `mainClass`
```
"vmArgs": "--module-path \"C:/Program Files/Java/javafx-sdk-23.0.1/lib\" --add-modules javafx.controls,javafx.fxml,javafx.graphics",
```
- Notes, replace `C:/Program Files/Java/javafx-sdk-23.0.1/lib` with the direction to the javafx library in you machine. Example usage:
```
{
    "type": "java",
    "name": "Current File",
    "request": "launch",
    "vmArgs": "--module-path \"C:/Program Files/Java/javafx-sdk-23.0.1/lib\" --add-modules javafx.controls,javafx.fxml,javafx.graphics",
    "mainClass": "${file}"
},
```
### Install these extensions
- Extension Pack for Java
- JavaFX Support by Shrey Pandya
- Language Support for Java(TM) by Red Hat

After those step, you should be done with the set up part.

### Start running the GUI
- Go the src/java_files, find the MainGUI.java.
- Run it at the main method.
- Alternatively go to the Run Java button on the top right and run the file, and a GUI will pop up.

## Installing python notes
- We are using 3.12.x, which can be download at: https://www.python.org/downloads/release/python-3126.
- Remember to add python to PATH environments variables when setting up, there is a checkbox for that at the bottom left corner when first running the installer file.
- Go to this site to install pip the first time: https://pip.pypa.io/en/stable/installation/, remember to pick your operating system, we recommend using the ensurepip method(the first one on the site).

## JUnit setup 
- Navigate to the RunLocalTest class.
- Hover over the junit import statement.
- IntelliJ will prompt you to download JUnit 5, click OK, and IntelliJ does the rest for you!
