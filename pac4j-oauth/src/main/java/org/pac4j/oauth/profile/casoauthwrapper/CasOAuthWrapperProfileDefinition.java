package org.pac4j.oauth.profile.casoauthwrapper;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.scribejava.core.model.OAuth2AccessToken;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.client.CasOAuthWrapperClient;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.definition.OAuth20ProfileDefinition;

import java.util.Iterator;

/**
 * {@link CasOAuthWrapperProfile} profile definition.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class CasOAuthWrapperProfileDefinition extends OAuth20ProfileDefinition<CasOAuthWrapperProfile> {

    public static final String IS_FROM_NEW_LOGIN = "isFromNewLogin";
    public static final String AUTHENTICATION_DATE = "authenticationDate";
    public static final String AUTHENTICATION_METHOD = "authenticationMethod";
    public static final String SUCCESSFUL_AUTHENTICATION_HANDLERS = "successfulAuthenticationHandlers";
    public static final String LONG_TERM_AUTHENTICATION_REQUEST_TOKEN_USED = "longTermAuthenticationRequestTokenUsed";

    public CasOAuthWrapperProfileDefinition() {
        super(x -> new CasOAuthWrapperProfile());
        primary(IS_FROM_NEW_LOGIN, Converters.BOOLEAN);
        primary(AUTHENTICATION_DATE, new CasAuthenticationDateFormatter());
        primary(AUTHENTICATION_METHOD, Converters.STRING);
        primary(SUCCESSFUL_AUTHENTICATION_HANDLERS, Converters.STRING);
        primary(LONG_TERM_AUTHENTICATION_REQUEST_TOKEN_USED, Converters.BOOLEAN);
    }

    @Override
    public String getProfileUrl(final OAuth2AccessToken accessToken, final OAuth20Configuration configuration) {
        return ((CasOAuthWrapperClient) configuration.getClient()).getCasOAuthUrl() + "/profile";
    }

    @Override
    public CasOAuthWrapperProfile extractUserProfile(final String body) throws HttpAction {
        final CasOAuthWrapperProfile profile = newProfile();
        JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            profile.setId(JsonHelper.getElement(json, "id"));
            json = json.get("attributes");
            if (json != null) {
                // CAS <= v4.2
                if (json instanceof ArrayNode) {
                    final Iterator<JsonNode> nodes = json.iterator();
                    while (nodes.hasNext()) {
                        json = nodes.next();
                        final String attribute = json.fieldNames().next();
                        convertAndAdd(profile, attribute, JsonHelper.getElement(json, attribute));
                    }
                    // CAS v5
                } else if (json instanceof ObjectNode) {
                    final Iterator<String> keys = json.fieldNames();
                    while (keys.hasNext()) {
                        final String key = keys.next();
                        convertAndAdd(profile, key, JsonHelper.getElement(json, key));
                    }
                }
            }
        }
        return profile;
    }
}
