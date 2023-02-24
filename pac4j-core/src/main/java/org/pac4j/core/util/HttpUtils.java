package org.pac4j.core.util;

import lombok.val;
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
     *
     * @param connection HttpURLConnection
     * @return String by combining response code, message and error stream
     * @throws java.io.IOException an IO exception
     */
    public static String buildHttpErrorMessage(final HttpURLConnection connection) throws IOException {
        val messageBuilder = new StringBuilder("(").append(connection.getResponseCode()).append(")");
        if (connection.getResponseMessage() != null) {
            messageBuilder.append(" ");
            messageBuilder.append(connection.getResponseMessage());
        }
        try (var isr = new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8);
             var br = new BufferedReader(isr)) {
            String output;
            messageBuilder.append("[");
            while ((output = br.readLine()) != null) {
                messageBuilder.append(output);
            }
            messageBuilder.append("]");
        } finally {
            connection.disconnect();
        }
        return messageBuilder.toString();
    }

    /**
     * <p>openPostConnection.</p>
     *
     * @param url a {@link java.net.URL} object
     * @return a {@link java.net.HttpURLConnection} object
     * @throws java.io.IOException if any.
     */
    public static HttpURLConnection openPostConnection(final URL url) throws IOException {
        return openConnection(url, HttpConstants.HTTP_METHOD.POST.name(), null);
    }

    /**
     * <p>openPostConnection.</p>
     *
     * @param url a {@link java.net.URL} object
     * @param headers a {@link java.util.Map} object
     * @return a {@link java.net.HttpURLConnection} object
     * @throws java.io.IOException if any.
     */
    public static HttpURLConnection openPostConnection(final URL url, final Map<String, String> headers) throws IOException {
        return openConnection(url, HttpConstants.HTTP_METHOD.POST.name(), headers);
    }

    /**
     * <p>openDeleteConnection.</p>
     *
     * @param url a {@link java.net.URL} object
     * @return a {@link java.net.HttpURLConnection} object
     * @throws java.io.IOException if any.
     */
    public static HttpURLConnection openDeleteConnection(final URL url) throws IOException {
        return openConnection(url, HttpConstants.HTTP_METHOD.DELETE.name(), null);
    }

    /**
     * <p>openConnection.</p>
     *
     * @param url a {@link java.net.URL} object
     * @param requestMethod a {@link java.lang.String} object
     * @param headers a {@link java.util.Map} object
     * @return a {@link java.net.HttpURLConnection} object
     * @throws java.io.IOException if any.
     */
    protected static HttpURLConnection openConnection(final URL url, final String requestMethod, final Map<String, String> headers)
        throws IOException {
        val connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod(requestMethod);
        connection.setConnectTimeout(connectTimeout);
        connection.setReadTimeout(readTimeout);
        if (headers != null) {
            for (val entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        return connection;
    }

    /**
     * <p>readBody.</p>
     *
     * @param connection a {@link java.net.HttpURLConnection} object
     * @return a {@link java.lang.String} object
     * @throws java.io.IOException if any.
     */
    public static String readBody(final HttpURLConnection connection) throws IOException {
        try (var isr = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8);
             var br = new BufferedReader(isr)) {
            val sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }
            return sb.toString();
        }
    }

    /**
     * <p>encodeQueryParam.</p>
     *
     * @param paramName a {@link java.lang.String} object
     * @param paramValue a {@link java.lang.String} object
     * @return a {@link java.lang.String} object
     */
    public static String encodeQueryParam(final String paramName, final String paramValue) {
        return CommonHelper.urlEncode(paramName) + "=" + CommonHelper.urlEncode(paramValue);
    }

    /**
     * <p>closeConnection.</p>
     *
     * @param connection a {@link java.net.HttpURLConnection} object
     */
    public static void closeConnection(final HttpURLConnection connection) {
        if (connection != null) {
            connection.disconnect();
        }
    }

    /**
     * <p>Getter for the field <code>connectTimeout</code>.</p>
     *
     * @return a int
     */
    public static int getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * <p>Setter for the field <code>connectTimeout</code>.</p>
     *
     * @param connectTimeout a int
     */
    public static void setConnectTimeout(final int connectTimeout) {
        HttpUtils.connectTimeout = connectTimeout;
    }

    /**
     * <p>Getter for the field <code>readTimeout</code>.</p>
     *
     * @return a int
     */
    public static int getReadTimeout() {
        return readTimeout;
    }

    /**
     * <p>Setter for the field <code>readTimeout</code>.</p>
     *
     * @param readTimeout a int
     */
    public static void setReadTimeout(final int readTimeout) {
        HttpUtils.readTimeout = readTimeout;
    }
}
