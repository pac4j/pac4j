/*
  Copyright 2012 - 2014 Jerome Leleu

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
package org.pac4j.openid.profile.yahoo;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.core.profile.converter.Converters;

/**
 * This class defines the attributes of the {@link YahooOpenIdProfile}.
 * 
 * @author Patrice de Saint Steban
 * @since 1.6.0
 */
public class YahooOpenIdAttributesDefinition extends AttributesDefinition {
    
    public static final String EMAIL = "email";
    public static final String LANGUAGE = "language";
    public static final String FULLNAME = "fullname";
    public static final String PROFILEPICTURE = "picture_url";
    
    public YahooOpenIdAttributesDefinition() {
        addAttribute(EMAIL, Converters.stringConverter);
        addAttribute(LANGUAGE, Converters.localeConverter);
        addAttribute(FULLNAME, Converters.stringConverter);
        addAttribute(PROFILEPICTURE, Converters.genderConverter);
    }
}
