package org.pac4j.oauth.client;

import com.github.scribejava.core.builder.api.DefaultApi20;
import com.github.scribejava.core.model.OAuthConfig;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.utils.OAuthEncoder;
import java.util.Map;
import java.util.logging.Logger;
import org.pac4j.core.context.WebContext;
import org.pac4j.oauth.profile.generic.GenericOAuth20Profile;
import org.pac4j.oauth.profile.generic.GenericOAuth20ProfileDefinition;

/**
 * <p>This class is a generic OAuth2 client to authenticate users in a standard OAuth2 server.</p>
 * <p>All configuration parameters can be specified setting the corresponding attribute.</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.generic.GenericOAuth20Profile}.</p>
 *
 * @author Julio Arrebola
 */
public class GenericOAuth20Client extends OAuth20Client<GenericOAuth20Profile> {

    private static final Logger LOG = Logger.getLogger(GenericOAuth20Client.class.getName());

    private String authUrl;
    private String tokenUrl;
    private String profileUrl;
    private String profilePath;
    private String profileMethod;
    private Map<String, String> profileAttrs;
    private Map<String, String> customParams;

    public GenericOAuth20Client() {
    }

    @Override
    protected void internalInit(final WebContext context) {

        LOG.info("InternalInit");

        GenericOAuth20Api genApi = new GenericOAuth20Api();
        genApi.setAuthorizationBaseUrl(authUrl);
        genApi.setAccessTokenEndpoint(tokenUrl);
        configuration.setApi(genApi);

        configuration.setCustomParams(customParams);

        setConfiguration(configuration);

        GenericOAuth20ProfileDefinition profileDefinition = new GenericOAuth20ProfileDefinition();
        profileDefinition.setFirstNodePath(profilePath);
        profileDefinition.setProfileMethod(profileMethod);
        profileDefinition.setProfileUrl(profileUrl);

        if (profileAttrs != null) {
            for (Map.Entry<String,String> entry : profileAttrs.entrySet()) {
                profileDefinition.profileAttribute(entry.getKey(), entry.getValue(), null);
            }
        }

        configuration.setProfileDefinition(profileDefinition);

        super.internalInit(context);
    }

    public void setAuthUrl(String authUrl) {
        this.authUrl = authUrl;
    }

    public void setTokenUrl(String tokenUrl) {
        this.tokenUrl = tokenUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public void setProfileNodePath(String profilePath) {
        this.profilePath = profilePath;
    }

    public void setProfileMethod(String profileMethod) {
        this.profileMethod = profileMethod;
    }

    public void setProfileAttrs(Map<String, String> profileAttrsMap) {
        this.profileAttrs = profileAttrsMap;
    }

    public void setCustomParams(Map<String, String> customParamsMap) {
        this.customParams = customParamsMap;
    }

    private static class GenericOAuth20Api extends DefaultApi20 {

        private final static String AUTHORIZATION_URL = "%s?response_type=code&client_id=%s&redirect_uri=%s";

        private String accessTokenEndpoint;
        private String authorizationBaseUrl;

        @Override
        public Verb getAccessTokenVerb() {
            return Verb.POST;
        }

        @Override
        public String getAccessTokenEndpoint() {
            return this.accessTokenEndpoint;
        }

        @Override
        protected String getAuthorizationBaseUrl() {
            return this.authorizationBaseUrl;
        }

        @Override
        public String getAuthorizationUrl(final OAuthConfig config, Map<String, String> additionalParams) {

            StringBuilder url = new StringBuilder(String.format(AUTHORIZATION_URL, getAuthorizationBaseUrl(),
                    config.getApiKey(), OAuthEncoder.encode(config.getCallback())));

            if (config.getScope() != null) {
                url.append("&scope=").append(OAuthEncoder.encode(config.getScope()));
            }

            if (config.getState() != null) {
                url.append("&state=").append(OAuthEncoder.encode(config.getState()));
            }

            if (additionalParams != null && !additionalParams.isEmpty()) {
                for (Map.Entry entry : additionalParams.entrySet()) {
                    if (entry.getValue() != null) {
                        url.append("&").append(entry.getKey()).append("=").append(OAuthEncoder.encode(entry.getValue().toString()));
                    }
                }
            }

            return url.toString();
        }

        private void setAuthorizationBaseUrl(String authUrl) {
            this.authorizationBaseUrl = authUrl;
        }

        protected void setAccessTokenEndpoint(String tokenUrl) {
            this.accessTokenEndpoint = tokenUrl;
        }

    }
}
