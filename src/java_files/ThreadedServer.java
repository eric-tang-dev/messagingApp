package java_files;
import java.io.*;
import java.net.*;

public class ThreadedServer extends Thread implements SharedResources {
    Socket client;
    static Object blockLock;

    // Constructor: Each thread of the server has its own client
    public ThreadedServer(Socket client) {
        this.client = client;
    }

    public Socket getClient() {
        return client;
    }

    public static void main(String[] args) {
        Socket client;
        // Default socket number we'll attempt to connect to
        System.out.println("Waiting for client to connect on port num " + 4242);
        // Wait for a client to attempt to connect on a port number
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(4242);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        while (true) {
            try {
                client = serverSocket.accept();
                System.out.println("Client connected on " + 4242);
                // Increment socket number so when it loops, a new port number is used
//                portNum++;
                // Create a new object so the client can communicate with the server
                // independently of other users
                ThreadedServer yippee = new ThreadedServer(client);
                // Run whatever inputs need to go back and forth between client and server
                yippee.start();
            } catch (IOException e) {
                // WARNING: HAVEN'T DONE TESTING TO CATCH EXCEPTIONS
                throw new RuntimeException(e);
            }
        }
    }

    // Whatever we want to display once connection is made will be here,
    // like GUI and listening for inputs like login or messaging people
    public void run() {
        Socket clientCopy = getClient();
        String input = "";
        String serverReturn;
        boolean logout = false;
        // I'm thinking if we have this 'username' string outside the scope of the while loop,
        // then we can just save the username of whoever is logged in from the "LOGIN" case.
        // We can use this for a lot of other methods to know which user is logged in and who we're accessing data for
        String username;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientCopy.getInputStream()));
            PrintWriter writer = new PrintWriter(clientCopy.getOutputStream());
            while (!logout) {
                if (!logout) {
                    input = reader.readLine();
                    System.out.println("Read input: " + input);
                }
                if (input == null) {
                    break;
                }
                // The user will have all sorts of buttons that send back to the server input
                // strings in the form of 'ACTION:INPUT' where the input is what they typed, if applicable,
                // and action corresponds to what they pressed.
                switch (input.substring(0, input.indexOf(":"))) {
                    case "LOGIN" -> {
                        username = login(input, writer);
                        if (username.contains("SUCCESS")) {
                            username = username.substring(username.indexOf(":") + 2);
                            writer.write("LOGIN SUCCESS: " + username);
                            writer.println();
                            writer.flush();
                            System.out.println("Wrote back to client");
                        } else {
                            username = username.substring(username.indexOf(":") + 2);
                            writer.write("LOGIN FAILED: " + username);
                            writer.println();
                            writer.flush();
                            System.out.println("Wrote back to client");
                            username = null;
                        }
                        serverReturn = null;
                    }
                    case "CREATEUSER" -> {
                        // Let's say the formating would be CREATEUSER:username:password:bio:email
                        // WARNING: I'd probably make a method that gets called that way we can just
                        // individually test each method in the test cases
                        serverReturn = createUser(input, writer);
                        if (serverReturn.contains("SUCCESS")) {
                            writer.write("SUCCESS:"); // Send back the created username
                            writer.println();
                            writer.flush();
                            System.out.println("Wrote back to client: Success");
                        } else if (serverReturn.contains("already exists")) {
                            username = serverReturn.substring(serverReturn.indexOf(":") + 1, serverReturn.indexOf("|"));
                            writer.write("FAILURE:" + username + "Username already exists"); // Send back the created username
                            writer.println();
                            writer.flush();
                            System.out.println("Wrote back to client: Already Exists");
                        } else if (serverReturn.contains("FAILED")) {
                            writer.write("FAILURE:"); // Send back the created username
                            writer.println();
                            writer.flush();
                            System.out.println("Wrote back to client: Failure");
                        } else {
                            writer.write("FAILURE: An exception was thrown");
                            writer.println();
                            writer.flush();
                            System.out.println("Wrote back to client: Exception");
                        }
                        serverReturn = null;
                    }
                    case "GETUSER" -> {
                        // WARNING: I'd probably make a method that gets called that way we can just
                        // individually test each method in the test cases
                        // Format: GETUSER:USERNAME
                        serverReturn = getUser(input, writer);
                        System.out.println( "LINE 125: " + serverReturn);
                        if (serverReturn == null) {
                            writer.write("ERROR: CANNOT BE EMPTY");
                            writer.println();
                            writer.flush();
                            System.out.println("Wrote back to client: Empty Input");
                        } else if (serverReturn.contains("User Data")) {
                            writer.write(serverReturn); // Send back user data
                            writer.println();
                            writer.flush();
                            System.out.println(serverReturn);
                        } else if (serverReturn.contains("could not be found")) {
                            writer.write(serverReturn);
                            writer.println();
                            writer.flush();
                            System.out.println(serverReturn);
                        } else if (serverReturn.contains("Failed to retrieve")) {
                            writer.write("FAILED:Failed to retrieve user");
                            writer.println();
                            writer.flush();
                            System.out.println(serverReturn);
                        } else {
                            writer.write("FAILED:An exception occurred while retrieving user data");
                            writer.println();
                            writer.flush();
                            System.out.println("Exception caught while retrieving user data");
                        }
                        serverReturn = null;
                    }
                    case "DELETEUSER" -> {
                        // WARNING: I'd probably make a method that gets called that way we can just
                        // individually test each method in the test cases
                        // Format: DELETEUSER:USERNAME
                        serverReturn = deleteUser(input, writer);
                        if (serverReturn == null) {
                            writer.write("ERROR: CANNOT BE EMPTY");
                            writer.println();
                            writer.flush();
                            System.out.println("Wrote back to client: Empty Input");
                        } else {
                            writer.write(serverReturn);
                            writer.println();
                            writer.flush();
                        }
                        serverReturn = null;
                    }
                    case "EDIT" -> {
                        // Should only need formatting like EDIT:Bio:email or whatever else, but because username is already
                        // saved in line 50, you can just draw the username from that variable.
                        serverReturn = editUser(input, writer);
                        manager.populateHashMap();
                        if (serverReturn.contains("error")) {
                            writer.write("An error occurred while processing the request.");
                            writer.println();
                            writer.flush();
                            System.out.println("Sent editUser error occurred to client");
                        } else {
                            writer.write(serverReturn);
                            writer.println();
                            writer.flush();
                            System.out.println("Sent editUser result to client");
                        }
                    }
                    case "ADDFRIEND" -> {
                        // WARNING: I'd probably make a method that gets called that way we can just
                        // individually test each method in the test cases
                    }
                    case "UNFRIEND" -> {
                        // WARNING: I'd probably make a method that gets called that way we can just
                        // individually test each method in the test cases
                    }
                    case "BLOCK" -> {
                        // Format: BLOCK:BLOCKER:BLOCKED
                        serverReturn = blockUser(input);
                    }
                    case "UNBLOCK" -> {

                    }
                    case "SENDMESSAGE" -> {

                    }
                    case "LOGOUT" -> {
                        // Do whatever to log out and then exit the while true loop to end the server thread
                        logout = true;
                        break;
                    }
                }
            }
            reader.close();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // I'm just gonna have the login method return a String for the username, everything else can probably return void
    public String login(String input, PrintWriter writer) {
        manager.populateHashMap();
        // Have login input string be "LOGIN:USERNAME:PASSWORD" so if it begins with 'LOGIN',
        // then we know that there's two fields to read for, USERNAME and PASSWORD
        String username = input.substring(input.indexOf(":") + 1);
        username = username.substring(0, username.indexOf(":"));
        String password = input.substring(input.indexOf(":") + 1);
        password = password.substring(password.indexOf(":") + 1);
        // Need to find out how to use authenticator, ask Eric
        // Attempt to authenticate using input username and password
        boolean successfulLogin = authenticator.authenticate(username, password);
        // Write to the client whether the login succeeded or failed.
        // Could be a string like this or the words "true" or "false" and just parseBoolean on the client end
        if (successfulLogin) {
            // The client needs to know the username if it's successful, to know which
            // GUI to show, so we're just going to relay it from the server here
            return "SUCCESS: " + username;
        } else {
            return "FAILED: " + username;
        }
    }

    // NOTE: THIS METHOD WAS WRITTEN BY PRISHA. COMMITTED BY WYATT TO PREVENT GITHUB CONFLICTS
    public String createUser(String input, PrintWriter writer) {
        manager.populateHashMap();
        // Ensure the input format is correct, e.g., "CREATEUSER:USERNAME:PASSWORD:EMAIL:BIO"
        if (!input.startsWith("CREATEUSER:")) {
            writer.write("FAILED:Invalid command format");
            writer.println();
            writer.flush();
        }
        // ACTION:USERNAME:PASSWORD:EMAIL:BIO
        // Extract the username and password from the input string
        String username = input.substring(input.indexOf(":") + 1);
        username = username.substring(0, username.indexOf(":"));
        String password = input.substring(input.indexOf(":") + 1);
        password = password.substring(password.indexOf(":") + 1);
        password = password.substring(0, password.indexOf(":"));
        String email = input.substring(input.indexOf(":") + 1);
        email = email.substring(email.indexOf(":") + 1);
        email = email.substring(email.indexOf(":") + 1);
        email = email.substring(0, email.indexOf(":"));
        String bio = input.substring(input.indexOf(":") + 1);
        bio = bio.substring(bio.indexOf(":") + 1);
        bio = bio.substring(bio.indexOf(":") + 1);
        bio = bio.substring(bio.indexOf(":") + 1);

        // Attempt to create a new user using the username and password
        String userCreated = manager.createUser(username, password, email, bio, null); // Assuming there's a createUser method

        // Respond to the client depending on whether the user creation was successful or not
        if (userCreated.contains("successfully")) {
            return "SUCCESS: " + username;
        }
        if (userCreated.contains("already exists")) {
            return "FAILURE:" + username + "|Username already exists";
        }
        if (userCreated.contains("Failed")) {
            return "FAILED:" + username;
        } else {
            //I'm not sure how to do this last else statement for the final exception.
            return "EXCEPTION:";
        }
    }

    // NOTE: THIS METHOD WAS WRITTEN BY PRISHA. COMMITTED BY WYATT TO PREVENT GITHUB CONFLICTS
    public String getUser(String input, PrintWriter writer) {
        manager.populateHashMap();
        String userReturn;
        String username = input.substring(input.indexOf(":") + 1);
        try {
            // Check if the username is provided
            if (username == null || username.trim().isEmpty()) {
                writer.write("FAILED:Username cannot be empty");
                writer.println();
                writer.flush();
                return null; // Exit the method if the username is invalid
            }

            userReturn = manager.getUser(username);

            if (userReturn.contains("Data")) {
                // If the user data is retrieved successfully, send the response back to the client
                System.out.println("Method return: " + userReturn);
                return userReturn;
            }
            if (userReturn.contains("found")) {
                System.out.println("Method return: " + userReturn);
                return userReturn;
            } else {
                // Handle other errors (e.g., 500 server error)
                System.out.println("Method return: " + userReturn);
                return userReturn;
            }
        } catch (Exception e) {
            // Catch any exceptions, print the stack trace, and send a failure message to the client
            e.printStackTrace();
            return "FAILED:An exception occurred while retrieving user data";
        }
    }

    // NOTE: THIS METHOD WAS WRITTEN BY ANEESH. COMMITTED BY WYATT TO PREVENT GITHUB CONFLICTS
    public String deleteUser(String input, PrintWriter writer) {
        // Update the HashMap to ensure it has the latest data
        manager.populateHashMap();

        // Parse the input to extract the username
        String username = input.substring(input.indexOf(":") + 1).trim();

        if (username.isEmpty()) {
            // If no username is provided in the input, send a failure message to the client
            writer.write("FAILED: Username not specified.");
            writer.println();
            writer.flush();
            return null;
        }

        String deleteResult = manager.deleteUser(username);

        //System.out.println("debug result " + deleteResult);

        return deleteResult;
    }

    // NOTE: THIS METHOD WAS WRITTEN BY ANEESH. COMMITTED BY WYATT TO PREVENT GITHUB CONFLICTS
    public String editUser(String input, PrintWriter writer){

        manager.populateHashMap();

        try {
            // Parse the input to extract necessary fields
            // Example input format: "EDIT:USERNAME:PASSWORD:EMAIL:BIO"

            String[] parts = input.split(":");

            // Ensure we have the correct format (5 expected parts: EDIT, username, password, email, bio)
            if (parts.length < 5) {
                return "Invalid input format. Expected format: EDIT:USERNAME:PASSWORD:EMAIL:BIO";
            }

            String username = parts[1];
            String password = parts[2];
            String email = parts[3];
            String bio = parts[4];
            System.out.println(username);
            System.out.println(email);
            System.out.println(bio);
            // Set friends as null (as requested)

            // Call the editUser method in manager to update the user
            // Input by Eric: call editUser with a null password
            String result = manager.editUser(username, null, email, bio, null);

            // Send the response back to the client

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return "An error occurred while processing the request.";
        }

    }

    // Note: Written by Wyatt
    public String blockUser(String input) {
        // Input will for formatted as BLOCKUSER:BLOCKER:BLOCKED
        return "placeholder";
    }
}
