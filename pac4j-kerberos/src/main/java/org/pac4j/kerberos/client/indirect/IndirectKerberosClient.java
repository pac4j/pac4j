package org.pac4j.kerberos.client.indirect;

/**
 * @author Vidmantas Zemleris, at Kensu.io
 *
 * @since 2.1.0
 */
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.exception.HttpAction;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.core.redirect.RedirectAction;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.kerberos.credentials.KerberosCredentials;
import org.pac4j.kerberos.credentials.extractor.KerberosExtractor;
import org.pac4j.kerberos.profile.KerberosProfile;

public class IndirectKerberosClient extends IndirectClient<KerberosCredentials, KerberosProfile> {
    public IndirectKerberosClient() {}

    public IndirectKerberosClient(final Authenticator authenticator) {
        defaultAuthenticator(authenticator);
    }

    public IndirectKerberosClient(final Authenticator authenticator, final ProfileCreator<KerberosCredentials, KerberosProfile> profileCreator) {
        defaultAuthenticator(authenticator);
        defaultProfileCreator(profileCreator);
    }

    @Override
    protected void clientInit(final WebContext context) {
        defaultRedirectActionBuilder(webContext ->  RedirectAction.redirect(computeFinalCallbackUrl(webContext)));
        defaultCredentialsExtractor(new KerberosExtractor(getName()));
    }

    @Override
    protected KerberosCredentials retrieveCredentials(final WebContext context) throws HttpAction {
        CommonHelper.assertNotNull("credentialsExtractor", getCredentialsExtractor());
        CommonHelper.assertNotNull("authenticator", getAuthenticator());

        final KerberosCredentials credentials;
        try {
            // retrieve credentials
            credentials = getCredentialsExtractor().extract(context);
            logger.debug("kerberos credentials : {}", credentials);
            if (credentials == null) {
                throw HttpAction.unauthorizedNegotiate("Kerberos Header not found", context);
            }
            // validate credentials
            getAuthenticator().validate(credentials, context);
        } catch (final CredentialsException e) {
            throw HttpAction.unauthorizedNegotiate("Kerberos auth failed", context);
        }

        return credentials;
    }

    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "callbackUrl", this.callbackUrl, "name", getName(),
            "extractor", getCredentialsExtractor(), "authenticator", getAuthenticator(),
            "profileCreator", getProfileCreator());
    }
}
