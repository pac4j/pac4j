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
package org.pac4j.stormpath.credentials.authenticator;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.UsernamePasswordRequest;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.DefaultApiKey;
import com.stormpath.sdk.resource.ResourceException;
import org.pac4j.core.exception.BadCredentialsException;
import org.pac4j.http.credentials.UsernamePasswordCredentials;
import org.pac4j.http.credentials.authenticator.AbstractUsernamePasswordAuthenticator;
import org.pac4j.stormpath.profile.StormpathProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * An authentication handler for <a href="http://www.stormpath.com">Stormpath</a>.
 * This implementation uses Stormpath's
 * <a href="https://github.com/stormpath/stormpath-sdk-java/wiki">Java SDK</a>
 *
 * @author Misagh Moayyed
 * @since 1.8
 */
public class StormpathAuthenticator extends AbstractUsernamePasswordAuthenticator {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final Application application;

    /**
     * Receives the Stormpath admin credentials and applicationId and sets up and instance of a Stormpath's Application resource
     * which will be used to authenticate users.
     *
     * @param stormpathAccessId  accessId provided by Stormpath, for the admin user with the created API key.
     * @param stormpathSecretKey secret key provided by Stormpath, for the admin user with the created API key.
     * @param applicationId      This is application id configured on Stormpath whose login source will be used to authenticate users.
     */
    public StormpathAuthenticator(final String stormpathAccessId, final String stormpathSecretKey,
                                  final String applicationId) {
        try {
            final Client client = new Client(new DefaultApiKey(stormpathAccessId, stormpathSecretKey));
            this.application = client.getDataStore().getResource(
                    String.format("/applications/%s", applicationId), Application.class);
        }
        catch (Throwable e) {
            throw new BadCredentialsException("An exception is caught trying to access Stormpath cloud. " +
                    "Please verify that your provided Stormpath <accessId>, " +
                    "<secretKey>, and <applicationId> are correct. Original Stormpath error: " + e.getMessage());
        }
    }

    @Override
    public void validate(final UsernamePasswordCredentials credentials) {
        try {
            logger.debug("Attempting to authenticate user [{}] against application [{}] in Stormpath cloud...",
                    credentials.getUsername(), this.application.getName());
            final Account account = authenticateAccount(credentials);
            logger.debug("Successfully authenticated user [{}]", account.getUsername());

            final StormpathProfile profile = createProfile(account);
            credentials.setUserProfile(profile);
        }
        catch (final ResourceException e) {
            logger.error(e.getMessage(), e);
            throw new BadCredentialsException("Bad credentials for: " + credentials.getUsername());
        }
    }

    protected Account authenticateAccount(final UsernamePasswordCredentials credentials) throws ResourceException {
        final String expectedPassword = passwordEncoder.encode(credentials.getPassword());

        return this.application.authenticateAccount(
                new UsernamePasswordRequest(credentials.getUsername(), expectedPassword)).getAccount();
    }

    protected StormpathProfile createProfile(final Account account) {
        final StormpathProfile profile = new StormpathProfile();
        profile.setId(account.getUsername());
        profile.addAttributes(buildAttributesFromStormpathAccount(account));
        return profile;
    }

    protected Map<String, Object> buildAttributesFromStormpathAccount(final Account account) {
        final Map<String, Object> attributes = new HashMap<>();
        attributes.put("fullName", account.getFullName());
        attributes.put("email", account.getEmail());
        attributes.put("givenName", account.getGivenName());
        attributes.put("middleName", account.getMiddleName());
        attributes.put("surName", account.getSurname());
        attributes.put("groups", account.getGroups());
        attributes.put("groupMemberships", account.getGroupMemberships());
        attributes.put("status", account.getStatus());
        return attributes;
    }
}
