package org.pac4j.kerberos.client.indirect;

import lombok.ToString;
import lombok.val;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.HttpActionHelper;
import org.pac4j.kerberos.credentials.extractor.KerberosExtractor;

import java.util.Optional;

/**
 * <p>IndirectKerberosClient class.</p>
 *
 * @author Vidmantas Zemleris, at Kensu.io
 * @since 2.1.0
 */
@ToString(callSuper = true)
public class IndirectKerberosClient extends IndirectClient {

    /**
     * <p>Constructor for IndirectKerberosClient.</p>
     */
    public IndirectKerberosClient() {}

    /**
     * <p>Constructor for IndirectKerberosClient.</p>
     *
     * @param authenticator a {@link org.pac4j.core.credentials.authenticator.Authenticator} object
     */
    public IndirectKerberosClient(final Authenticator authenticator) {
        setAuthenticatorIfUndefined(authenticator);
    }

    /**
     * <p>Constructor for IndirectKerberosClient.</p>
     *
     * @param authenticator a {@link org.pac4j.core.credentials.authenticator.Authenticator} object
     * @param profileCreator a {@link org.pac4j.core.profile.creator.ProfileCreator} object
     */
    public IndirectKerberosClient(final Authenticator authenticator, final ProfileCreator profileCreator) {
        setAuthenticatorIfUndefined(authenticator);
        setProfileCreatorIfUndefined(profileCreator);
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        setRedirectionActionBuilderIfUndefined(ctx -> {
            val webContext = ctx.webContext();
            return Optional.of(HttpActionHelper.buildRedirectUrlAction(webContext, computeFinalCallbackUrl(webContext)));
        });
        setCredentialsExtractorIfUndefined(new KerberosExtractor());
    }

    /** {@inheritDoc} */
    @Override
    public Optional<Credentials> getCredentials(final CallContext ctx) {
        init();
        CommonHelper.assertNotNull("credentialsExtractor", getCredentialsExtractor());

        val webContext = ctx.webContext();
        // set the www-authenticate in case of error
        webContext.setResponseHeader(HttpConstants.AUTHENTICATE_HEADER, "Negotiate");

        final Optional<Credentials> credentials;
        try {
            credentials = getCredentialsExtractor().extract(ctx);
            logger.debug("kerberos credentials : {}", credentials);
            if (!credentials.isPresent()) {
                throw HttpActionHelper.buildUnauthenticatedAction(webContext);
            }
            return credentials;
        } catch (final CredentialsException e) {
            throw HttpActionHelper.buildUnauthenticatedAction(webContext);
        }
    }

    /** {@inheritDoc} */
    @Override
    protected Optional<Credentials> internalValidateCredentials(final CallContext ctx, final Credentials credentials) {
        CommonHelper.assertNotNull("authenticator", getAuthenticator());

        val webContext = ctx.webContext();
        // set the www-authenticate in case of error
        webContext.setResponseHeader(HttpConstants.AUTHENTICATE_HEADER, "Negotiate");

        try {
            return getAuthenticator().validate(ctx, credentials);
        } catch (final CredentialsException e) {
            throw HttpActionHelper.buildUnauthenticatedAction(webContext);
        }
    }
}
