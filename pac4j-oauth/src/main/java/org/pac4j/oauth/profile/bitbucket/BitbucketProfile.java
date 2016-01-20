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
package org.pac4j.oauth.profile.bitbucket;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.oauth.profile.OAuth10Profile;

/**
 * <p>This class is the user profile for Bitbucket with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.BitbucketClient}.</p>
 *
 * @author Sebastian Sdorra
 * @since 1.5.1
 */
public class BitbucketProfile extends OAuth10Profile {
    
    private static final long serialVersionUID = -8943779913358140436L;

    private transient final static AttributesDefinition ATTRIBUTES_DEFINITION = new BitbucketAttributesDefinition();

    @Override
    public AttributesDefinition getAttributesDefinition() {
        return ATTRIBUTES_DEFINITION;
    }
    
    @Override
    public String getFamilyName() {
        return (String) getAttribute(BitbucketAttributesDefinition.LAST_NAME);
    }
    
    @Override
    public String getPictureUrl() {
        return (String) getAttribute(BitbucketAttributesDefinition.AVATAR);
    }
    
    @Override
    public String getProfileUrl() {
        return (String) getAttribute(BitbucketAttributesDefinition.RESOURCE_URI);
    }
    
    public boolean isTeam() {
        return (Boolean) getAttribute(BitbucketAttributesDefinition.IS_TEAM);
    }
}
