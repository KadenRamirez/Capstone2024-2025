import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class listener {
    public static void startServer() {
        int port = 21000;
        ExecutorService executor = Executors.newFixedThreadPool(10); // Thread pool for handling multiple clients
        
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New connection from " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
                
                executor.execute(new ClientHandler(clientSocket));
            }
        } catch (IOException e) {
            System.err.println("Error in server: " + e.getMessage());
        } finally {
            executor.shutdown();
        }
    }

    public static void main(String[] args) {
        startServer();
    }
}

class ClientHandler implements Runnable {
    private final Socket clientSocket;

    public ClientHandler(Socket socket) {
        this.clientSocket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);) {
            String imageId;
            while ((imageId = in.readLine()) != null) {
                System.out.println("Received image ID: " + imageId);

                // Run the Docker scraper with the received image ID
                String dockerOutput = Scraper.runDocker(imageId);

                // Send Docker output back to client
                writer.println(dockerOutput);
                System.out.println("Response sent to client.");

                // Close connection after sending response
                break;
            }
        } catch (IOException e) {
            System.err.println("Connection error: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing socket: " + e.getMessage());
            }
            System.out.println("Connection closed");
        }
    }
}


class Scraper {
  
    public static String runDocker(String image_id) {
        String imageId = image_id; // Replace with your actual image ID
        StringBuilder output = new StringBuilder(); // Store output here

        try {
            // Run Docker container
            ProcessBuilder processBuilder = new ProcessBuilder("docker", "run", "--rm", imageId);
            processBuilder.redirectErrorStream(true); // Merge stderr into stdout
            Process process = processBuilder.start();

            // Capture output in real-time
            BufferedReader dock = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = dock.readLine()) != null) {
                output.append(line).append("\n"); // Save output to string
            }

            // Wait for process to complete
            int exitCode = process.waitFor();

            

            output.append("Process exited with code: ").append(exitCode).append("\n");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString(); // Return Docker output
    }
}