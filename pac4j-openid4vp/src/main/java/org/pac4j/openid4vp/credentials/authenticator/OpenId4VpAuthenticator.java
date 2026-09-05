package org.pac4j.openid4vp.credentials.authenticator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.openid4vp.client.OpenId4VpClient;
import org.pac4j.openid4vp.config.CredentialFormat;
import org.pac4j.openid4vp.credentials.VerifiablePresentationCredentials;
import org.pac4j.openid4vp.exceptions.OpenId4VpException;
import org.pac4j.openid4vp.transaction.VpTransaction;
import org.pac4j.openid4vp.verifier.CredentialVerifier;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Validates an OpenID4VP presentation.
 *
 * <p>The validation runs in two stages. This class owns the protocol stage: decrypting the response,
 * checking that it answers the pending transaction, and splitting the {@code vp_token} per DCQL query
 * identifier. The cryptographic stage, which depends on the credential format, is delegated to the
 * {@code CredentialVerifier} registered for that format.</p>
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@RequiredArgsConstructor
@Slf4j
public class OpenId4VpAuthenticator implements Authenticator {

    private final OpenId4VpClient client;

    /** {@inheritDoc} */
    @Override
    public Optional<Credentials> validate(final CallContext ctx, final Credentials credentials) {
        val vpCredentials = (VerifiablePresentationCredentials) credentials;
        val transaction = vpCredentials.getTransaction();
        if (transaction == null || transaction.getRawResponse() == null) {
            LOGGER.debug("no wallet response to validate");
            return Optional.empty();
        }

        vpCredentials.setVpToken(readVpToken(transaction));
        vpCredentials.getVpToken().forEach((queryId, presentations) -> {
            val raw = presentations.isEmpty() ? null : presentations.get(0);
            val verifier = findVerifier(raw);
            vpCredentials.getVerifiedCredentials()
                .put(queryId, verifier.verify(raw, transaction, client.getConfiguration()));
        });

        return Optional.of(vpCredentials);
    }

    /**
     * <p>Decrypt the response posted by the wallet and return its vp_token, indexed by DCQL query identifier.</p>
     *
     * <p>To be implemented: decrypt the JWE with the ephemeral key of the transaction, check the state against
     * the transaction, and read the {@code vp_token} member. The JWE header also carries the {@code apu} and
     * {@code apv} values, which the mobile document verification needs to rebuild its session transcript.</p>
     *
     * @param transaction the transaction the wallet answered
     * @return the raw presentations, indexed by the identifier of the DCQL credential query they answer
     */
    protected Map<String, List<String>> readVpToken(final VpTransaction transaction) {
        throw new OpenId4VpException("the response decryption is not implemented yet");
    }

    /**
     * <p>Find the verifier able to validate the given raw presentation.</p>
     *
     * <p>To be implemented: the format is not carried by the presentation itself, it is the one the DCQL
     * query asked for, so the query identifier has to be resolved against the query.</p>
     *
     * @param rawPresentation the presentation as found in the vp_token
     * @return the verifier of its format
     */
    protected CredentialVerifier findVerifier(final String rawPresentation) {
        val configuration = client.getConfiguration();
        val format = configuration.getSupportedFormats().stream().findFirst().orElse(CredentialFormat.SD_JWT_VC);
        val verifier = configuration.getCredentialVerifiers().get(format);
        if (verifier == null) {
            throw new OpenId4VpException("no credential verifier registered for the format: " + format.getValue());
        }
        return verifier;
    }
}
