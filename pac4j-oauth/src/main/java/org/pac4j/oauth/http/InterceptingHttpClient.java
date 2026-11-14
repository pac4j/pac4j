package org.pac4j.oauth.http;

import com.github.scribejava.core.httpclient.HttpClient;
import com.github.scribejava.core.httpclient.multipart.MultipartPayload;
import com.github.scribejava.core.model.OAuthAsyncRequestCallback;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.http.interceptor.HttpInterceptor;
import org.pac4j.core.http.oauth.OAuthHttpRequest;
import org.pac4j.core.http.oauth.OAuthHttpResponse;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * A ScribeJava HTTP client applying an {@link HttpInterceptor} to every request before sending it. Being the
 * single point all the requests go through, it leaves their construction to ScribeJava and to the provider services.
 *
 * @author Jerome LELEU
 * @since 6.7.0
 */
@RequiredArgsConstructor
public class InterceptingHttpClient implements HttpClient {

    private final HttpClient delegate;

    private final HttpInterceptor interceptor;

    private final CallContext context;

    /** {@inheritDoc} */
    @Override
    public void close() throws IOException {
        delegate.close();
    }

    /** {@inheritDoc} */
    @Override
    public <T> Future<T> executeAsync(final String userAgent, final Map<String, String> headers, final Verb httpVerb,
                                      final String completeUrl, final byte[] bodyContents,
                                      final OAuthAsyncRequestCallback<T> callback,
                                      final OAuthRequest.ResponseConverter<T> converter) {
        return delegate.executeAsync(userAgent, configure(headers, httpVerb, completeUrl).headers(), httpVerb, completeUrl,
            bodyContents, callback, converter);
    }

    /** {@inheritDoc} */
    @Override
    public <T> Future<T> executeAsync(final String userAgent, final Map<String, String> headers, final Verb httpVerb,
                                      final String completeUrl, final MultipartPayload bodyContents,
                                      final OAuthAsyncRequestCallback<T> callback,
                                      final OAuthRequest.ResponseConverter<T> converter) {
        return delegate.executeAsync(userAgent, configure(headers, httpVerb, completeUrl).headers(), httpVerb, completeUrl,
            bodyContents, callback, converter);
    }

    /** {@inheritDoc} */
    @Override
    public <T> Future<T> executeAsync(final String userAgent, final Map<String, String> headers, final Verb httpVerb,
                                      final String completeUrl, final String bodyContents,
                                      final OAuthAsyncRequestCallback<T> callback,
                                      final OAuthRequest.ResponseConverter<T> converter) {
        return delegate.executeAsync(userAgent, configure(headers, httpVerb, completeUrl).headers(), httpVerb, completeUrl,
            bodyContents, callback, converter);
    }

    /** {@inheritDoc} */
    @Override
    public <T> Future<T> executeAsync(final String userAgent, final Map<String, String> headers, final Verb httpVerb,
                                      final String completeUrl, final File bodyContents,
                                      final OAuthAsyncRequestCallback<T> callback,
                                      final OAuthRequest.ResponseConverter<T> converter) {
        return delegate.executeAsync(userAgent, configure(headers, httpVerb, completeUrl).headers(), httpVerb, completeUrl,
            bodyContents, callback, converter);
    }

    /** {@inheritDoc} */
    @Override
    public Response execute(final String userAgent, final Map<String, String> headers, final Verb httpVerb,
                            final String completeUrl, final byte[] bodyContents)
        throws InterruptedException, ExecutionException, IOException {
        return execute(headers, httpVerb, completeUrl,
            configured -> delegate.execute(userAgent, configured, httpVerb, completeUrl, bodyContents));
    }

    /** {@inheritDoc} */
    @Override
    public Response execute(final String userAgent, final Map<String, String> headers, final Verb httpVerb,
                            final String completeUrl, final MultipartPayload bodyContents)
        throws InterruptedException, ExecutionException, IOException {
        return execute(headers, httpVerb, completeUrl,
            configured -> delegate.execute(userAgent, configured, httpVerb, completeUrl, bodyContents));
    }

    /** {@inheritDoc} */
    @Override
    public Response execute(final String userAgent, final Map<String, String> headers, final Verb httpVerb,
                            final String completeUrl, final String bodyContents)
        throws InterruptedException, ExecutionException, IOException {
        return execute(headers, httpVerb, completeUrl,
            configured -> delegate.execute(userAgent, configured, httpVerb, completeUrl, bodyContents));
    }

    /** {@inheritDoc} */
    @Override
    public Response execute(final String userAgent, final Map<String, String> headers, final Verb httpVerb,
                            final String completeUrl, final File bodyContents)
        throws InterruptedException, ExecutionException, IOException {
        return execute(headers, httpVerb, completeUrl,
            configured -> delegate.execute(userAgent, configured, httpVerb, completeUrl, bodyContents));
    }

    /**
     * Send the request once configured and, when the interceptor asks for it after handling the response,
     * a second and last time.
     */
    private Response execute(final Map<String, String> headers, final Verb verb, final String url, final Sender sender)
        throws InterruptedException, ExecutionException, IOException {
        var request = configure(headers, verb, url);
        var response = sender.send(request.headers());
        if (interceptor.handleResponse(request, toResponse(response), context)) {
            response.close();
            request = configure(headers, verb, url);
            response = sender.send(request.headers());
            interceptor.handleResponse(request, toResponse(response), context);
        }
        return response;
    }

    /**
     * Apply the interceptor to a copy of the headers, those of the ScribeJava request being left untouched.
     */
    private OAuthHttpRequest configure(final Map<String, String> headers, final Verb verb, final String url) {
        val request = new OAuthHttpRequest(verb.name(), url, caseInsensitive(headers));
        interceptor.configureRequest(request, context);
        return request;
    }

    /**
     * Read the response for the interceptor: ScribeJava keeps its body once read, so it stays available.
     */
    private static OAuthHttpResponse toResponse(final Response response) throws IOException {
        return new OAuthHttpResponse(response.getCode(), caseInsensitive(response.getHeaders()), response.getBody());
    }

    private static Map<String, String> caseInsensitive(final Map<String, String> headers) {
        val copy = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
        // the JDK HTTP client gives the status line as a header without name
        headers.forEach((name, value) -> {
            if (name != null) {
                copy.put(name, value);
            }
        });
        return copy;
    }

    @FunctionalInterface
    private interface Sender {

        Response send(Map<String, String> headers) throws InterruptedException, ExecutionException, IOException;
    }
}
