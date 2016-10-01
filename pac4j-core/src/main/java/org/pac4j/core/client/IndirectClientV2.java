package org.pac4j.core.client;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.extractor.CredentialsExtractor;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.creator.AuthenticatorProfileCreator;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.core.redirect.RedirectActionBuilder;
import org.pac4j.core.util.CommonHelper;

/**
 * New indirect client type using the {@link RedirectActionBuilder}, {@link CredentialsExtractor}, {@link Authenticator}
 * and {@link ProfileCreator} concepts.
 * 
 * @author Jerome Leleu
 * @since 1.9.0
 */
public abstract class IndirectClientV2<C extends Credentials, U extends CommonProfile> extends IndirectClient<C, U> {

    private RedirectActionBuilder redirectActionBuilder;

    private CredentialsExtractor<C> credentialsExtractor;

    private Authenticator<C> authenticator;

    private ProfileCreator<C, U> profileCreator =  AuthenticatorProfileCreator.INSTANCE;

    @Override
    protected RedirectAction retrieveRedirectAction(final WebContext context) throws HttpAction {
        CommonHelper.assertNotNull("redirectActionBuilder", this.redirectActionBuilder);

        return redirectActionBuilder.redirect(context);
    }

    @Override
    protected C retrieveCredentials(final WebContext context) throws HttpAction {
        CommonHelper.assertNotNull("credentialsExtractor", this.credentialsExtractor);
        CommonHelper.assertNotNull("authenticator", this.authenticator);

        try {
            final C credentials = this.credentialsExtractor.extract(context);
            if (credentials == null) {
                return null;
            }
            this.authenticator.validate(credentials, context);
            return credentials;
        } catch (CredentialsException e) {
            logger.info("Failed to retrieve or validate credentials: {}", e.getMessage());
            logger.debug("Failed to retrieve or validate credentials", e);

            return null;
        }
    }

    @Override
    protected U retrieveUserProfile(final C credentials, final WebContext context) throws HttpAction {
        CommonHelper.assertNotNull("profileCreator", this.profileCreator);

        final U profile = this.profileCreator.create(credentials, context);
        logger.debug("profile: {}", profile);
        return profile;
    }

    public RedirectActionBuilder getRedirectActionBuilder() {
        return redirectActionBuilder;
    }

    public void setRedirectActionBuilder(final RedirectActionBuilder redirectActionBuilder) {
        if (this.redirectActionBuilder == null) {
            this.redirectActionBuilder = redirectActionBuilder;
        }
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

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "name", getName(), "callbackUrl", this.callbackUrl,
                "callbackUrlResolver", this.callbackUrlResolver, "ajaxRequestResolver", getAjaxRequestResolver(),
                "redirectActionBuilder", this.redirectActionBuilder, "credentialsExtractor", this.credentialsExtractor,
                "authenticator", this.authenticator, "profileCreator", this.profileCreator);
    }
}
