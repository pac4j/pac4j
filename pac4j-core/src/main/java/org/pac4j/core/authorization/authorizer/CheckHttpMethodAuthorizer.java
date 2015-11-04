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
package org.pac4j.core.authorization.authorizer;

import org.pac4j.core.authorization.Authorizer;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.UserProfile;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.pac4j.core.context.HttpConstants.*;

/**
 * Checks the HTTP method.
 *
 * @author Jerome Leleu
 * @since 1.8.1
 */
public class CheckHttpMethodAuthorizer<U extends UserProfile> implements Authorizer<U> {

    private Set<HTTP_METHOD> methods;

    public CheckHttpMethodAuthorizer() { }

    public CheckHttpMethodAuthorizer(final HTTP_METHOD... methods) {
        setMethods(methods);
    }

    public CheckHttpMethodAuthorizer(final List<HTTP_METHOD> methods) {
        setMethods(methods);
    }

    public CheckHttpMethodAuthorizer(final Set<HTTP_METHOD> methods) {
        this.methods = methods;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAuthorized(final WebContext context, final U profile) {
        if (methods == null || methods.size() == 0) {
            return true;
        }
        final String requestMethod = context.getRequestMethod();
        for (final HTTP_METHOD method : methods) {
            if (requestMethod.equalsIgnoreCase(method.toString())) {
                return true;
            }
        }
        return false;
    }

    public Set<HTTP_METHOD> getMethods() {
        return methods;
    }

    public void setMethods(final Set<HTTP_METHOD> methods) {
        this.methods = methods;
    }

    public void setMethods(final List<HTTP_METHOD> methods) {
        if (methods != null) {
            this.methods = new HashSet<>(methods);
        }
    }

    public void setMethods(final HTTP_METHOD... methods) {
        if (methods != null) {
            setMethods(Arrays.asList(methods));
        }
    }
}
