package org.pac4j.oauth.config;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
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
}
