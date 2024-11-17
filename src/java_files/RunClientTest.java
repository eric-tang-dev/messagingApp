package java_files;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.*;
import java.net.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

import java_files.Client;


public class RunClientTest implements SharedResources {
    @Test
    public void testLogin(){
        manager.flushDatabase();
        try {
            // Connect to the server
            Socket socket = new Socket("localhost", 4242);
            Client client = new Client();
            client.newClient();
            assertTrue(socket.isConnected(), "Failed to connect to the server");
            // Create a test user
            manager.createUser("test", "test", "test", "test", null);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            // Login Test with valid credentials
            writer.write("LOGIN:test:test");  writer.println();   writer.flush();
            String serverReturn = reader.readLine();
            assertTrue(serverReturn.substring(0, serverReturn.indexOf(":")).contains("SUCCESS"), "Login failed");
            // Login Test with invalid credentials
            writer.write("LOGIN:test:wrong");  writer.println();   writer.flush();
            serverReturn = reader.readLine();
            assertTrue(serverReturn.substring(0, serverReturn.indexOf(":")).contains("FAILED"), "Login succeeded with invalid credentials");

            manager.flushDatabase();
        } catch (Exception e) {
            fail("Failed: " + e.getMessage());
        }
    }

    @Test
    public void testCreateUser(){
        manager.flushDatabase();
        try {
            // Connect to the server
            Socket socket = new Socket("localhost", 4242);
            Client client = new Client();
            client.newClient();
            assertTrue(socket.isConnected(), "Failed to connect to the server");
            // Read and write to the server
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            // Create User Test
            writer.write("CREATEUSER:test:test:test:test"); writer.println(); writer.flush();
            String serverResponse = reader.readLine();
            assertTrue(serverResponse.contains("SUCCESS"), "User creation failed");

            manager.flushDatabase();
        } catch (Exception e) {
            fail("Failed: " + e.getMessage());
        }
    }

    @Test
    public void testEdit(){
        manager.flushDatabase();
        try {
            // Connect to the server
            Socket socket = new Socket("localhost", 4242);
            Client client = new Client();
            client.newClient();
            assertTrue(socket.isConnected(), "Failed to connect to the server");
            // Create a test user
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
            assertTrue(serverReturn.contains("successfully"), "Edit failed");

            manager.flushDatabase();
        } catch (Exception e) {
            fail("Failed: " + e.getMessage());
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
            // Create a test user
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
        } catch (Exception e) {
            fail("Failed: " + e.getMessage());
        }
    }

    @Test
    public void testGetUser(){
        manager.flushDatabase();
        try{
            // Connect to the server
            Socket socket = new Socket("localhost", 4242);
            Client client = new Client();
            client.newClient();
            assertTrue(socket.isConnected(), "Failed to connect to the server");
            // Create a test user
            manager.createUser("test", "test", "test", "test", null);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            // Get User Test
            writer.write("GETUSER:test\n");   writer.flush();
            String serverReturn = reader.readLine();
            assertTrue(serverReturn.contains("User Data"), "getUser failed");

            manager.flushDatabase();
        } catch (Exception e) {
            fail("Failed: " + e.getMessage());
        }
    }

    @Test
    public void logout() {
        // Reset the database and populate it for testing
        manager.flushDatabase();
        try {
            // Connect to the server
            Socket socket = new Socket("localhost", 4242);
            Client client = new Client();
            client.newClient();
            assertTrue(socket.isConnected(), "Failed to connect to the server");
            // Create a test user
            manager.createUser("test", "test", "test", "test", null);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            // Logout Test
            writer.write("LOGOUT:test\n"); writer.flush();
            String serverResponse = reader.readLine();
            assertTrue(serverResponse.contains("SUCCESS"), "Logout failed or unexpected response");

            manager.flushDatabase();
        } catch (Exception e) {
            fail("Failed: " + e.getMessage());
        }
    }

    @Test
    public void sendMessage(){
        manager.flushDatabase();
        try {
            // Connect to the server
            Socket socket = new Socket("localhost", 4242);
            Client client = new Client();
            client.newClient();
            assertTrue(socket.isConnected(), "Failed to connect to the server");
            // Create 2 test users
            manager.createUser("test", "test", "test", "test", null);
            manager.createUser("test2", "test2", "test2", "test2", null);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            // Send Message test
            writer.write("SENDMESSAGE:test:test2:testmessage"); writer.println(); writer.flush();
            String serverResponse = reader.readLine();
            assertTrue(serverResponse.contains("SUCCESS"), "Server response should be successful");

            manager.flushDatabase();
        } catch (Exception e) {
            fail("Failed " + e.getMessage());
        }
    }

