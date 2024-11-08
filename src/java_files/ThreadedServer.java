package java_files;
import java.io.*;
import java.net.*;

public class ThreadedServer extends Thread {
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
                // Wait for a client to attempt to connect on a port number
                ServerSocket serverSocket = new ServerSocket(portNum);
                client = serverSocket.accept();
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
        String input;
        String serverReturn;
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientCopy.getInputStream()));
            PrintWriter writer = new PrintWriter(clientCopy.getOutputStream());
            while (true) {
                input = reader.readLine();
                // The user will have all sorts of buttons that send back to the server input
                // strings in the form of 'ACTION:INPUT' where the input is what they typed, if applicable,
                // and action corresponds to what they pressed.
                switch (input.substring(0, input.indexOf(":"))) {
                    case "LOGIN" -> {
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
                            writer.write("SUCCESS");
                            writer.println();
                            writer.flush();
                        } else {
                            writer.write("FAILED");
                            writer.println();
                            writer.flush();
                        }
                    }
                    case "CREATEUSER" -> {

                    }
                    case "GETUSER" -> {

                    }
                    case "DELETEUSER" -> {

                    }
                    case "EDIT" -> {

                    }
                    case "SENDMESSAGE" -> {

                    }
                    case "LOGOUT" -> {
                        // Do whatever to log out
                        break;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
