package org.pac4j.stormpath.credentials.authenticator;

import com.stormpath.sdk.account.Account;
import com.stormpath.sdk.application.Application;
import com.stormpath.sdk.authc.UsernamePasswordRequest;
import com.stormpath.sdk.client.Client;
import com.stormpath.sdk.client.DefaultApiKey;
import com.stormpath.sdk.resource.ResourceException;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.BadCredentialsException;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableWebObject;
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
 * @since 1.8.0
 */
public class StormpathAuthenticator extends InitializableWebObject
        implements Authenticator<UsernamePasswordCredentials> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private Application application;

    private String accessId;

    private String secretKey;

    private String applicationId;

    public StormpathAuthenticator() {}

    /**
     * @param accessId accessId provided by Stormpath, for the admin user with the created API key.
     * @param secretKey secret key provided by Stormpath, for the admin user with the created API key.
     * @param applicationId This is application id configured on Stormpath whose login source will be used to authenticate users.
     */
    public StormpathAuthenticator(final String accessId, final String secretKey,
                                  final String applicationId) {
        this.accessId = accessId;
        this.secretKey = secretKey;
        this.applicationId = applicationId;
    }

    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotBlank("accessId", accessId);
        CommonHelper.assertNotBlank("secretKey", secretKey);
        CommonHelper.assertNotBlank("applicationId", applicationId);

        try {
            final Client client = new Client(new DefaultApiKey(accessId, secretKey));
            this.application = client.getDataStore().getResource(
                    String.format("/applications/%s", applicationId), Application.class);
        } catch (final Exception e) {
            throw new BadCredentialsException("An exception is caught trying to access Stormpath cloud. " +
                    "Please verify that your provided Stormpath <accessId>, " +
                    "<secretKey>, and <applicationId> are correct. Original Stormpath error: " + e.getMessage());
        }
    }

    @Override
    public void validate(final UsernamePasswordCredentials credentials, final WebContext context) throws HttpAction {

        init(context);

        try {
            logger.debug("Attempting to authenticate user [{}] against application [{}] in Stormpath cloud...",
                    credentials.getUsername(), this.application.getName());
            final Account account = authenticateAccount(credentials);
            logger.debug("Successfully authenticated user [{}]", account.getUsername());

            final StormpathProfile profile = createProfile(account);
            credentials.setUserProfile(profile);
        } catch (final ResourceException e) {
            throw new BadCredentialsException("Bad credentials for: " + credentials.getUsername(), e);
        }
    }

    protected Account authenticateAccount(final UsernamePasswordCredentials credentials) throws ResourceException {
        return this.application.authenticateAccount(
                new UsernamePasswordRequest(credentials.getUsername(), credentials.getPassword())).getAccount();
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

    public String getAccessId() {
        return accessId;
    }

    public void setAccessId(String accessId) {
        this.accessId = accessId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }
}
