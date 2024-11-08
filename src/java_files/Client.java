package java_files;
import java.io.*;
import java.net.*;

public class Client {
    // Hardcoding in the starting port number of the server
    static int portNum = 4242;
    String serverReturn;
    String clientInput;
    public static void main(String[] args) {
        try {
            // Try creating a new socket connection to that server
            Socket socket = new Socket("localhost", portNum);
            // Increment the port number the next time a client object is created
            // and tries connecting to server
            portNum++;
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());
            // LOTS OF GUI DISPLAY AND INPUT WILL BE SHOWN HERE. EACH DIALOG WILL WRITE WHAT
            // THE RETURN IS TO THE SERVER. THIS IS MOSTLY PHASE 3 SO I'M WAITING FOR NOW


        } catch (IOException e) {
            // MAY NEED TO DO SOME TESTING TO PREVENT EXCEPTIONS
            throw new RuntimeException(e);
        }
    }
}
