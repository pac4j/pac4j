package org.pac4j.http.client.direct;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.val;
import org.pac4j.core.client.DirectClient;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.core.profile.factory.ProfileManagerFactory;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.http.credentials.CredentialUtil;
import org.pac4j.http.credentials.extractor.DigestAuthExtractor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * <p>This class is the client to authenticate users directly through HTTP digest auth.</p>
 * <p>Add the <code>commons-codec</code> dependency to use this class.</p>
 *
 * @author Mircea Carasel
 * @since 1.9.0
 */
@Getter
@Setter
@ToString(callSuper = true)
public class DirectDigestAuthClient extends DirectClient {

    private String realm = "pac4jRealm";

    public DirectDigestAuthClient() {
    }

    public DirectDigestAuthClient(final Authenticator digestAuthenticator) {
        defaultAuthenticator(digestAuthenticator);
    }

    public DirectDigestAuthClient(final Authenticator digestAuthenticator,
                                 final ProfileCreator profileCreator) {
        defaultAuthenticator(digestAuthenticator);
        defaultProfileCreator(profileCreator);
    }

    @Override
    protected void internalInit(final boolean forceReinit) {
        defaultCredentialsExtractor(new DigestAuthExtractor());
    }

    /** Per RFC 2617
     * If a server receives a request for an access-protected object, and an
     * acceptable Authorization header is not sent, the server responds with
     * a "401 Unauthorized" status code, and a WWW-Authenticate header
     */
    @Override
    protected Optional<Credentials> retrieveCredentials(final WebContext context, final SessionStore sessionStore,
                                                        final ProfileManagerFactory profileManagerFactory) {
        // set the www-authenticate in case of error
        val nonce = calculateNonce();
        context.setResponseHeader(HttpConstants.AUTHENTICATE_HEADER, "Digest realm=\"" + realm + "\", qop=\"auth\", nonce=\""
            + nonce + "\"");

        return super.retrieveCredentials(context, sessionStore, profileManagerFactory);
    }

    /**
     * A server-specified data string which should be uniquely generated each time a 401 response is made (RFC 2617)
     * Based on current time including nanoseconds
     */
    private String calculateNonce() {
        val time = LocalDateTime.now();
        val formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss.SSS");
        val fmtTime = formatter.format(time);
        return CredentialUtil.encryptMD5(fmtTime + CommonHelper.randomString(10));
    }
}
