package org.pac4j.http.client.direct;

import org.pac4j.core.client.DirectClient;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.http.credentials.CredentialUtil;
import org.pac4j.http.credentials.DigestCredentials;
import org.pac4j.http.credentials.extractor.DigestAuthExtractor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <p>This class is the client to authenticate users directly through HTTP digest auth.</p>
 * <p>Add the <code>commons-codec</code> dependency to use this class.</p>
 *
 * @author Mircea Carasel
 * @since 1.9.0
 */
public class DirectDigestAuthClient extends DirectClient<DigestCredentials, CommonProfile> {

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
    protected void clientInit() {
        defaultCredentialsExtractor(new DigestAuthExtractor());
    }

    /** Per RFC 2617
     * If a server receives a request for an access-protected object, and an
     * acceptable Authorization header is not sent, the server responds with
     * a "401 Unauthorized" status code, and a WWW-Authenticate header
     */
    @Override
    protected DigestCredentials retrieveCredentials(final WebContext context) {
        // set the www-authenticate in case of error
        final String nonce = calculateNonce();
        context.setResponseHeader(HttpConstants.AUTHENTICATE_HEADER, "Digest realm=\"" + realm + "\", qop=\"auth\", nonce=\""
            + nonce + "\"");

        return super.retrieveCredentials(context);
    }

    /**
     * A server-specified data string which should be uniquely generated each time a 401 response is made (RFC 2617)
     * Based on current time including nanoseconds
     */
    private String calculateNonce() {
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy:MM:dd:HH:mm:ss.SSS");
        String fmtTime = formatter.format(time);
        return CredentialUtil.encryptMD5(fmtTime + CommonHelper.randomString(10));
    }

    public String getRealm() {
        return realm;
    }

    public void setRealm(final String realm) {
        this.realm = realm;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "name", getName(), "realm", this.realm, "extractor", getCredentialsExtractor(),
                "authenticator", getAuthenticator(), "profileCreator", getProfileCreator());
    }
}
