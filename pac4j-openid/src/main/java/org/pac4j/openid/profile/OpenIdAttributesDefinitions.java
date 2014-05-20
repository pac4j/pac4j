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
package org.pac4j.openid.profile;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.openid.profile.google.GoogleOpenIdAttributesDefinition;
import org.pac4j.openid.profile.yahoo.YahooOpenIdAttributesDefinition;

/**
 * This class defines all the attributes definitions.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class OpenIdAttributesDefinitions {
    
    public static final AttributesDefinition googleOpenIdDefinition = new GoogleOpenIdAttributesDefinition();
    public static final AttributesDefinition yahooOpenIdDefinition = new YahooOpenIdAttributesDefinition();
}
