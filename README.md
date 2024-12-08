# MessagingApp Setup Guide

## CHANGES MADE TO FINAL VERSION || READ BEFORE RUNNING

We changed our JavaFX configuration. The main class has changed from `java_files.MainGUI` to `java_files.Client`. 
Below are instructions to change it: 

1. Click on the configuration on the top right. It should say "JavaFXApp".
2. Click on "Edit Configurations..."
3. Change the Main Class from `java_files.MainGUI` to `java_files.Client`.

## Submissions
- Wyatt submitted Vocareum Workspace.
- Aneesh submitted the Presentation.
- Wyatt submitted the Report on Brightspace. 

## Instructions

### Before Running Any Part of the Program
Copy and execute the following commands in the terminal:

```bash
cd python_backend
source venv/bin/activate
python manage.py runserver
```

Before running the `Client.java` class or `RunClientTest.java`:
</br>
**Make sure to run `ThreadedServer.java` first**

## Threading Documentation

We implement three layers of thread safety. 

It’s important to note that our database is stored in **SQLite**. We structured our program to minimize the amount of data that Java has to save locally. 

### First Layer: Java Thread Safety
Our first layer of thread safety is in Java itself. We have a few data sets that are shared by all threads. For the most part, write-access to these data sets is extremely limited. We try to limit each thread’s write-access to only allow it to change data related to its own user. 

Of course, there are instances where threads must edit users’ data. In such cases, we use a **manager object** to synchronize those calls. These synchronized blocks are placed throughout the `ThreadedServer.java` class to ensure thread safety.

### Second Layer: Python Serialization
Our second layer of thread safety is **Python serialization**. Our Java program sends JSON data to Python, which must be serialized before being sent to the database. 

- Methods in `python_backend` handle the serialization of data.
- Python processes serialized data **one at a time**. This ensures that even if multiple threads send JSON data to Python, they are processed sequentially, with other threads waiting in a queue.

This allows multiple threads to make concurrent calls in Java while ensuring that they are handled one-by-one in the backend.

### Third Layer: SQLite Database
Our last layer of thread safety lies in the **database itself**. SQLite operations are atomic, meaning that a transaction must complete fully or not at all. 

- Only one thread will access the database at a time.
- In Java, we ensure that threads read the database before each call, guaranteeing access to the most recent version of the database.
- SQLite provides additional thread safety through features like rollback, write-ahead logging, and crash recovery.

With these three levels of thread safety, we can confidently claim that our program is thread-safe.

The ‘Client.java’ class has a few commented out sections of code meant to try and create race conditions within the program. Use these to compare expected results with actual results from running the program.

---

## Test Cases Documentation

We have three test case files, all implemented using **JUnit**.

1. **RunLocalTest.java** and **RunLocalTest2.java**
   - These files test all methods related to the database.
   - They can be run independently.

2. **RunClientTest.java**
   - This file tests methods related to the server.
   - To run this test, you must first run `ThreadedServer.java`.

---

## Class Documentation

### Authenticator
The `Authenticator` class handles user authentication. 

- It uses the authentication methods provided by **Django’s REST framework**.
- Takes in a username and password, checks their validity in the database, and returns a boolean.

### Client
The `Client` class manages the connection to the server. *(Future development will integrate it with the GUI.)*

- The `newClientCommand()` method takes a string input and sends it to the server.
- This string input corresponds to potential user actions in the GUI (e.g., button clicks).
- For testing, strings can be manually input, adhering to the format of `ThreadedServer`'s switch cases.

### MainGUI and Other GUI Classes
The GUI is currently under development and is used primarily for testing purposes. 

Current features include:
- Creating a user
- Logging in and out
- Viewing a user’s profile
- Viewing a list of all other users

### MessageManager
The `MessageManager` class handles message operations.

- Provides methods for creating messages.
- Manages message storage and retrieval responses (success/failure) from the database.

### UserManager
The `UserManager` class manages user data and interactions within the server. It provides the following methods:

- **`createUser`**: Creates a new user with specified username, password, and additional info (e.g., email, bio).
- **`editUser`**: Updates user information, excluding username or password.
- **`deleteUser`**: Deletes a user by username.
- **`getUser`**: Retrieves information for a specified username.
- **`addFriend`**: Adds a friend to a user’s friend list.
- **`unFriend`**: Removes a friend from a user’s friend list.
- **`block`**: Blocks a specified user for the given user.
- **`unblock`**: Unblocks a specified user for the given user.

### ThreadedServer
The `ThreadedServer` class acts as a mediator between the database and the client.

- Reads input from the client.
- Calls methods in `UserManager.java`.
- Retrieves responses from the database.
- Sends **SUCCESS/FAILED** messages back to the client.

### Python_backend
The **Python_backend** is a Django project that contains an application called **messaging**. 

The `messaging` app includes:
- **Serializer**: Handles data serialization.
- **Views.py**: Defines view logic.
- **Models.py**: Defines database models.
- **URL routing**: Manages URL configurations.


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
