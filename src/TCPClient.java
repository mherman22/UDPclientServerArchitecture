import java.net.*;
import java.io.*;

public class TCPClient {

    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 7896);
             DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {

            String expression;
            System.out.println("Enter arithmetic expressions (type 'exit' to quit):");

            while (!(expression = reader.readLine()).equalsIgnoreCase("exit")) {
                out.writeUTF(expression);
                String result = in.readUTF();
                System.out.println("Result: " + result);
            }

        } catch (IOException e) {
            System.out.println("Client error: " + e.getMessage());
        }
    }
}
