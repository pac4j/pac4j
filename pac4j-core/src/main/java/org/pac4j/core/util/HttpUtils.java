package org.pac4j.core.util;

import org.pac4j.core.context.HttpConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * This class provides utility functions to deal with opening connections,
 * building error messages and closing connections, etc.
 *
 * @author Misagh Moayyed
 * @since 1.8.0
 */
public final class HttpUtils {

    private static int connectTimeout = HttpConstants.DEFAULT_CONNECT_TIMEOUT;

    private static int readTimeout = HttpConstants.DEFAULT_READ_TIMEOUT;

    private HttpUtils() {
    }

    /**
     * Build error message from connection in case of failure
     * @param connection HttpURLConnection
     * @return String by combining response code, message and error stream
     * @throws IOException
     */
    public static String buildHttpErrorMessage(final HttpURLConnection connection) throws IOException {
        final StringBuilder messageBuilder = new StringBuilder("(").append(connection.getResponseCode()).append(")");
        if (connection.getResponseMessage() != null) {
            messageBuilder.append(" ");
            messageBuilder.append(connection.getResponseMessage());
        }
        try (final InputStreamReader isr = new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(isr)){
            String output;
            messageBuilder.append("[");
            while ((output = br.readLine()) != null) {
                messageBuilder.append(output);
            }
            messageBuilder.append("]");
        }finally {
            connection.disconnect();
        }
        return messageBuilder.toString();
    }

    public static HttpURLConnection openPostConnection(final URL url) throws IOException {
        return openConnection(url, HttpConstants.HTTP_METHOD.POST.name(), null);
    }

    public static HttpURLConnection openPostConnection(final URL url, final Map<String, String> headers) throws IOException {
        return openConnection(url, HttpConstants.HTTP_METHOD.POST.name(), headers);
    }

    public static HttpURLConnection openDeleteConnection(final URL url) throws IOException {
        return openConnection(url, HttpConstants.HTTP_METHOD.DELETE.name(), null);
    }

    protected static HttpURLConnection openConnection(final URL url, final String requestMethod, final Map<String, String> headers)
        throws IOException {
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod(requestMethod);
        connection.setConnectTimeout(connectTimeout);
        connection.setReadTimeout(readTimeout);
        if (headers != null) {
            for (final Map.Entry<String, String> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        return connection;
    }

    public static String readBody(final HttpURLConnection connection) throws IOException {
        try (final InputStreamReader isr = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8);
            final BufferedReader br = new BufferedReader(isr)) {
            final StringBuilder sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            return sb.toString();
        }
    }

    public static String encodeQueryParam(final String paramName, final String paramValue) {
        return CommonHelper.urlEncode(paramName) + "=" + CommonHelper.urlEncode(paramValue);
    }

    public static void closeConnection(final HttpURLConnection connection) {
        if (connection != null) {
            connection.disconnect();
        }
    }

    public static int getConnectTimeout() {
        return connectTimeout;
    }

    public static void setConnectTimeout(final int connectTimeout) {
        HttpUtils.connectTimeout = connectTimeout;
    }

    public static int getReadTimeout() {
        return readTimeout;
    }

    public static void setReadTimeout(final int readTimeout) {
        HttpUtils.readTimeout = readTimeout;
    }
}
