package org.pac4j.core.util;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;

/**
 * This class test {@link HttpUtils}
 *
 * @author Ravi Bhushan
 * @since 3.3.1
 */
public class HttpUtilsTest {


    @Test
    public void testBuildHttpErrorMessage() throws IOException {
        // creating mock htp connection
        HttpURLConnection connectionMock = null ;

        // expected test data for mock connection
        String testResponseBody = "{\"error_description\":\"MSIS9612: The authorization code received in [code] parameter is invalid. \"}";
        int testConnectionResponseCode =400;
        String testConnResponseMessage = "Bad Request.";

        // mocking expected test data
        try(InputStream in = new ByteArrayInputStream(testResponseBody.getBytes(StandardCharsets.UTF_8))) {
            connectionMock = Mockito.mock(HttpURLConnection.class);
            Mockito.when(connectionMock.getResponseCode()).thenReturn(testConnectionResponseCode);
            Mockito.when(connectionMock.getResponseMessage()).thenReturn(testConnResponseMessage);
            Mockito.when(connectionMock.getErrorStream()).thenReturn(in);

            //evaluating test
            String actual = HttpUtils.buildHttpErrorMessage(connectionMock);
            String expected = String.format("(%d) %s[%s]", testConnectionResponseCode, testConnResponseMessage, testResponseBody);
            Assert.assertTrue(expected.equals(actual));
        }

    }

}
