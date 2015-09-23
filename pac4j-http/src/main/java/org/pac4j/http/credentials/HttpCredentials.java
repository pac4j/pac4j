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
package org.pac4j.http.credentials;

import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.profile.UserProfile;

/**
 * <p>This credentials is retrieved from a HTTP request.</p>
 * <p>A user profile can be attached with the credentials if it has been created by a {@link org.pac4j.http.credentials.authenticator.Authenticator}.
 * In that case, the {@link org.pac4j.http.profile.creator.AuthenticatorProfileCreator} must be used to retrieve the attached user profile.</p>
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public abstract class HttpCredentials extends Credentials {

    private UserProfile userProfile = null;

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public abstract boolean equals(Object o);

    public abstract int hashCode();
}
