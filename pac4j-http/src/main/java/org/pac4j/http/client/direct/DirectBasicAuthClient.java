package org.pac4j.http.client.direct;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.pac4j.core.client.DirectClient;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.extractor.BasicAuthExtractor;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.core.util.Pac4jConstants;

import java.util.Optional;

import static org.pac4j.core.util.CommonHelper.assertNotBlank;

/**
 * <p>This class is the client to authenticate users directly through HTTP basic auth.</p>
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
@Getter
@Setter
@ToString(callSuper = true)
public class DirectBasicAuthClient extends DirectClient {

    private String realmName = Pac4jConstants.DEFAULT_REALM_NAME;

    public DirectBasicAuthClient() {
    }

    public DirectBasicAuthClient(final Authenticator usernamePasswordAuthenticator) {
        setAuthenticatorIfUndefined(usernamePasswordAuthenticator);
    }

    public DirectBasicAuthClient(final Authenticator usernamePasswordAuthenticator,
                                 final ProfileCreator profileCreator) {
        setAuthenticatorIfUndefined(usernamePasswordAuthenticator);
        setProfileCreatorIfUndefined(profileCreator);
    }

    @Override
    protected void internalInit(final boolean forceReinit) {
        assertNotBlank("realmName", this.realmName);

        setCredentialsExtractorIfUndefined(new BasicAuthExtractor());
    }

    @Override
    protected Optional<Credentials> retrieveCredentials(final CallContext ctx) {
        addAuthenticateHeader(ctx.webContext());

        return super.retrieveCredentials(ctx);
    }

    protected void addAuthenticateHeader(final WebContext context) {
        // set the www-authenticate in case of error
        context.setResponseHeader(HttpConstants.AUTHENTICATE_HEADER, "Basic realm=\"" + realmName + "\"");
    }
}
