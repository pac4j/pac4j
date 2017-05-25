package org.pac4j.kerberos.client.direct;


import org.pac4j.core.client.DirectClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.authenticator.LocalCachingAuthenticator;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.kerberos.credentials.KerberosCredentials;
import org.pac4j.kerberos.credentials.authenticator.KerberosAuthenticator;
import org.pac4j.kerberos.credentials.extractor.KerberosExtractor;
import org.pac4j.kerberos.profile.KerberosProfile;

/**
 * <p>This class is the client to authenticate users directly based on Kerberos ticket.</p>
 *
 * @author Garry Boyce
 * @since 2.1.0
 */
public class KerberosClient extends DirectClient<KerberosCredentials, KerberosProfile> {

    public KerberosClient() {
    }

    public KerberosClient(final Authenticator authenticator) {
        setAuthenticator(authenticator);
    }

    public KerberosClient(final Authenticator authenticator, final ProfileCreator<KerberosCredentials, KerberosProfile> profileCreator) {
        setAuthenticator(authenticator);
        setProfileCreator(profileCreator);
    }

    @Override
    protected void clientInit(final WebContext context) {
        setCredentialsExtractor(new KerberosExtractor(getName()));
        // FIXME: is this needed
        assertAuthenticatorTypes(KerberosAuthenticator.class);
    }

    // FIXME: this was copied from earlier version
    protected void assertAuthenticatorTypes(final Class<? extends Authenticator>... classes) {
        Authenticator<KerberosCredentials> authenticator = getAuthenticator();
        if (authenticator != null && classes != null) {
            for (final Class<? extends Authenticator> clazz : classes) {
                Class<? extends Authenticator> authClazz = authenticator.getClass();
                if (LocalCachingAuthenticator.class.isAssignableFrom(authClazz)) {
                    authClazz = ((LocalCachingAuthenticator) authenticator).getDelegate().getClass();
                }
                if (!clazz.isAssignableFrom(authClazz)) {
                    throw new TechnicalException("Unsupported authenticator type: " + authClazz);
                }
            }
        }
    }

}
