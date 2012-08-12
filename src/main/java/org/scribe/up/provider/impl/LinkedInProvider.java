/*
  Copyright 2012 Jerome Leleu

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
package org.scribe.up.provider.impl;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.LinkedInApi;
import org.scribe.up.profile.AttributesDefinitions;
import org.scribe.up.profile.UserProfile;
import org.scribe.up.profile.linkedin.LinkedInAttributesDefinition;
import org.scribe.up.profile.linkedin.LinkedInProfile;
import org.scribe.up.provider.BaseOAuth10Provider;
import org.scribe.up.util.StringHelper;

/**
 * This class is the OAuth provider to authenticate user in LinkedIn. Scope is not used.<br />
 * Attributes (Java type) available in {@link org.scribe.up.profile.linkedin.LinkedInProfile} : first-name (String), last-name (String),
 * headline (String) and url (String).<br />
 * More information at https://developer.linkedin.com/documents/profile-api
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public class LinkedInProvider extends BaseOAuth10Provider {
    
    protected LinkedInProvider newProvider() {
        return new LinkedInProvider();
    }
    
    @Override
    protected void internalInit() {
        service = new ServiceBuilder().provider(LinkedInApi.class).apiKey(key).apiSecret(secret).callback(callbackUrl)
            .build();
    }
    
    @Override
    protected String getProfileUrl() {
        return "http://api.linkedin.com/v1/people/~";
    }
    
    @Override
    protected UserProfile extractUserProfile(String body) {
        LinkedInProfile profile = new LinkedInProfile();
        for (String attribute : AttributesDefinitions.linkedinDefinition.getAllAttributes()) {
            String value = StringHelper.substringBetween(body, "<" + attribute + ">", "</" + attribute + ">");
            profile.addAttribute(attribute, value);
            if (LinkedInAttributesDefinition.URL.equals(attribute)) {
                String id = StringHelper.substringBetween(value, "&amp;key=", "&amp;authToken=");
                profile.setId(id);
            }
        }
        return profile;
    }
}
