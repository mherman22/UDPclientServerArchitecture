import java.net.*;
import java.io.*;
import javax.script.*;

public class TCPServer {

    public static void main(String[] args) {
        try {
            int serverPort = 7896;
            ServerSocket listenSocket = new ServerSocket(serverPort);
            System.out.println("Server is listening on port " + serverPort);

            while (true) {
                Socket clientSocket = listenSocket.accept();
                new Connection(clientSocket).start();
            }

        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    static class Connection extends Thread {
        private DataInputStream in;
        private DataOutputStream out;
        private Socket clientSocket;

        public Connection(Socket clientSocket) {
            this.clientSocket = clientSocket;
            try {
                in = new DataInputStream(clientSocket.getInputStream());
                out = new DataOutputStream(clientSocket.getOutputStream());
            } catch (IOException e) {
                System.out.println("Connection error: " + e.getMessage());
                closeConnection();
            }
        }

        public void run() {
            try {
                while (true) {
                    String expression = in.readUTF();
                    String result = evaluateExpression(expression);
                    out.writeUTF(result);
                }
            } catch (EOFException e) {
                System.out.println("Client disconnected: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("IO error: " + e.getMessage());
            } finally {
                closeConnection();
            }
        }

        /**
         * Evaluates a given arithmetic expression and returns the result as a string.
         *
         * <p>This method uses the JavaScript {@link ScriptEngine} to evaluate the
         * arithmetic expression provided as input. The expression can include basic
         * arithmetic operations such as addition, subtraction, multiplication, and
         * division. For example, the expression "3 + 5 * 2" will return "13".
         *
         * @param expression The arithmetic expression to evaluate, provided as a {@link String}.
         *                   The expression must be a valid JavaScript expression.
         * @return The result of the evaluation as a {@link String}. If the expression is
         *         invalid or an error occurs during evaluation, an error message is returned.
         * @throws IllegalArgumentException if the provided expression is null or empty.
         */

        private String evaluateExpression(String expression) {
            try {
                ScriptEngineManager manager = new ScriptEngineManager();
                ScriptEngine engine = manager.getEngineByName("JavaScript");
                Object result = engine.eval(expression);
                return result.toString();
            } catch (ScriptException e) {
                return "Error evaluating expression: " + e.getMessage();
            }
        }

        private void closeConnection() {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (clientSocket != null) clientSocket.close();
            } catch (IOException e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}
