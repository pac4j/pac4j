package org.pac4j.oauth.profile.definition;

import com.github.scribejava.core.model.OAuth2AccessToken;
import org.pac4j.core.profile.factory.ProfileFactory;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.profile.OAuth20Profile;

/**
 * OAuth 2.0 profile definition.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public abstract class OAuth20ProfileDefinition<P extends OAuth20Profile, C extends OAuth20Configuration>
    extends OAuthProfileDefinition<P, OAuth2AccessToken, C> {

    public OAuth20ProfileDefinition() {
        super();
    }

    public OAuth20ProfileDefinition(final ProfileFactory<P> profileFactory) {
        super(profileFactory);
    }
}
