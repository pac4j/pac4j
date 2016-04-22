package org.pac4j.core.client;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.authenticator.LocalCachingAuthenticator;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.profile.creator.AuthenticatorProfileCreator;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.InitializableWebObject;

/**
 * New direct client type using the {@link CredentialsExtractor}, {@link Authenticator} and {@link ProfileCreator} concepts.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public abstract class DirectClientV2<C extends Credentials, U extends CommonProfile> extends DirectClient<C, U> {

    private CredentialsExtractor<C> credentialsExtractor;

    private Authenticator<C> authenticator;

    private ProfileCreator<C, U> profileCreator = AuthenticatorProfileCreator.INSTANCE;

    @Override
    protected void internalInit(final WebContext context) {
        CommonHelper.assertNotNull("credentialsExtractor", this.credentialsExtractor);
        CommonHelper.assertNotNull("authenticator", this.authenticator);
        CommonHelper.assertNotNull("profileCreator", this.profileCreator);
        if (authenticator instanceof InitializableWebObject) {
            ((InitializableWebObject) this.authenticator).init(context);
        }
    }

    @Override
    public C getCredentials(final WebContext context) throws RequiresHttpAction {
        init(context);
        final C credentials = this.credentialsExtractor.extract(context);
        if (credentials == null) {
            return null;
        }
        this.authenticator.validate(credentials);
        return credentials;
    }

    @Override
    protected U retrieveUserProfile(final C credentials, final WebContext context) throws RequiresHttpAction {
        final U profile = this.profileCreator.create(credentials);
        logger.debug("profile: {}", profile);
        return profile;
    }

    protected void assertAuthenticatorTypes(final Class<? extends Authenticator>... classes) {
        if (this.authenticator != null && classes != null) {
            for (final Class<? extends Authenticator> clazz : classes) {
                Class<? extends Authenticator> authClazz = this.authenticator.getClass();
                if (LocalCachingAuthenticator.class.isAssignableFrom(authClazz)) {
                    authClazz = ((LocalCachingAuthenticator) this.authenticator).getDelegate().getClass();
                }
                if (!clazz.isAssignableFrom(authClazz)) {
                    throw new TechnicalException("Unsupported authenticator type: " + authClazz);
                }
            }
        }
    }


    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "name", getName(), "credentialsExtractor", this.credentialsExtractor,
                "authenticator", this.authenticator, "profileCreator", this.profileCreator);
    }

    public CredentialsExtractor<C> getCredentialsExtractor() {
        return credentialsExtractor;
    }

    public void setCredentialsExtractor(final CredentialsExtractor<C> credentialsExtractor) {
        if (this.credentialsExtractor == null) {
            this.credentialsExtractor = credentialsExtractor;
        }
    }

    public Authenticator<C> getAuthenticator() {
        return authenticator;
    }

    public void setAuthenticator(final Authenticator<C> authenticator) {
        if (this.authenticator == null) {
            this.authenticator = authenticator;
        }
    }

    public ProfileCreator<C, U> getProfileCreator() {
        return profileCreator;
    }

    public void setProfileCreator(final ProfileCreator<C, U> profileCreator) {
        if (this.profileCreator == null || this.profileCreator == AuthenticatorProfileCreator.INSTANCE) {
            this.profileCreator = profileCreator;
        }
    }
}
