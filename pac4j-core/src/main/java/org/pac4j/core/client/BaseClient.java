package org.pac4j.core.client;

import java.util.*;

import org.pac4j.core.authorization.generator.AuthorizationGenerator;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.profile.creator.AuthenticatorProfileCreator;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.core.profile.factory.ProfileFactory;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>This class is the default implementation of an authentication client (whatever the mechanism). It has the core concepts:</p>
 * <ul>
 * <li>The initialization process is handled by the {@link InitializableObject} inheritance, the {@link #internalInit()}
 * must be implemented in sub-classes. The {@link #init()} method must be called implicitly by the main methods of
 * the {@link Client} interface, so that no explicit call is required to initialize the client</li>
 * <li>The name of the client is handled through the {@link #setName(String)} and {@link #getName()} methods</li>
 * <li>After retrieving the user profile, the client can generate the authorization information (roles, permissions and remember-me) by
 * using the appropriate {@link AuthorizationGenerator}</li>
 * <li>The credentials extraction and validation in the {@link #getCredentials(WebContext)} method are handled by the
 * {@link #credentialsExtractor} and {@link #authenticator} components</li>
 * <li>The user profile retrieval in the {@link #getUserProfile(Credentials, WebContext)} method is ensured by the {@link #profileCreator}
 * component.</li>
 * </ul>
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public abstract class BaseClient extends InitializableObject implements Client {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private String name;

    private List<AuthorizationGenerator> authorizationGenerators = new ArrayList<>();

    private CredentialsExtractor credentialsExtractor;

    private Authenticator authenticator;

    private ProfileCreator profileCreator = AuthenticatorProfileCreator.INSTANCE;

    private Map<String, Object> customProperties = new LinkedHashMap<>();

    private ProfileFactory profileFactoryWhenNotAuthenticated;

    private boolean multiProfile = false;

    protected Boolean saveProfileInSession;

    private static boolean warned;

    /**
     * Retrieve the credentials.
     *
     * @param context the web context
     * @return the credentials
     */
    protected Optional<Credentials> retrieveCredentials(final WebContext context) {
        try {
            final Optional<Credentials> optCredentials = this.credentialsExtractor.extract(context);
            optCredentials.ifPresent(credentials -> {
                final long t0 = System.currentTimeMillis();
                try {
                    this.authenticator.validate(credentials, context);
                } finally {
                    final long t1 = System.currentTimeMillis();
                    logger.debug("Credentials validation took: {} ms", t1 - t0);
                }
            });
            return optCredentials;
        } catch (CredentialsException e) {
            logger.info("Failed to retrieve or validate credentials: {}", e.getMessage());
            logger.debug("Failed to retrieve or validate credentials", e);

            return Optional.empty();
        }
    }

    @Override
    public final Optional<UserProfile> getUserProfile(final Credentials credentials, final WebContext context) {
        init();
        logger.debug("credentials : {}", credentials);
        if (credentials == null) {
            if (profileFactoryWhenNotAuthenticated != null) {
                final UserProfile customProfile = profileFactoryWhenNotAuthenticated.apply(new Object[] {context});
                logger.debug("force custom profile when not authenticated: {}", customProfile);
                return Optional.ofNullable(customProfile);
            } else {
                return Optional.empty();
            }
        }

        Optional<UserProfile> profile = retrieveUserProfile(credentials, context);
        if (profile.isPresent()) {
            profile.get().setClientName(getName());
            if (this.authorizationGenerators != null) {
                for (final AuthorizationGenerator authorizationGenerator : this.authorizationGenerators) {
                    profile = authorizationGenerator.generate(context, profile.get());
                }
            }
        }
        return profile;
    }

    /**
     * Retrieve a user profile.
     *
     * @param credentials the credentials
     * @param context     the web context
     * @return the user profile
     */
    protected final Optional<UserProfile> retrieveUserProfile(final Credentials credentials, final WebContext context) {
        final Optional<UserProfile> profile = this.profileCreator.create(credentials, context);
        logger.debug("profile: {}", profile);
        return profile;
    }

    @Override
    public Optional<UserProfile> renewUserProfile(final UserProfile profile, final WebContext context) {
        return Optional.empty();
    }

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
     * Notify of the web session renewal.
     *
     * @param oldSessionId the old session identifier
     * @param context      the web context
     */
    public void notifySessionRenewal(final String oldSessionId, final WebContext context) {
    }

    public List<AuthorizationGenerator> getAuthorizationGenerators() {
        return this.authorizationGenerators;
    }

    public void setAuthorizationGenerators(final List<AuthorizationGenerator> authorizationGenerators) {
        CommonHelper.assertNotNull("authorizationGenerators", authorizationGenerators);
        this.authorizationGenerators = authorizationGenerators;
    }

    public void setAuthorizationGenerators(final AuthorizationGenerator... authorizationGenerators) {
        CommonHelper.assertNotNull("authorizationGenerators", authorizationGenerators);
        this.authorizationGenerators = Arrays.asList(authorizationGenerators);
    }

    /**
     * Add an authorization generator.
     *
     * @param authorizationGenerator an authorizations generator
     */
    public void setAuthorizationGenerator(final AuthorizationGenerator authorizationGenerator) {
        addAuthorizationGenerator(authorizationGenerator);
    }

    public void addAuthorizationGenerator(final AuthorizationGenerator authorizationGenerator) {
        CommonHelper.assertNotNull("authorizationGenerator", authorizationGenerator);
        this.authorizationGenerators.add(authorizationGenerator);
    }

    public void addAuthorizationGenerators(final List<AuthorizationGenerator> authorizationGenerators) {
        CommonHelper.assertNotNull("authorizationGenerators", authorizationGenerators);
        this.authorizationGenerators.addAll(authorizationGenerators);
    }

    public CredentialsExtractor getCredentialsExtractor() {
        return credentialsExtractor;
    }

    protected void defaultCredentialsExtractor(final CredentialsExtractor credentialsExtractor) {
        if (this.credentialsExtractor == null) {
            this.credentialsExtractor = credentialsExtractor;
        }
    }

    public Authenticator getAuthenticator() {
        return authenticator;
    }

    protected void defaultAuthenticator(final Authenticator authenticator) {
        if (this.authenticator == null) {
            this.authenticator = authenticator;
        }
    }

    public ProfileCreator getProfileCreator() {
        return profileCreator;
    }

    protected void defaultProfileCreator(final ProfileCreator profileCreator) {
        if (this.profileCreator == null || this.profileCreator == AuthenticatorProfileCreator.INSTANCE) {
            this.profileCreator = profileCreator;
        }
    }

    public void setCredentialsExtractor(final CredentialsExtractor credentialsExtractor) {
        this.credentialsExtractor = credentialsExtractor;
    }

    public void setAuthenticator(final Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    public void setProfileCreator(final ProfileCreator profileCreator) {
        this.profileCreator = profileCreator;
    }

    public Map<String, Object> getCustomProperties() {
        return customProperties;
    }

    public void setCustomProperties(final Map<String, Object> customProperties) {
        CommonHelper.assertNotNull("customProperties", customProperties);
        this.customProperties =  customProperties;
    }

    public ProfileFactory getProfileFactoryWhenNotAuthenticated() {
        return profileFactoryWhenNotAuthenticated;
    }

    public void setProfileFactoryWhenNotAuthenticated(final ProfileFactory profileFactoryWhenNotAuthenticated) {
        if (!warned) {
            logger.warn("Be careful when using the 'setProfileFactoryWhenNotAuthenticated' method: a custom profile "
                + "is returned when the authentication fails or is cancelled and the access is granted for the whole session. "
                + "You certainly need to define additional 'Authorizer's to secure your web resources.");
            warned = true;
        }
        this.profileFactoryWhenNotAuthenticated = profileFactoryWhenNotAuthenticated;
    }

    public boolean isMultiProfile(final WebContext context, final UserProfile profile) {
        return multiProfile;
    }

    public void setMultiProfile(final boolean multiProfile) {
        this.multiProfile = multiProfile;
    }

    public Boolean getSaveProfileInSession(final WebContext context, final UserProfile profile) {
        return saveProfileInSession;
    }

    public void setSaveProfileInSession(final boolean saveProfileInSession) {
        this.saveProfileInSession = saveProfileInSession;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "name", getName(), "credentialsExtractor", this.credentialsExtractor,
            "authenticator", this.authenticator, "profileCreator", this.profileCreator,
            "authorizationGenerators", authorizationGenerators, "customProperties", customProperties,
            "profileFactoryWhenNotAuthenticated", profileFactoryWhenNotAuthenticated, "multiProfile", multiProfile,
            "saveProfileInSession", saveProfileInSession);
    }
}
