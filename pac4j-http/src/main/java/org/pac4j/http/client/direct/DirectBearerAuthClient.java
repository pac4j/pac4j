package org.pac4j.http.client.direct;

import com.nimbusds.jwt.JWTParser;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.client.DirectClient;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.credentials.extractor.BearerAuthExtractor;
import org.pac4j.core.profile.UserProfile;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.core.util.Announcement;
import org.pac4j.core.util.Pac4jConstants;

import java.text.ParseException;
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
@Slf4j
public class DirectBearerAuthClient extends DirectClient {

    private static final Announcement ANNOUNCE_TOKEN_VALIDATION = new Announcement(
        "DirectBearerAuthClient uses Authenticator.ALWAYS_VALIDATE, which accepts every access token. "
            + "The ProfileCreator must validate the access token before returning a profile.");

    private String realmName = Pac4jConstants.DEFAULT_REALM_NAME;

    /**
     * <p>Constructor for DirectBearerAuthClient.</p>
     */
    public DirectBearerAuthClient() {
    }

    /**
     * <p>Constructor for DirectBearerAuthClient.</p>
     *
     * @param tokenAuthenticator a {@link Authenticator} object
     */
    public DirectBearerAuthClient(final Authenticator tokenAuthenticator) {
        setAuthenticatorIfUndefined(tokenAuthenticator);
    }

    /**
     * <p>Constructor for DirectBearerAuthClient.</p>
     *
     * @param profileCreator validates the access token and creates the profile
     */
    public DirectBearerAuthClient(final ProfileCreator profileCreator) {
        setAuthenticatorIfUndefined(Authenticator.ALWAYS_VALIDATE);
        setProfileCreatorIfUndefined(profileCreator);
    }

    /**
     * <p>Constructor for DirectBearerAuthClient.</p>
     *
     * @param tokenAuthenticator a {@link Authenticator} object
     * @param profileCreator a {@link ProfileCreator} object
     */
    public DirectBearerAuthClient(final Authenticator tokenAuthenticator,
                                 final ProfileCreator profileCreator) {
        setAuthenticatorIfUndefined(tokenAuthenticator);
        setProfileCreatorIfUndefined(profileCreator);
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        assertNotBlank("realmName", this.realmName);

        if (getAuthenticator() == Authenticator.ALWAYS_VALIDATE) {
            ANNOUNCE_TOKEN_VALIDATION.announce();
        }
        setCredentialsExtractorIfUndefined(new BearerAuthExtractor());
    }

    /** {@inheritDoc} */
    @Override
    protected Optional<Credentials> internalValidateCredentials(final CallContext ctx, final Credentials credentials) {
        val validated = super.internalValidateCredentials(ctx, credentials);
        if (validated.isEmpty() || !acceptsToken(ctx, credentials)) {
            return Optional.empty();
        }
        return validated.filter(result -> result == credentials || acceptsToken(ctx, result));
    }

    /** {@inheritDoc} */
    @Override
    protected Optional<UserProfile> createProfile(final CallContext ctx, final Credentials credentials) {
        val profile = super.createProfile(ctx, credentials);
        // A ProfileCreator can validate the token itself and discover its binding during introspection.
        return acceptsToken(ctx, credentials) ? profile : Optional.empty();
    }

    private boolean acceptsToken(final CallContext ctx, final Credentials credentials) {
        if (credentials instanceof TokenCredentials token && isDPoPBound(token)) {
            LOGGER.error("Rejected a DPoP-bound access token presented with the Bearer authentication scheme");
            ctx.webContext().setResponseHeader(HttpConstants.AUTHENTICATE_HEADER,
                HttpConstants.BEARER_HEADER_PREFIX + "realm=\"" + realmName + "\", error=\"invalid_token\"");
            return false;
        }
        return true;
    }

    private boolean isDPoPBound(final TokenCredentials credentials) {
        val bound = credentials.getDpopBound();
        if (bound != null) {
            return bound;
        }
        // A custom validator may return only an identity. A readable JWT can still rule out Bearer use.
        val token = credentials.getToken();
        if (token == null || token.indexOf('.') < 0) {
            return false;
        }
        try {
            val claims = JWTParser.parse(token).getJWTClaimsSet();
            if (claims != null) {
                credentials.setTokenConfirmation(claims.getClaim("cnf"));
            }
        } catch (final ParseException e) {
            // Opaque tokens get their confirmation from the component performing introspection.
            return false;
        }
        return Boolean.TRUE.equals(credentials.getDpopBound());
    }

    /** {@inheritDoc} */
    @Override
    protected void checkCredentials(final CallContext ctx, final Credentials credentials) {
        if (credentials == null) {
            LOGGER.debug("Adding authenticate bearer header");
            // set the www-authenticate in case of error
            ctx.webContext().setResponseHeader(HttpConstants.AUTHENTICATE_HEADER,
                HttpConstants.BEARER_HEADER_PREFIX + "realm=\"" + realmName + "\"");
        } else {
            LOGGER.debug("Remove authenticate header");
            ctx.webContext().setResponseHeader(HttpConstants.AUTHENTICATE_HEADER, Pac4jConstants.EMPTY_STRING);
        }
    }
}
