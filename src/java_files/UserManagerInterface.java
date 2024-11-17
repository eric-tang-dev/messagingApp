package java_files;

import java.util.ArrayList;
import java.util.HashMap;

public interface UserManagerInterface {

    String createUser(String username, String password, String email, String bio, HashMap<String, ArrayList<String>> friends);

    boolean flushDatabase();

    String getUser(String username);

    String editUser(String username, String password, String email, String bio,
                    HashMap<String, ArrayList<String>> friends);

    String deleteUser(String username);

    String addFriend(String username, String friend);

    String unfriend(String username, String friend);

    ArrayList<String> idTrackerToString();

    boolean writeHashMapToFile();

    String block(String username, String blocked);

    String unblock(String username, String blocked);

    boolean writeBlockedListToFile();

    void setIdTracker(HashMap<String, Integer> idTracker);

    HashMap<String, Integer> getIdTracker();

}
