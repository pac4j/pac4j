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
 * @author Vidmantas Zemleris, at Kensu.io
 *
 * @since 2.1.0
 */
@ToString(callSuper = true)
public class IndirectKerberosClient extends IndirectClient {

    public IndirectKerberosClient() {}

    public IndirectKerberosClient(final Authenticator authenticator) {
        setAuthenticatorIfUndefined(authenticator);
    }

    public IndirectKerberosClient(final Authenticator authenticator, final ProfileCreator profileCreator) {
        setAuthenticatorIfUndefined(authenticator);
        setProfileCreatorIfUndefined(profileCreator);
    }

    @Override
    protected void internalInit(final boolean forceReinit) {
        setRedirectionActionBuilderIfUndefined(ctx -> {
            val webContext = ctx.webContext();
            return Optional.of(HttpActionHelper.buildRedirectUrlAction(webContext, computeFinalCallbackUrl(webContext)));
        });
        setCredentialsExtractorIfUndefined(new KerberosExtractor());
    }

    @Override
    protected Optional<Credentials> retrieveCredentials(final CallContext ctx) {
        CommonHelper.assertNotNull("credentialsExtractor", getCredentialsExtractor());
        CommonHelper.assertNotNull("authenticator", getAuthenticator());

        val webContext = ctx.webContext();

        // set the www-authenticate in case of error
        webContext.setResponseHeader(HttpConstants.AUTHENTICATE_HEADER, "Negotiate");

        final Optional<Credentials> credentials;
        try {
            // retrieve credentials
            credentials = getCredentialsExtractor().extract(ctx);
            logger.debug("kerberos credentials : {}", credentials);
            if (!credentials.isPresent()) {
                throw HttpActionHelper.buildUnauthenticatedAction(webContext);
            }
            // validate credentials
            getAuthenticator().validate(ctx, credentials.get());
        } catch (final CredentialsException e) {
            throw HttpActionHelper.buildUnauthenticatedAction(webContext);
        }

        return credentials;
    }
}