    @Test
    public void testAddFriend() {
        manager.flushDatabase();
        try {
            // Connect to the server
            Socket socket = new Socket("localhost", 4242);
            Client client = new Client();
            client.newClient();
            assertTrue(socket.isConnected(), "Failed to connect to the server");
            // create 2 test users
            manager.createUser("test", "test", "test", null, null);
            String george= manager.createUser("test2", "test2", "test2", "test2", null);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            // Add Friend Test
            writer.write("ADDFRIEND:test:test2"); writer.println(); writer.flush();
            String serverResponse = reader.readLine();
            assertTrue(serverResponse.contains("SUCCESS"), "Friend was not added successfully.");

            manager.flushDatabase();
        } catch (Exception e) {
            fail("Failed: " + e.getMessage());
        }
    }

    @Test
    public void testUnfriend() {
        manager.flushDatabase();
        try {
            // Connect to the server
            Socket socket = new Socket("localhost", 4242);
            Client client = new Client();
            client.newClient();
            assertTrue(socket.isConnected(), "Failed to connect to the server");
            // Create 2 test users
            manager.createUser("test", "test", "test", "test", null);
            manager.createUser("test2", "test2", "test2", "test2", null);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            // Add Friend Test
            writer.write("ADDFRIEND:test:test2"); writer.println(); writer.flush();
            String serverResponse = reader.readLine();
            assertTrue(serverResponse.contains("SUCCESS"), "Friend was not added successfully.");
            // Un Friend Test
            writer.write("UNFRIEND:test:test2"); writer.println(); writer.flush();
            serverResponse = reader.readLine();
            assertTrue(serverResponse.contains("SUCCESS"), "Friend was not removed successfully.");

            manager.flushDatabase();
        } catch (Exception e) {
            fail("Failed: " + e.getMessage());
        }
    }

    @Test
    public void testBlock(){
        manager.flushDatabase();
        try {
            // Connect to the server
            Socket socket = new Socket("localhost", 4242);
            Client client = new Client();
            client.newClient();
            assertTrue(socket.isConnected(), "Failed to connect to the server");
            // Create 2 test users
            manager.createUser("test", "test", "test", "test", null);
            manager.createUser("test2", "test2", "test2", "test2", null);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            // Block Test
            writer.write("BLOCK:test:test2"); writer.println(); writer.flush();
            String serverResponse = reader.readLine();
            System.out.println(serverResponse);
            assertTrue(serverResponse.contains("SUCCESS"), "Block failed");

            manager.flushDatabase();
        } catch (Exception e) {
            fail("Failed: " + e.getMessage());
        }
    }

    @Test
    public void testUnblock(){
        manager.flushDatabase();
        try {
            // Connect to the server
            Socket socket = new Socket("localhost", 4242);
            Client client = new Client();
            client.newClient();
            assertTrue(socket.isConnected(), "Failed to connect to the server");
            // Create 2 test users
            manager.createUser("test", "test", "test", "test", null);
            manager.createUser("test2", "test2", "test2", "test2", null);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            // Block Test
            writer.write("BLOCK:test:test2"); writer.println(); writer.flush();
            String serverResponse = reader.readLine();
            assertTrue(serverResponse.contains("SUCCESS"), "Block failed");
            // Unblock Test
            writer.write("UNBLOCK:test:test2"); writer.println(); writer.flush();
            serverResponse = reader.readLine();
            assertTrue(serverResponse.contains("SUCCESS"), "Unblock failed");

            manager.flushDatabase();
        } catch (Exception e) {
            fail("Failed: " + e.getMessage());
        }
    }

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
            fail("Failed: " + e.getMessage());
        }
    }

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
        } catch (Exception e) {
            fail("Failed: " + e.getMessage());
        }
    }

    @Test
    public void testNewClientCommand2() {
        // This test case is similar to newClient one as they more or less perform similar actions;
        // newClientCommand just is there for the main method tests created in Client
        int portNum = 4242;
        Client client = new Client();
        manager.flushDatabase();
        try {
            client.newClient();
            Socket socket = new Socket("localhost", portNum);
            assertTrue(socket.isConnected(), "Client failed to connect to server");
            // Now to test other commands that this method tests
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            // Now to test user input
            writer.println("CREATEUSER:test:test:test:test\n"); writer.flush();
            String responseCreateUser = reader.readLine();
            assertNotNull(responseCreateUser, "CREATEUSER command did not receive a response");
            assertTrue(responseCreateUser.contains("SUCCESS"), "CREATEUSER command failed: " + responseCreateUser);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
