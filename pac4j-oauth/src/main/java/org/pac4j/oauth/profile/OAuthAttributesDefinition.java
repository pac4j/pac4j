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
package org.pac4j.oauth.profile;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.core.profile.converter.Converters;

/**
 * This class is the definition of the attributes for an OAuth profile (just the access token).
 * 
 * @author Jerome Leleu
 * @since 1.3.0
 */
public class OAuthAttributesDefinition extends AttributesDefinition {
    
    public transient static final String ACCESS_TOKEN = "access_token";
    
    public transient static final String ACCESS_SECRET = "access_secret";
    
    public OAuthAttributesDefinition() {
        addAttribute(ACCESS_TOKEN, Converters.stringConverter, false);
        addAttribute(ACCESS_SECRET, Converters.stringConverter, false);
    }
}
