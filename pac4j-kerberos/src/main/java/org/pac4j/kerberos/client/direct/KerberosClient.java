package org.pac4j.kerberos.client.direct;

import org.pac4j.core.client.ClientType;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.http.client.direct.DirectHttpClient;
import org.pac4j.http.profile.creator.ProfileCreator;
import org.pac4j.kerberos.credentials.KerberosCredentials;
import org.pac4j.kerberos.credentials.authenticator.KerberosAuthenticator;
import org.pac4j.kerberos.credentials.extractor.KerberosExtractor;

/**
 * <p>This class is the client to authenticate users directly based on Kerberos ticket.</p>
 *
 * @author Garry Boyce
 * @since 1.8.10
 */
public class KerberosClient extends DirectHttpClient<KerberosCredentials> {

    public KerberosClient() {
    }

    public KerberosClient(final KerberosAuthenticator kerberosAuthenticator) {
        setAuthenticator(kerberosAuthenticator);
    }

    public KerberosClient(final KerberosAuthenticator kerberosAuthenticator, final ProfileCreator<KerberosCredentials, CommonProfile> profileCreator) {
        setAuthenticator(kerberosAuthenticator);
        setProfileCreator(profileCreator);
    }

    @Override
    protected void internalInit(WebContext context) {
        extractor = new KerberosExtractor(getName());
        super.internalInit(context);
    }

    @Override
    protected KerberosClient newClient() {
        return new KerberosClient();
    }

    @Override
    public ClientType getClientType() {
        return ClientType.HEADER_BASED;
    }

	/**
	 * This method provides a way for a direct client to request additional
	 * information from browser
	 * 
	 * @param context the web context
	 * 
	 * TODO: This method should be exposed via a common direct interface
	 * for direct clients that support it
	 */
    public void handleForbidden(WebContext context) {

        context.setResponseHeader("WWW-Authenticate", "Negotiate");
        context.setResponseStatus(HttpConstants.UNAUTHORIZED);

    }

}
