package java_files;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;

public class Client extends Application implements SharedResources {

    // Hardcoding in the starting port number of the server
    static int portNum = 4242;
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        Client client1 = new Client();

        showMain(client1);
    }

    public void showMain(Client client) {
        MainGUI mainGUI = new MainGUI(client);
        try {
            mainGUI.start(primaryStage); // Reuse the primary stage
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
//        System.out.println("here");
//        Client client1 = new Client();
//        Client client2 = new Client();
//        Client client3 = new Client();

        /**
         *  Here are a few "test" cases that we've laid out to attempt to simulate race conditions.
         *  Feel free to use these to test for race conditions. Outputs will be printed to terminal.
         *  Look for the long lines that return getUser from the terminal!
         *  Make sure both server and database are running!
         */

        // Test 1: Creating new users from multiple clients
//        client1.newClientCommand("CREATEUSER:test1:password1:bio:email");
//        client1.newClientCommand("GETUSER:test1");
//        client2.newClientCommand("CREATEUSER:test2:password1:bio:email");
//        client1.newClientCommand("GETUSER:test2");
//        client3.newClientCommand("GETUSER:test3"); // Should show that test3 has not been created yet
//        client2.newClientCommand("CREATEUSER:test3:password1:bio:email");
//        client3.newClientCommand("GETUSER:test3");

        // Test 2: Blocking users and unblocking them.
//        client1.newClientCommand("UNBLOCK:test1:test2");
//        client2.newClientCommand("BLOCK:test1:test2");
//        client1.newClientCommand("BLOCK:test1:test3");
//        client3.newClientCommand("BLOCK:test2:test3");
//        client1.newClientCommand("BLOCK:test3:test2");
//        client2.newClientCommand("UNBLOCK:test1:test3");

        // Test3: Sending messages between users
//        client1.newClientCommand("SENDMESSAGE:test1:test2:Message one");
//        client2.newClientCommand("GETUSER:test1");
//        client1.newClientCommand("GETUSER:test2");
//        client2.newClientCommand("SENDMESSAGE:test2:test1:Message two");
//        client1.newClientCommand("GETUSER:test1");
//        client1.newClientCommand("GETUSER:test2");
//        client1.newClientCommand("SENDMESSAGE:test1:test2:Message three");
//        client1.newClientCommand("GETUSER:test1");
//        client1.newClientCommand("GETUSER:test2");
//        client2.newClientCommand("SENDMESSAGE:test2:test1:Message Four");
//        client3.newClientCommand("SENDMESSAGE:test2:test3:Secret Message!");
//        client1.newClientCommand("GETUSER:test3");
//        client1.newClientCommand("GETUSER:test2");
//        client1.newClientCommand("GETUSER:test1");
//        client1.newClientCommand("GETUSER:test2");
//        client2.newClientCommand("SENDMESSAGE:test1:test2:Message Five");
//        client1.newClientCommand("GETUSER:test1");
//        client1.newClientCommand("GETUSER:test2");
//        client2.newClientCommand("SENDMESSAGE:test2:test1:Message Six");
//        client1.newClientCommand("GETUSER:test1");
//        client1.newClientCommand("GETUSER:test2");

        // Test 4: Friending users
//        client1.newClientCommand("UNFRIEND:test1:test2");
//        client2.newClientCommand("UNFRIEND:test2:test1");
//        client3.newClientCommand("ADDFRIEND:test2:test1");
//        client2.newClientCommand("ADDFRIEND:test1:test3");
//        client1.newClientCommand("ADDFRIEND:test3:test2");
//        client2.newClientCommand("ADDFRIEND:test2:test3");
//        client3.newClientCommand("UNFRIEND:test3:test2");
//        client3.newClientCommand("UNFRIEND:test1:test2"); // Shouldn't be friends yet, so it won't find test2 in the friends list

        // Test 5: Editing profiles
//        client2.newClientCommand("EDIT:test1:password1:Bio:Email");
//        client3.newClientCommand("GETUSER:test1");
//        client1.newClientCommand("EDIT:test1:password1:Bio2:Email2");
//        client3.newClientCommand("GETUSER:test1");
//        client2.newClientCommand("EDIT:test3:password1:test3Bio:test3Email");
//        client3.newClientCommand("GETUSER:test3");
//        client2.newClientCommand("EDIT:test1:password1:Bio3:Email3");
//        client3.newClientCommand("GETUSER:test1");
//        client1.newClientCommand("EDIT:test1:password1:Bio4:Email4");
//        client3.newClientCommand("GETUSER:test1");
//        client2.newClientCommand("EDIT:test1:password1:Bio5:Email5");
//        client3.newClientCommand("GETUSER:test1");



        // These 'logout' commands are required, as they tell the server thread to terminate. It will still function without
        // these methods, but the server threads will forever be waiting for inputs instead of terminating.
//        client1.newClientCommand("LOGOUT:");
//        client2.newClientCommand("LOGOUT:");
//        client3.newClientCommand("LOGOUT:");
//        MainGUI mainGUI = new MainGUI();
//        mainGUI.showLogin();
    }

    public void newClient() {
        String serverReturn;
        try {
            // Try creating a new socket connection to that server
            Socket socket = new Socket("localhost", portNum);
            System.out.println("Connected to server on port num " + portNum);
            // Increment the port number the next time a client object is created
            // and tries connecting to server
//            portNum++;
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            // LOTS OF GUI DISPLAY AND INPUT WILL BE SHOWN HERE. EACH DIALOG WILL WRITE WHAT
            // THE RETURN IS TO THE SERVER. THIS IS MOSTLY PHASE 3 SO I'M WAITING FOR NOW

            // Login Test
            writer.write("LOGIN:Wyatt:wpassword");
            writer.println();
            writer.flush();
            System.out.println("Wrote to server");
            serverReturn = reader.readLine();
            System.out.println("Returned from server: " + serverReturn);
            // Retrieving username from the login return
            // Instead of printing call showUserProfile from mainGUI
//            System.out.println("Username: " + username);
//            if (serverReturn.substring(0, serverReturn.indexOf(":")).contains("SUCCESS")) {
//                // Here is where we would display the GUI
//            } else {
//                // Here we could display GUI for failing login
//                System.out.println("Failed Login GUI Display");
//            }


            // Edit Test
//            writer.write("EDIT:Wyatt:wpassword:2nd New bio descript:2nd new email descript");
//            writer.println();
//            writer.flush();
//            System.out.println("Wrote to server");
//            serverReturn = reader.readLine();
            // Retrieving username from the login return
//            username = serverReturn.substring(serverReturn.indexOf(":") + 1);
//            System.out.println(serverReturn);// Instead of printing call showUserProfile from mainGUI
//            System.out.println("Username: " + username);

            // Delete Test
//            writer.write("DELETEUSER:Wyatt:wpassword");
//            writer.println();
//            writer.flush();
//            System.out.println("Wrote to server");
//            serverReturn = reader.readLine();
//            System.out.println(serverReturn);
//            serverReturn = null;

            // GetUser Test
//            writer.write("SENDMESSAGE:Wyatt:test2:Hey what's up?");
//            writer.println();
//            writer.flush();
//            System.out.println("Wrote to server");
//            serverReturn = reader.readLine();
//            System.out.println(serverReturn);
//            serverReturn = null;


            // Logout Test
//            writer.write("LOGOUT:");
//            writer.println();
//            writer.flush();
//            System.out.println("Okay to log out");
//            reader.close();
//            writer.close();


            socket.close();
        } catch (IOException e) {
            // MAY NEED TO DO SOME TESTING TO PREVENT EXCEPTIONS
            System.out.println("Failed to connect or other exception");
            throw new RuntimeException(e);
        }
    }

    public String newClientCommand(String input) {
        String serverReturn;
        try {
            // Try creating a new socket connection to that server
            Socket socket = new Socket("localhost", portNum);
            System.out.println("Connected to server on port num " + portNum);
            // Increment the port number the next time a client object is created
            // and tries connecting to server
//            portNum++;
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            // LOTS OF GUI DISPLAY AND INPUT WILL BE SHOWN HERE. EACH DIALOG WILL WRITE WHAT
            // THE RETURN IS TO THE SERVER. THIS IS MOSTLY PHASE 3 SO I'M WAITING FOR NOW

            //Login Test
            writer.write(input);
            writer.println();
            writer.flush();
            System.out.println("Wrote to server");
            serverReturn = reader.readLine();
            System.out.println("Returned from server: " + serverReturn);
            return serverReturn;
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
