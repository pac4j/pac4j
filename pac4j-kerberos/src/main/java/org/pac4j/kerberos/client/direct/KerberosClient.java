package org.pac4j.kerberos.client.direct;


import org.pac4j.kerberos.credentials.KerberosCredentials;
import org.pac4j.kerberos.credentials.authenticator.KerberosAuthenticator;
import org.pac4j.kerberos.credentials.extractor.KerberosExtractor;
import org.pac4j.kerberos.profile.KerberosProfile;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.client.DirectClientV2;
import org.pac4j.core.profile.creator.ProfileCreator;

/**
 * <p>This class is the client to authenticate users directly based on Kerberos ticket.</p>
 *
 * @author Garry Boyce
 * @since 1.9.1
 */
public class KerberosClient extends DirectClientV2<KerberosCredentials, KerberosProfile> {

    public KerberosClient() {
    }

    public KerberosClient(final KerberosAuthenticator kerberosAuthenticator) {
        setAuthenticator(kerberosAuthenticator);
    }

    public KerberosClient(final KerberosAuthenticator kerberosAuthenticator, final ProfileCreator<KerberosCredentials, KerberosProfile> profileCreator) {
        setAuthenticator(kerberosAuthenticator);
        setProfileCreator(profileCreator);
    }

    @Override
    protected void internalInit(final WebContext context) {
    	setCredentialsExtractor(new KerberosExtractor(getName()));
        super.internalInit(context);
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
