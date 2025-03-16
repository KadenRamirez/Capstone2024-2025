import java.io.*;
import java.net.Socket;

public class ClientTest {
    private String host = "localhost";
    private int port = 21000;
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;

    public ClientTest() {
        try {
            socket = new Socket(host, port);
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Start a listener thread
            new Thread(this::listenForMessages).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        if (writer != null) {
            writer.println(message);
            System.out.println("Message sent: " + message);
        }
    }

    private void listenForMessages() {
        String response;
        try {
            while ((response = reader.readLine()) != null) {
                System.out.println("Message received: " + response);
            }
        } catch (IOException e) {
            System.out.println("Connection closed.");
        }
    }

    public static void main(String[] args) {
        ClientTest client = new ClientTest();
        client.sendMessage("02597b2228e6");
    }
}

