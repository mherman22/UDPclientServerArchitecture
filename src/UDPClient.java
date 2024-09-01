import java.net.*;
import java.io.*;

public class UDPClient {

    public static void main(String[] args) {
        DatagramSocket aSocket = null;
        final int MAX_RETRIES = 3;
        final int TIMEOUT_MS = 2000;

        try {
            // Check if arguments are provided
            if (args.length < 2) {
                System.out.println("Usage: java UDPClient <message> <hostname>");
                return;
            }

            aSocket = new DatagramSocket();
            aSocket.setSoTimeout(TIMEOUT_MS);
            byte[] m = args[0].getBytes();
            InetAddress aHost = InetAddress.getByName(args[1]);
            int serverPort = 6789;
            DatagramPacket request = new DatagramPacket(m, m.length, aHost, serverPort);
            byte[] buffer = new byte[1000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

            boolean receivedResponse = false;
            int attempts = 0;

            // Retry loop
            while (attempts < MAX_RETRIES && !receivedResponse) {
                try {
                    aSocket.send(request); // Send the request
                    aSocket.receive(reply); // Try to receive the reply
                    receivedResponse = true; // If successful, set flag to true
                    System.out.println("Reply: " + new String(reply.getData(), 0, reply.getLength()));
                } catch (SocketTimeoutException e) {
                    // If timeout occurs
                    attempts++;
                    System.out.println("Timeout, attempt " + attempts + " of " + MAX_RETRIES);
                }
            }

            if (!receivedResponse) {
                System.out.println("No response from server after " + MAX_RETRIES + " attempts.");
            }

        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (aSocket != null) aSocket.close();
        }
    }
}
