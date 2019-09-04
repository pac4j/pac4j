package org.pac4j.oauth.config;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.state.StateGenerator;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.state.StaticOrRandomStateGenerator;

import java.util.HashMap;
import java.util.Map;

/**
 * The OAuh 2.0 configuration.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class OAuth20Configuration extends OAuthConfiguration<OAuth20Service, OAuth2AccessToken> {

    public static final String OAUTH_CODE = "code";

    public static final String STATE_REQUEST_PARAMETER = "state";

    private static final String STATE_SESSION_PARAMETER = "#oauth20StateParameter";

    /* Map containing user defined parameters */
    private Map<String, String> customParams = new HashMap<>();

    private boolean withState;

    private StateGenerator stateGenerator = new StaticOrRandomStateGenerator();

    private DefaultApi20 api;

    @Override
    protected void internalInit() {
        CommonHelper.assertNotNull("api", api);
        super.internalInit();
    }

    /**
     * Build an OAuth service from the web context.
     *
     * @param context the web context
     * @param client the client
     * @return the OAuth service
     */
    public OAuth20Service buildService(final WebContext context, final IndirectClient client) {
        init();

        final String finalCallbackUrl = client.computeFinalCallbackUrl(context);

        return api.createService(this.key, this.secret, finalCallbackUrl, this.scope, this.responseType, null, this.httpClientConfig, null);
    }

    /**
     * Return the name of the attribute storing the state in session.
     *
     * @param clientName the client name
     * @return the name of the attribute storing the state in session
     */
    public String getStateSessionAttributeName(final String clientName) {
        return clientName + STATE_SESSION_PARAMETER;
    }

    public Map<String, String> getCustomParams() {
        return customParams;
    }

    public void setCustomParams(final Map<String, String> customParams) {
        this.customParams = customParams;
    }

    public boolean isWithState() {
        return withState;
    }

    public void setWithState(final boolean withState) {
        this.withState = withState;
    }

    public StateGenerator getStateGenerator() {
        return stateGenerator;
    }

    public void setStateGenerator(final StateGenerator stateGenerator) {
        CommonHelper.assertNotNull("stateGenerator", stateGenerator);
        this.stateGenerator = stateGenerator;
    }

    public DefaultApi20 getApi() {
        return api;
    }

    public void setApi(final DefaultApi20 api) {
        this.api = api;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "key", key, "secret", "[protected]", "tokenAsHeader", tokenAsHeader,
            "responseType", responseType, "scope", scope, "api", api, "hasBeenCancelledFactory", hasBeenCancelledFactory,
            "profileDefinition", profileDefinition, "httpClientConfig", httpClientConfig);
    }
}
