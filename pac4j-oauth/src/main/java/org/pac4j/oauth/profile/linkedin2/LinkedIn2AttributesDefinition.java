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
package org.pac4j.oauth.profile.linkedin2;

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.profile.OAuthAttributesDefinition;

/**
 * This class defines the attributes of the {@link LinkedIn2Profile}.
 * 
 * @author Jerome Leleu
 * @since 1.4.1
 */
public class LinkedIn2AttributesDefinition extends OAuthAttributesDefinition {
    
    public static final String FIRST_NAME = "first-name";
    public static final String LAST_NAME = "last-name";
    public static final String MAIDEN_NAME = "maiden-name";
    public static final String FORMATTED_NAME = "formatted-name";
    public static final String LOCATION = "location";
    public static final String EMAIL_ADDRESS = "email-address";
    public static final String HEADLINE = "headline";
    public static final String INDUSTRY = "industry";
    public static final String NUM_CONNECTIONS = "num-connections";
    public static final String SUMMARY = "summary";
    public static final String SPECIALTIES = "specialties";
    public static final String POSITIONS = "positions";
    public static final String PICTURE_URL = "picture-url";
    public static final String PUBLIC_PROFILE_URL = "public-profile-url";
    public static final String SITE_STANDARD_PROFILE_REQUEST = "site-standard-profile-request";
    
    public LinkedIn2AttributesDefinition() {
        addAttribute(FIRST_NAME, Converters.stringConverter);
        addAttribute(LAST_NAME, Converters.stringConverter);
        addAttribute(MAIDEN_NAME, Converters.stringConverter);
        addAttribute(FORMATTED_NAME, Converters.stringConverter);
        addAttribute(LOCATION, LinkedIn2Converters.locationConverter);
        addAttribute(EMAIL_ADDRESS, Converters.stringConverter);
        addAttribute(HEADLINE, Converters.stringConverter);
        addAttribute(INDUSTRY, Converters.stringConverter);
        addAttribute(NUM_CONNECTIONS, Converters.integerConverter);
        addAttribute(SUMMARY, Converters.stringConverter);
        addAttribute(SPECIALTIES, Converters.stringConverter);
        addAttribute(POSITIONS, LinkedIn2Converters.positionsConverter);
        addAttribute(PICTURE_URL, Converters.stringConverter);
        addAttribute(PUBLIC_PROFILE_URL, Converters.stringConverter);
        addAttribute(SITE_STANDARD_PROFILE_REQUEST, Converters.stringConverter, false);
    }
}
