import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

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
        
    }

    @Test
    public void shouldCorrectlyAddKeepAlive() {

    }

    @Test
    public void shouldCorrectlyAddHttpFile() {

    }

    @Test
    public void shouldCorrectlyAddFavicon() {

    }

    @Test
    public void shouldCorrectlyAddFile() {

    }

}
