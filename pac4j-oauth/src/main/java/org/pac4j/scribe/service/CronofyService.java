package org.pac4j.scribe.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.httpclient.HttpClient;
import com.github.scribejava.core.httpclient.HttpClientConfig;
import com.github.scribejava.core.model.OAuthConstants;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.oauth.AccessTokenRequestParams;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.oauth.profile.JsonHelper;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Specific OAuth 2 service for Cronofy to send the parameters as a JSON body for the access token.
 *
 * @author Jerome LELEU
 * @since 5.3.1
 */
public class CronofyService extends OAuth20Service {

    private final DefaultApi20 api;

    /**
     * <p>Constructor for CronofyService.</p>
     *
     * @param api a {@link com.github.scribejava.core.builder.api.DefaultApi20} object
     * @param apiKey a {@link java.lang.String} object
     * @param apiSecret a {@link java.lang.String} object
     * @param callback a {@link java.lang.String} object
     * @param defaultScope a {@link java.lang.String} object
     * @param responseType a {@link java.lang.String} object
     * @param debugStream a {@link java.io.OutputStream} object
     * @param userAgent a {@link java.lang.String} object
     * @param httpClientConfig a {@link com.github.scribejava.core.httpclient.HttpClientConfig} object
     * @param httpClient a {@link com.github.scribejava.core.httpclient.HttpClient} object
     */
    public CronofyService(final DefaultApi20 api, final String apiKey, final String apiSecret, final String callback,
                          final String defaultScope, final String responseType, final OutputStream debugStream,
                          final String userAgent, final HttpClientConfig httpClientConfig, final HttpClient httpClient) {
        super(api, apiKey, apiSecret, callback, defaultScope, responseType, debugStream, userAgent, httpClientConfig, httpClient);
        this.api = api;
    }

    /** {@inheritDoc} */
    protected OAuthRequest createAccessTokenRequest(AccessTokenRequestParams params) {
        final OAuthRequest request = new OAuthRequest(api.getAccessTokenVerb(), api.getAccessTokenEndpoint());

        final Map<String, String> map = new HashMap<>();
        map.put("client_id", getApiKey());
        map.put("client_secret", getApiSecret());
        map.put(OAuthConstants.GRANT_TYPE, OAuthConstants.AUTHORIZATION_CODE);
        map.put(OAuthConstants.CODE, params.getCode());
        map.put(OAuthConstants.REDIRECT_URI, getCallback());
        final String json;
        try {
            json = JsonHelper.getMapper().writeValueAsString(map);
        } catch (final JsonProcessingException e) {
            throw new TechnicalException(e);
        }
        request.setPayload(json);

        request.addHeader("Content-Type", "application/json; charset=utf-8");

        logRequestWithParams("access token", request);
        return request;
    }
}
