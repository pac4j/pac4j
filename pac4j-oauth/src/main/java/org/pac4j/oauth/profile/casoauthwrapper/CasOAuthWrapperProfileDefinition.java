package org.pac4j.oauth.profile.casoauthwrapper;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.scribejava.core.model.Token;
import lombok.val;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.config.OAuthConfiguration;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.definition.OAuthProfileDefinition;
import org.pac4j.scribe.builder.api.CasOAuthWrapperApi20;

import static org.pac4j.core.profile.AttributeLocation.PROFILE_ATTRIBUTE;

/**
 * {@link CasOAuthWrapperProfile} profile definition.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class CasOAuthWrapperProfileDefinition extends OAuthProfileDefinition {

    /** Constant <code>IS_FROM_NEW_LOGIN="isFromNewLogin"</code> */
    public static final String IS_FROM_NEW_LOGIN = "isFromNewLogin";
    /** Constant <code>AUTHENTICATION_DATE="authenticationDate"</code> */
    public static final String AUTHENTICATION_DATE = "authenticationDate";
    /** Constant <code>AUTHENTICATION_METHOD="authenticationMethod"</code> */
    public static final String AUTHENTICATION_METHOD = "authenticationMethod";
    /** Constant <code>SUCCESSFUL_AUTHENTICATION_HANDLERS="successfulAuthenticationHandlers"</code> */
    public static final String SUCCESSFUL_AUTHENTICATION_HANDLERS = "successfulAuthenticationHandlers";
    /** Constant <code>LONG_TERM_AUTHENTICATION_REQUEST_TOKEN_USED="longTermAuthenticationRequestTokenUsed"</code> */
    public static final String LONG_TERM_AUTHENTICATION_REQUEST_TOKEN_USED = "longTermAuthenticationRequestTokenUsed";

    /**
     * <p>Constructor for CasOAuthWrapperProfileDefinition.</p>
     */
    public CasOAuthWrapperProfileDefinition() {
        super(x -> new CasOAuthWrapperProfile());
        primary(IS_FROM_NEW_LOGIN, Converters.BOOLEAN);
        primary(AUTHENTICATION_DATE, new CasAuthenticationDateFormatter());
        primary(AUTHENTICATION_METHOD, Converters.STRING);
        primary(SUCCESSFUL_AUTHENTICATION_HANDLERS, Converters.STRING);
        primary(LONG_TERM_AUTHENTICATION_REQUEST_TOKEN_USED, Converters.BOOLEAN);
    }

    /** {@inheritDoc} */
    @Override
    public String getProfileUrl(final Token accessToken, final OAuthConfiguration configuration) {
        return ((CasOAuthWrapperApi20) configuration.getApi()).getCasServerUrl() + "/profile";
    }

    /** {@inheritDoc} */
    @Override
    public CasOAuthWrapperProfile extractUserProfile(final String body) {
        val profile = (CasOAuthWrapperProfile) newProfile();
        val attributesNode = "attributes";
        var json = JsonHelper.getFirstNode(body);
        if (json != null) {
            profile.setId(ProfileHelper.sanitizeIdentifier(JsonHelper.getElement(json, "id")));
            json = json.get(attributesNode);
            if (json != null) {
                // CAS <= v4.2
                if (json instanceof ArrayNode) {
                    val nodes = json.iterator();
                    while (nodes.hasNext()) {
                        json = nodes.next();
                        val attribute = json.fieldNames().next();
                        convertAndAdd(profile, PROFILE_ATTRIBUTE, attribute, JsonHelper.getElement(json, attribute));
                    }
                    // CAS v5
                } else if (json instanceof ObjectNode) {
                    val keys = json.fieldNames();
                    while (keys.hasNext()) {
                        val key = keys.next();
                        convertAndAdd(profile, PROFILE_ATTRIBUTE, key, JsonHelper.getElement(json, key));
                    }
                }
            } else {
                raiseProfileExtractionJsonError(body, attributesNode);
            }
        } else {
            raiseProfileExtractionJsonError(body);
        }
        return profile;
    }
}
