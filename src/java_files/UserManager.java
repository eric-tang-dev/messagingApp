package java_files;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.*;
import java.util.stream.*;
import java.io.*;

// NOTE: ONLY MAKE ONE UserManager OBJECT IN THE PROGRAM
public class UserManager {

    // it's more useful to store username as the key, and access ID number as the value
    // may change to private variable
    HashMap<String, Integer> idTracker = new HashMap<>();

    public UserManager() {
        // Constructor can be empty since idTracker is already initialized
    }

    public String populateHashMap() {
        try {
            // Create an HttpClient
            HttpClient client = HttpClient.newHttpClient();

            // Create a GET request to fetch the user data from the backend
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://127.0.0.1:8000/messaging/users/"))
                    .header("Content-Type", "application/json")
                    .GET()
                    .build();

            // Send the request and get the response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // Parse the response body manually
                String responseBody = response.body();

                // Simple parsing logic to extract ID and Username
                // Step 1: Remove square brackets to isolate individual user objects
                responseBody = responseBody.substring(1, responseBody.length() - 1);

                // Step 2: Split by "},{" to separate individual user objects
                String[] userObjects = responseBody.split("\\},\\{");

                // Step 3: Iterate over each user object
                for (String userObject : userObjects) {
                    // Clean up the object string to ensure it has no curly braces or quotes
                    userObject = userObject.replaceAll("[{}\"]", "");

                    // Split by commas to get key-value pairs
                    String[] keyValuePairs = userObject.split(",");

                    // Variables to hold the ID and username
                    int id = -1;
                    String username = "";

                    // Extract ID and username from the key-value pairs
                    for (String pair : keyValuePairs) {
                        String[] keyValue = pair.split(":");
                        if (keyValue.length == 2) {
                            String key = keyValue[0].trim();
                            String value = keyValue[1].trim();

                            if (key.equals("id")) {
                                id = Integer.parseInt(value);
                            } else if (key.equals("username")) {
                                username = value;
                            }
                        }
                    }

                    // Add to the HashMap if ID and username were found
                    if (id != -1 && !username.isEmpty()) {
                        idTracker.put(username, id);
                    }
                }

                return "HashMap populated successfully with data from the backend.";
            } else {
                return "Failed to fetch data from the backend. Status code: " + response.statusCode();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "An exception occurred while populating the HashMap.";
        }
    }

    // NOTE: users cannot have the same username
    public String createUser(String username, String password, String email, String bio, HashMap<String, ArrayList<String>> friends) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            // convert the friends array to a JSON field
            // turns it into a Set of key value pairs, with entry as the key and friend as the value
            // .map() allows us to format the String. .collect() to group together array elements
            String friendsToJson;
            if (friends == null) {
                friendsToJson = "{}";
            } else {
                friendsToJson = friends.entrySet().stream()
                        .map(entry -> "\"" + entry.getKey() + "\": " +
                                entry.getValue().stream()
                                        .map(friend -> "\"" + friend + "\"")
                                        .collect(Collectors.joining(", ", "[", "]")))
                        .collect(Collectors.joining(", ", "{", "}"));
            }

            String json = String.format(
                    """
                    {
                        \"username\": \"%s\",
                        \"password\": \"%s\",
                        \"email\": \"%s\",
                        \"bio\": \"%s\",
                        \"friends\": %s
                    }
                    """,
                    username, password, email, bio, friendsToJson
            );

