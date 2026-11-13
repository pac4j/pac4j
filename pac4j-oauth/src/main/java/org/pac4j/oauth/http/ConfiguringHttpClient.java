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
import org.pac4j.core.http.configurer.HttpRequestConfigurer;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * A ScribeJava HTTP client applying an {@link HttpRequestConfigurer} to every request before sending it. Being the
 * single point all the requests go through, it leaves their construction to ScribeJava and to the provider services.
 *
 * @author Jerome LELEU
 * @since 6.7.0
 */
@RequiredArgsConstructor
public class ConfiguringHttpClient implements HttpClient {

    private final HttpClient delegate;

    private final HttpRequestConfigurer<OAuthHttpRequest> configurer;

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
        return delegate.executeAsync(userAgent, configure(headers, httpVerb, completeUrl), httpVerb, completeUrl,
            bodyContents, callback, converter);
    }

    /** {@inheritDoc} */
    @Override
    public <T> Future<T> executeAsync(final String userAgent, final Map<String, String> headers, final Verb httpVerb,
                                      final String completeUrl, final MultipartPayload bodyContents,
                                      final OAuthAsyncRequestCallback<T> callback,
                                      final OAuthRequest.ResponseConverter<T> converter) {
        return delegate.executeAsync(userAgent, configure(headers, httpVerb, completeUrl), httpVerb, completeUrl,
            bodyContents, callback, converter);
    }

    /** {@inheritDoc} */
    @Override
    public <T> Future<T> executeAsync(final String userAgent, final Map<String, String> headers, final Verb httpVerb,
                                      final String completeUrl, final String bodyContents,
                                      final OAuthAsyncRequestCallback<T> callback,
                                      final OAuthRequest.ResponseConverter<T> converter) {
        return delegate.executeAsync(userAgent, configure(headers, httpVerb, completeUrl), httpVerb, completeUrl,
            bodyContents, callback, converter);
    }

    /** {@inheritDoc} */
    @Override
    public <T> Future<T> executeAsync(final String userAgent, final Map<String, String> headers, final Verb httpVerb,
                                      final String completeUrl, final File bodyContents,
                                      final OAuthAsyncRequestCallback<T> callback,
                                      final OAuthRequest.ResponseConverter<T> converter) {
        return delegate.executeAsync(userAgent, configure(headers, httpVerb, completeUrl), httpVerb, completeUrl,
            bodyContents, callback, converter);
    }

    /** {@inheritDoc} */
    @Override
    public Response execute(final String userAgent, final Map<String, String> headers, final Verb httpVerb,
                            final String completeUrl, final byte[] bodyContents)
        throws InterruptedException, ExecutionException, IOException {
        return delegate.execute(userAgent, configure(headers, httpVerb, completeUrl), httpVerb, completeUrl, bodyContents);
    }

    /** {@inheritDoc} */
    @Override
    public Response execute(final String userAgent, final Map<String, String> headers, final Verb httpVerb,
                            final String completeUrl, final MultipartPayload bodyContents)
        throws InterruptedException, ExecutionException, IOException {
        return delegate.execute(userAgent, configure(headers, httpVerb, completeUrl), httpVerb, completeUrl, bodyContents);
    }

    /** {@inheritDoc} */
    @Override
    public Response execute(final String userAgent, final Map<String, String> headers, final Verb httpVerb,
                            final String completeUrl, final String bodyContents)
        throws InterruptedException, ExecutionException, IOException {
        return delegate.execute(userAgent, configure(headers, httpVerb, completeUrl), httpVerb, completeUrl, bodyContents);
    }

    /** {@inheritDoc} */
    @Override
    public Response execute(final String userAgent, final Map<String, String> headers, final Verb httpVerb,
                            final String completeUrl, final File bodyContents)
        throws InterruptedException, ExecutionException, IOException {
        return delegate.execute(userAgent, configure(headers, httpVerb, completeUrl), httpVerb, completeUrl, bodyContents);
    }

    /**
     * Apply the configurer to a copy of the headers, those of the ScribeJava request being left untouched.
     */
    private Map<String, String> configure(final Map<String, String> headers, final Verb verb, final String url) {
        val configuredHeaders = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
        configuredHeaders.putAll(headers);
        configurer.configure(new OAuthHttpRequest(verb, url, configuredHeaders), context);
        return configuredHeaders;
    }
}
