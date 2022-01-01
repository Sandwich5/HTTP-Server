package server;


import java.net.*;
import java.io.*;
import java.util.Date;

public class HttpServer extends Thread {
    protected Socket clientSocket;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = null;
        int port = 8011;

        try {
            serverSocket = new ServerSocket(port);
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
            System.err.println("Could not listen on port: " + port);
            System.exit(1);
        } finally {
            try {
                if (serverSocket != null)
                    serverSocket.close();
            } catch (IOException e) {
                System.err.println("Could not close port: " + port);
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
            // read request
            InputStream in = clientSocket.getInputStream();
            HttpRequest request = new HttpRequest();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            try { request.readFromSoketStream(reader); }
            catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }

            HttpResponse response = (new HttpResponse(StatusCode.OK))
                    .addDate(new Date())
                    .addKeepAlive();

            if (request.requestsFavicon()) {
                response.addFavicon(ContentEncoding.NONE);
            }
            else if (request.requestsRoot()){
                response.addHttpFile("/index.html", ContentEncoding.NONE);
            } else {
                System.out.println("Sending file: " + request.getRequestedPath());
                response.addHttpFile(request.getRequestedPath(), ContentEncoding.NONE);
            }

            // sends back the response
            OutputStream out = clientSocket.getOutputStream();
            response.sendToSoketStream(out);
            out.close();

            // closing the socket input stream will close the socket connection too
            in.close();
        } catch (IOException e) {
            System.err.println("Problem with Communication Server");
            System.exit(1);
        }
    }

}