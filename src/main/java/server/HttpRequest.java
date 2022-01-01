package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Parsed a HTTP request.
 * */
public class HttpRequest {

    Map<String, String> headerLines;
    RequestType type;
    String requestedPath;

    public HttpRequest() {
        headerLines = new HashMap<>();
    }

    public void readFromSoketStream(BufferedReader reader) throws Exception {
        //BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String inputLine;

        try {
            String requestLine = reader.readLine();
            parseRequestLine(requestLine);

            while ((inputLine = reader.readLine()) != null) {

                int pos;
                if ((pos = inputLine.indexOf(':')) != -1) {
                    String a;
                    headerLines.put(inputLine.substring(0, pos).trim().toLowerCase(),
                            inputLine.substring(pos + 1).trim());
                }

                if (inputLine.trim().equals(""))
                    break;
            }
        } catch (IOException ex) {
            Logger.getLogger(HttpResponse.class.getName()).log(Level.SEVERE, "Reading header from socket", ex);
        }
    }

    public RequestType getRequestType() { return  type; }

    public String getRequestedPath() {
        if (requestsFavicon())
            return "/favicon.png";
        if (requestsRoot())
            return "/index.html";
        return requestedPath;
    }

    public String getFieldValue(String fieldName) {
        return headerLines.get(fieldName.toLowerCase());
    }

    public boolean requestsRoot() {
        return type == RequestType.GET && requestedPath.equals("/");
    }

    public boolean requestsFavicon() {
        return type == RequestType.GET && requestedPath.equals("/favicon.ico");
    }


    public Map<String, String> getHeaderLines() { return headerLines; }

    private void parseRequestLine(String requestLine) throws Exception {
        String[] parts = requestLine.split("\\s+");
        // TODO("add a custom exception for this")
        if (parts.length != 3) throw new Exception("Illformed Header");

        type = RequestType.fromString(parts[0]);
        requestedPath = parts[1];

        System.out.println("A new " + parts[2] + " request is being processed");
    }

}
