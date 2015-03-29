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

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.profile.OAuthAttributesDefinition;

/**
 * This class defines the attributes of the WordPress profile.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class WordPressAttributesDefinition extends OAuthAttributesDefinition {
    
    public static final String DISPLAY_NAME = "display_name";
    public static final String USERNAME = "username";
    public static final String EMAIL = "email";
    public static final String PRIMARY_BLOG = "primary_blog";
    public static final String AVATAR_URL = "avatar_URL";
    public static final String PROFILE_URL = "profile_URL";
    public static final String LINKS = "links";
    
    public WordPressAttributesDefinition() {
        addAttribute(DISPLAY_NAME, Converters.stringConverter);
        addAttribute(USERNAME, Converters.stringConverter);
        addAttribute(EMAIL, Converters.stringConverter);
        addAttribute(PRIMARY_BLOG, Converters.integerConverter);
        addAttribute(AVATAR_URL, Converters.urlConverter);
        addAttribute(PROFILE_URL, Converters.urlConverter);
        addAttribute(LINKS, WordPressConverters.linksConverter, false);
    }
}
