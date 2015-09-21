/*
 * Copyright 2012 - 2015 pac4j organization
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.pac4j.cas.util;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * This is {@link HttpUtils} that provides utility functions
 * to deal with opening connections, building error messages
 * and closing connections, etc.
 *
 * @author Misagh Moayyed
 * @since 1.8.0
 */
public final class HttpUtils {
    private static final String DEFAULT_QUERY_PARAM_ENCODING = "UTF-8";

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
        return connection;
    }

    public static String encodeQueryParam(final String paramName, final String paramValue) throws UnsupportedEncodingException {
        return URLEncoder.encode(paramName, DEFAULT_QUERY_PARAM_ENCODING) + "=" + URLEncoder.encode(paramValue, DEFAULT_QUERY_PARAM_ENCODING);
    }

    public static void closeConnection(final HttpURLConnection connection) {
        if (connection != null) {
            connection.disconnect();
        }
    }
}
