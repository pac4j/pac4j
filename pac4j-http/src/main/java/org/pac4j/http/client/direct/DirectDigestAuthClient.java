package org.pac4j.http.client.direct;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.http.credentials.CredentialUtil;
import org.pac4j.http.credentials.DigestCredentials;
import org.pac4j.http.credentials.authenticator.DigestAuthenticator;
import org.pac4j.http.credentials.extractor.DigestAuthExtractor;
import org.pac4j.http.profile.creator.ProfileCreator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * <p>This class is the client to authenticate users directly through HTTP digest auth.</p>
 * <p>It returns a {@link org.pac4j.http.profile.HttpProfile}.</p>
 *
 * @see org.pac4j.http.profile.HttpProfile
 * @author Mircea Carasel
 */
public class DirectDigestAuthClient extends DirectHttpClient<DigestCredentials> {
    private String realm = "pac4jRealm";

    public DirectDigestAuthClient() {
    }

    public DirectDigestAuthClient(final DigestAuthenticator digestAuthenticator) {
        setAuthenticator(digestAuthenticator);
    }

    public DirectDigestAuthClient(final DigestAuthenticator digestAuthenticator,
                                 final ProfileCreator profileCreator) {
        setAuthenticator(digestAuthenticator);
        setProfileCreator(profileCreator);
    }

    @Override
    public DigestCredentials getCredentials(WebContext context) throws RequiresHttpAction {
        DigestCredentials credentials = super.getCredentials(context);
        if (credentials == null) {
            String nonce = calculateNonce();
            /* Per RFC 2617
            If a server receives a request for an access-protected object, and an
            acceptable Authorization header is not sent, the server responds with
            a "401 Unauthorized" status code, and a WWW-Authenticate header
             */
            RequiresHttpAction.unauthorizedDigest("Digest required", context, realm, "auth", nonce);
        }
        return credentials;
    }

    @Override
    protected void internalInit(final WebContext context) {
        extractor = new DigestAuthExtractor(getName());
        super.internalInit(context);
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    /**
     * A server-specified data string which should be uniquely generated each time a 401 response is made (RFC 2617)
     */
    private String calculateNonce() {
        Date d = new Date();
        SimpleDateFormat f = new SimpleDateFormat("yyyy:MM:dd:hh:mm:ss");
        String fmtDate = f.format(d);
        return CredentialUtil.H(fmtDate + CommonHelper.randomString(10));
    }
}