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
package org.pac4j.oauth.profile.dropbox;

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.profile.OAuthAttributesDefinition;

/**
 * This class defines the attributes of the DropBox profile.
 * 
 * @author Jerome Leleu
 * @since 1.2.0
 */
public class DropBoxAttributesDefinition extends OAuthAttributesDefinition {
    
    public static final String REFERRAL_LINK = "referral_link";
    public static final String DISPLAY_NAME = "display_name";
    public static final String COUNTRY = "country";
    public static final String SHARED = "shared";
    public static final String QUOTA = "quota";
    public static final String NORMAL = "normal";
    
    public DropBoxAttributesDefinition() {
        addAttribute(REFERRAL_LINK, Converters.stringConverter);
        addAttribute(DISPLAY_NAME, Converters.stringConverter);
        addAttribute(COUNTRY, Converters.localeConverter);
        addAttribute(SHARED, Converters.longConverter, false);
        addAttribute(QUOTA, Converters.longConverter, false);
        addAttribute(NORMAL, Converters.longConverter, false);
    }
}
