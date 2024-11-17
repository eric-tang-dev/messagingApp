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
            // Create a user
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
            // Create a user
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
            // Create a user
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
        // This test case impiments the messageManager method, sendMessage which just verifies if the message was sent
        // also visible on the server side to see that a message was sent.
        try {
            Socket socket = new Socket("localhost", 4242);
            Client client = new Client();
            client.newClient();
            assertTrue(socket.isConnected(), "Failed to connect to the server");
            // If not failed, next is creating the users
            manager.createUser("test", "test", "test", "test", null);
            manager.createUser("mewtwo", "mewtwo", "mewtwo", "mewtwo", null);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            // Send Message test
            String input = "SENDMESSAGE:test:mewtwo:howdy partner";
            writer.write(input); writer.println(); writer.flush();
            String serverResponse = reader.readLine();
            assertTrue(serverResponse.contains("successfully"), "Server response should be successful");

            manager.flushDatabase();
        } catch (Exception e) {
            fail("Failed " + e.getMessage());
        }
    }

    @Test
    public void testAddFriend() {
        // flush the database first
        manager.flushDatabase();

        try {
            Socket socket = new Socket("localhost", 4242);
            Client client = new Client();
            client.newClient();
            assertTrue(socket.isConnected(), "Failed to connect to the server");

            // create users to test
            manager.createUser("Eric", "test", null, null, null);
            String george= manager.createUser("George", "test", null, null, null);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());

            // send request to server
            String input = "ADDFRIEND:Eric:George";
            writer.write(input); writer.println(); writer.flush();
            String serverResponse = reader.readLine();

            System.out.println(serverResponse);
            assertFalse(serverResponse.contains("Cannot"), "Cannot find user George.");
            assertFalse(serverResponse.contains("found"), "Cannot find user Eric.");
            assertFalse(serverResponse.contains("Failed"), "Server could not fulfill the request.");
            assertTrue(serverResponse.contains("successfully"), "Friend was not added successfully.");

            // flush the database
            manager.flushDatabase();
        } catch (Exception e) {
            fail("Failed: " + e.getMessage());
        }
    }

    @Test
    public void testUnfriend() {
        // flush the database first
        manager.flushDatabase();

        try {
            Socket socket = new Socket("localhost", 4242);
            Client client = new Client();
            client.newClient();
            assertTrue(socket.isConnected(), "Failed to connect to the server");

            // create users to test
            manager.createUser("Eric", "test", null, null, null);
            manager.createUser("George", "test", null, null, null);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());

            // send request to server
            String input = "ADDFRIEND:Eric:George";
            writer.write(input); writer.println(); writer.flush();
            String serverResponse = reader.readLine();
            System.out.println(serverResponse);
            assertTrue(serverResponse.contains("successfully"), "Friend was not added successfully.");

            // now try to unfriend
            String input2 = "UNFRIEND:Eric:George";
            writer.write(input2); writer.println(); writer.flush();
            String serverResponse2 = reader.readLine();
            assertFalse(serverResponse.contains("Cannot"), "Cannot find user George.");
            assertFalse(serverResponse.contains("found"), "Cannot find user Eric.");
            assertFalse(serverResponse.contains("Failed"), "Server could not fulfill the request.");
            assertTrue(serverResponse.contains("successfully"), "Friend was not removed successfully.");

            // flush the database
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
            // If not failed, next is creating the users
            manager.createUser("test", "test", "test", "test", null);
            manager.createUser("mewtwo", "mewtwo", "mewtwo", "mewtwo", null);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            // Block Test
            writer.write("BLOCK:test:mewtwo"); writer.println(); writer.flush();
            String serverResponse = reader.readLine();
            System.out.println(serverResponse);
            assertTrue(serverResponse.contains("success"), "Block failed");

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
            // If not failed, next is creating the users
            manager.createUser("test", "test", "test", "test", null);
            manager.createUser("mewtwo", "mewtwo", "mewtwo", "mewtwo", null);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            // Block Test
            writer.write("BLOCK:test:mewtwo"); writer.println(); writer.flush();
            String serverResponse = reader.readLine();
            // Unblock Test
            writer.write("UNBLOCK:test:mewtwo"); writer.println(); writer.flush();
            serverResponse = reader.readLine();
            assertTrue(serverResponse.contains("success"), "Unblock failed");

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
}