package org.pac4j.oauth.config;

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import org.pac4j.oauth.client.OAuth20Client;

import java.util.HashMap;
import java.util.Map;

/**
 * The OAuh 2.0 configuration.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class OAuth20Configuration extends OAuthConfiguration<OAuth20Client, OAuth20Service, OAuth2AccessToken> {

    public static final String OAUTH_CODE = "code";

    public static final String STATE_REQUEST_PARAMETER = "state";

    private static final String STATE_SESSION_PARAMETER = "#oauth20StateParameter";

    /* Map containing user defined parameters */
    private Map<String, Object> customParams = new HashMap<>();

    private boolean withState;

    private String stateData;

    public String getStateSessionAttributeName() {
        return getClient().getName() + STATE_SESSION_PARAMETER;
    }

    public Map<String, Object> getCustomParams() {
        return customParams;
    }

    public Object getCustomParam(String name) {
        return (customParams!=null)?customParams.get(name):null;
    }

    public void setCustomParams(final Map<String, Object> customParams) {
        this.customParams = customParams;
    }

    public void addCustomParam(String name, Object value) {
        this.customParams.put(name, value);
    }    
    
    public boolean isWithState() {
        return withState;
    }

    public void setWithState(final boolean withState) {
        this.withState = withState;
    }

    public String getStateData() {
        return stateData;
    }

    public void setStateData(final String stateData) {
        this.stateData = stateData;
    }
}
