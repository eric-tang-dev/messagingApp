package java_files;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import javax.swing.*;
import java.lang.reflect.Array;
import java.util.*;
import java.io.*;

import static java_files.ThreadedServer.checkIfBlocked;
import static java_files.ThreadedServer.checkIfFriend;

public class MainGUI extends Application implements SharedResources {

    private Stage primaryStage; // the GUI being displayed
    private Client client;

    public MainGUI(Client client) {
        this.client = client;
    }

    @Override
    public void start(Stage primaryStage) {
        manager.populateHashMap();
        // this populates the hashmap that we use to save a local copy of a part of the database
        client.newClientCommand("WRITEHASHMAP:");

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
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.centerOnScreen();
    }

    private void showCreateUser() {
        CreateUserGUI createUserGUI = new CreateUserGUI(client);
        try {
            createUserGUI.start(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showLogin() {
        LoginGUI loginGUI = new LoginGUI(client);
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
    private Client client;

    private TextField usernameField = new TextField();
    private PasswordField passwordField = new PasswordField();
    private TextField emailField = new TextField();
    private TextField bioField = new TextField();
    private Label terminalOutputLabel = new Label();
    private StringProperty terminalOutput = new SimpleStringProperty("");

    public CreateUserGUI(Client client) {
        this.client = client;
    }

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

        Scene scene = new Scene(root, 800, 600);;
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.centerOnScreen();
    }

    // Method to handle POST with all inputs
    private void handleSubmit(String username, String password, String email, String bio) {
        if (username.isEmpty() || password.isEmpty() || email.isEmpty() || bio.isEmpty()) {
            printTerminalOutput("All fields are required.");
        } else {
            handleClear();
            printTerminalOutput(client.newClientCommand("CREATEUSER:" + username + ":" + password + ":" + email + ":" + bio));
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
        MainGUI mainGUI = new MainGUI(client);
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
    private Client client;

    private TextField usernameField = new TextField();
    private PasswordField passwordField = new PasswordField();
    private Label terminalOutputLabel = new Label();
    private StringProperty terminalOutput = new SimpleStringProperty("");

    public LoginGUI(Client client) {
        this.client = client;
    }

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

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.centerOnScreen();
    }

    private void handleSubmit(String username, String password) {
        if (client.newClientCommand("LOGIN:" + username + ":" + password).contains("SUCCESS")) {
            printTerminalOutput("User successfully logged in.");
            showUserProfile(username);
        } else {
            printTerminalOutput("Login failed. Username or password is incorrect.");
        }
    }

    private void showMain() {
        MainGUI mainGUI = new MainGUI(client);
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
        UserGUI userGUI = new UserGUI(primaryStage, username, client);
        try {
            userGUI.start(primaryStage); // Reuse the primary stage
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // update the terminal (what the user sees in the GUI)
    private void printTerminalOutput(String newValue) {
        terminalOutput.set(newValue);
    }
}

class UserGUI extends Application implements SharedResources {

    private Stage primaryStage; // current GUI being displayed
    private Client client;

    private Label terminalOutputLabel = new Label();
    private StringProperty terminalOutput = new SimpleStringProperty("");
    private String username;
    private ComboBox<String> dropdownMenu;
    private BorderPane mainLayout;

    public UserGUI(Stage primaryStage, String username, Client client) {
        this.primaryStage = primaryStage;
        this.username = username;
        this.client = client;
    }

    @Override
    public void start(Stage primaryStage) {
        // Set the primary stage
        this.primaryStage = primaryStage;
        primaryStage.setTitle(username + "'s Social Media Profile");

        // data that is available
        String userData = client.newClientCommand("GETUSER:" + username);
        /*** Example Output of userData
         * {"id":4,"username":"test2","email":"test@ourdue.edu","bio":"test","friends":{}}
         */
        int id = Integer.parseInt(userData.split("\"id\":")[1].split(",")[0].trim());
        String username = userData.split("\"username\":\"")[1].split("\"")[0];
        String email = userData.split("\"email\":\"")[1].split("\"")[0];
        String bio = userData.split("\"bio\":\"")[1].split("\"")[0];

        // initialize the main layout
        this.mainLayout = new BorderPane();

        // nav bar on the left side
        VBox sideNavBar = new VBox();
        sideNavBar.setPadding(new Insets(10));
        sideNavBar.setSpacing(20);
        sideNavBar.setStyle("-fx-background-color: #333; -fx-min-width: 150px;");

        Button profileButton = new Button("Profile");
        Button messagesButton = new Button("Messages");
        Button otherButton = new Button("Actions");

        profileButton.setMaxWidth(Double.MAX_VALUE);
        messagesButton.setMaxWidth(Double.MAX_VALUE);
        otherButton.setMaxWidth(Double.MAX_VALUE);

        sideNavBar.getChildren().addAll(profileButton, messagesButton, otherButton);

        // panes
        StackPane profilePane = createProfilePane(username, email, bio);
        StackPane otherPane = createActionPane();

        // terminal output
        HBox terminalOutputPane = new HBox();
        terminalOutputPane.setPadding(new Insets(10));
        terminalOutputPane.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #ccc;");
        Label terminalLabel = new Label("Terminal Output: ");
        terminalOutputLabel.textProperty().bind(terminalOutput);
        terminalOutputPane.getChildren().addAll(terminalLabel, terminalOutputLabel);

        // put logout button on the very bottom
        Button logOutButtonBottom = new Button("Log Out");
        logOutButtonBottom.setOnAction(e -> showLogin());
        VBox bottomPane = new VBox(terminalOutputPane, logOutButtonBottom);
        bottomPane.setSpacing(10);
        bottomPane.setPadding(new Insets(10));

        // place the panes on the screen where they belong
        BorderPane.setAlignment(sideNavBar, Pos.CENTER_LEFT);
        mainLayout.setLeft(sideNavBar);
        mainLayout.setBottom(bottomPane);
        mainLayout.setCenter(profilePane);

        // buttons on the side to switch views
        profileButton.setOnAction(e -> mainLayout.setCenter(profilePane));
        messagesButton.setOnAction(e -> {
            StackPane messagesPane = createMessagesPane();
            mainLayout.setCenter(messagesPane);
        });
        otherButton.setOnAction(e -> mainLayout.setCenter(otherPane));

        // show stage
        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.centerOnScreen();
    }

    private StackPane createProfilePane(String username, String email, String bio) {
        // profile pane with user information
        StackPane profilePane = new StackPane();
        VBox profileBox = new VBox();
        profileBox.setPadding(new Insets(20));
        profileBox.setSpacing(10);

        Label usernameLabel = new Label(username);
        usernameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        Label emailLabel = new Label(email);
        Label bioLabel = new Label(bio);

        Button editDataButton = new Button("Edit Data");
        Button viewFriendButton = new Button("View Friends");
        Button viewBlockedButton = new Button("View Blocked Users");

        editDataButton.setOnAction(e -> showEdit());
        viewFriendButton.setOnAction(e -> viewFriend());
        viewBlockedButton.setOnAction(e -> viewBlockList());

        profileBox.getChildren().addAll(usernameLabel, emailLabel, bioLabel, editDataButton, viewFriendButton, viewBlockedButton);
        profilePane.getChildren().add(profileBox);

        return profilePane;
    }

    private StackPane createMessagesPane() {
        // messages pane
        System.out.println("run");
        StackPane messagesPane = new StackPane();
        VBox messagesBox = new VBox();
        messagesBox.setPadding(new Insets(20));
        messagesBox.setSpacing(10);

        HashMap<String, ArrayList<String>> friendsMap = manager.getFriendsHashMap(client.newClientCommand("GETUSER:" + username));
        for (Map.Entry<String, ArrayList<String>> entry : friendsMap.entrySet()) {
            String otherUser = entry.getKey();
            if (username.equals(otherUser)) continue;

            VBox messageBox = new VBox();
            messageBox.setPadding(new Insets(10));
            messageBox.setSpacing(5);
            messageBox.setStyle("-fx-background-color: #DCDCDC; -fx-border-radius: 10; -fx-background-radius: 10;");
            messageBox.setOnMouseClicked(event -> showMessage(otherUser));

            Label userLabel = new Label(otherUser);
            userLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

            ArrayList<String> messages = entry.getValue();
            String[] data = messages.get(messages.size() - 1).split("-");
            Label latestMessage = new Label(messages.isEmpty() ? "No messages" : data[0]);
            latestMessage.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");


            messageBox.getChildren().addAll(userLabel, latestMessage);
            messagesBox.getChildren().add(messageBox);
        }

        messagesPane.getChildren().add(messagesBox);
        return messagesPane;
    }

    private StackPane createActionPane() {
        // pane with all actions
        StackPane otherPane = new StackPane();
        VBox otherBox = new VBox();
        otherBox.setPadding(new Insets(20));
        otherBox.setSpacing(10);

        dropdownMenu = new ComboBox<>();
        for (String key : manager.getIdTracker().keySet()) {
            if (key.equals(username)) continue;
            dropdownMenu.getItems().add(key);
        }
        dropdownMenu.getItems().add("See All Users");
        dropdownMenu.setPromptText("Search for User");

        Button addFriendButton = new Button("Add Friend");
        Button unfriendButton = new Button("Remove Friend");
        Button viewUserButton = new Button("View User's Profile");
        Button blockButton = new Button("Block User");
        Button unblockButton = new Button("Unblock User");

        addFriendButton.setOnAction(e -> addFriend(username, dropdownMenu));
        unfriendButton.setOnAction(e -> unfriend(username, dropdownMenu));
        viewUserButton.setOnAction(e -> showViewUser());
        blockButton.setOnAction(e -> block(dropdownMenu));
        unblockButton.setOnAction(e -> unblock(dropdownMenu));

        otherBox.getChildren().addAll(dropdownMenu, viewUserButton, addFriendButton, unfriendButton, blockButton, unblockButton);
        otherPane.getChildren().add(otherBox);

        return otherPane;
    }

    private void showViewUser() {
        if (dropdownMenu.getValue() == null || dropdownMenu.getValue().equals("See All Users")) {
            System.out.println("No user selected.");
            terminalOutput.set("Please select a valid user to view.");
        } else {
            ViewGUI viewGUI = new ViewGUI(primaryStage, username, dropdownMenu.getValue(), client);
            try {
                viewGUI.start(primaryStage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void showLogin() {
        LoginGUI loginGUI = new LoginGUI(client);
        try {
            loginGUI.start(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showEdit() {
        EditGUI editGUI = new EditGUI(username, client);
        try {
            editGUI.start(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showMessage(String user) {
        MessageGUI messageGUI = new MessageGUI(this.username, user, client);
        try {
            messageGUI.start(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addFriend(String username, ComboBox<String> dropdownMenu) {
        String selectedFriend = dropdownMenu.getValue();
        System.out.println("Add Friend button clicked. Selected friend: " + selectedFriend);

        if (selectedFriend == null || selectedFriend.equals("See All Users")) {
            System.out.println("Invalid friend selection.");
            terminalOutput.set("Please select a valid user to add as a friend.");
        } else if (checkIfBlocked(this.username, selectedFriend)) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Messaging App");
                alert.setHeaderText(null);
                alert.setContentText("Error: " + selectedFriend + " is blocked. Unblock them first.");
                alert.showAndWait();
            });
            return;
        } else {
            System.out.println("Calling manager.addFriend with username: " + username + " and friend: " + selectedFriend);
            String result = client.newClientCommand("ADDFRIEND:" + username + ":" + selectedFriend);
            System.out.println("Result from manager.addFriend: " + result);
            terminalOutput.set(result);
        }
        refreshUserGUI();
    }

    private void unfriend(String username, ComboBox<String> dropdownMenu) {
        String selectedFriend = dropdownMenu.getValue();
        System.out.println("Unfriend button clicked. Selected friend: " + selectedFriend);

        if (selectedFriend == null || selectedFriend.equals("See All Users")) {
            System.out.println("Invalid friend selection.");
            terminalOutput.set("Please select a valid user to unfriend.");
        } else {
            System.out.println("Calling manager.unfriend with username: " + username + " and friend: " + selectedFriend);
            String result = client.newClientCommand("UNFRIEND:" + username + ":" + selectedFriend);
            System.out.println("Result from manager.unfriend: " + result);
            terminalOutput.set(result); // Display the result in the terminal output
        }
        refreshUserGUI();
    }

    private void block(ComboBox<String> dropdownMenu) {
        String selectedFriend = dropdownMenu.getValue();

        if (selectedFriend == null || selectedFriend.equals("See All Users")) {
            System.out.println("Invalid selection.");
            terminalOutput.set("Please select a valid user to block.");
        } else {
            System.out.println("Calling manager.block with username: " + username + " and user: " + selectedFriend);
            String result = client.newClientCommand("BLOCK:" + username + ":" + selectedFriend);
            System.out.println("Result from manager.block: " + result);
            terminalOutput.set(result); // Display the result in the terminal output
        }
        refreshUserGUI();
    }

    private void unblock(ComboBox<String> dropdownMenu) {
        String selectedFriend = dropdownMenu.getValue();

        if (selectedFriend == null || selectedFriend.equals("See All Users")) {
            System.out.println("Invalid selection.");
            terminalOutput.set("Please select a valid user to unblock.");
        } else {
            System.out.println("Calling manager.unblock with username: " + username + " and user: " + selectedFriend);
            String result = client.newClientCommand("UNBLOCK:" + username + ":" + selectedFriend);
            System.out.println("Result from manager.unblock: " + result);
            terminalOutput.set(result); // Display the result in the terminal output
        }
        refreshUserGUI();
    }

    private void viewFriend() {
        String userData = manager.getUser(username);
        HashMap<String, ArrayList<String>> friendsMap = manager.getFriendsHashMap(userData);
        if (friendsMap.isEmpty()) {
            terminalOutput.set("No friends found.");
        } else {
            StringBuilder friendList = new StringBuilder("Friends List:\n");
            for (Map.Entry<String, ArrayList<String>> entry : friendsMap.entrySet()) {
                friendList.append(entry.getKey());
                friendList.append("\n");
            }
            terminalOutput.set(friendList.toString());
        }
    }

    private void viewBlockList() {
        File file = new File("blockedList.txt");

        if (!file.exists()) {
            terminalOutput.set("Blocked list file does not exist.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Check if the line starts with the specified username
                if (line.startsWith(username + ":")) {
                    int startOfList = line.indexOf("[");
                    line = "Blocked List:\n" + line.substring(startOfList + 1, line.length() - 1);
                    if (line.length() == 2) {
                        terminalOutput.set("No Users Blocked.");
                    } else {
                        terminalOutput.set(line);
                    }
                    return; // Stop after finding the user
                }
            }
            // If the user is not found
            terminalOutput.set("User '" + username + "' not found in the blocked list.");
        } catch (IOException e) {
            terminalOutput.set("Error reading blocked list file.");
            e.printStackTrace();
        }
    }

    private void refreshUserGUI() {
        // To help with refreshing instantly
        start(primaryStage);
    }

    private void refreshUserGUI(String pane) {
        switch (pane.toLowerCase()) {
            case "messages":
                mainLayout.setCenter(createMessagesPane());
                break;
            case "action":
                mainLayout.setCenter(createActionPane());
                break;
            default:
                mainLayout.setCenter(createProfilePane(username,
                        client.newClientCommand("GETUSER:" + username).split("\"email\":")[1].split("\"")[0],
                        client.newClientCommand("GETUSER:" + username).split("\"bio\":")[1].split("\"")[0]));
                break;
        }
    }


    // update the terminal (what the user sees in the GUI)
    private void printTerminalOutput(String newValue) {
        terminalOutput.set(newValue);
    }
}

class EditGUI extends Application implements SharedResources {

    private Stage primaryStage; // current GUI being displayed
    private Client client;

    private Label terminalOutputLabel = new Label();
    private StringProperty terminalOutput = new SimpleStringProperty("");
    private String user;

    public EditGUI(String user, Client client) {
        this.user = user;
        this.client = client;
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Edit Profile");
        String username = user;
        // create grid
        GridPane editGrid = new GridPane();
        editGrid.setPadding(new Insets(10));
        editGrid.setHgap(10);
        editGrid.setVgap(10);
        editGrid.setAlignment(Pos.CENTER);

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
            String newEmail = email.getText().trim();
            String newBio = bio.getText().trim();
            // validation of fields
            while (true) {
                if (newEmail.isEmpty()) {
                    String userData = client.newClientCommand("GETUSER:" + username);
                    newEmail = userData.split("\"email\":\"")[1].split("\"")[0];
                } else if (newBio.isEmpty()) {
                    String userData = client.newClientCommand("GETUSER:" + username);
                    newBio = userData.split("\"bio\":\"")[1].split("\"")[0];
                } else {
                    String editData = "EDIT:" + username + ":" + null + ":" + newEmail + ":" + newBio;
                    terminalOutput.set(client.newClientCommand(editData));
                    email.setText(newEmail);
                    bio.setText(newBio);
                    terminalOutput.set("Profile updated successfully!");

                    // print to the console for debugging
                    // here, i will send the PUT/PATCH request
                    System.out.println("Updated Email: " + newEmail);
                    System.out.println("Updated Bio: " + newBio);
                    showUser(username);

                    break;
                }
            }
        });

        terminalOutputLabel.textProperty().bind(terminalOutput);

        // Add buttons to the grid
        editGrid.add(saveButton, 0, 5);
        editGrid.add(backButton, 1, 5);

        // More setup - using a VBox as the root container
        VBox root = new VBox(editGrid);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        // Create a scene with the VBox
        Scene scene = new Scene(root, 800, 600);
        this.primaryStage.setScene(scene);
        this.primaryStage.show();
        primaryStage.centerOnScreen();
    }

    private void showUser(String username) {
        UserGUI userGUI = new UserGUI(primaryStage, username, client);
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

class MessageGUI extends Application implements SharedResources {

    private Stage primaryStage;
    private Client client;

    private Label terminalOutputLabel = new Label();
    private StringProperty terminalOutput = new SimpleStringProperty("");
    private String user1;
    private String user2;

    public MessageGUI(String user1, String user2, Client client) {
        this.user1 = user1;
        this.user2 = user2;

        this.client = client;
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Chat with " + user2);

        // get user data
        String user1data = client.newClientCommand("GETUSER:" + user1);

        // use a method to get the friends hashmap
        MessageManager mm = new MessageManager(user1, user2);
        HashMap<String, ArrayList<String>> user1friends = mm.getFriendsHashMap(user1data);

        // get the chain of messages with user 2 by searching for the key
        ArrayList<String> messageChain = user1friends.get(user2);
        String[] messages = messageChain.toArray(new String[0]);

        // create grid
        VBox root = new VBox();
        root.setPadding(new Insets(10));
        root.setSpacing(10);
        root.setStyle("-fx-background-color: #ffffff;");

        // back button
        HBox backBox = new HBox();
        backBox.setSpacing(10);
        backBox.setPadding(new Insets(10));
        backBox.setAlignment(Pos.CENTER_LEFT);

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> { showUser(); });
        backButton.setStyle("-fx-translate-x: 150px;");

        // title
        Label chatTitle = new Label("Chat with " + user2);
        chatTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        backBox.getChildren().addAll(chatTitle, backButton);

        root.getChildren().add(backBox);

        // Chat display area
        VBox chatBox = new VBox();
        chatBox.setPadding(new Insets(10));
        chatBox.setSpacing(10);
        chatBox.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #ddd; -fx-border-radius: 10; -fx-padding: 10;");
        chatBox.setPrefSize(400, 900);
        VBox.setVgrow(chatBox, Priority.ALWAYS); // Make chatBox grow as the window resizes

        for (String message : messages) {
            String[] messageData = message.split("-");
            if (messageData.length != 3) {
                continue;
            }
            Label messageLabel = new Label();
            if (messageData[1].equals("1")) {
                messageLabel.setText(messageData[0]);
            } else {
                messageLabel.setText(messageData[0]);
            }
            messageLabel.setStyle(
                    "-fx-font-family: 'Arial';" +
                            "-fx-font-size: 14px;" +
                            "-fx-background-color: #e0e0e0;" + // Light gray bubble
                            "-fx-padding: 10px;" +
                            "-fx-border-radius: 15px;" +
                            "-fx-background-radius: 15px;"
            );

            HBox messageBubble = new HBox();
            if (messageData[1].equals("1")) {
                messageLabel.setStyle(messageLabel.getStyle() + "-fx-background-color: #d1f0ff;");
                messageBubble.setAlignment(Pos.CENTER_RIGHT); // Align to left within the HBox
                messageBubble.getChildren().add(messageLabel);
            } else {
                messageBubble.setAlignment(Pos.CENTER_LEFT); // Align to right within the HBox
                messageBubble.getChildren().add(messageLabel);
            }
            chatBox.getChildren().add(messageBubble);
        }

        root.getChildren().add(chatBox);

        // Input area for typing messages
        VBox inputBox = new VBox();
        inputBox.setPadding(new Insets(10));
        inputBox.setSpacing(5);
        inputBox.setStyle("-fx-background-color: #ffffff; -fx-border-color: #ddd; -fx-border-radius: 5;");

        TextField messageField = new TextField();
        messageField.setPromptText("Type your message...");
        messageField.setStyle("-fx-font-size: 14px;");

        Button sendButton = new Button("Send");
        sendButton.setAlignment(Pos.BOTTOM_RIGHT);
        sendButton.setOnAction(e -> {
            if (checkIfBlocked(user2, user1)) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Messaging App");
                    alert.setHeaderText(null);
                    alert.setContentText("Error: This user has you blocked!");
                    alert.showAndWait();
                });
                return;
            } else if (!(checkIfFriend(user1, user2) && checkIfFriend(user2, user1))) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Messaging App");
                    alert.setHeaderText(null);
                    alert.setContentText("Error: Cannot message user unless you are 2-way friends");
                    alert.showAndWait();
                });
                return;
            }

            // first, send the message on the backend
            System.out.println("SENDMESSAGE:" + user1 + ":" + user2 + ":" + messageField.getText());
            System.out.println(client.newClientCommand("SENDMESSAGE:" + user1 + ":" + user2 + ":" + messageField.getText()));

            // create a new label and add it to the chatbox
            Label newMessage = new Label(messageField.getText());
            newMessage.setStyle(
                    "-fx-font-family: 'Arial';" +
                            "-fx-font-size: 14px;" +
                            "-fx-background-color: #d1f0ff;" + // Light blue bubble for user
                            "-fx-padding: 10px;" +
                            "-fx-border-radius: 15px;" +
                            "-fx-background-radius: 15px;"
            );

            HBox newMessageBubble = new HBox();
            newMessageBubble.setAlignment(Pos.CENTER_RIGHT); // Align to the right for user messages
            newMessageBubble.getChildren().add(newMessage);

            // add to chatBox
            chatBox.getChildren().add(newMessageBubble);
            messageField.clear();
        }); // Placeholder send action

        inputBox.getChildren().addAll(messageField, sendButton);
        root.getChildren().add(inputBox);

        // Set up the scene
        Scene scene = new Scene(root, 400, 1000);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.centerOnScreen();
    }

    private void showUser() {
        UserGUI userGUI = new UserGUI(primaryStage, user1, client);
        try {
            userGUI.start(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class ViewGUI extends Application implements SharedResources {
    private Stage primaryStage; // current GUI being displayed
    private Client client;

    private Label terminalOutputLabel = new Label();
    private StringProperty terminalOutput = new SimpleStringProperty("");
    private String username;
    private String viewedUser;

    public ViewGUI(Stage primaryStage, String username, String viewedUser, Client client) {
        this.primaryStage = primaryStage;
        this.username = username;
        this.viewedUser = viewedUser;
        this.client = client;
    }

    @Override
    public void start(Stage primaryStage) {
        // data that is available
        String userData = client.newClientCommand("GETUSER:" + viewedUser);
        /*** Example Output of userData
         * {"id":4,"username":"test2","email":"test@ourdue.edu","bio":"test","friends":{}}
         */
        int id = Integer.parseInt(userData.split("\"id\":")[1].split(",")[0].trim());
        String viewedUsername = userData.split("\"username\":\"")[1].split("\"")[0];
        String viewedEmail = userData.split("\"email\":\"")[1].split("\"")[0];
        String viewedBio = userData.split("\"bio\":\"")[1].split("\"")[0];

        // initialize new Stage here
        this.primaryStage = primaryStage;
        primaryStage.setTitle(viewedUsername + "'s Profile");

        // Create a single grid for both profile and messages
        GridPane userGrid = new GridPane();
        userGrid.setPadding(new Insets(10));
        userGrid.setHgap(20);
        userGrid.setVgap(10);

        // Profile info section (left side)
        Label usernameLabel = new Label(viewedUsername);
        usernameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        Label bioLabel = new Label(viewedBio);
        bioLabel.setStyle("-fx-font-size: 14px;");
        Label emailLabel = new Label(viewedEmail);
        emailLabel.setStyle("-fx-font-size: 14px;");

        Button backButton = new Button("Back");

        backButton.setOnAction(e -> showUser());

        userGrid.add(usernameLabel, 0, 0);
        userGrid.add(bioLabel, 0, 1);
        userGrid.add(emailLabel, 0, 2);
        userGrid.add(backButton, 0, 10);

        VBox root = new VBox();
        root.setPadding(new Insets(20));

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.centerOnScreen();

        userGrid.prefHeightProperty().bind(root.heightProperty().multiply(0.35));
        root.getChildren().addAll(userGrid);
    }

    private void showUser() {
        UserGUI userGUI = new UserGUI(primaryStage, this.username, client);
        try {
            userGUI.start(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



