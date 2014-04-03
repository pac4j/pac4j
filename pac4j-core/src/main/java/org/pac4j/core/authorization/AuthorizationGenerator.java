/*
  Copyright 2012 - 2014 Jerome Leleu

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
package org.pac4j.core.authorization;

import org.pac4j.core.profile.CommonProfile;

/**
 * Generate the authorization information for this user profile.
 * 
 * @author Jerome Leleu
 * @since 1.5.0
 */
public interface AuthorizationGenerator<U extends CommonProfile> {
    
    /**
     * Generate the authorization information from and for the user profile.
     * 
     * @param profile the user profile for which to generate the authorization information.
     */
    void generate(U profile);
}
