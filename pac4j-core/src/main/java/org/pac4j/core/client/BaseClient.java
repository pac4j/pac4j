package org.pac4j.core.client;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.val;
import org.pac4j.core.authorization.generator.AuthorizationGenerator;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.AuthenticationCredentials;
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

import java.util.*;

/**
 * This class is the default implementation of an authentication client (whatever the mechanism).
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
@Getter
@Setter
@ToString(exclude = "logger")
@Accessors(chain = true)
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

    @Override
    public String getName() {
        if (CommonHelper.isBlank(this.name)) {
            return this.getClass().getSimpleName();
        }
        return this.name;
    }

    @Override
    public Optional<Credentials> getCredentials(final CallContext ctx) {
        init();
        try {
            val credentials = this.credentialsExtractor.extract(ctx).orElse(null);
            checkCredentials(ctx, credentials);
            return Optional.ofNullable(credentials);
        } catch (CredentialsException e) {
            logger.info("Failed to retrieve credentials: {}", e.getMessage());
            logger.debug("Failed to retrieve redentials", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<AuthenticationCredentials> validateCredentials(final CallContext ctx, final AuthenticationCredentials credentials) {
        init();
        val t0 = System.currentTimeMillis();
        try {
            val newCredentials = this.authenticator.validate(ctx, credentials).orElse(null);
            checkCredentials(ctx, credentials);
            return Optional.ofNullable(newCredentials);
        } catch (CredentialsException e) {
            logger.info("Failed to validate credentials: {}", e.getMessage());
            logger.debug("Failed to validate credentials", e);
        } finally {
            val t1 = System.currentTimeMillis();
            logger.debug("Credentials validation took: {} ms", t1 - t0);
        }
        return Optional.empty();
    }

    /**
     * Check the credentials.
     *
     * @param ctx the context
     * @param credentials the credentials
     */
    protected void checkCredentials(final CallContext ctx, final Credentials credentials) {}

    @Override
    public final Optional<UserProfile> getUserProfile(CallContext ctx, final AuthenticationCredentials credentials) {
        init();
        logger.debug("credentials : {}", credentials);
        if (credentials == null) {
            if (profileFactoryWhenNotAuthenticated != null) {
                val customProfile = profileFactoryWhenNotAuthenticated.apply(new Object[] {ctx.webContext()});
                logger.debug("force custom profile when not authenticated: {}", customProfile);
                return Optional.ofNullable(customProfile);
            } else {
                return Optional.empty();
            }
        }

        var profile = this.profileCreator.create(ctx, credentials);
        logger.debug("profile: {}", profile);
        if (profile.isPresent()) {
            profile.get().setClientName(getName());
            if (this.authorizationGenerators != null) {
                for (val authorizationGenerator : this.authorizationGenerators) {
                    profile = authorizationGenerator.generate(ctx, profile.get());
                }
            }
        }
        return profile;
    }

    @Override
    public Optional<UserProfile> renewUserProfile(final CallContext ctx, final UserProfile profile) {
        return Optional.empty();
    }

    /**
     * Notify of the web session renewal.
     *
     * @param ctx          the context
     * @param oldSessionId the old session identifier
     */
    public void notifySessionRenewal(final CallContext ctx, final String oldSessionId) {}

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

    protected void setCredentialsExtractorIfUndefined(final CredentialsExtractor credentialsExtractor) {
        if (this.credentialsExtractor == null) {
            this.credentialsExtractor = credentialsExtractor;
        }
    }

    protected void setAuthenticatorIfUndefined(final Authenticator authenticator) {
        if (this.authenticator == null) {
            this.authenticator = authenticator;
        }
    }

    protected void setProfileCreatorIfUndefined(final ProfileCreator profileCreator) {
        if (this.profileCreator == null || this.profileCreator == AuthenticatorProfileCreator.INSTANCE) {
            this.profileCreator = profileCreator;
        }
    }

    public void setCustomProperties(final Map<String, Object> customProperties) {
        CommonHelper.assertNotNull("customProperties", customProperties);
        this.customProperties =  customProperties;
    }

    public void setProfileFactoryWhenNotAuthenticated(final ProfileFactory profileFactoryWhenNotAuthenticated) {
        if (!warned) {
            logger.warn("Be careful when using the 'setProfileFactoryWhenNotAuthenticated' method: a custom profile "
                + "is returned when the authentication fails or is cancelled and it is stored for the whole session. "
                + "You may need to define additional 'Authorizer's to secure your web resources.");
            warned = true;
        }
        this.profileFactoryWhenNotAuthenticated = profileFactoryWhenNotAuthenticated;
    }

    public boolean isMultiProfile(final WebContext context, final UserProfile profile) {
        return multiProfile;
    }

    public Boolean getSaveProfileInSession(final WebContext context, final UserProfile profile) {
        return saveProfileInSession;
    }
}
