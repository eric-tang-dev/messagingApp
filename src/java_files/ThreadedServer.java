package java_files;
import java.io.*;
import java.net.*;

public class ThreadedServer extends Thread implements SharedResources {
    Socket client;
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
        int portNum = 4242;
        while (true) {
            try {
                System.out.println("Waiting for client to connect on port num " + portNum);
                // Wait for a client to attempt to connect on a port number
                ServerSocket serverSocket = new ServerSocket(portNum);
                client = serverSocket.accept();
                System.out.println("Client connected on " + portNum);
                // Increment socket number so when it loops, a new port number is used
                portNum++;
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
                    }
                    case "CREATEUSER" -> {
                        // Let's say the formating would be CREATEUSER:username:password (Note: Feel free to change
                        // this if you're implementing it, but that's probably how I'd do it)
                        // WARNING: I'd probably make a method that gets called that way we can just
                        // individually test each method in the test cases
                        createUser(input, writer);
                    }
                    case "GETUSER" -> {
                        // WARNING: I'd probably make a method that gets called that way we can just
                        // individually test each method in the test cases
                        getUser(input, writer);
                    }
                    case "DELETEUSER" -> {
                        // WARNING: I'd probably make a method that gets called that way we can just
                        // individually test each method in the test cases
                    }
                    case "EDIT" -> {
                        // Should only need formatting like EDIT:Bio:email or whatever else, but because username is already
                        // saved in line 50, you can just draw the username from that variable.
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

                    }
                    case "SENDMESSAGE" -> {

                    }
                    case "LOGOUT:" -> {
                        // Do whatever to log out and then exit the while true loop to end the server thread
                        logout = true;
                        break;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    // I'm just gonna have the login method return a String for the username, everything else can probably return void
    public String login(String input, PrintWriter writer) {
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
            writer.write("SUCCESS:" + username);
            writer.println();
            writer.flush();
            System.out.println("Wrote back to client");
            return username;
        } else {
            writer.write("FAILED");
            writer.println();
            writer.flush();
            System.out.println("Wrote back to client");
            return null;
        }
    }

    public void createUser(String input, PrintWriter writer) {
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
        UserManager manager = new UserManager();
        String userCreated = manager.createUser(username, password, email, bio, null); // Assuming there's a createUser method

        // Respond to the client depending on whether the user creation was successful or not
        if (userCreated.contains("successfully")) {
            writer.write("SUCCESS:" + username); // Send back the created username
            writer.println();
            writer.flush();
            System.out.println("Wrote back to client: Success");

        }
        if (userCreated.contains("already exists")) {
            writer.write("FAILURE:" + username + "Username already exists"); // Send back the created username
            writer.println();
            writer.flush();
            System.out.println("Wrote back to client: Already Exists");

        }
        if (userCreated.contains("Failed")) {
            writer.write("FAILURE:" + username); // Send back the created username
            writer.println();
            writer.flush();
            System.out.println(userCreated);

        } else {
            //I'm not sure how to do this last else statement for the final exception.
            writer.write("FAILURE" + username + "An exception was thrown");
            writer.println();
            writer.flush();
            System.out.println("Wrote back to client: Exception");

        }
    }

    public void getUser(String input, PrintWriter writer) {
        UserManager manager = new UserManager();
        String userReturn;
        String username = input.substring(input.indexOf(":") + 1);
        try {
            // Check if the username is provided
            if (username == null || username.trim().isEmpty()) {
                writer.write("FAILED:Username cannot be empty");
                writer.println();
                writer.flush();
                return; // Exit the method if the username is invalid
            }

            userReturn = manager.getUser(username);

            if (userReturn.contains("Data")) {
                // If the user data is retrieved successfully, send the response back to the client
                writer.write("SUCCESS:" + userReturn); // Send back user data
                writer.println();
                writer.flush();
                System.out.println(userReturn);
            } if (userReturn.contains("found")) {
                writer.write("FAILED:User " + username + " could not be found.");
                writer.println();
                writer.flush();
                System.out.println("User not found: " + username);
            } else {
                // Handle other errors (e.g., 500 server error)
                writer.write("FAILED:Failed to retrieve user. Status code: " + userReturn);
                writer.println();
                writer.flush();
                System.out.println(userReturn);
            }
        } catch (Exception e) {
            // Catch any exceptions, print the stack trace, and send a failure message to the client
            e.printStackTrace();
            writer.write("FAILED:An exception occurred while retrieving user data");
            writer.println();
            writer.flush();
            System.out.println("Exception caught while retrieving user data");
        }
    }
}
