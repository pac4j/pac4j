package org.pac4j.http.client.direct;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.pac4j.core.client.DirectClient;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.extractor.BearerAuthExtractor;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.core.util.Pac4jConstants;

import java.util.Optional;

import static org.pac4j.core.util.CommonHelper.assertNotBlank;

/**
 * <p>This class is the client to authenticate users directly through RFC 6750 HTTP bearer authentication.</p>
 *
 * @author Graham Leggett
 * @since 3.5.0
 */
@Getter
@Setter
@ToString(callSuper = true)
public class DirectBearerAuthClient extends DirectClient {

    private String realmName = Pac4jConstants.DEFAULT_REALM_NAME;

    public DirectBearerAuthClient() {
    }

    public DirectBearerAuthClient(final Authenticator tokenAuthenticator) {
        setAuthenticatorIfUndefined(tokenAuthenticator);
    }

    public DirectBearerAuthClient(final ProfileCreator profileCreator) {
        setAuthenticatorIfUndefined(Authenticator.ALWAYS_VALIDATE);
        setProfileCreatorIfUndefined(profileCreator);
    }

    public DirectBearerAuthClient(final Authenticator tokenAuthenticator,
                                 final ProfileCreator profileCreator) {
        setAuthenticatorIfUndefined(tokenAuthenticator);
        setProfileCreatorIfUndefined(profileCreator);
    }

    @Override
    protected void internalInit(final boolean forceReinit) {
        assertNotBlank("realmName", this.realmName);

        setCredentialsExtractorIfUndefined(new BearerAuthExtractor());
    }

    @Override
    public Optional<Credentials> getCredentials(final CallContext ctx) {
        // set the www-authenticate in case of error
        ctx.webContext().setResponseHeader(HttpConstants.AUTHENTICATE_HEADER,
            HttpConstants.BEARER_HEADER_PREFIX + "realm=\"" + realmName + "\"");

        return super.getCredentials(ctx);
    }
}
