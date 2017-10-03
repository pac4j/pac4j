package org.pac4j.scribe.builder.api;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.OAuthConfig;
import com.github.scribejava.core.utils.OAuthEncoder;
import java.util.Map;
import org.pac4j.core.util.CommonHelper;

/**
 * This class represents the OAuth API implementation for PayPal. It could be part of the Scribe library.
 *
 * <p>More information at https://developer.paypal.com/docs/api/#identity</p>
 *
 * @author Jerome Leleu
 * @since 1.4.2
 */
public class PayPalApi20 extends DefaultApi20 {
    private static final String AUTHORIZATION_URL = "https://www.paypal.com/webapps/auth/protocol/openidconnect/v1/authorize?client_id=%s"
        + "&redirect_uri=%s&scope=%s&response_type=code&nonce=%s";

    @Override
    public String getAuthorizationUrl(final OAuthConfig config, Map<String, String> additionalParams) {
        CommonHelper.assertNotBlank("config.getCallback()", config.getCallback(),
            "Must provide a valid url as callback. PayPal does not support OOB");

        final String nonce = System.currentTimeMillis() + CommonHelper.randomString(10);
        return String.format(AUTHORIZATION_URL, config.getApiKey(), OAuthEncoder.encode(config.getCallback()),
                             (config.getScope()!=null)?OAuthEncoder.encode(config.getScope()):"", nonce);
    }
    @Override
    protected String getAuthorizationBaseUrl() {
        return "https://www.paypal.com/webapps/auth/protocol/openidconnect/v1/authorize";
    }

    @Override
    public String getAccessTokenEndpoint() {
        return "https://api.paypal.com/v1/identity/openidconnect/tokenservice";
    }
}
