package java_files;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;

public class Client {
    // Hardcoding in the starting port number of the server
    static int portNum = 4242;
    String serverReturn;
    String clientInput;
    public static void main(String[] args) {
        Client client1 = new Client();
        //Client client2 = new Client();
        client1.newClient();
        //client2.newClient();
    }
    public void newClient() {
        String serverReturn;
        try {
            // Try creating a new socket connection to that server
            Socket socket = new Socket("localhost", portNum);
            System.out.println("Connected to server on port num " + portNum);
            // Increment the port number the next time a client object is created
            // and tries connecting to server
            portNum++;
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            // LOTS OF GUI DISPLAY AND INPUT WILL BE SHOWN HERE. EACH DIALOG WILL WRITE WHAT
            // THE RETURN IS TO THE SERVER. THIS IS MOSTLY PHASE 3 SO I'M WAITING FOR NOW
            writer.write("LOGIN:Wyatt:wpassword");
            writer.println();
            writer.flush();
            System.out.println("Wrote to server");
            serverReturn = reader.readLine();
            // Retrieving username from the login return
            String username = serverReturn.substring(serverReturn.indexOf(":") + 1);
            System.out.println(serverReturn);// Instead of printing call showUserProfile from mainGUI
            if (serverReturn.substring(0, serverReturn.indexOf(":")).equals("SUCCESS")) {
                // Here is where we would display the GUI
                Application.launch(LoginGUI.class);
            } else {
                // Here we could display GUI for failing login
            }
            System.out.println("Returned from server: " + serverReturn);
            writer.write("LOGOUT:");
            writer.println();
            writer.flush();
            System.out.println("Okay to log out");
            reader.close();
            writer.close();
        } catch (IOException e) {
            // MAY NEED TO DO SOME TESTING TO PREVENT EXCEPTIONS
            System.out.println("Failed to connect or other exception");
            throw new RuntimeException(e);
        }
    }
}
