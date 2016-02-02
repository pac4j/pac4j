/*
  Copyright 2012 - 2015 pac4j organization

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
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
