import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import server.HttpRequest;
import server.RequestType;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class HttpRequestTests {

    HttpRequest request;
    BufferedReader mockSocketStream;

    @BeforeEach
    public void setUp() {
        request = new HttpRequest();
        mockSocketStream = mock(BufferedReader.class);
    }

    @ParameterizedTest
    @EnumSource
    public void shouldReturnCorrectRequestType(RequestType req) {
        String header = req.name() + " / HTTP/1.1";

        try {
            when(mockSocketStream.readLine())
                    .thenReturn(header)
                    .thenReturn(null);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }

        try {
            request.readFromSoketStream(mockSocketStream);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        Assert.assertEquals(request.getRequestType(), req);
    }

    @ParameterizedTest
    @ValueSource(strings = {"/someFile.html", "/style.css"})
    public void shouldReturnCorrectRequestedResourcePath(String resourcePath) {
        String header = "GET " + resourcePath + " HTTP/1.1";

        try {
            when(mockSocketStream.readLine())
                    .thenReturn(header)
                    .thenReturn(null);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }

        try {
            request.readFromSoketStream(mockSocketStream);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        Assert.assertEquals(request.getRequestedPath(), resourcePath);
    }

    @Test
    public void shouldDetectAndReturnCorrectFaviconPath() {
        String header = "GET /favicon.ico HTTP/1.1";

        try {
            when(mockSocketStream.readLine())
                    .thenReturn(header)
                    .thenReturn(null);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }

        try {
            request.readFromSoketStream(mockSocketStream);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        Assert.assertTrue(request.requestsFavicon());
        Assert.assertEquals(request.getRequestedPath(), "/favicon.png");
    }

    @Test
    public void shouldDetectAndReturnCorrectRootPath() {
        String header = "GET / HTTP/1.1";

        try {
            when(mockSocketStream.readLine())
                    .thenReturn(header)
                    .thenReturn(null);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }

        try {
            request.readFromSoketStream(mockSocketStream);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        Assert.assertTrue(request.requestsRoot());
        Assert.assertEquals(request.getRequestedPath(), "/index.html");
    }

    @Test
    public void shouldThrowExceptionForIllFormedHeaders() {
        String illformedHeader = "GET HTTP/1.1";

        try {
            when(mockSocketStream.readLine())
                    .thenReturn(illformedHeader)
                    .thenReturn(null);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }

        Exception thrown = Assertions.assertThrows(Exception.class,
                () -> {
                    request.readFromSoketStream(mockSocketStream);
                });
        Assert.assertEquals("Illformed Header", thrown.getMessage());
    }

    @Test
    public void shouldGetCorrectFieldValue() {
        try {
            when(mockSocketStream.readLine())
                    .thenReturn("GET / HTTP/1.1")
                    .thenReturn("some_Attribute: value1")
                    .thenReturn("OTHER_ATTRIBUTE: value2")
                    .thenReturn("last_attribute: value3")
                    .thenReturn(null);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }

        try {
            request.readFromSoketStream(mockSocketStream);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
        Assert.assertEquals("value1", request.getFieldValue("some_Attribute"));
        Assert.assertEquals("value2", request.getFieldValue("other_attribute"));
        Assert.assertEquals("value3", request.getFieldValue("LAST_ATTRIBUTE"));
    }

    @Test
    public void shouldReturnCorrectHeaderLinesMap() {
        Map<String, String> map = new HashMap<>();
        map.put("some_attribute", "value1");
        map.put("other_attribute", "value2");
        map.put("last_attribute", "value3");

        try {
            when(mockSocketStream.readLine())
                    .thenReturn("GET / HTTP/1.1")
                    .thenReturn("some_Attribute: value1")
                    .thenReturn("OTHER_ATTRIBUTE: value2")
                    .thenReturn("last_attribute: value3")
                    .thenReturn(null);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }

        try {
            request.readFromSoketStream(mockSocketStream);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

        Assert.assertEquals(map, request.getHeaderLines());
    }
    
    @Test
    public void shouldIgnoreIllformedAttributeLinesAndLinesAfterEmptyLine() {
        try {
            when(mockSocketStream.readLine())
                    .thenReturn("GET / HTTP/1.1")
                    .thenReturn("bad_attribute")
                    .thenReturn("correct_attribute: value")
                    .thenReturn("")
                    .thenReturn("ignored_attribute: value");
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }

        try {
            request.readFromSoketStream(mockSocketStream);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }

        Assert.assertEquals(1, request.getHeaderLines().size());
        Assert.assertEquals("value", request.getFieldValue("correct_attribute"));
    }

    @Test
    public void shouldHandleIOException() {
        try {
            when(mockSocketStream.readLine())
                    .thenReturn("GET / HTTP/1.1")
                    .thenThrow(IOException.class);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        }

        try {
            request.readFromSoketStream(mockSocketStream);
        } catch(IOException e) {
            Assert.fail("Shouldn't throw IOException");
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

}
