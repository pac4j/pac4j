package org.pac4j.dpop.client.direct;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.client.DirectClient;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.profile.creator.ProfileCreator;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.dpop.credentials.extractor.DPoPAuthExtractor;

import java.util.Optional;
import java.util.stream.Collectors;

import static org.pac4j.core.util.CommonHelper.assertNotBlank;
import static org.pac4j.dpop.util.DPoPConstants.ERROR_REQUEST_ATTRIBUTE;
import static org.pac4j.dpop.util.DPoPConstants.INVALID_TOKEN_ERROR;

/**
 * <p>This class is the client to authenticate users directly through the RFC 9449 HTTP DPoP authentication:
 * an access token bound to a key, sent along with a proof of possession of that key.</p>
 *
 * <p>The proof is checked by the {@link DPoPAuthExtractor}, the access token by the authenticator.</p>
 *
 * @author Jerome LELEU
 * @since 6.7.0
 */
@Getter
@Setter
@ToString(callSuper = true)
@Slf4j
public class DirectDPoPAuthClient extends DirectClient {

    private String realmName = Pac4jConstants.DEFAULT_REALM_NAME;

    /**
     * <p>Constructor for DirectDPoPAuthClient.</p>
     */
    public DirectDPoPAuthClient() {
    }

    /**
     * <p>Constructor for DirectDPoPAuthClient.</p>
     *
     * @param tokenAuthenticator a {@link Authenticator} object
     */
    public DirectDPoPAuthClient(final Authenticator tokenAuthenticator) {
        setAuthenticatorIfUndefined(tokenAuthenticator);
    }

    /**
     * <p>Constructor for DirectDPoPAuthClient.</p>
     *
     * @param profileCreator a {@link ProfileCreator} object
     */
    public DirectDPoPAuthClient(final ProfileCreator profileCreator) {
        setAuthenticatorIfUndefined(Authenticator.ALWAYS_VALIDATE);
        setProfileCreatorIfUndefined(profileCreator);
    }

    /**
     * <p>Constructor for DirectDPoPAuthClient.</p>
     *
     * @param tokenAuthenticator a {@link Authenticator} object
     * @param profileCreator a {@link ProfileCreator} object
     */
    public DirectDPoPAuthClient(final Authenticator tokenAuthenticator,
                                final ProfileCreator profileCreator) {
        setAuthenticatorIfUndefined(tokenAuthenticator);
        setProfileCreatorIfUndefined(profileCreator);
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        assertNotBlank("realmName", this.realmName);

        setCredentialsExtractorIfUndefined(new DPoPAuthExtractor());
    }

    /** {@inheritDoc} */
    @Override
    protected Optional<Credentials> internalValidateCredentials(final CallContext ctx, final Credentials credentials) {
        val validatedCredentials = super.internalValidateCredentials(ctx, credentials);
        if (validatedCredentials.isEmpty()) {
            // the proof has been accepted: the access token is what the authenticator refuses (RFC 9449, section 7.1)
            ctx.webContext().setRequestAttribute(ERROR_REQUEST_ATTRIBUTE, INVALID_TOKEN_ERROR);
            checkCredentials(ctx, null);
        }
        return validatedCredentials;
    }

    /**
     * Without credentials, challenge the client to provide a DPoP authentication (RFC 9449, section 7.1).
     */
    @Override
    protected void checkCredentials(final CallContext ctx, final Credentials credentials) {
        val webContext = ctx.webContext();
        if (credentials == null) {
            LOGGER.debug("Adding authenticate DPoP header");
            val challenge = new StringBuilder(HttpConstants.DPOP_HEADER_PREFIX).append("realm=\"").append(realmName).append('"');
            webContext.getRequestAttribute(ERROR_REQUEST_ATTRIBUTE)
                .ifPresent(error -> challenge.append(", error=\"").append(error).append('"'));
            if (getCredentialsExtractor() instanceof DPoPAuthExtractor extractor) {
                challenge.append(", algs=\"").append(extractor.getJwsAlgorithms().stream().map(Object::toString)
                    .collect(Collectors.joining(" "))).append('"');
            }
            webContext.setResponseHeader(HttpConstants.AUTHENTICATE_HEADER, challenge.toString());
        } else {
            LOGGER.debug("Remove authenticate header");
            webContext.setResponseHeader(HttpConstants.AUTHENTICATE_HEADER, Pac4jConstants.EMPTY_STRING);
        }
    }
}
