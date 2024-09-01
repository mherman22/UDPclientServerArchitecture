# Server-Client Applications

This repository contains implementations of TCP and UDP server-client applications. The servers can accept requests from clients, process them, and send back responses. The TCP server can evaluate arithmetic expressions provided by the clients and return the results, while the UDP server echoes back the received messages.

## Table of Contents

- [TCP Server](#tcp-server)
- [TCP Client](#tcp-client)
- [UDP Server](#udp-server)
- [UDP Client](#udp-client)
- [How to Run](#how-to-run)
- [Examples](#examples)

## TCP Server

### Description

The TCP server listens on a specific port for incoming client connections. Once a client connects, the server reads an arithmetic expression sent by the client, evaluates it, and sends back the result.

### Key Features

- Evaluates arithmetic expressions (e.g., `3 + 5 * 2`) sent by clients.
- Returns the result to the client.
- Runs indefinitely, handling multiple client connections sequentially.

### Source Code

```java
import java.net.*;
import java.io.*;

public class TCPServer {
    public static void main(String args[]) {
        try {
            int serverPort = 7896;
            ServerSocket listenSocket = new ServerSocket(serverPort);
            while (true) {
                Socket clientSocket = listenSocket.accept();
                Connection c = new Connection(clientSocket);
            }
        } catch (IOException e) {
            System.out.println("Listen :" + e.getMessage());
        }
    }

    static class Connection extends Thread {
        DataInputStream in;
        DataOutputStream out;
        Socket clientSocket;

        public Connection(Socket aClientSocket) {
            try {
                clientSocket = aClientSocket;
                in = new DataInputStream(clientSocket.getInputStream());
                out = new DataOutputStream(clientSocket.getOutputStream());
                this.start();
            } catch (IOException e) {
                System.out.println("Connection:" + e.getMessage());
            }
        }

        public void run() {
            try {
                String data = in.readUTF();
                String result = evaluateExpression(data);
                out.writeUTF(result);
            } catch (EOFException e) {
                System.out.println("EOF:" + e.getMessage());
            } catch (IOException e) {
                System.out.println("IO:" + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {/*close failed*/}
            }
        }

        private String evaluateExpression(String expression) {
            if (expression == null || expression.isEmpty()) {
                throw new IllegalArgumentException("Expression cannot be null or empty");
            }
            try {
                ScriptEngineManager manager = new ScriptEngineManager();
                ScriptEngine engine = manager.getEngineByName("JavaScript");
                Object result = engine.eval(expression);
                return result.toString();
            } catch (ScriptException e) {
                return "Error evaluating expression: " + e.getMessage();
            }
        }
    }
}
```
## TCP Client

### Description

The TCP client connects to the TCP server, sends an arithmetic expression, and receives the result.

### Source code

```java
import java.net.*;
import java.io.*;

public class TCPClient {
    public static void main(String args[]) {
        try (Socket socket = new Socket("localhost", 7896);
             DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

            String expression = "3 + 5 * 2";
            out.writeUTF(expression);
            String result = in.readUTF();
            System.out.println("Result: " + result);
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        }
    }
}
```

## UDP Server

### Description

The UDP server listens on a specific port for incoming datagrams. When it receives a datagram from a client, it echoes the message back to the client.

### Source code

```java
import java.net.*;
import java.io.*;

public class UDPServer {
    public static void main(String args[]) {
        try (DatagramSocket aSocket = new DatagramSocket(6789)) {
            byte[] buffer = new byte[1000];
            while (true) {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);
                DatagramPacket reply = new DatagramPacket(request.getData(), request.getLength(), request.getAddress(), request.getPort());
                aSocket.send(reply);
            }
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        }
    }
}
```

## UDP Client

### Description

The UDP client sends a message to the UDP server and waits for the server's reply.

### Source code

```java
import java.net.*;

public class UDPClient {
    public static void main(String args[]) {
        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket();
            String message = "Hello, UDP Server";
            InetAddress aHost = InetAddress.getByName("localhost");
            int serverPort = 6789;
            DatagramPacket request = new DatagramPacket(message.getBytes(), message.length(), aHost, serverPort);
            aSocket.send(request);

            byte[] buffer = new byte[1000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
            aSocket.receive(reply);
            System.out.println("Reply: " + new String(reply.getData()));
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            if (aSocket != null) aSocket.close();
        }
    }
}
```
## How to Run
### Running the TCP Server and Client
1. Compile the TCP server and client:
   ```bash
    javac TCPServer.java TCPClient.java
   ```
	
2. Start the TCP server:
    ```bash
    java TCPServer
    ```
3. In a new terminal, start the TCP client:
    ```
    java TCPClient
    ```
The client will send an arithmetic expression (e.g., "3 + 5 * 2") to the server, and the server will evaluate it and return the result.

### Running the UDP Server and Client
1. Compile the UDP server and client:
```
javac UDPServer.java UDPClient.java
```

2. Start the UDP server:
```
java UDPServer
```

3. In a new terminal, start the UDP client:
```
java UDPClient "Your message" "localhost"
```
The client will send the message to the server, and the server will echo it back.

## Examples
### TCP Example
1. Start the TCP server.
2. Run the TCP client. The output will be:
```
Result: 13
```

### UDP Example
1. Start the UDP server.
2. Run the UDP client with the message "Hello, UDP Server". The output will be:
```
Reply: Hello, UDP Server
```

## Notes
- Ensure that the server is running before starting the client.
- The TCP server handles multiple clients sequentially.
- The UDP server handles multiple requests concurrently but may drop packets in a real-world scenario.