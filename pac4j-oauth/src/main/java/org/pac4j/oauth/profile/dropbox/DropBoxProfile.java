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
package org.pac4j.oauth.profile.dropbox;

import java.util.Locale;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.oauth.profile.OAuth10Profile;

/**
 * <p>This class is the user profile for DropBox with appropriate getters.</p>
 * <p>It is returned by the {@link org.pac4j.oauth.client.DropBoxClient}.</p>
 *
 * @author Jerome Leleu
 * @since 1.2.0
 */
public class DropBoxProfile extends OAuth10Profile {
    
    private static final long serialVersionUID = 6671295443243112368L;

    private transient final static AttributesDefinition ATTRIBUTES_DEFINITION = new DropBoxAttributesDefinition();

    @Override
    public AttributesDefinition getAttributesDefinition() {
        return ATTRIBUTES_DEFINITION;
    }
    
    @Override
    public Locale getLocale() {
        return (Locale) getAttribute(DropBoxAttributesDefinition.COUNTRY);
    }
    
    @Override
    public String getProfileUrl() {
        return (String) getAttribute(DropBoxAttributesDefinition.REFERRAL_LINK);
    }
    
    public Long getNormal() {
        return (Long) getAttribute(DropBoxAttributesDefinition.NORMAL);
    }
    
    public Long getQuota() {
        return (Long) getAttribute(DropBoxAttributesDefinition.QUOTA);
    }
    
    public Long getShared() {
        return (Long) getAttribute(DropBoxAttributesDefinition.SHARED);
    }
}
