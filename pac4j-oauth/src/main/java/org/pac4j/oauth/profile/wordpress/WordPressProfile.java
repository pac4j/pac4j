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
package org.pac4j.oauth.profile.wordpress;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.oauth.profile.OAuthAttributesDefinitions;
import org.pac4j.oauth.profile.OAuth20Profile;

/**
 * <p>This class is the user profile for WordPress with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.WordPressClient}.</p>
 *
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class WordPressProfile extends OAuth20Profile {
    
    private static final long serialVersionUID = 6790248892408246089L;
    
    @Override
    protected AttributesDefinition getAttributesDefinition() {
        return OAuthAttributesDefinitions.wordPressDefinition;
    }
    
    @Override
    public String getPictureUrl() {
        return (String) getAttribute(WordPressAttributesDefinition.AVATAR_URL);
    }
    
    @Override
    public String getProfileUrl() {
        return (String) getAttribute(WordPressAttributesDefinition.PROFILE_URL);
    }
    
    public Integer getPrimaryBlog() {
        return (Integer) getAttribute(WordPressAttributesDefinition.PRIMARY_BLOG);
    }
    
    public WordPressLinks getLinks() {
        return (WordPressLinks) getAttribute(WordPressAttributesDefinition.LINKS);
    }
}
