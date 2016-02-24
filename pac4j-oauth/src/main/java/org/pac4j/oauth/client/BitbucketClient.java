package org.pac4j.oauth.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.scribejava.core.builder.api.Api;
import com.github.scribejava.core.model.Token;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.bitbucket.BitbucketProfile;
import org.pac4j.scribe.builder.api.BitBucketApi;

/**
 * This class is the OAuth client to authenticate users in Bitbucket.
 * 
 * It returns a {@link org.pac4j.oauth.profile.bitbucket.BitbucketProfile}.
 * 
 * @author Sebastian Sdorra
 * @since 1.5.1
 */
public class BitbucketClient extends BaseOAuth10Client<BitbucketProfile> {

    public BitbucketClient() {
    }

    public BitbucketClient(final String key, final String secret) {
        setKey(key);
        setSecret(secret);
    }

    @Override
    protected Api getApi() {
        return new BitBucketApi();
    }

    @Override
    protected String getProfileUrl(Token accessToken) {
        return "https://bitbucket.org/api/1.0/user/";
    }

    @Override
    protected BitbucketProfile extractUserProfile(String body) {
        BitbucketProfile profile = new BitbucketProfile();
        JsonNode json = JsonHelper.getFirstNode(body);
        if (json != null) {
            json = (JsonNode) JsonHelper.getElement(json, "user");
            if (json != null) {
                for (final String attribute : profile.getAttributesDefinition().getPrimaryAttributes()) {
                   profile.addAttribute(attribute, JsonHelper.getElement(json, attribute));
                }
            }
        }
       return profile;
    }
}
