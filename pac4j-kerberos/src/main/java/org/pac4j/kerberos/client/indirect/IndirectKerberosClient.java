package org.pac4j.kerberos.client.indirect;

/**
 * @author Vidmantas Zemleris, at Kensu.io
 *
 * @since 2.1.0
 */
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.authenticator.Authenticator;
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
    public String toString() {
        return CommonHelper.toString(this.getClass(), "callbackUrl", this.callbackUrl, "name", getName(),
            "extractor", getCredentialsExtractor(), "authenticator", getAuthenticator(),
            "profileCreator", getProfileCreator());
    }
}
