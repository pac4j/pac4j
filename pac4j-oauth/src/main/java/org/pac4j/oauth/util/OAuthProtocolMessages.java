package org.pac4j.oauth.util;

import com.github.scribejava.core.httpclient.HttpClient;
import com.github.scribejava.core.httpclient.HttpClientConfig;
import com.github.scribejava.core.httpclient.HttpClientProvider;
import com.github.scribejava.core.httpclient.jdk.JDKHttpClient;
import com.github.scribejava.core.httpclient.multipart.MultipartPayload;
import com.github.scribejava.core.model.OAuthAsyncRequestCallback;
import com.github.scribejava.core.model.OAuthConstants;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.ProtocolMessages;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * The messages exchanged with the browser and the OAuth server (OAuth 1.0 and 2.0), logged raw on the
 * {@code PROTOCOL_MESSAGE.OAUTH} logger, at debug level.
 *
 * <p>The requests to the OAuth server are sent by ScribeJava: they are logged by wrapping its HTTP client, through
 * {@link #httpClient(HttpClientConfig)}, when the logger is enabled. The client secret is masked, in the body of the
 * requests as in their query string (OAuth 2.0, RFC 6749, section 2.3.1); the {@code Authorization} header, carrying
 * the OAuth 1.0 signature or the access token, is not logged.</p>
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@UtilityClass
public class OAuthProtocolMessages {

    /** The logger of the messages exchanged. */
    public static final ProtocolMessages LOGGER = new ProtocolMessages("OAUTH");

    /** The other party: the OAuth server. */
    public static final String OAUTH_SERVER = "OAuth server";

    private static final String[] SECRETS = {OAuthConstants.CLIENT_SECRET};

    private static final String FORM_CONTENT_TYPE = "application/x-www-form-urlencoded";

    /**
     * <p>Log a message sent to the browser.</p>
     *
     * @param message the message
     */
    public void sentToBrowser(final String message) {
        LOGGER.sent(ProtocolMessages.BROWSER, message);
    }

    /**
     * <p>Log a request received from the browser: its method and all its parameters.</p>
     *
     * @param context the web context
     */
    public void receivedFromBrowser(final WebContext context) {
        LOGGER.received(null, ProtocolMessages.BROWSER, context);
    }

    /**
     * <p>Log a request sent to the OAuth server: its method, its URL and its body, the client secret being masked.</p>
     *
     * @param verb the method
     * @param url the URL, query string included
     * @param body the body, as a form or anything else, or null
     */
    public void sent(final Verb verb, final String url, final String body) {
        if (LOGGER.isEnabled()) {
            LOGGER.sent(OAUTH_SERVER, verb + " " + ProtocolMessages.maskUrl(url, SECRETS)
                + (body == null || body.isEmpty() ? "" : " " + ProtocolMessages.maskForm(body, SECRETS)));
        }
    }

    /**
     * <p>Log a response received from the OAuth server: its status and its body.</p>
     *
     * @param response the response
     */
    public void received(final Response response) {
        if (LOGGER.isEnabled()) {
            String body;
            try {
                body = response.getBody();
            } catch (final IOException e) {
                body = "<unreadable body: " + e.getMessage() + ">";
            }
            LOGGER.received(OAUTH_SERVER, response.getCode() + " " + body);
        }
    }

    /**
     * <p>The HTTP client ScribeJava must use so that the messages exchanged with the OAuth server are logged: the client
     * the configuration defines, wrapped. Nothing when the logger is disabled, ScribeJava then building its client
     * as usual.</p>
     *
     * @param config the HTTP client configuration, or null for the default JDK client
     * @return the logging HTTP client, or null when the messages are not logged
     */
    public HttpClient httpClient(final HttpClientConfig config) {
        return LOGGER.isEnabled() ? httpClient(buildHttpClient(config)) : null;
    }

    /**
     * <p>Wrap an HTTP client so that the messages it exchanges with the OAuth server are logged.</p>
     *
     * @param client the HTTP client
     * @return the client, wrapped
     */
    public HttpClient httpClient(final HttpClient client) {
        return new LoggingHttpClient(client);
    }

    private HttpClient buildHttpClient(final HttpClientConfig config) {
        if (config != null) {
            for (val provider : ServiceLoader.load(HttpClientProvider.class)) {
                val client = provider.createClient(config);
                if (client != null) {
                    return client;
                }
            }
        }
        return new JDKHttpClient();
    }

    /**
     * A ScribeJava HTTP client logging the requests it sends and the responses it receives. The asynchronous requests,
     * which pac4j never sends, are logged when sent only.
     */
    @RequiredArgsConstructor
    private static final class LoggingHttpClient implements HttpClient {

        private final HttpClient delegate;

        @Override
        public void close() throws IOException {
            delegate.close();
        }

        @Override
        public <T> Future<T> executeAsync(final String userAgent, final Map<String, String> headers, final Verb httpVerb,
                                          final String completeUrl, final byte[] bodyContents,
                                          final OAuthAsyncRequestCallback<T> callback,
                                          final OAuthRequest.ResponseConverter<T> converter) {
            sent(httpVerb, completeUrl, body(headers, bodyContents));
            return delegate.executeAsync(userAgent, headers, httpVerb, completeUrl, bodyContents, callback, converter);
        }

        @Override
        public <T> Future<T> executeAsync(final String userAgent, final Map<String, String> headers, final Verb httpVerb,
                                          final String completeUrl, final MultipartPayload bodyContents,
                                          final OAuthAsyncRequestCallback<T> callback,
                                          final OAuthRequest.ResponseConverter<T> converter) {
            sent(httpVerb, completeUrl, body(bodyContents));
            return delegate.executeAsync(userAgent, headers, httpVerb, completeUrl, bodyContents, callback, converter);
        }

        @Override
        public <T> Future<T> executeAsync(final String userAgent, final Map<String, String> headers, final Verb httpVerb,
                                          final String completeUrl, final String bodyContents,
                                          final OAuthAsyncRequestCallback<T> callback,
                                          final OAuthRequest.ResponseConverter<T> converter) {
            sent(httpVerb, completeUrl, bodyContents);
            return delegate.executeAsync(userAgent, headers, httpVerb, completeUrl, bodyContents, callback, converter);
        }

        @Override
        public <T> Future<T> executeAsync(final String userAgent, final Map<String, String> headers, final Verb httpVerb,
                                          final String completeUrl, final File bodyContents,
                                          final OAuthAsyncRequestCallback<T> callback,
                                          final OAuthRequest.ResponseConverter<T> converter) {
            sent(httpVerb, completeUrl, body(bodyContents));
            return delegate.executeAsync(userAgent, headers, httpVerb, completeUrl, bodyContents, callback, converter);
        }

        @Override
        public Response execute(final String userAgent, final Map<String, String> headers, final Verb httpVerb,
                                final String completeUrl, final byte[] bodyContents)
            throws InterruptedException, ExecutionException, IOException {
            sent(httpVerb, completeUrl, body(headers, bodyContents));
            return received(delegate.execute(userAgent, headers, httpVerb, completeUrl, bodyContents));
        }

        @Override
        public Response execute(final String userAgent, final Map<String, String> headers, final Verb httpVerb,
                                final String completeUrl, final MultipartPayload bodyContents)
            throws InterruptedException, ExecutionException, IOException {
            sent(httpVerb, completeUrl, body(bodyContents));
            return received(delegate.execute(userAgent, headers, httpVerb, completeUrl, bodyContents));
        }

        @Override
        public Response execute(final String userAgent, final Map<String, String> headers, final Verb httpVerb,
                                final String completeUrl, final String bodyContents)
            throws InterruptedException, ExecutionException, IOException {
            sent(httpVerb, completeUrl, bodyContents);
            return received(delegate.execute(userAgent, headers, httpVerb, completeUrl, bodyContents));
        }

        @Override
        public Response execute(final String userAgent, final Map<String, String> headers, final Verb httpVerb,
                                final String completeUrl, final File bodyContents)
            throws InterruptedException, ExecutionException, IOException {
            sent(httpVerb, completeUrl, body(bodyContents));
            return received(delegate.execute(userAgent, headers, httpVerb, completeUrl, bodyContents));
        }

        private static Response received(final Response response) {
            OAuthProtocolMessages.received(response);
            return response;
        }

        /** A form is decoded to mask the client secret; another kind of body is only described. */
        private static String body(final Map<String, String> headers, final byte[] contents) {
            if (contents == null || contents.length == 0) {
                return null;
            }
            val contentType = headers == null ? null : headers.get(CONTENT_TYPE);
            return contentType == null || contentType.startsWith(FORM_CONTENT_TYPE)
                ? new String(contents, StandardCharsets.UTF_8) : "<" + contents.length + " bytes of " + contentType + ">";
        }

        private static String body(final MultipartPayload contents) {
            return contents == null ? null : "<multipart body>";
        }

        private static String body(final File contents) {
            return contents == null ? null : "<file " + contents.getName() + ">";
        }
    }
}
