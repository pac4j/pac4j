package org.pac4j.scribe.builder.api;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.httpclient.HttpClient;
import com.github.scribejava.core.httpclient.HttpClientConfig;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.scribe.service.CronofyService;

import java.io.OutputStream;

/**
 * This class represents the OAuth API implementation for Cronofy.
 *
 * <p>More information at https://docs.cronofy.com/developers/authorization/</p>
 *
 * @author Jerome LELEU
 * @since 5.3.1
 */
public final class CronofyApi20 extends DefaultApi20 {

    private final String sdkIdentifier;

    /**
     * <p>Constructor for CronofyApi20.</p>
     *
     * @param sdkIdentifier a {@link java.lang.String} object
     */
    public CronofyApi20(final String sdkIdentifier) {
        this.sdkIdentifier = sdkIdentifier;
    }

    /** {@inheritDoc} */
    @Override
    public String getAccessTokenEndpoint() {
        return computeBaseUrl() + "/oauth/token";
    }

    /** {@inheritDoc} */
    @Override
    protected String getAuthorizationBaseUrl() {
        return computeBaseUrl() + "/oauth/authorize";
    }

    private String computeBaseUrl() {
        if (CommonHelper.isNotBlank(sdkIdentifier)) {
            return "https://app-" + sdkIdentifier + ".cronofy.com";
        } else {
            return "https://app.cronofy.com";
        }
    }

    /** {@inheritDoc} */
    @Override
    public OAuth20Service createService(String apiKey, String apiSecret, String callback, String defaultScope,
                                        String responseType, OutputStream debugStream, String userAgent,
                                        HttpClientConfig httpClientConfig, HttpClient httpClient) {
        return new CronofyService(this, apiKey, apiSecret, callback, defaultScope, responseType, debugStream,
            userAgent, httpClientConfig, httpClient);
    }
}
