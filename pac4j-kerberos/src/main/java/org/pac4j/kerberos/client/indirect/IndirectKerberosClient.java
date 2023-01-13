package org.pac4j.kerberos.client.indirect;

import lombok.ToString;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.core.profile.factory.ProfileManagerFactory;
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
        setRedirectionActionBuilderIfUndefined((webContext, sessionStore, profileManagerFactory) ->
            Optional.of(HttpActionHelper.buildRedirectUrlAction(webContext, computeFinalCallbackUrl(webContext))));
        setCredentialsExtractorIfUndefined(new KerberosExtractor());
    }

    @Override
    protected Optional<Credentials> retrieveCredentials(final WebContext context, final SessionStore sessionStore,
                                                        final ProfileManagerFactory profileManagerFactory) {
        CommonHelper.assertNotNull("credentialsExtractor", getCredentialsExtractor());
        CommonHelper.assertNotNull("authenticator", getAuthenticator());

        // set the www-authenticate in case of error
        context.setResponseHeader(HttpConstants.AUTHENTICATE_HEADER, "Negotiate");

        final Optional<Credentials> credentials;
        try {
            // retrieve credentials
            credentials = getCredentialsExtractor().extract(context, sessionStore, profileManagerFactory);
            logger.debug("kerberos credentials : {}", credentials);
            if (!credentials.isPresent()) {
                throw HttpActionHelper.buildUnauthenticatedAction(context);
            }
            // validate credentials
            getAuthenticator().validate(credentials.get(), context, sessionStore);
        } catch (final CredentialsException e) {
            throw HttpActionHelper.buildUnauthenticatedAction(context);
        }

        return credentials;
    }
}
