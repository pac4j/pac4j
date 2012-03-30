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
package org.scribe.up.profile.facebook;

import org.scribe.up.profile.AttributesDefinition;
import org.scribe.up.profile.converter.JsonListConverter;
import org.scribe.up.profile.converter.JsonObjectConverter;

/**
 * This class defines the Facebook education object.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class FacebookEducationDefinition extends AttributesDefinition {
    
    public static final String SCHOOL = "school";
    public static final String DEGREE = "degree";
    public static final String YEAR = "year";
    public static final String CONCENTRATION = "concentration";
    public static final String TYPE = "type";
    
    public FacebookEducationDefinition() {
        JsonObjectConverter facebookObjectConverter = new JsonObjectConverter(FacebookObject.class);
        attributes.add(SCHOOL);
        converters.put(SCHOOL, facebookObjectConverter);
        attributes.add(DEGREE);
        converters.put(DEGREE, facebookObjectConverter);
        attributes.add(YEAR);
        converters.put(YEAR, facebookObjectConverter);
        attributes.add(CONCENTRATION);
        converters.put(CONCENTRATION, new JsonListConverter(FacebookObject.class));
        attributes.add(TYPE);
        converters.put(TYPE, stringConverter);
    }
}
