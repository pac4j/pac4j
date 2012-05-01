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
package org.scribe.up.profile.linkedin;

import org.scribe.up.profile.AttributesDefinition;
import org.scribe.up.profile.converter.Converters;

/**
 * This class defines the attributes of the LinkedIn profile.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class LinkedInAttributesDefinition extends AttributesDefinition {
    
    public static final String FIRST_NAME = "first-name";
    public static final String LAST_NAME = "last-name";
    public static final String HEADLINE = "headline";
    public static final String URL = "url";
    
    public LinkedInAttributesDefinition() {
        attributes.add(FIRST_NAME);
        converters.put(FIRST_NAME, Converters.stringConverter);
        attributes.add(LAST_NAME);
        converters.put(LAST_NAME, Converters.stringConverter);
        attributes.add(HEADLINE);
        converters.put(HEADLINE, Converters.stringConverter);
        attributes.add(URL);
        converters.put(URL, Converters.stringConverter);
    }
}
