package java_files;
import javafx.application.Application;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
        System.out.println("Line 55");
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
                try {
                    input = reader.readLine();
                    System.out.println("Input read: " + input);
                } catch (IOException e) {
                    try {
                        sleep(1000);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    System.out.println("ReLooping");
                    continue;
                }
                if (input == null) {
                    break;
                }
                // The user will have all sorts of buttons that send back to the server input
                // strings in the form of 'ACTION:INPUT' where the input is what they typed, if applicable,
                // and action corresponds to what they pressed.
                switch (input.substring(0, input.indexOf(":"))) {
                    case "POPULATE" -> {
                        manager.populateHashMap();
                        writer.write("Populated HashMap");
                        writer.println();
                        writer.flush();
                    }
                    case "WRITEHASHMAP" -> {
                        manager.writeHashMapToFile();
                        writer.write("HashMap Written to File");
                        writer.println();
                        writer.flush();
                    }
                    case "LOGIN" -> {
                        // Format: LOGIN:USERNAME:PASSWORD
                        serverReturn = login(input, writer);
                        if (serverReturn.contains("SUCCESS")) {
                            writer.write(serverReturn);
                            writer.println();
                            writer.flush();
                        } else if (serverReturn.contains("FAILED")) {
                            writer.write(serverReturn);
                            writer.println();
                            writer.flush();
                            username = null;
                        } else if (serverReturn.contains("Invalid Input")) {
                            writer.write("Failed: " + serverReturn);
                            writer.println();
                            writer.flush();
                        } else {
                            writer.write("FAILED: An exception occurred while login user");
                            writer.println();
                            writer.flush();
                        }
                        serverReturn = null;
                    }
                    case "CREATEUSER" -> {
                        // Format: CREATEUSER:USERNAME:PASSWORD:BIO:EMAIL
                        serverReturn = createUser(input, writer);
                        if (serverReturn.contains("successfully")) {
                            writer.write("SUCCESS: " + serverReturn);
                            writer.println();
                            writer.flush();
                        } else if (serverReturn.contains("already exists")) {
                            writer.write("FAILED: " + serverReturn);
                            writer.println();
                            writer.flush();
                        } else if (serverReturn.contains("Failed to create")) {
                            writer.write("FAILED: " + serverReturn);
                            writer.println();
                            writer.flush();
                        } else if (serverReturn.contains("Invalid input")) {
                            writer.write("FAILED: " + serverReturn);
                            writer.println();
                            writer.flush();
                        } else {
                            writer.write("FAILED: An exception occurred while creating user");
                            writer.println();
                            writer.flush();
                        }
                        serverReturn = null;
                    }
                    case "GETUSER" -> {
                        // Format: GETUSER:USERNAME
                        serverReturn = getUser(input, writer);
                        if (serverReturn.contains("User Data")) { // user found
                            writer.write("SUCCESS: " + serverReturn); // Send back user data
                            writer.println();
                            writer.flush();
                        } else if (serverReturn.contains("could not be found")) {
                            writer.write("FAILED: " + serverReturn);
                            writer.println();
                            writer.flush();
                        } else if (serverReturn.contains("Failed to retrieve")) {
                            writer.write("FAILED :" + serverReturn);
                            writer.println();
                            writer.flush();
                        } else if (serverReturn.contains("Invalid input")) {
                            writer.write("FAILED: " + serverReturn);
                            writer.println();
                            writer.flush();
                        } else {
                            writer.write("FAILED: An exception occurred while retrieving user data");
                            writer.println();
                            writer.flush();
                        }
                        serverReturn = null;
                    }
                    case "DELETEUSER" -> {
                        // Format: DELETEUSER:USERNAME
                        serverReturn = deleteUser(input, writer);
                        if (serverReturn.contains("successfully deleted")) {
                            writer.write("SUCCESS: " + serverReturn);
                            writer.println();
                            writer.flush();
                        } else if (serverReturn.contains("could not be found")) {
                            writer.write("FAILED: " + serverReturn);
                            writer.println();
                            writer.flush();
                        } else if (serverReturn.contains("Failed to delete user")) {
                            writer.write("FAILED: " + serverReturn);
                            writer.println();
                            writer.flush();
                        } else if (serverReturn.contains("Invalid input")) {
                            writer.write("FAILED: " + serverReturn);
                            writer.println();
                            writer.flush();
                        } else {
                            writer.write("FAILED: An exception occurred while deleting user");
                            writer.println();
                            writer.flush();
                        }
                    }
                    case "EDIT" -> {
                        // Format: EDIT:USERNAME:PASSWORD:EMAIL:BIO
                        serverReturn = editUser(input, writer);
                        if (serverReturn.contains("successfully")) {
                            writer.write("SUCCESS: " + serverReturn);
                            writer.println();
                            writer.flush();
                        } else if (serverReturn.contains("Invalid input")) {
                            writer.write("FAILED: " + serverReturn);
                            writer.println();
                            writer.flush();
                        } else if (serverReturn.contains("could not be found")) {
                            writer.write("FAILED: " + serverReturn);
                            writer.println();
                            writer.flush();
                        } else if (serverReturn.contains("Failed to update")) {
                            writer.write("FAILED: " + serverReturn);
                            writer.println();
                            writer.flush();
                        } else {
                            writer.write("FAILED: An exception occurred while editing user");
                            writer.println();
                            writer.flush();
                        }
                    }
                    case "ADDFRIEND" -> {
                        // Format: ADDFRIEND:USER:FRIEND
                        serverReturn = addFriend(input);
                        if (serverReturn.contains("Invalid Input")) {
                            writer.write("FAILED: " + serverReturn);
                            writer.println();
                            writer.flush();
                        } else if (serverReturn.contains("is already a friend")) {
                            writer.write("FAILED: " + serverReturn);
                            writer.println();
                            writer.flush();
                        } else if (serverReturn.contains("Cannot find User")) {
                            writer.write("FAILED: " + serverReturn);
                            writer.println();
                            writer.flush();
                        } else if (serverReturn.contains("successfully")) {
                            writer.write("SUCCESS: " + serverReturn);
                            writer.println();
                            writer.flush();
                        } else if (serverReturn.contains("Failed")) {
                            writer.write("FAILED: " + serverReturn);
                            writer.println();
                            writer.flush();
                        } else if (serverReturn.contains("could not be found")) {
                            writer.write("FAILED: " + serverReturn);
                            writer.println();
                            writer.flush();
                        } else {
                            writer.write("FAILED: An exception occurred while adding friend");
                            writer.println();
                            writer.flush();
                        }
                    }
                    case "UNFRIEND" -> {
                        // Format: UNFRIEND:USER:FRIEND
                        serverReturn = unfriend(input);
                        if (serverReturn.contains("Invalid Input")) {
                            writer.write("FAILED: " + serverReturn);
                            writer.println();
                            writer.flush();
                        } else if (serverReturn.contains("is already unfriended")) {
                            writer.write("FAILED: " + serverReturn);
                            writer.println();
                            writer.flush();
                        } else if (serverReturn.contains("Cannot find User")) {
                            writer.write("FAILED: " + serverReturn);
                            writer.println();
                            writer.flush();
                        } else if (serverReturn.contains("successfully")) {
                            writer.write("SUCCESS: " + serverReturn);
                            writer.println();
                            writer.flush();
                        } else if (serverReturn.contains("could not be found")) {
                            writer.write("FAILED: " + serverReturn);
                            writer.println();
                            writer.flush();
                        } else {
                            writer.write("FAILED: An exception occurred while unfriending user");
                            writer.println();
                            writer.flush();
                        }
                    }
                    case "BLOCK" -> {
                        // Format: BLOCK:BLOCKER:BLOCKED
                        synchronized(manager) {
                            String[] split = input.split(":");
                            if (checkIfFriend(split[1], split[2])) {
                                unfriend(input);
                                System.out.println("Unfriended " + split[2]);
                            }
                            serverReturn = blockUser(input);
                        }
                        if (serverReturn.contains("Invalid Input")) {
                            writer.write("FAILED: " + serverReturn);
                            writer.println();
                            writer.flush();
                        } else if (serverReturn.contains("Cannot block yourself")) {
                            writer.write("FAILED: " + serverReturn);
                            writer.println();
                            writer.flush();
                        } else if (serverReturn.contains("does not exist")) {
                            writer.write("FAILED: " + serverReturn);
                            writer.println();
                            writer.flush();
                        } else if (serverReturn.contains("already blocked")) {
                            writer.write("FAILED: " + serverReturn);
                            writer.println();
                            writer.flush();
                        } else if (serverReturn.contains("successfully blocked")) {
                            writer.write("SUCCESS: " + serverReturn);
                            writer.println();
                            writer.flush();
                        } else {
                            writer.write("FAILED: An exception occurred while blocking user");
                            writer.println();
                            writer.flush();
                        }
                    }
                    case "UNBLOCK" -> {
                        // Format: UNBLOCK:BLOCKER:UNBLOCKED
                        synchronized (manager) {
                            serverReturn = unblockUser(input);
                        }
                        if (serverReturn.contains("Invalid Input")) {
                            writer.write("FAILED: " + serverReturn);
                            writer.println();
                            writer.flush();
                        } else if (serverReturn.contains("Cannot unblock yourself")) {
                            writer.write("FAILED: " + serverReturn);
                            writer.println();
                            writer.flush();
                        } else if (serverReturn.contains("does not exist")) {
                            writer.write("FAILED: " + serverReturn);
                            writer.println();
                            writer.flush();
                        } else if (serverReturn.contains("not currently blocked")) {
                            writer.write("FAILED: " + serverReturn);
                            writer.println();
                            writer.flush();
                        } else if (serverReturn.contains("successfully unblocked")) {
                            writer.write("SUCCESS: " + serverReturn);
                            writer.println();
                            writer.flush();
                        } else {
                            writer.write("FAILED: An exception occurred while unblocking user");
                            writer.println();
                            writer.flush();
                        }
                    }
                    case "SENDMESSAGE" -> {
                        // Format: SENDMESSAGE:SENDER:RECEIVER:MESSAGE
                        // Conditions: Both users must have each other friended and cannot be blocked.
                        String[] split = input.split(":");
                        if (!(checkIfFriend(split[1], split[2])) || !(checkIfFriend(split[2], split[1]))) {
                            writer.write("FAILED: Users must be 2-way friends");
                            writer.println();
                            writer.flush();
                            break;
                        }
                        if (checkIfBlocked(split[1], split[2]) || checkIfBlocked(split[2], split[1])) {
                            writer.write("FAILED: One of the two users is blocking the other!");
                            writer.println();
                            writer.flush();
                            break;
                        }
                        serverReturn = sendMessage(input, writer);
                        if (serverReturn.contains("User") && serverReturn.contains("could not be found")) {
                            writer.write("FAILED: " + serverReturn);
                            writer.println();
                            writer.flush();
                        } else if (serverReturn.contains("successfully")) {
                            writer.write("SUCCESS: " + serverReturn);
                            writer.println();
                            writer.flush();
                        } else if (serverReturn.contains("An error occurred while sending the message")) {
                            writer.write("FAILED: " + serverReturn);
                            writer.println();
                            writer.flush();
                        } else if (serverReturn.contains("Failed to")) {
                            writer.write("FAILED: " + serverReturn);
                            writer.println();
                            writer.flush();
                        } else {
                            writer.write("FAILED: An exception occurred while sending message");
                            writer.println();
                            writer.flush();
                        }
                    }
                    case "LOGOUT" -> {
                        // Do whatever to log out and then exit the while true loop to end the server thread
                        logout = true;
                        if(logout){
                            writer.write("SUCCESS: Logout Success");
                            writer.println();
                            writer.flush();
                        } else {
                            writer.write("FAILED: Logout Failed");
                            writer.println();
                            writer.flush();
                        }
                        break; //stop the while loop
                    }
                    default -> {
                        writer.write("Invalid formatting command");
                        writer.println();
                        writer.flush();
                        System.out.println("Invalid command format sent");
                    }
                }
            }
            reader.close();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String addFriend(String input) {
        synchronized(manager) {
            manager.populateHashMap();
        }
        // Format: ADDFRIEND:USER:FRIEND
        String[] parts = input.split(":");

        if(parts.length != 3) {
            return "Error: Invalid Input";
        }

        String username = parts[1];
        String friend = parts[2];

        synchronized (manager) {
            if (checkIfFriend(username, friend)) {
                return("User " + friend + " is already a friend");
            }
        }

        // Check if the friend exists
        synchronized(manager) {
            if (!manager.getIdTracker().containsKey(friend)) {
                return "Cannot find User" + friend;
            }
        }

        // Use the UserManager's addFriend method to update the friend's list
        String serverReturn = manager.addFriend(username, friend);
        return serverReturn;
    }

    public String unfriend(String input) {
        synchronized(manager) {
            manager.populateHashMap();
        }
        // Format: UNFRIEND:USER:FRIEND
        String [] parts = input.split(":");

        if(parts.length != 3) {
            return "Error: Invalid Input";
        }

        String username = parts[1];
        String friend = parts[2];

        synchronized (manager) {
            if (!checkIfFriend(username, friend)) {
                return ("User " + friend + " is not a friend");
            }
        }

        // Check if the friend exists
        synchronized(manager) {
            if (!manager.getIdTracker().containsKey(friend)) {
                return "Cannot find User" + friend;
            }
        }
        // Use the UserManager's unfriend method to update the friend's list
        String serverReturn = manager.unfriend(username, friend);

        return serverReturn;
    }

    public String sendMessage(String input, PrintWriter writer) {
        // Format: SENDMESSAGE:SENDER:RECEIVER:MESSAGE
        synchronized(manager) {
            manager.populateHashMap();
        }

        String[] parts = input.split(":");
        if (parts.length != 4) {
            return "Error: Invalid Input";
        }

        String sender = parts[1];
        String receiver = parts[2];
        String message = parts[3];

        MessageManager messageManager = new MessageManager(sender, receiver);
        System.out.println("SENDER: " + sender);
        System.out.println("RECEIVER: " + receiver);
        messageManager.populateHashMap();
        ArrayList<String> a = messageManager.idTrackerToString();

        String serverReturn = messageManager.sendMessage(sender, message);
        return serverReturn;
    }

    // I'm just gonna have the login method return a String for the username, everything else can probably return void
    public String login(String input, PrintWriter writer) {
        // Format: LOGIN:USERNAME:PASSWORD
        synchronized(manager) {
            manager.populateHashMap();
        }

        String parts[] = input.split(":");
        if (parts.length != 3) {
            return "Error: Invalid Input";
        }

        String username = parts[1];
        String password = parts[2];

        // Authenticate the user
        boolean successfulLogin = authenticator.authenticate(username, password);
        // Write to the client whether the login succeeded or failed.
        if (successfulLogin) {
            return "SUCCESS: " + username;
        } else {
            return "FAILED: " + username;
        }
    }

    // NOTE: THIS METHOD WAS WRITTEN BY PRISHA. COMMITTED BY WYATT TO PREVENT GITHUB CONFLICTS
    public String createUser(String input, PrintWriter writer) {
        // Format: CREATEUSER:USERNAME:PASSWORD:EMAIL:BIO
        synchronized(manager) {
            manager.populateHashMap();
        }

        String parts[] = input.split(":");
        if (parts.length != 5) {
            return "Error: Invalid input";
        }

        String username = parts[1];
        String password = parts[2];
        String email = parts[3];
        String bio = parts[4];

        String serverReturn = manager.createUser(username, password, email, bio, null);
        return serverReturn;
    }

    // NOTE: THIS METHOD WAS WRITTEN BY PRISHA. COMMITTED BY WYATT TO PREVENT GITHUB CONFLICTS
    public String getUser(String input, PrintWriter writer) {
        // Format: GETUSER:USERNAME
        synchronized(manager) {
            manager.populateHashMap();
        }

        String parts[] = input.split(":");
        if (parts.length != 2) {
            return "Error: Invalid input";
        }

        String username = parts[1];
        if (username == null || username.trim().isEmpty()) {
            return "Error: Invalid input";
        }

        String serverReturn = manager.getUser(username);
        return serverReturn;
    }

    // NOTE: THIS METHOD WAS WRITTEN BY ANEESH. COMMITTED BY WYATT TO PREVENT GITHUB CONFLICTS
    public String deleteUser(String input, PrintWriter writer) {
        // Format: DELETEUSER:USERNAME
        synchronized(manager) {
            manager.populateHashMap();
        }

        String parts[] = input.split(":");
        String username = parts[1];
        if (parts.length != 2) {
            return "Error: Invalid input";
        }

        if (username == null || username.trim().isEmpty()) {
            return "Error: Invalid input";
        }

        String serverReturn = manager.deleteUser(username);
        return serverReturn;
    }

    public String blockUser(String input) {
        synchronized(manager) {
            manager.populateHashMap();
        }
        // Format: BLOCKUSER:BLOCKER:BLOCKED
        String[] parts = input.split(":");
        if (parts.length != 3) {
            return "Error: Invalid Input";
        }

        String blocker = parts[1];
        String blocked = parts[2];

        String serverReturn = manager.block(blocker, blocked);
        return serverReturn;
    }

    // Note: Written by Wyatt
    public String unblockUser(String input) {
        synchronized(manager) {
            manager.populateHashMap();
        }
        // Format: UNBLOCK:BLOCKER:UNBLOCKED
        String[] parts = input.split(":");
        if (parts.length != 3) {
            return "Error: Invalid Input";
        }

        String blocker = parts[1];
        String unblocked = parts[2];

        String serverReturn = manager.unblock(blocker, unblocked);
        return serverReturn;
    }

    // NOTE: THIS METHOD WAS WRITTEN BY ANEESH. COMMITTED BY WYATT TO PREVENT GITHUB CONFLICTS
    public String editUser(String input, PrintWriter writer){
        synchronized(manager) {
            manager.populateHashMap();
        }

        String[] parts = input.split(":");
        if (parts.length != 5) {
            return "Error: Invalid input";
        }

        String username = parts[1];
        String email = parts[3];
        String bio = parts[4];

        // set password and friend to null when calling editUser
        String serverReturn = manager.editUser(username, null, email, bio, null);
        return serverReturn;
    }

    // Note: Written by Aneesh
    public static boolean checkIfFriend (String username, String friend) {

        synchronized(manager) {
            manager.populateHashMap();

            String userData = manager.getUser(username);
            if (userData == null || userData.isEmpty()) {
                return false; // User doesn't exist or data is invalid
            }
            // Extract info from friends list
            HashMap<String, ArrayList<String>> friendsMap = manager.getFriendsHashMap(userData);

            // Check if the friend exists in the user's friends list
            return friendsMap.containsKey(friend);
        }
    }

    // Note: Written by Triet
    public static boolean checkIfBlocked(String username1, String username2) {
        // this is just reading from a file so no need to synchronize
        HashMap<String, List<String>> blockedMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader("blockedList.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Formatting the input from file
                String[] parts = line.split(":");
                String blocker = parts[0].trim();
                String blockedList = parts[1].trim();
                blockedList = blockedList.substring(1, blockedList.length() - 1); // Remove [ and ]
                List<String> blockedUsers = Arrays.asList(blockedList.split(","));

                // Creating clean blocked users list
                List<String> cleanBlockedUsers = new ArrayList<>();
                for (String user : blockedUsers) {
                    cleanBlockedUsers.add(user.trim());
                }
                // Putting blocker and blocked users into a map
                blockedMap.put(blocker, cleanBlockedUsers);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // Check if either username blocks the other
        if ((blockedMap.containsKey(username1) && blockedMap.get(username1).contains(username2))) {
            return true;
        }

        return false;
    }
}
