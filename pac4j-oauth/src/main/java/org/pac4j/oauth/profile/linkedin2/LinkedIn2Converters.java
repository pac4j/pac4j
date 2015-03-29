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
package org.pac4j.oauth.profile.linkedin2;

import org.pac4j.oauth.profile.converter.XmlListConverter;
import org.pac4j.oauth.profile.converter.XmlObjectConverter;

/**
 * This class defines all the converters specific to LinkedIn.
 * 
 * @author Jerome Leleu
 * @since 1.4.1
 */
public final class LinkedIn2Converters {
    public final static XmlObjectConverter locationConverter = new XmlObjectConverter(LinkedIn2Location.class);
    
    public final static XmlListConverter positionsConverter = new XmlListConverter(LinkedIn2Position.class);
    
    public final static XmlObjectConverter dateConverter = new XmlObjectConverter(LinkedIn2Date.class);
    
    public final static XmlObjectConverter companyConverter = new XmlObjectConverter(LinkedIn2Company.class);
}
