package java_files;

import java.util.ArrayList;
import java.util.HashMap;

public interface MessageManagerInterface {

    String sendMessage(String sender, String message);

    String sendMessageForUser(Integer userId, String sender, String receiver, String message, String sent);

    // NOTE: in message manager for this class, static should be removed as it's initialised here
    // at least that's how I understood it (you can remove this comment when turn it in)
    HashMap<String, ArrayList<String>> getFriendsHashMap(String response);

}
