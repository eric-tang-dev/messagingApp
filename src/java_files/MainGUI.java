package java_files;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.*;
import java.io.*;

public class MainGUI extends Application implements SharedResources {

    private Stage primaryStage; // the GUI being displayed

    @Override
    public void start(Stage primaryStage) {
        // this populates the hashmap that we use to save a local copy of a part of the database
        System.out.println(manager.populateHashMap()); // do not remove this line of code from the top
        boolean huh = manager.writeHashMapToFile(); // do not remove this line of code from the top
        System.out.println(manager.block("Wyatt", "test1"));
        System.out.println(manager.block("Eric", "test1"));
        System.out.println(manager.unblock("test2", "Eric"));
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Main Screen");

        // create grid
        GridPane mainGrid = new GridPane();
        mainGrid.setPadding(new Insets(10));
        mainGrid.setHgap(10);
        mainGrid.setVgap(10);
        mainGrid.setAlignment(Pos.CENTER);

        // buttons
        Button createUserButton = new Button("Create User");
        Button loginButton = new Button("Login");

        // buttons actions
        createUserButton.setOnAction(e -> showCreateUser());
        loginButton.setOnAction(e -> showLogin());

        mainGrid.add(createUserButton, 0, 0);
        mainGrid.add(loginButton, 1, 0);

        // more setup
        VBox root = new VBox(mainGrid);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        // show the GUI
        Scene scene = new Scene(root, 1000, 1000);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showCreateUser() {
        CreateUserGUI createUserGUI = new CreateUserGUI();
        try {
            createUserGUI.start(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showLogin() {
        LoginGUI loginGUI = new LoginGUI();
        try {
            loginGUI.start(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

class CreateUserGUI extends Application implements SharedResources {

    private Stage primaryStage; // current GUI being displayed

    private TextField usernameField = new TextField();
    private PasswordField passwordField = new PasswordField();
    private TextField emailField = new TextField();
    private TextField bioField = new TextField();
    private Label terminalOutputLabel = new Label();
    private StringProperty terminalOutput = new SimpleStringProperty("");

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Create User");

        // create grid
        GridPane createUserGrid = new GridPane();
        createUserGrid.setPadding(new Insets(10));
        createUserGrid.setHgap(10);
        createUserGrid.setVgap(10);
        createUserGrid.setAlignment(Pos.CENTER);

        // add labels and text fields
        createUserGrid.add(new Label("Username:"), 0, 0);
        createUserGrid.add(usernameField, 1, 0);

        createUserGrid.add(new Label("Password:"), 0, 1);
        createUserGrid.add(passwordField, 1, 1);

        createUserGrid.add(new Label("Email:"), 0, 2);
        createUserGrid.add(emailField, 1, 2);

        createUserGrid.add(new Label("Bio:"), 0, 3);
        createUserGrid.add(bioField, 1, 3);

        // bind the terminal output label to its output
        terminalOutputLabel.textProperty().bind(terminalOutput);

        // buttons for creating a user
        Button submitButton = new Button("Submit");
        Button clearButton = new Button("Clear");
        Button backButton = new Button("Back");

        // set up button actions and adds buttons
        // trims fields and calls handleSubmit()
        submitButton.setOnAction(e -> handleSubmit(
                usernameField.getText().trim(),
                passwordField.getText().trim(),
                emailField.getText().trim(),
                bioField.getText()  // do not trim() bio, it's allowed to have spaces
        ));
        clearButton.setOnAction(e -> handleClear());
        backButton.setOnAction(e -> showMain());

        createUserGrid.add(submitButton, 0, 5);
        createUserGrid.add(clearButton, 1, 5);
        createUserGrid.add(backButton, 2, 5);

        // adds terminal output to the bottom
        createUserGrid.add(new Label("Terminal Output:"), 0, 7);
        createUserGrid.add(terminalOutputLabel, 1, 7);

        VBox root = new VBox(createUserGrid);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 1000, 1000);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Method to handle POST with all inputs
    private void handleSubmit(String username, String password, String email, String bio) {
        if (username.isEmpty() || password.isEmpty() || email.isEmpty() || bio.isEmpty()) {
            printTerminalOutput("All fields are required.");
        } else {
            handleClear();
            printTerminalOutput(manager.createUser(username, password, email, bio, null));
        }
    }

    // clear all fields
    private void handleClear() {
        usernameField.clear();
        passwordField.clear();
        emailField.clear();
        bioField.clear();
        printTerminalOutput("Cleared Fields!");
    }

    private void showMain() {
        MainGUI mainGUI = new MainGUI();
        try {
            mainGUI.start(primaryStage); // Reuse the primary stage
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // update the terminal (what the user sees in the GUI)
    private void printTerminalOutput(String newValue) {
        terminalOutput.set(newValue);
    }
}

class LoginGUI extends Application implements SharedResources{

    private Stage primaryStage; // Current GUI being displayed

    private TextField usernameField = new TextField();
    private PasswordField passwordField = new PasswordField();
    private Label terminalOutputLabel = new Label();
    private StringProperty terminalOutput = new SimpleStringProperty("");

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Login");

        // create grid
        GridPane loginGrid = new GridPane();
        loginGrid.setPadding(new Insets(10));
        loginGrid.setHgap(10);
        loginGrid.setVgap(10);
        loginGrid.setAlignment(Pos.CENTER);

        // add labels
        loginGrid.add(new Label("Username:"), 0, 0);
        loginGrid.add(usernameField, 1, 0);

        loginGrid.add(new Label("Password:"), 0, 1);
        loginGrid.add(passwordField, 1, 1);

        // Bind the terminal output label to its output
        terminalOutputLabel.textProperty().bind(terminalOutput);

        // Create buttons
        Button submitButton = new Button("Submit");
        Button backButton = new Button("Back");

        // Set up button actions
        submitButton.setOnAction(e -> handleSubmit(
                usernameField.getText().trim(),
                passwordField.getText().trim()
        ));
        backButton.setOnAction(e -> showMain());

        // Add buttons to the grid
        loginGrid.add(submitButton, 0, 2);
        loginGrid.add(backButton, 1, 2);

        // adds terminal output to the bottom
        loginGrid.add(new Label("Terminal Output:"), 0, 7);
        loginGrid.add(terminalOutputLabel, 1, 7);

        // more setup
        VBox root = new VBox(loginGrid);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 1000, 1000);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleSubmit(String username, String password) {
        if (authenticator.authenticate(username, password)) {
            printTerminalOutput("User successfully logged in.");
            showUserProfile(username);
        } else {
            printTerminalOutput("Login failed. Username or password is incorrect.");
        }
    }

    private void showMain() {
        MainGUI mainGUI = new MainGUI();
        try {
            mainGUI.start(primaryStage); // Reuse the primary stage
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showUserProfile(String username) {
        // would redirect to UserGUI
        // to write this GUI and on, likely need to create an interface, where idTracker is static
        // interface -> only purpose is so each GUI has access to necessary data
        // data stored is the manager and authenticator in the interface
        UserGUI userGUI = new UserGUI(primaryStage, username);
        try {
            userGUI.start(primaryStage); // Reuse the primary stage
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }


    // update the terminal (what the user sees in the GUI)
    private void printTerminalOutput(String newValue) {
        terminalOutput.set(newValue);
    }
}

class UserGUI extends Application implements SharedResources {

    private Stage primaryStage; // current GUI being displayed
    private Label terminalOutputLabel = new Label();
    private StringProperty terminalOutput = new SimpleStringProperty("");
    private String username;

    public UserGUI(Stage primaryStage, String username) {
        this.primaryStage = primaryStage;
        this.username = username;
    }

    @Override
    public void start(Stage primaryStage) {
        // data that is available
        String userData = manager.getUser(this.username);
        /*** Example Output of userData
         * {"id":4,"username":"test2","email":"test@ourdue.edu","bio":"test","friends":{}}
         */
        int id = Integer.parseInt(userData.split("\"id\":")[1].split(",")[0].trim());
        String username = userData.split("\"username\":\"")[1].split("\"")[0];
        String email = userData.split("\"email\":\"")[1].split("\"")[0];
        String bio = userData.split("\"bio\":\"")[1].split("\"")[0];

        // initialize new Stage here
        this.primaryStage = primaryStage;
        primaryStage.setTitle(username + "'s Profile");

        // Create a single grid for both profile and messages
        GridPane userGrid = new GridPane();
        userGrid.setPadding(new Insets(10));
        userGrid.setHgap(20);
        userGrid.setVgap(10);

        // Profile info section (left side)
        Label usernameLabel = new Label(username);
        usernameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        Label bioLabel = new Label(bio);
        bioLabel.setStyle("-fx-font-size: 14px;");
        Label emailLabel = new Label(email);
        emailLabel.setStyle("-fx-font-size: 14px;");

        ComboBox<String> dropdownMenu = new ComboBox<>();
        for (String key: manager.getIdTracker().keySet()) {
            if (key.equals(username)) continue;
            dropdownMenu.getItems().add(key);
        }
        dropdownMenu.getItems().add("See All Users");
        dropdownMenu.setPromptText("Search for User");

        Button sendFriendRequestButton = new Button("Send Friend Request");
        Button editDataButton = new Button("Edit Data");
        Button logOutButton = new Button("Log Out");

        // button actions here
        editDataButton.setOnAction(e -> showEdit());
        logOutButton.setOnAction(e -> showLogin());
        terminalOutputLabel.textProperty().bind(terminalOutput);

        // add profile info
        userGrid.add(usernameLabel, 0, 0);
        userGrid.add(bioLabel, 0, 1);
        userGrid.add(dropdownMenu, 0, 2);
        userGrid.add(sendFriendRequestButton, 0, 3);
        userGrid.add(editDataButton, 1, 2);

        // RIGHT SIDE OF SCREEN FOR MESSAGES
        HashMap<String, Integer> friendsMap = manager.getIdTracker();

        // new grid
        GridPane grid2 = new GridPane();
        grid2.setPadding(new Insets(10));
        grid2.setHgap(10);
        grid2.setVgap(10);

        int messageRow = 0;

        for (Map.Entry<String, Integer> entry : friendsMap.entrySet()) {
            String otherUser = entry.getKey();
            if (username.equals(otherUser)) continue;

            List<String> messages = List.of("Hello there!");

            VBox messageBox = new VBox();
            messageBox.setPadding(new Insets(15));
            messageBox.setSpacing(20);
            messageBox.setStyle("-fx-background-color: #f0f0f0; -fx-border-radius: 10; -fx-background-radius: 10; -fx-min-width: 300px;");

            Label userLabel = new Label(otherUser);
            userLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

            Label latestMessage = new Label(messages.get(messages.size() - 1));
            latestMessage.setStyle("-fx-font-size: 14px; -fx-text-fill: #555555;");

            messageBox.getChildren().addAll(userLabel, latestMessage);
            grid2.add(messageBox, 0, messageRow);
            messageRow++;
            if (messageRow >= 6) { // don't display more than 6 at once
                break;
            }
        }

        messageRow += 1; // numRows between message and logout

        grid2.add(logOutButton, 0, messageRow + 1);
        grid2.add(new Label("Terminal Output:"), 0, messageRow + 2);
        grid2.add(terminalOutputLabel, 1, messageRow + 2);

        // put the grid in a vbox
        VBox root = new VBox();
        root.setPadding(new Insets(20));

        userGrid.prefHeightProperty().bind(root.heightProperty().multiply(0.35));
        grid2.prefHeightProperty().bind(root.heightProperty().multiply(0.65));
        root.getChildren().addAll(userGrid, grid2);

        // set the Stage
        Scene scene = new Scene(root, 550, 1000);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    private void showLogin() {
        LoginGUI loginGUI = new LoginGUI();
        try {
            loginGUI.start(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showEdit() {
        EditGUI editGUI = new EditGUI(username);
        try {
            editGUI.start(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // update the terminal (what the user sees in the GUI)
    private void printTerminalOutput(String newValue) {
        terminalOutput.set(newValue);
    }
}

class EditGUI extends Application implements SharedResources {

    private Stage primaryStage; // current GUI being displayed
    private Label terminalOutputLabel = new Label();
    private StringProperty terminalOutput = new SimpleStringProperty("");
    private String user;

    public EditGUI(String user) {
        this.user = user;
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Edit Profile");

        // create grid
        GridPane editGrid = new GridPane();
        editGrid.setPadding(new Insets(10));
        editGrid.setHgap(10);
        editGrid.setVgap(10);
        editGrid.setAlignment(Pos.CENTER);

        // edit username
        editGrid.add(new Label("Edit Username:"), 0, 0);
        TextField username = new TextField();
        editGrid.add(username, 1, 0);

        // edit email
        editGrid.add(new Label("Edit Email:"), 0, 2);
        TextField email = new TextField();
        editGrid.add(email, 1, 2);

        // edit bio
        editGrid.add(new Label("Edit Bio:"), 0, 3);
        TextField bio = new TextField();
        editGrid.add(bio, 1, 3);

        // terminal output
        editGrid.add(new Label("Terminal Output:"), 0, 7);
        editGrid.add(terminalOutputLabel, 0, 8);

        // bind the terminal output label to its output
        terminalOutputLabel.textProperty().bind(terminalOutput);

        // create buttons
        Button saveButton = new Button("Save Changes");
        Button backButton = new Button("Back");

        // get username

        // eet up button actions
        backButton.setOnAction(e -> showUser(user));
        saveButton.setOnAction(e -> {
            // get text fom user and trim
            String newUsername = username.getText().trim();
            String newEmail = email.getText().trim();
            String newBio = bio.getText().trim();

            // validation of fields
            while (true) {
                if (newUsername.isEmpty()) {
                    terminalOutput.set("Username cannot be empty. Please provide a valid username.");
                    break;
                } else if (newEmail.isEmpty()) {
                    terminalOutput.set("Email cannot be empty. Please provide a valid email address.");
                    break; // Exit the loop since validation failed
                } else if (newBio.isEmpty()) {
                    terminalOutput.set("Bio cannot be empty. Please provide some information in your bio.");
                    break;
                } else {
                    username.setText(newUsername);
                    email.setText(newEmail);
                    bio.setText(newBio);
                    terminalOutput.set("Profile updated successfully!");

                    // print to the console for debugging
                    // here, i will send the PUT/PATCH request
                    System.out.println("Updated Username: " + newUsername);
                    System.out.println("Updated Email: " + newEmail);
                    System.out.println("Updated Bio: " + newBio);
                    showUser(newUsername);

                    break;
                }
            }
        });

        terminalOutputLabel.textProperty().bind(terminalOutput);

        // Add buttons to the grid
        editGrid.add(saveButton, 0, 4);
        editGrid.add(backButton, 1, 4);

        // More setup - using a VBox as the root container
        VBox root = new VBox(editGrid);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        // Create a scene with the VBox
        Scene scene = new Scene(root, 1000, 1000);
        this.primaryStage.setScene(scene);
        this.primaryStage.show();
    }

    private void showUser(String username) {
        UserGUI userGUI = new UserGUI(primaryStage, username);
        try {
            userGUI.start(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // update the terminal (what the user sees in the GUI)
    private void printTerminalOutput(String newValue) {
        terminalOutput.set(newValue);
    }
}


