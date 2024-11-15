package java_files;

import java.util.ArrayList;
import java.util.HashMap;

public class User {
    private String username;
    private String password;
    private ArrayList<User> friends;
    private ArrayList<User> blockedPeople;
    private HashMap<User, ArrayList<String>> messageHistory;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.friends = new ArrayList<>();
        this.blockedPeople = new ArrayList<>();
        this.messageHistory = new HashMap<>();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<User> getFriends() {
        return friends;
    }

    public void addFriend(User friend) {
        this.friends.add(friend);
    }

    public void removeFriend(User friend) {
        this.friends.remove(friend);
    }

    public ArrayList<User> getBlockedPeople() {
        return blockedPeople;
    }

    public void blockUser(User user) {
        this.blockedPeople.add(user);
    }

    public void unblockUser(User user) {
        this.blockedPeople.remove(user);
    }

    public HashMap<User, ArrayList<String>> getMessageHistory() {
        return messageHistory;
    }

    public void sendMessage(User recipient, String message) {
        messageHistory.putIfAbsent(recipient, new ArrayList<>());
        messageHistory.get(recipient).add(message);
    }

    public ArrayList<String> getMessages(User user) {
        return messageHistory.getOrDefault(user, new ArrayList<>());
    }
}
