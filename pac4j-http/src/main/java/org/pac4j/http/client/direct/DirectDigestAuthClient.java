package org.pac4j.http.client.direct;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.val;
import org.pac4j.core.client.DirectClient;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.profile.creator.ProfileCreator;
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

    /**
     * <p>Constructor for DirectDigestAuthClient.</p>
     */
    public DirectDigestAuthClient() {
    }

    /**
     * <p>Constructor for DirectDigestAuthClient.</p>
     *
     * @param digestAuthenticator a {@link Authenticator} object
     */
    public DirectDigestAuthClient(final Authenticator digestAuthenticator) {
        setAuthenticatorIfUndefined(digestAuthenticator);
    }

    /**
     * <p>Constructor for DirectDigestAuthClient.</p>
     *
     * @param digestAuthenticator a {@link Authenticator} object
     * @param profileCreator a {@link ProfileCreator} object
     */
    public DirectDigestAuthClient(final Authenticator digestAuthenticator,
                                 final ProfileCreator profileCreator) {
        setAuthenticatorIfUndefined(digestAuthenticator);
        setProfileCreatorIfUndefined(profileCreator);
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        setCredentialsExtractorIfUndefined(new DigestAuthExtractor());
    }

    /**
     * {@inheritDoc}
     *
     * Per RFC 2617
     * If a server receives a request for an access-protected object, and an
     * acceptable Authorization header is not sent, the server responds with
     * a "401 Unauthorized" status code, and a WWW-Authenticate header
     */
    @Override
    public Optional<Credentials> getCredentials(final CallContext ctx) {
        // set the www-authenticate in case of error
        val nonce = calculateNonce();
        ctx.webContext().setResponseHeader(HttpConstants.AUTHENTICATE_HEADER, "Digest realm=\"" + realm + "\", qop=\"auth\", nonce=\""
            + nonce + "\"");

        return super.getCredentials(ctx);
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
