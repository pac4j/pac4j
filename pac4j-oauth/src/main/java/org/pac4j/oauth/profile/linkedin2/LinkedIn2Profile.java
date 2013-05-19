/*
  Copyright 2012 - 2013 Jerome Leleu

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

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.oauth.profile.OAuth20Profile;
import org.pac4j.oauth.profile.OAuthAttributesDefinitions;

/**
 * @author Jerome Leleu
 * @since 1.4.1
 */
public class LinkedIn2Profile extends OAuth20Profile {
    
    private static final long serialVersionUID = 5106104495553943320L;
    
    @Override
    protected AttributesDefinition getAttributesDefinition() {
        return OAuthAttributesDefinitions.linkedin2Definition;
    }
    
    @Override
    public String getFirstName() {
        return (String) getAttribute(LinkedIn2AttributesDefinition.FIRST_NAME);
    }
    
    @Override
    public String getFamilyName() {
        return (String) getAttribute(LinkedIn2AttributesDefinition.LAST_NAME);
    }
    
    @Override
    public String getLocation() {
        LinkedIn2Location location = (LinkedIn2Location) getAttribute("location");
        return location.getName();
    }
}
