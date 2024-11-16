package java_files;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.*;

/***
 * This test case file corresponds to MessageManager.java
 * Only one method needs to be tested:
 * The other methods are called in sendMessage()
 * If they fail, sendMessage() also fails. So the other methods are implicitly tested
 */

public class RunLocalTest2 implements SharedResources {

    // this method sends a message to both user's data
    @Test
    public void testSendMessage() {
        // for this test file, we will use a MessageManager
        // it has access to all of manager's methods, since it's a subclass
        MessageManager mm = new MessageManager("Eric", "George");

        // flush the database
        mm.flushDatabase();

        // create users to test with
        mm.createUser("Eric", "1234", null, null, null);
        mm.createUser("George", "1234", null, null, null);

        // add friend, make sure it was successful
        String friendAdded = mm.addFriend("Eric", "George");
        assertTrue(friendAdded.contains("successfully"), "Friend was not added successfully.");

        // send a message from user to user
        String messageSent = mm.sendMessage("Eric", "testMessage");
        assertTrue(messageSent.contains("Message sent"), "Message was not sent successfully.");

        // add a manual check to make sure
        String data = mm.getUser("Eric");
        int friendsIndex = data.indexOf("friends");
        data = data.substring(friendsIndex);
        assertTrue(data.contains("testMessage-1-1"), "Message was not sent successfully.");

        String data2 = mm.getUser("George");
        int friendsIndex2 = data2.indexOf("friends");
        data2 = data2.substring(friendsIndex2);
        System.out.println(data2);
        assertTrue(data2.contains("testMessage-2-1"), "Message was not sent successfully.");

        // flush the database at the end
        mm.flushDatabase();
    }

}
