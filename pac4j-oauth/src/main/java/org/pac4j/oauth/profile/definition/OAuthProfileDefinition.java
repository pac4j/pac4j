package org.pac4j.oauth.profile.definition;

import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verb;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.definition.CommonProfileDefinition;

import java.util.function.Function;

/**
 * OAuth profile definition.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public abstract class OAuthProfileDefinition<P extends CommonProfile, T extends Token> extends CommonProfileDefinition<P> {

    public OAuthProfileDefinition() {
        super();
    }

    public OAuthProfileDefinition(final Function<Object[], P> profileFactory) {
        super(profileFactory);
    }

    /**
     * Get HTTP Method to request profile.
     *
     * @return http verb
     */
    public Verb getProfileVerb() {
        return Verb.GET;
    }

    /**
     * Retrieve the url of the profile of the authenticated user for the provider.
     *
     * @param accessToken only used when constructing dynamic urls from data in the token
     * @return the url of the user profile given by the provider
     */
    public abstract String getProfileUrl(final T accessToken);

    /**
     * Extract the user profile from the response (JSON, XML...) of the profile url.
     *
     * @param body the response body
     * @return the returned profile
     */
    public abstract P extractUserProfile(final String body);
}
