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

import org.scribe.up.profile.converter.DateConverter;
import org.scribe.up.profile.converter.FormattedDateConverter;
import org.scribe.up.profile.converter.JsonListConverter;
import org.scribe.up.profile.converter.JsonObjectConverter;

/**
 * This class defines all the converters specific to Facebook.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class FacebookConverters {
    
    public final static FormattedDateConverter birthdayConverter = new FormattedDateConverter("MM/dd/yyyy");
    
    public final static FacebookRelationshipStatusConverter relationshipStatusConverter = new FacebookRelationshipStatusConverter();
    
    public final static JsonListConverter listObjectConverter = new JsonListConverter(FacebookObject.class);
    
    public final static JsonListConverter listEducationConverter = new JsonListConverter(FacebookEducation.class);
    
    public final static JsonObjectConverter objectConverter = new JsonObjectConverter(FacebookObject.class);
    
    public final static JsonListConverter listWorkConverter = new JsonListConverter(FacebookWork.class);
    
    public final static DateConverter workDateConverter = new DateConverter("yyyy-MM");
}
