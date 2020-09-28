package org.pac4j.oauth.profile.definition;

import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.model.Verb;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.definition.CommonProfileDefinition;
import org.pac4j.core.profile.factory.ProfileFactory;
import org.pac4j.oauth.config.OAuthConfiguration;

import org.pac4j.core.exception.TechnicalException;

/**
 * OAuth profile definition.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public abstract class OAuthProfileDefinition<P extends CommonProfile, T extends Token, O extends OAuthConfiguration>
    extends CommonProfileDefinition {

    public OAuthProfileDefinition() {
        super();
    }

    public OAuthProfileDefinition(final ProfileFactory profileFactory) {
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
     * @param configuration the current configuration
     * @return the url of the user profile given by the provider
     */
    public abstract String getProfileUrl(T accessToken, O configuration);

    /**
     * Extract the user profile from the response (JSON, XML...) of the profile url.
     *
     * @param body the response body
     * @return the returned profile
     */
    public abstract P extractUserProfile(String body);

    /**
     * Throws a {@link TechnicalException} to indicate that user profile extraction has failed.
     *
     * @param body the request body that the user profile should be have been extracted from
     * @param missingNode the name of a JSON node that was found missing. may be omitted
     */
    protected void raiseProfileExtractionJsonError(String body, String missingNode) {
        logger.error("Unable to extract user profile as no JSON node '{}' was found in body: {}", missingNode, body);
        throw new TechnicalException("No JSON node '" + missingNode + "' to extract user profile from");
    }

    /**
     * Throws a {@link TechnicalException} to indicate that user profile extraction has failed.
     *
     * @param body the request body that the user profile should have been extracted from
     */
    protected void raiseProfileExtractionJsonError(String body) {
        logger.error("Unable to extract user profile as no JSON node was found in body: {}", body);
        throw new TechnicalException("No JSON node to extract user profile from");
    }

    /**
     * Throws a {@link TechnicalException} to indicate that user profile extraction has failed.
     *
     * @param body the request body that the user profile should have been extracted from
     */
    protected void raiseProfileExtractionError(String body) {
        logger.error("Unable to extract user profile from body: {}", body);
        throw new TechnicalException("Unable to extract user profile");
    }

}
