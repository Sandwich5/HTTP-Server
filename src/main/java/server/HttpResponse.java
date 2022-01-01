package server;

import java.awt.image.BufferedImage;
import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

/**
 *  Creates a HTTP response
 * */
public class HttpResponse {

    private StringBuilder responseString;
    private InputStream inputStream;

    public HttpResponse(StatusCode status) {
        responseString = new StringBuilder();
        responseString.append("HTTP/1.1 " + status + "\n");
        responseString.append("Server: TinyServer\n");
    }

    public HttpResponse addDate(Date date) {
        DateFormat df = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss z");
        responseString.append("Date: " + df.format(date) + "\n");
        return this;
    }

    public HttpResponse addKeepAlive() {
        return addKeepAlive(5, 999);
    }

    public HttpResponse addKeepAlive(int timeout, int max) {
        responseString.append("Connection: Keep-Alive\n");
        responseString.append("Keep-Alive: timeout=" + timeout + ", max=" + max
                + "\n");
        return this;
    }

    public HttpResponse addHttpFile(String fileName, ContentEncoding enc) {
        if (inputStream != null) {
            Logger.getLogger(HttpResponse.class.getName()).log(Level.WARNING, null, "Another file scheduled");
            return this;
        }

        responseString.append("Content-Type: text/html; charset=utf-8\n");
        addFile(new File("site" + fileName), enc);

        return this;
    }

    public HttpResponse addFavicon(ContentEncoding enc) {
        if (inputStream != null) {
            Logger.getLogger(HttpResponse.class.getName()).log(Level.WARNING, null, "Another file scheduled");
            return this;
        }

        responseString.append("Content-Type: image/png\n");
        addFile(new File("site/favicon.png"), enc);

        return this;
    }

    private void addFile(File file, ContentEncoding enc) {
        try {
            BufferedInputStream stream = new BufferedInputStream(new FileInputStream(file));
            if (enc == ContentEncoding.NONE)
                inputStream = stream;
            else if (enc == ContentEncoding.GZIP)
                inputStream = new GZIPInputStream(stream);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(HttpResponse.class.getName()).log(Level.SEVERE, "Cannot find specified file", ex);
        } catch (IOException ex) {
            Logger.getLogger(HttpResponse.class.getName()).log(Level.SEVERE, "GZIP exception", ex);
        }
    }

    @Override
    public String toString() {
        return responseString.toString();
    }

    public void sendToSoketStream(OutputStream out) {
        Logger.getLogger(HttpResponse.class.getName()).log(Level.FINEST, null, "Sending response to socket");
        String header = responseString.toString() + "\n";
        try {
            out.write(header.getBytes());
            // send message body
            if (inputStream != null) {
                byte[] b = new byte[255];
                while (inputStream.read(b) > 0) {
                    out.write(b);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(HttpResponse.class.getName()).log(Level.SEVERE, "Socket Output", ex);
        } catch (NullPointerException ex) {
            Logger.getLogger(HttpResponse.class.getName()).log(Level.SEVERE, "Buffer I/O", ex);
        }
    }

}
