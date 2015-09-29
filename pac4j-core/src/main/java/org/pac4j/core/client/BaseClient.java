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
package org.pac4j.core.client;

import java.util.ArrayList;
import java.util.List;

import org.pac4j.core.authorization.AuthorizationGenerator;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>This class is the default implementation of an authentication client (whatever the protocol). It has the core concepts:</p>
 * <ul>
 * <li>The initialization process is handled by the {@link InitializableObject} inheritance, the {@link #internalInit()} must be implemented
 * in sub-classes. The {@link #init()} method must be called implicitly by the main methods of the {@link Client} interface, so that no explicit call is
 * required to initialize the client</li>
 * <li>The cloning process is handled by the {@link #clone()} method, the {@link #newClient()} method must be implemented in sub-classes to
 * create a new instance</li>
 * <li>The name of the client is handled through the {@link #setName(String)} and {@link #getName()} methods</li>
 * <li>The {@link #getClientType()} method returns the implemented {@link ClientType} by the client.</li>
 * <li>After retrieving the user profile, the client can generate the authorization information (roles, permissions and remember-me) by using
 * the appropriate {@link AuthorizationGenerator}.</li>
 * </ul>
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public abstract class BaseClient<C extends Credentials, U extends CommonProfile> extends InitializableObject implements
        Client<C, U>, Cloneable {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private String name;

    private List<AuthorizationGenerator<U>> authorizationGenerators = new ArrayList<AuthorizationGenerator<U>>();

    /**
     * Clone the current client.
     * 
     * @return the cloned client
     */
    @Override
    public BaseClient<C, U> clone() {
        final BaseClient<C, U> newClient = newClient();
        newClient.setName(this.name);
        return newClient;
    }

    /**
     * Create a new instance of the client.
     * 
     * @return A new instance of the client
     */
    protected abstract BaseClient<C, U> newClient();

    public void setName(final String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        if (CommonHelper.isBlank(this.name)) {
            return this.getClass().getSimpleName();
        }
        return this.name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final U getUserProfile(final C credentials, final WebContext context) {
        init();
        logger.debug("credentials : {}", credentials);
        if (credentials == null) {
            return null;
        }

        final U profile = retrieveUserProfile(credentials, context);
        if (this.authorizationGenerators != null) {
            for (AuthorizationGenerator<U> authorizationGenerator : this.authorizationGenerators) {
                authorizationGenerator.generate(profile);
            }
        }
        return profile;
    }

    protected abstract U retrieveUserProfile(final C credentials, final WebContext context);

    /**
     * Return the client type.
     * 
     * @return the client type
     */
    public abstract ClientType getClientType();

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "name", getName());
    }

    public void addAuthorizationGenerator(AuthorizationGenerator<U> authorizationGenerator) {
        if (this.authorizationGenerators != null) {
            this.authorizationGenerators.add(authorizationGenerator);
        }
    }

    public List<AuthorizationGenerator<U>> getAuthorizationGenerators() {
        return this.authorizationGenerators;
    }

    public void setAuthorizationGenerators(List<AuthorizationGenerator<U>> authorizationGenerators) {
        this.authorizationGenerators = authorizationGenerators;
    }

    /**
     * Use addAuthorizationGenerator instead.
     * 
     * @param authorizationGenerator an authorizations generator
     */
    @Deprecated
    public void setAuthorizationGenerator(final AuthorizationGenerator<U> authorizationGenerator) {
        addAuthorizationGenerator(authorizationGenerator);
    }
}
