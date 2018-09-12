package org.pac4j.scribe.builder.api;

import com.github.scribejava.core.builder.api.DefaultApi20;
import java.util.HashMap;
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

    @Override
    public String getAuthorizationUrl(String responseType, String apiKey, String callback, String scope, String state,
            Map<String, String> additionalParams) {
        CommonHelper.assertNotBlank("callback", callback,
            "Must provide a valid url as callback. PayPal does not support OOB");
        
        if (additionalParams == null) {
            additionalParams = new HashMap<>();
        }
        final String nonce = System.currentTimeMillis() + CommonHelper.randomString(10);
        additionalParams.put("nonce", nonce);
        return super.getAuthorizationUrl(responseType, apiKey, callback, scope, state, additionalParams);
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
