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
package org.pac4j.openid.profile.google;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.core.profile.converter.Converters;

/**
 * This class defines the attributes of the {@link GoogleOpenIdProfile}.
 * 
 * @author Stephane Gleizes
 * @since 1.4.1
 */
public class GoogleOpenIdAttributesDefinition extends AttributesDefinition {
    
    public static final String COUNTRY = "country";
    public static final String EMAIL = "email";
    public static final String FIRSTNAME = "firstname";
    public static final String LANGUAGE = "language";
    public static final String LASTNAME = "lastname";
    
    public GoogleOpenIdAttributesDefinition() {
        addAttribute(COUNTRY, Converters.stringConverter);
        addAttribute(EMAIL, Converters.stringConverter);
        addAttribute(FIRSTNAME, Converters.stringConverter);
        addAttribute(LANGUAGE, Converters.localeConverter);
        addAttribute(LASTNAME, Converters.stringConverter);
    }
}
