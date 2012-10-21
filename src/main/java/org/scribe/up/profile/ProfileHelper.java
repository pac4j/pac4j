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
package org.scribe.up.profile;

/**
 * This class is an helper to find the kind of profile regarding the typed id profile.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class ProfileHelper {
    
    /**
     * Indicate if the user identifier matches this kind of profile.
     * 
     * @param id
     * @param clazz
     * @return if the user identifier matches this kind of profile
     */
    public static boolean isTypedIdOf(final String id, final Class<? extends UserProfile> clazz) {
        if (id != null && id.startsWith(clazz.getSimpleName() + UserProfile.SEPARATOR)) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Set whether the input data should be stored in object to be restored for CAS serialization when toString() is called. Save memory
     * also.
     * 
     * @param keepRawData
     */
    public static void setKeepRawData(final boolean keepRawData) {
        JsonObject.setKeepRawData(keepRawData);
    }
}
