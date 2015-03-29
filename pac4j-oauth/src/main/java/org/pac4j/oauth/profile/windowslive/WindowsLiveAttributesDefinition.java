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
package org.pac4j.oauth.profile.windowslive;

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.profile.OAuthAttributesDefinition;

/**
 * This class defines the attributes of the Windows Live profile.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class WindowsLiveAttributesDefinition extends OAuthAttributesDefinition {
    
    public static final String NAME = "name";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String LINK = "link";
    public static final String GENDER = "gender";
    public static final String LOCALE = "locale";
    public static final String UPDATED_TIME = "updated_time";
    
    public WindowsLiveAttributesDefinition() {
        addAttribute(NAME, Converters.stringConverter);
        addAttribute(FIRST_NAME, Converters.stringConverter);
        addAttribute(LAST_NAME, Converters.stringConverter);
        addAttribute(LINK, Converters.stringConverter);
        addAttribute(GENDER, Converters.genderConverter);
        addAttribute(LOCALE, Converters.localeConverter);
        addAttribute(UPDATED_TIME, Converters.dateConverter);
    }
}
