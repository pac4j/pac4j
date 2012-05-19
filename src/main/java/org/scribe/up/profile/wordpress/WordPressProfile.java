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
package org.scribe.up.profile.wordpress;

import java.util.Map;

import org.scribe.up.profile.AttributesDefinition;
import org.scribe.up.profile.AttributesDefinitions;
import org.scribe.up.profile.UserProfile;

/**
 * This class is the user profile for WordPress with appropriate getters.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class WordPressProfile extends UserProfile {
    
    private static final long serialVersionUID = -6217746917499078805L;
    
    protected AttributesDefinition getAttributesDefinition() {
        return AttributesDefinitions.wordPressDefinition;
    }
    
    public WordPressProfile() {
        super();
    }
    
    public WordPressProfile(Object id) {
        super(id);
    }
    
    public WordPressProfile(Object id, Map<String, Object> attributes) {
        super(id, attributes);
    }
    
    public String getDisplayName() {
        return (String) attributes.get(WordPressAttributesDefinition.DISPLAY_NAME);
    }
    
    public String getUsername() {
        return (String) attributes.get(WordPressAttributesDefinition.USERNAME);
    }
    
    public String getEmail() {
        return (String) attributes.get(WordPressAttributesDefinition.EMAIL);
    }
    
    public int getPrimaryBlog() {
        return getSafeInt((Integer) attributes.get(WordPressAttributesDefinition.PRIMARY_BLOG));
    }
    
    public boolean isPrimaryBlogDefined() {
        return attributes.get(WordPressAttributesDefinition.PRIMARY_BLOG) != null;
    }
    
    public String getAvatarUrl() {
        return (String) attributes.get(WordPressAttributesDefinition.AVATAR_URL);
    }
    
    public String getProfileUrl() {
        return (String) attributes.get(WordPressAttributesDefinition.PROFILE_URL);
    }
    
    public WordPressLinks getLinks() {
        return (WordPressLinks) attributes.get(WordPressAttributesDefinition.LINKS);
    }
}
