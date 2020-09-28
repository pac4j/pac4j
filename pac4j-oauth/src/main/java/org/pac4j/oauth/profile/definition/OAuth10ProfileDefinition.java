package org.pac4j.oauth.profile.definition;

import com.github.scribejava.core.model.OAuth1Token;
import org.pac4j.core.profile.factory.ProfileFactory;
import org.pac4j.oauth.config.OAuth10Configuration;
import org.pac4j.oauth.profile.OAuth10Profile;

/**
 * OAuth 1.0 profile definition.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public abstract class OAuth10ProfileDefinition<P extends OAuth10Profile>
    extends OAuthProfileDefinition<P, OAuth1Token, OAuth10Configuration> {

    public OAuth10ProfileDefinition() {
        super();
    }

    public OAuth10ProfileDefinition(final ProfileFactory profileFactory) {
        super(profileFactory);
    }
}
