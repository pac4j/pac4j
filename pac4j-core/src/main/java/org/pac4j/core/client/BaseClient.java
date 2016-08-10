package org.pac4j.core.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.pac4j.core.authorization.generator.AuthorizationGenerator;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableWebObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>This class is the default implementation of an authentication client (whatever the mechanism). It has the core concepts:</p>
 * <ul>
 * <li>The initialization process is handled by the {@link InitializableWebObject} inheritance, the {@link #internalInit(WebContext)} must be implemented
 * in sub-classes. The {@link #init(WebContext)} method must be called implicitly by the main methods of the {@link Client} interface, so that no explicit call is
 * required to initialize the client</li>
 * <li>The name of the client is handled through the {@link #setName(String)} and {@link #getName()} methods</li>
 * <li>After retrieving the user profile, the client can generate the authorization information (roles, permissions and remember-me) by using
 * the appropriate {@link AuthorizationGenerator}.</li>
 * </ul>
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public abstract class BaseClient<C extends Credentials, U extends CommonProfile> extends InitializableWebObject implements Client<C, U> {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private String name;

    private List<AuthorizationGenerator<U>> authorizationGenerators = new ArrayList<>();

    /**
     * Retrieve the credentials.
     *
     * @param context the web context
     * @return the credentials
     * @throws HttpAction whether an additional HTTP action is required
     */
    protected abstract C retrieveCredentials(final WebContext context) throws HttpAction;

    @Override
    public final U getUserProfile(final C credentials, final WebContext context) throws HttpAction {
        init(context);
        logger.debug("credentials : {}", credentials);
        if (credentials == null) {
            return null;
        }

        final U profile = retrieveUserProfile(credentials, context);
        if (profile != null) {
            profile.setClientName(getName());
            if (this.authorizationGenerators != null) {
                for (AuthorizationGenerator<U> authorizationGenerator : this.authorizationGenerators) {
                    authorizationGenerator.generate(profile);
                }
            }
        }
        return profile;
    }

    /**
     * Retrieve a user userprofile.
     *
     * @param credentials the credentials
     * @param context the web context
     * @return the user profile
     * @throws HttpAction whether an additional HTTP action is required
     */
    protected abstract U retrieveUserProfile(final C credentials, final WebContext context) throws HttpAction;

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

    public List<AuthorizationGenerator<U>> getAuthorizationGenerators() {
        return this.authorizationGenerators;
    }

    public void setAuthorizationGenerators(final List<AuthorizationGenerator<U>> authorizationGenerators) {
        CommonHelper.assertNotNull("authorizationGenerators", authorizationGenerators);
        this.authorizationGenerators = authorizationGenerators;
    }

    public void setAuthorizationGenerators(final AuthorizationGenerator<U>... authorizationGenerators) {
        CommonHelper.assertNotNull("authorizationGenerators", authorizationGenerators);
        this.authorizationGenerators = Arrays.asList(authorizationGenerators);
    }

    /**
     * Add an authorization generator.
     * 
     * @param authorizationGenerator an authorizations generator
     */
    public void setAuthorizationGenerator(final AuthorizationGenerator<U> authorizationGenerator) {
        addAuthorizationGenerator(authorizationGenerator);
    }

    public void addAuthorizationGenerator(final AuthorizationGenerator<U> authorizationGenerator) {
        CommonHelper.assertNotNull("authorizationGenerator", authorizationGenerator);
        this.authorizationGenerators.add(authorizationGenerator);
    }

    public void addAuthorizationGenerators(final List<AuthorizationGenerator<U>> authorizationGenerators) {
        CommonHelper.assertNotNull("authorizationGenerators", authorizationGenerators);
        this.authorizationGenerators.addAll(authorizationGenerators);
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "name", getName());
    }
}
