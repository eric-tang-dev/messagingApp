package java_files;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import com.sun.tools.javac.Main;
import javafx.application.Application;
import javafx.stage.Stage;


public class Authenticator extends Application implements AuthenticatorInterface {

    private static MainGUI main;
    private static Stage primaryStage;

    public void start(Stage stage) {
        primaryStage = stage;
        main = new MainGUI();
        main.start(primaryStage);
        this.showLogin();
    }

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

    public static void main(String[] args) {
        launch(args);
    }

    public void showLogin() {
        LoginGUI loginGUI = new LoginGUI();
        try {
            loginGUI.start(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
