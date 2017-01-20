package org.pac4j.oauth.client;

import java.util.Map;
import java.util.logging.Logger;

import com.github.scribejava.core.model.Verb;
import org.pac4j.core.context.WebContext;
import org.pac4j.oauth.profile.OAuth20Profile;
import org.pac4j.oauth.profile.generic.GenericOAuth20ProfileDefinition;
import org.pac4j.scribe.builder.api.GenericApi20;

/**
 * <p>This class is a generic OAuth2 client to authenticate users in a standard OAuth2 server.</p>
 * <p>All configuration parameters can be specified setting the corresponding attribute.</p>
 * <p>It returns a {@link org.pac4j.oauth.profile.OAuth20Profile}.</p>
 *
 * @author Julio Arrebola
 */
public class GenericOAuth20Client extends OAuth20Client<OAuth20Profile> {

    private static final Logger LOG = Logger.getLogger(GenericOAuth20Client.class.getName());

    private String authUrl;
    private String tokenUrl;
    private String profileUrl;
    private String profilePath;
    private Verb profileVerb;
    private Map<String, String> profileAttrs;
    private Map<String, String> customParams;

    public GenericOAuth20Client() {
    }

    @Override
    protected void clientInit(final WebContext context) {

        LOG.info("InternalInit");

        GenericApi20 genApi = new GenericApi20(authUrl, tokenUrl);
        configuration.setApi(genApi);

        configuration.setCustomParams(customParams);

        setConfiguration(configuration);

        GenericOAuth20ProfileDefinition profileDefinition = new GenericOAuth20ProfileDefinition();
        profileDefinition.setFirstNodePath(profilePath);
        profileDefinition.setProfileVerb(profileVerb);
        profileDefinition.setProfileUrl(profileUrl);

        if (profileAttrs != null) {
            for (Map.Entry<String,String> entry : profileAttrs.entrySet()) {
                profileDefinition.profileAttribute(entry.getKey(), entry.getValue(), null);
            }
        }

        configuration.setProfileDefinition(profileDefinition);

        super.clientInit(context);
    }

    public void setAuthUrl(final String authUrl) {
        this.authUrl = authUrl;
    }

    public void setTokenUrl(final String tokenUrl) {
        this.tokenUrl = tokenUrl;
    }

    public void setProfileUrl(final String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public void setProfileNodePath(final String profilePath) {
        this.profilePath = profilePath;
    }

    public void setProfileVerb(final Verb profileVerb) {
        this.profileVerb = profileVerb;
    }

    public void setProfileAttrs(final Map<String, String> profileAttrsMap) {
        this.profileAttrs = profileAttrsMap;
    }

    public void setCustomParams(final Map<String, String> customParamsMap) {
        this.customParams = customParamsMap;
    }
}
