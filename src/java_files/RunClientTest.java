package java_files;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.*;
import java.net.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java_files.Client;


public class RunClientTest implements SharedResources {
    @Test
    public void testLogin(){
        manager.flushDatabase();
        try {
            Socket socket = new Socket("localhost", 4242);
            Client client = new Client();
            client.newClient();
            assertTrue(socket.isConnected(), "Failed to connect to the server");
            manager.createUser("test", "test", "test", "test", null);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            writer.write("LOGIN:test:test");  writer.println();   writer.flush();
            String serverReturn = reader.readLine();
            assertTrue(serverReturn.substring(0, serverReturn.indexOf(":")).contains("SUCCESS"), "Login failed");
            manager.flushDatabase();
        } catch (IOException e) {
            fail("Failed to connect to the server: " + e.getMessage());
        }
    }

    @Test
    public void testGetUser(){
        manager.flushDatabase();
        try{
            Socket socket = new Socket("localhost", 4242);
            Client client = new Client();
            client.newClient();
            assertTrue(socket.isConnected(), "Failed to connect to the server");
            manager.createUser("test", "test", "test", "test", null);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            // here is where we will test the retreival of user information when method is called to
            writer.write("GETUSER:test\n");   writer.flush();
            String serverReturn = reader.readLine();
            assertTrue(serverReturn.contains("User Data"), "getUser failed");
            manager.flushDatabase();
        } catch (IOException e) {
            fail("Failed " + e.getMessage());
        }
    }

    @Test
    public void deleteUser(){
        manager.flushDatabase();
        try {
            // Connect to the server
            Socket socket = new Socket("localhost", 4242);
            Client client = new Client();
            client.newClient();
            assertTrue(socket.isConnected(), "Failed to connect to the server");
            // Create a user
            manager.createUser("test", "test", "test", "test", null);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            // login
            writer.write("LOGIN:test:test");  writer.println();   writer.flush();
            String serverReturn = reader.readLine();
            assertTrue(serverReturn.substring(0, serverReturn.indexOf(":")).contains("SUCCESS"), "Login failed");
            // Delete Test
            writer.write("DELETEUSER:test");  writer.println();   writer.flush();
            serverReturn = reader.readLine();
            assertTrue(serverReturn.contains("successfully"), "Delete failed");
            manager.flushDatabase();
        } catch (IOException e) {
            fail("Failed " + e.getMessage());
        }
    }
    // had to debug since it was attempting to read the wrong input
    @Test
    public void testEdit(){
        manager.flushDatabase();
        try {
            // Connect to the server
            Socket socket = new Socket("localhost", 4242);
            Client client = new Client();
            client.newClient();
            assertTrue(socket.isConnected(), "Failed to connect to the server");
            // Create a user
            manager.createUser("test", "test", "test", "test", null);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            // Login
            writer.write("LOGIN:test:test");  writer.println();   writer.flush();
            String serverReturn = reader.readLine();
            assertTrue(serverReturn.substring(0, serverReturn.indexOf(":")).contains("SUCCESS"), "Login failed");
            // Edit Test
            writer.write("EDIT:test:test:new email:new bio");  writer.println();   writer.flush();
            serverReturn = reader.readLine();
            assertTrue(!serverReturn.contains("error"), "Edit failed");
            manager.flushDatabase();
        } catch (IOException e) {
            fail("Failed " + e.getMessage());
        }
    }

    @Test
    public void logout() {
        // Reset the database and populate it for testing
        manager.flushDatabase();

        try (Socket socket = new Socket("localhost", 4242)) {
            Client client = new Client();
            client.newClient();
            assertTrue(socket.isConnected(), "Failed to connect to the server");
            manager.createUser("test", "test", "test", "test", null);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            writer.write("LOGOUT:test\n"); writer.flush();
            String serverResponse = reader.readLine();
            assertEquals(null, serverResponse, "Logout failed or unexpected response");
            manager.flushDatabase();
        } catch (IOException e) {
            fail("Failed " + e.getMessage());
        }
    }
    // new test case added
    @Test
    public void sendMessage(){
        manager.flushDatabase();
        // This test case impiments the messageManager method, sendMessage which just verifies if the message was sent
        // also visible on the server side to see that a message was sent.
        try {
            Socket socket = new Socket("localhost", 4242);
            Client client = new Client();
            client.newClient();
            assertTrue(socket.isConnected(), "Failed to connect to the server");
            //if not failed, next is creating the users
            manager.createUser("test", "test", "test", "test", null);
            manager.createUser("mewtwo", "mewtwo", "mewtwo", "mewtwo", null);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            //send message test
            String input = "SENDMESSAGE:test:mewtwo:howdy partner";
            writer.write(input); writer.println(); writer.flush();
            String serverResponse = reader.readLine();
            assertTrue(serverResponse.contains("Message sent successfully."), "Null or Error occurred when testing (unexpected response).");
            manager.flushDatabase();
        } catch (IOException e) {
            fail("Failed " + e.getMessage());
        }
    }
    // new test case added
    @Test
    public void testThreadedServerConstructor() {
        try {
            // this constructor on threaded server was to just create the port number and host, just to show it works
            Socket mockSocket = new Socket("localhost", 4242);
            ThreadedServer server = new ThreadedServer(mockSocket);
            assertNotNull(server, "ThreadedServer instance should not be null");
            assertNotNull(server.client, "Client socket should not be null");
            assertEquals(mockSocket, server.client, "Client socket in ThreadedServer should match the provided socket");
            manager.flushDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // new test case added
    @Test
    public void testNewClientConnection() {
        int portNum = 4242;
        Client client = new Client();
        // basic server connection to show our client works
        try {
            client.newClient();
            Socket socket = new Socket("localhost", portNum);
            assertTrue(socket.isConnected(), "Client failed to connect to server");
            socket.close();
        } catch (IOException e) {
            fail("Connection failed or another exception occurred: " + e.getMessage());
        }
    }
}
