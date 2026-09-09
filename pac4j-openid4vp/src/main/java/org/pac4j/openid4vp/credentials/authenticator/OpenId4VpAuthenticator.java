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
            val verifier = findVerifier(queryId);
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
     * <p>Find the verifier of a presentation: the one registered for the format of the credential query it
     * answers, the presentations of the response being indexed by the identifiers of those queries.</p>
     *
     * @param queryId the identifier of the credential query the presentation answers
     * @return the verifier
     */
    protected CredentialVerifier findVerifier(final String queryId) {
        val configuration = client.getConfiguration();
        // the presentations are indexed by the identifier of the credential query they answer, which says the format;
        // with a scope, the query is not known here, and the first configured format is assumed
        val format = configuration.getDcqlQuery() == null
            ? configuration.getSupportedFormats().stream().findFirst().orElse(CredentialFormat.SD_JWT_VC)
            : configuration.getDcqlQuery().findCredential(queryId)
                .orElseThrow(() -> new OpenId4VpException("no credential query for the presentation: " + queryId))
                .getFormat();
        val verifier = configuration.getCredentialVerifiers().get(format);
        if (verifier == null) {
            throw new OpenId4VpException("no credential verifier registered for the format: " + format.getValue());
        }
        return verifier;
    }
}
