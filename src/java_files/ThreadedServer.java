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
                    }
                    case "GETUSER" -> {
                        // WARNING: I'd probably make a method that gets called that way we can just
                        // individually test each method in the test cases
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
}
