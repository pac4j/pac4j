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
package org.pac4j.core.profile;

import org.pac4j.core.authorization.Authorizer;
import org.pac4j.core.context.WebContext;

/**
 * <p>This abstract class is to be implemented in various implementation libraries,
 * to manage the current user profile, i.e. the one of the current authenticated user.</p>
 * <p>The constructors of the subclasses will tell which inputs are required in the specific libraries.</p>
 *
 * 
 * @author Jerome Leleu
 * @since 1.8.0
 */
public abstract class ProfileManager<C extends CommonProfile> {

    /**
     * Retrieve the user profile.
     *
     * @return the user profile
     */
    public abstract C get();

    /**
     * Remove the current user profile.
     */
    public abstract void remove();

    /**
     * Save the given user profile as the current one.
     *
     * @param profile a given user profile
     */
    public abstract void save(final C profile);

    /**
     * Perform a logout by removing the current user profile.
     */
    public void logout() {
        remove();
    }

    /**
     * Tests if the current is authenticated (meaning a user profile exists).
     *
     * @return whether the current user is authenticated
     */
    public boolean isAuthenticated() {
        return get() != null;
    }

    /**
     * Tests whether the current user is authorized.
     *
     * @param context the web context
     * @param authorizer a given authorizer
     * @return whether the user is authorized
     */
    public boolean isAuthorized(final WebContext context, final Authorizer<C> authorizer) {
        return authorizer.isAuthorized(context, get());
    }
}
