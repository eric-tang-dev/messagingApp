package java_files;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class MessageManager extends UserManager implements SharedResources {

    // inherits access to manager
    // will only be able to edit two users data --> NOTE: CODE THIS IN LATER
    private String username1;
    private String username2;

    public MessageManager(String username1, String username2) {
        super();
        this.username1 = username1;
        this.username2 = username2;
    }

    // how to deal with user typing input that is reserved, like commmas
    // we need to add functionality to tell the program to escape it
    // NOTE: need username and password
    public String sendMessage(String sender, String message) {
        super.populateHashMap();
        super.writeHashMapToFile();

        // first, check that both usernames exist in idTracker

        if (!(idTracker.containsKey(username1))) {
            return "User " + username1 + " could not be found in the database";
        }
        if (!(idTracker.containsKey(username2))) {
            return "User " + username2 + " could not be found in the database";
        }
        try {
            // use sender to get important information
            Integer userId = idTracker.get(sender);
            String receiver = sender.equals(username1) ? username2 : username1;
            Integer receiverId = idTracker.get(receiver);

            // construct URI for get request to database
            System.out.println(sendMessageForUser(userId, sender, receiver, message, "sent"));
            System.out.println(sendMessageForUser(receiverId, receiver, sender, message, "received"));
        } catch (Exception e) {
            e.printStackTrace();
            return "An error occurred while sending the message.";
        }
        return "";
    }

    public String sendMessageForUser(Integer userId, String sender, String receiver, String message, String sent) {
        // add info to message before storing it
        message += sent.equals("sent") ? "-1" : "-2";
        try {
            HttpClient client = HttpClient.newHttpClient();

            URI uri = new URI("http://127.0.0.1:8000/messaging/users/" + userId + "/");

            // get the current friends hashmap
            HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> getResponse = client.send(getRequest, HttpResponse.BodyHandlers.ofString());
            if (getResponse.statusCode() != 200) {
                return "Failed to retrieve user data. Status code: " + getResponse.statusCode();
            }

            String response = getResponse.body();

            HashMap<String, ArrayList<String>> friends = getFriendsHashMap(response);

            // append the friends hashmap, get the messages with the receiver
            ArrayList<String> user1messages = friends.get(receiver);
            if (user1messages == null) {
                if (idTracker.containsKey(receiver)) {
                    user1messages = new ArrayList<>();
                    friends.put(receiver, user1messages);
                    message += "-1";
                } else return "Cannot message non-existent user";
            } else if (user1messages.isEmpty()) {
                message += "-1";
            } else {
                // get index of last message
                String lastMessage = user1messages.getLast();
                if (lastMessage.isEmpty()) {
                    message += "-1";
                } else {
                    String[] parts = lastMessage.split("-");
                    int index = Integer.parseInt(parts[2]) + 1;
                    message += "-" + index;
                }
            }
            user1messages.add(message);
            friends.replace(receiver, user1messages);

            // send the PUT request to the sender's data
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("{");

            jsonBuilder.append("\"username\": \"").append(sender).append("\", ");

            jsonBuilder.append("\"friends\": {");

            for (Map.Entry<String, ArrayList<String>> entry : friends.entrySet()) {
                String friendName = entry.getKey();
                ArrayList<String> messages = entry.getValue();

                // for each friend
                jsonBuilder.append("\"").append(friendName).append("\": [");

                // for each message
                for (int i = 0; i < messages.size(); i++) {
                    if (messages.get(i).isEmpty()) {
                        continue;
                    }
                    jsonBuilder.append("\"").append(messages.get(i)).append("\"");
                    if (i < messages.size() - 1) {
                        jsonBuilder.append(", ");
                    }
                }
                jsonBuilder.append("], ");
            }

            if (!friends.isEmpty()) {
                jsonBuilder.setLength(jsonBuilder.length() - 2);
            }

            jsonBuilder.append("}");

            jsonBuilder.append("}");
            String json = jsonBuilder.toString();
            System.out.println(json);

            HttpRequest putRequest = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Content-Type", "application/json")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                    .build();

            // send put request
            HttpResponse<String> putResponse = client.send(putRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println(putResponse.body());
            if (putResponse.statusCode() == 200 || putResponse.statusCode() == 204) {
                return "Message sent successfully.";
            } else {
                return "Failed to send message. Status code: " + putResponse.statusCode();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "An error occurred while sending the message.";
        }
    }

    public static HashMap<String, ArrayList<String>> getFriendsHashMap(String response) {
        String friendsData = "\"friends\":{";
        int friendsStart = response.indexOf(friendsData);

        int friendsEnd = response.indexOf("}", friendsStart) + 1; // End of the friends object
        String friendsSection = response.substring(friendsStart, friendsEnd);

        HashMap<String, ArrayList<String>> friendsMap = new HashMap<>();

        String title = friendsSection.replaceFirst("\"friends\":\\{", "").replaceAll("}$", "");

        String[] friendEntries = title.split("],");

        for (String entry : friendEntries) {
            String[] parts = entry.split(":", 2);
            if (parts.length < 2) continue;

            String friendName = parts[0].replaceAll("[\"{}]", "").trim();

            String[] messagesArray = parts[1].replaceAll("[\\[\\]\"]", "").split(",");
            ArrayList<String> messages = new ArrayList<>(Arrays.asList(messagesArray));
            for (int i = 0; i < messages.size(); i++) {
                messages.set(i, messages.get(i).trim());
            }

            friendsMap.put(friendName, messages);
        }

        return friendsMap;
    }

}