            // send POST request and handle the response
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("http://127.0.0.1:8000/messaging/users/"))
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());

            if (response.statusCode() == 201) {
                String responseContent = response.body();
                String idString = responseContent.substring(responseContent.indexOf("\"id\":") + 5, responseContent.indexOf(",", responseContent.indexOf("\"id\":"))).trim();
                int userId = Integer.parseInt(idString);
                idTracker.put(username, userId); // add it to the HashMap
                writeHashMapToFile(); // write new hashmap to file
                return "User created successfully: " + response.body();
            } else if (response.statusCode() == 200) {
                return "A user with that username already exists.";
            } else {
                return "Failed to create UserProfile. Status code: " + response.statusCode();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "An exception was thrown before the program could complete execution.";
        }
    }

    public boolean flushDatabase() {
        try (BufferedReader bfr = new BufferedReader(new FileReader("localDatabase.txt"))) {
            HttpClient client = HttpClient.newHttpClient();

            while (true) {
                String line = bfr.readLine();
                if (line == null) {
                    break;
                }

                String[] keyValuePair = line.split(": ");
                if (keyValuePair.length != 2) {
                    continue;
                } // if for any reason, there is no key, no value, or both, just ignore it

                String userId = keyValuePair[1].trim(); // trim() in case of weird white spaces
                URI uri = new URI("http://127.0.0.1:8000/messaging/users/" + userId + "/");
                System.out.println(uri.toString());

                HttpRequest deleteRequest = HttpRequest.newBuilder()
                        .uri(uri)
                        .DELETE()
                        .header("Content-Type", "application/json")
                        .build();

                HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());

                // all cases where deletion was unsuccessful, just return false
                if (deleteResponse.statusCode() != 204 && deleteResponse.statusCode() != 404) {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        setIdTracker(new HashMap<String, Integer>()); // also clear idTracker, since the database is empty
        return true; // all users have been deleted, database is now empty
    }

    public String getUser(String username) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            // connect the username to an ID in the Map
            Integer userId = idTracker.get(username);

            // construct the URI
            URI uri = new URI("http://127.0.0.1:8000/messaging/users/" + userId + "/");

            // build GET request
            HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(uri)
                    .GET()
                    .header("Content-Type", "application/json")
                    .build();

            // send the GET request and handle the response
            HttpResponse<String> response = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return "User Data: " + response.body();
            } else if (response.statusCode() == 404) {
                return "User " + username + " could not be found.";
            } else {
                return "Failed to retrieve user. Status code: " + response.statusCode();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    // This is a PUT/PATCH request
    // fields that are non-empty will be updated, leave fields empty if don't want to update
    // NOTE: MUST ENTER CORRECT USERNAME
    public String editUser(String username, String password, String email, String bio,
            HashMap<String, ArrayList<String>> friends) {
//        if (username == null || username.isEmpty()) {
//            return "Did not specify a username.";
//        } else {
//
//        }
        try {
            HttpClient client = HttpClient.newHttpClient();

            // user data to be inputted
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("{");

            if (username != null && !username.isEmpty()) {
                jsonBuilder.append("\"username\": \"").append(username).append("\", ");
            }
            if (password != null && !password.isEmpty()) {
                jsonBuilder.append("\"password\": \"").append(password).append("\", ");
            }
            if (email != null && !email.isEmpty()) {
                jsonBuilder.append("\"email\": \"").append(email).append("\", ");
            }
            if (bio != null && !bio.isEmpty()) {
                jsonBuilder.append("\"bio\": \"").append(bio).append("\", ");
            }

            // Convert friends to JSON
            // Same logic as in .createUser()
            jsonBuilder.append("\"friends\": {");
            for (Map.Entry<String, ArrayList<String>> entry : friends.entrySet()) {
                jsonBuilder.append("\"").append(entry.getKey()).append("\"").append(":").append(entry.getValue()).append(",");
            }
            if (friends.isEmpty()) { // if friends hashmap is empty
                jsonBuilder.setLength(jsonBuilder.length() - 1);
                jsonBuilder.append("{}");
                jsonBuilder.append("}");
            } else {
                jsonBuilder.setLength(jsonBuilder.length() - 1);
                jsonBuilder.append("}}");
            }
            String json = jsonBuilder.toString();

            // connect the username to an ID in the Map
            Integer userId = idTracker.get(username);

            // construct the URI
            URI uri = new URI("http://127.0.0.1:8000/messaging/users/" + userId + "/");

            // build the PUT request
            HttpRequest patchRequest = HttpRequest.newBuilder()
                    .uri(uri)
                    .header("Content-Type", "application/json")
                    .method("PATCH", HttpRequest.BodyPublishers.ofString(json))
                    .build();

            // send the PATCH request and handle the response
            HttpResponse<String> response = client.send(patchRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 || response.statusCode() == 204) {
                return "User updated successfully.";
            } else if (response.statusCode() == 404) {
                return "User " + username + " could not be found.";
            } else {
                return "Failed to update user. Status code: " + response.statusCode();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    // This DELETES a user PERMANENTLY using its username
    public String deleteUser(String username) {
        try {
            HttpClient client = HttpClient.newHttpClient();

            // connect the username to an ID in the Map
            Integer userId = idTracker.get(username);

            // construct the URI
            URI uri = new URI("http://127.0.0.1:8000/messaging/users/" + userId + "/");

            // build DELETE request
            HttpRequest deleteRequest = HttpRequest.newBuilder()
                    .uri(uri)
                    .DELETE()
                    .header("Content-Type", "application/json")
                    .build();

            // send the DELETE request and handle the response
            HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());

            if (deleteResponse.statusCode() == 204) {
                idTracker.remove(username); // delete it from the HashMap too
                return "User " + username + " successfully deleted.";
            } else if (deleteResponse.statusCode() == 404) {
                return "User " + username + " could not be found.";
            } else {
                return "Failed to delete user. Status code: " + deleteResponse.statusCode();
            }
        } catch (Exception e) {
            return "";
        }
    }

    // adds a friend
    public String addFriend(String username, String friend) {
        String data = getUser(username); // get user data
        HashMap<String, ArrayList<String>> friends = new HashMap<>(); // to be populated

        int friendsIndex = data.indexOf("friends\":{") + 9;
        data = data.substring(friendsIndex, data.length() - 1);
        data = data.substring(1, data.length() - 1);

        String[] parts = data.split("]");
        for (String part : parts) {
            // get name
            int nameStartIndex = part.indexOf("\"");
            int nameEndIndex = part.indexOf("\":");
            if (nameStartIndex == -1 && nameEndIndex == -1) {
                break;
            }
            String name = part.substring(nameStartIndex + 1, nameEndIndex);

            // get array
            int messageStartIndex = part.indexOf("[");
            String[] messageList = part.substring(messageStartIndex + 1).split(",");
            ArrayList<String> messages = new ArrayList<>(Arrays.asList(messageList));

            // put in hashmap
            friends.put(name, messages);
        }

        // add the friend and an empty ArrayList<String>
        ArrayList<String> empty = new ArrayList<>();
        friends.put(friend, empty);

        return editUser(username, null, null, null, friends);
    }

    public String unfriend(String username, String friend) {
        String data = getUser(username); // get user data
        HashMap<String, ArrayList<String>> friends = new HashMap<>(); // to be populated

        int friendsIndex = data.indexOf("friends\":{") + 9;
        data = data.substring(friendsIndex, data.length() - 1);
        data = data.substring(1, data.length() - 1);

        String[] parts = data.split("]");
        for (String part : parts) {
            // get name
            int nameStartIndex = part.indexOf("\"");
            int nameEndIndex = part.indexOf("\":");
            if (nameStartIndex == -1 && nameEndIndex == -1) {
                break;
            }
            String name = part.substring(nameStartIndex + 1, nameEndIndex);

            // get array
            int messageStartIndex = part.indexOf("[");
            String[] messageList = part.substring(messageStartIndex + 1).split(",");
            ArrayList<String> messages = new ArrayList<>(Arrays.asList(messageList));

            // put in hashmap
            friends.put(name, messages);
        }

        boolean hasFriend = false;
        for (Map.Entry<String, ArrayList<String>> entry : friends.entrySet()) {
            if (entry.getKey().equals(friend)) {
                hasFriend = true;
            }
        }
        if (hasFriend) {
            friends.remove(friend);
        }
        return editUser(username, null, null, null, friends);
    }

    // mainly for debugging purposes
    public ArrayList<String> idTrackerToString() {
        ArrayList<String> list = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : idTracker.entrySet()) {
            list.add(entry.getKey() + ": " + entry.getValue());
        }
        return list;
    }

    // this saves our HashMap, idTracker, to a local .txt file
    // for persistence purposes

    /*** RUN THIS COMMAND BEFORE EXITING THE PROGRAM ALWAYS ALWAYS ALWAYS ALWAYS
     *   ENSURE THAT IT RETURNS TRUE BEFORE EXITING
     */
    public boolean writeHashMapToFile() {
        // note, trying to write an empty/null hashmap returns false
        if (idTracker == null || idTracker.isEmpty()) {
            return false;
        }
        try (PrintWriter writer = new PrintWriter(new FileWriter("localDatabase.txt"))) {
            for (Map.Entry<String, Integer> entry : idTracker.entrySet()) {
                // store pairs like this --> Key: Value
                writer.println(entry.getKey() + ": " + entry.getValue());
            }
            return true;
        } catch (IOException e) {
            // should not be an issue, return false on all exceptions
            return false;
        }
    }

    public void setIdTracker(HashMap<String, Integer> idTracker) {
        this.idTracker = idTracker;
    }

    public HashMap<String, Integer> getIdTracker() {
        return idTracker;
    }

}
