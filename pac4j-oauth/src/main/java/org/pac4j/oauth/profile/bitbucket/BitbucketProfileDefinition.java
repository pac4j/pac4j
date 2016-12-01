package org.pac4j.oauth.profile.bitbucket;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.core.model.OAuth1Token;
import org.pac4j.core.context.Pac4jConstants;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.config.OAuth10Configuration;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.definition.OAuth10ProfileDefinition;

import java.util.Arrays;

/**
 * This class is the Bitbucket profile definition.
 * 
 * @author Sebastian Sdorra
 * @since 1.5.1
 */
public class BitbucketProfileDefinition extends OAuth10ProfileDefinition<BitbucketProfile> {

    public static final String LAST_NAME = "last_name";
    public static final String IS_TEAM = "is_team";
    public static final String AVATAR = "avatar";
    public static final String RESOURCE_URI = "resource_uri";

    public BitbucketProfileDefinition() {
        super(x -> new BitbucketProfile());
        Arrays.stream(new String[] { Pac4jConstants.USERNAME, LAST_NAME })
                .forEach(a -> primary(a, Converters.STRING));
        primary(IS_TEAM, Converters.BOOLEAN);
        primary(AVATAR, Converters.URL);
        primary(RESOURCE_URI, Converters.URL);
    }

    @Override
    public String getProfileUrl(final OAuth1Token token, final OAuth10Configuration configuration) {
        return "https://bitbucket.org/api/1.0/user/";
    }

    @Override
    public BitbucketProfile extractUserProfile(final String body) throws HttpAction {
        final BitbucketProfile profile = newProfile();
        JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            json = (JsonNode) JsonHelper.getElement(json, "user");
            if (json != null) {
                profile.setId(JsonHelper.getElement(json, Pac4jConstants.USERNAME));
                for (final String attribute : getPrimaryAttributes()) {
                    convertAndAdd(profile, attribute, JsonHelper.getElement(json, attribute));
                }
            }
        }
        return profile;
    }
}
