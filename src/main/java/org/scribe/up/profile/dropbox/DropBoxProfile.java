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
package org.scribe.up.profile.dropbox;

import java.util.Locale;
import java.util.Map;

import org.scribe.up.profile.AttributesDefinition;
import org.scribe.up.profile.AttributesDefinitions;
import org.scribe.up.profile.UserProfile;

/**
 * This class is the user profile for DropBox with appropriate getters.
 * 
 * @author Jerome Leleu
 * @since 1.2.0
 */
public class DropBoxProfile extends UserProfile {
    
    private static final long serialVersionUID = 3593309519711828560L;
    
    protected AttributesDefinition getAttributesDefinition() {
        return AttributesDefinitions.dropBoxDefinition;
    }
    
    public DropBoxProfile() {
        super();
    }
    
    public DropBoxProfile(Object id) {
        super(id);
    }
    
    public DropBoxProfile(Object id, Map<String, Object> attributes) {
        super(id, attributes);
    }
    
    public String getReferralLink() {
        return (String) attributes.get(DropBoxAttributesDefinition.REFERRAL_LINK);
    }
    
    public String getDisplayName() {
        return (String) attributes.get(DropBoxAttributesDefinition.DISPLAY_NAME);
    }
    
    public Locale getCountry() {
        return (Locale) attributes.get(DropBoxAttributesDefinition.COUNTRY);
    }
    
    public long getNormal() {
        return getSafeLong((Long) attributes.get(DropBoxAttributesDefinition.NORMAL));
    }
    
    /**
     * Indicate if the normal attribute exists.
     * 
     * @return if the normal attribute exists
     */
    public boolean isNormalDefined() {
        return attributes.get(DropBoxAttributesDefinition.NORMAL) != null;
    }
    
    public long getQuota() {
        return getSafeLong((Long) attributes.get(DropBoxAttributesDefinition.QUOTA));
    }
    
    /**
     * Indicate if the quota attribute exists.
     * 
     * @return if the quota attribute exists
     */
    public boolean isQuotaDefined() {
        return attributes.get(DropBoxAttributesDefinition.QUOTA) != null;
    }
    
    public long getShared() {
        return getSafeLong((Long) attributes.get(DropBoxAttributesDefinition.SHARED));
    }
    
    /**
     * Indicate if the shared attribute exists.
     * 
     * @return if the shared attribute exists
     */
    public boolean isSharedDefined() {
        return attributes.get(DropBoxAttributesDefinition.SHARED) != null;
    }
}
