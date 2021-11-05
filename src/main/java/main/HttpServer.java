package main;


import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HttpServer extends Thread {
    protected Socket clientSocket;
    private final String successHeader =
            "HTTP/1.1 200 OK";
    private final String endHeader =
            "Connection: Closed";

    public static void main(String[] args) {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(8010);
            System.out.println("Connection Socket Created");
            try {
                while (true) {
                    System.out.println("Waiting for Connection");
                    new HttpServer(serverSocket.accept());
                }
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
        } catch (IOException e) {
            System.err.println("Could not listen on port: 10008.");
            System.exit(1);
        } finally {
            try {
                if (serverSocket != null)
                    serverSocket.close();
            } catch (IOException e) {
                System.err.println("Could not close port: 10008.");
                System.exit(1);
            }
        }
    }

    private HttpServer(Socket clientSoc) {
        clientSocket = clientSoc;
        start();
    }

    public void run() {
        System.out.println("New Communication Thread Started");

        try {
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),
                    true);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    clientSocket.getInputStream()));

            StringBuilder stringBuilder = new StringBuilder();
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                stringBuilder.append(inputLine);
                out.println(inputLine);

                if (inputLine.trim().equals(""))
                    break;
            }

            handleRequest(stringBuilder.toString());

            out.close();
            in.close();
            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Problem with Communication Server");
            System.exit(1);
        }
    }

    private void handleRequest(String request) {

    }

    private void handleFileRequest(String fileName, List<String> headerTag) {


    }
}