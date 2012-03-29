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
import org.scribe.up.profile.converter.DateConverter;
import org.scribe.up.profile.converter.JsonObjectConverter;

/**
 * This class defines the Facebook work.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class FacebookWorkDefinition extends AttributesDefinition {
    
    public final static String EMPLOYER = "employer";
    public final static String LOCATION = "location";
    public final static String POSITION = "position";
    public final static String DESCRIPTION = "description";
    public final static String START_DATE = "start_date";
    public final static String END_DATE = "end_date";
    
    public FacebookWorkDefinition() {
        JsonObjectConverter facebookObjectConverter = new JsonObjectConverter(FacebookObject.class);
        attributes.add(EMPLOYER);
        converters.put(EMPLOYER, facebookObjectConverter);
        attributes.add(LOCATION);
        converters.put(LOCATION, facebookObjectConverter);
        attributes.add(POSITION);
        converters.put(POSITION, facebookObjectConverter);
        attributes.add(DESCRIPTION);
        converters.put(DESCRIPTION, stringConverter);
        DateConverter dateConverter = new DateConverter("yyyy-MM");
        attributes.add(START_DATE);
        converters.put(START_DATE, dateConverter);
        attributes.add(END_DATE);
        converters.put(END_DATE, dateConverter);
    }
}
