package org.pac4j.cas.util;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.util.CommonHelper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This is {@link HttpUtils} that provides utility functions
 * to deal with opening connections, building error messages
 * and closing connections, etc.
 *
 * @author Misagh Moayyed
 * @since 1.8.0
 */
public final class HttpUtils {

    private static int connectTimeout = HttpConstants.DEFAULT_CONNECT_TIMEOUT;

    private static int readTimeout = HttpConstants.DEFAULT_READ_TIMEOUT;

    private HttpUtils() {
    }

    public static String buildHttpErrorMessage(final HttpURLConnection connection) throws IOException {
        final StringBuilder messageBuilder = new StringBuilder("(").append(connection.getResponseCode()).append(")");
        if (connection.getResponseMessage() != null) {
            messageBuilder.append(" ");
            messageBuilder.append(connection.getResponseMessage());
        }
        return messageBuilder.toString();
    }

    public static HttpURLConnection openPostConnection(final URL url) throws IOException {
        return openConnection(url, "POST");
    }

    public static HttpURLConnection openDeleteConnection(final URL url) throws IOException {
        return openConnection(url, "DELETE");
    }

    public static HttpURLConnection openConnection(final URL url, final String requestMethod) throws IOException {
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoInput(true);
        connection.setDoOutput(true);
        connection.setRequestMethod(requestMethod);
        connection.setConnectTimeout(connectTimeout);
        connection.setReadTimeout(readTimeout);
        return connection;
    }

    public static String encodeQueryParam(final String paramName, final String paramValue) throws UnsupportedEncodingException {
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
