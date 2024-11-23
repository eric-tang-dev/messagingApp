package java_files;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javafx.stage.Stage;


public class Authenticator implements AuthenticatorInterface {

    private static MainGUI main;
    private static Stage primaryStage;

    public boolean authenticate(String username, String password) {
        try {
            // this is our API's authentication url
            URL url = new URL("http://127.0.0.1:8000/messaging/api/login/");

            // establish a connection, send a POST request (def post() is overriden in views.py)
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Construct JSON string
            String jsonInputString = String.format("{\"username\": \"%s\", \"password\": \"%s\"}", username, password);
            // MAY NEED TO CONSIDER CATCHING ERRORS HERE
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Get the response code
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
