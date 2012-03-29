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

/**
 * This class defines the common Facebook object.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public class FacebookObjectDefinition extends AttributesDefinition {
    
    public static final String ID = "id";
    public static final String NAME = "name";
    
    public FacebookObjectDefinition() {
        attributes.add(ID);
        converters.put(ID, stringConverter);
        attributes.add(NAME);
        converters.put(NAME, stringConverter);
    }
}
