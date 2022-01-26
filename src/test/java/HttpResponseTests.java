import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import server.ContentEncoding;
import server.HttpResponse;
import server.StatusCode;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import static org.mockito.Mockito.*;

public class HttpResponseTests {

    HttpResponse response;
    ByteArrayOutputStream outputStream;
    String header;

    @BeforeEach
    public void setUp() {
        response = new HttpResponse(StatusCode.OK);
        outputStream = new ByteArrayOutputStream();
        header = new StringBuilder()
                .append("HTTP/1.1 200 OK\n")
                .append("Server: TinyServer\n")
                .toString();
    }

    @ParameterizedTest
    @EnumSource
    public void shouldCorrectlySetReturnCodeAndServerName(StatusCode status) {
        response = new HttpResponse(status);
        response.sendToSoketStream(outputStream);

        String expectedString = new StringBuilder()
                .append("HTTP/1.1 ").append(status).append('\n')
                .append("Server: TinyServer\n")
                .append('\n')
                .toString();
        String responseString = outputStream.toString();

        Assert.assertEquals(expectedString, responseString);
    }

    @Test
    public void shouldCorrectlyAddDate() {
        Date date = mock(Date.class);
        when(date.getTime()).thenReturn(2000L);
        response.addDate(date);
        response.sendToSoketStream(outputStream);

        String expected = new StringBuilder(header)
                .append("Date: ")
                .append("Thu, 01 Jan 1970 02:00:02 EET\n")
                .append("\n")
                .toString();
        String actual = outputStream.toString();

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldCorrectlyAddDefaultKeepAlive() {
        int defaultTimeout = 5;
        int defaultMax = 999;
        response.addKeepAlive();
        response.sendToSoketStream(outputStream);

        String expected = new StringBuilder(header)
                .append("Connection: Keep-Alive\n")
                .append("Keep-Alive: timeout=").append(defaultTimeout)
                .append(", max=").append(defaultMax).append("\n\n")
                .toString();
        String actual = outputStream.toString();

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldCorrectlyAddKeepAlive() {
        int timeout = 10;
        int max = 10000;
        response.addKeepAlive(timeout, max);
        response.sendToSoketStream(outputStream);

        String expected = new StringBuilder(header)
                .append("Connection: Keep-Alive\n")
                .append("Keep-Alive: timeout=").append(timeout)
                .append(", max=").append(max).append("\n\n")
                .toString();
        String actual = outputStream.toString();

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldCorrectlyAddHttpFile() {
        response.addHttpFile("/test.html", ContentEncoding.NONE);
        response.sendToSoketStream(outputStream);

        String expected = new StringBuilder(header)
                .append("Content-Type: text/html; charset=utf-8\n\n")
                .append("test")
                .toString();
        String actual = outputStream.toString();

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void shouldCorrectlyAddFile() {
        //
    }

}
