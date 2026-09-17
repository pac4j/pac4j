package org.pac4j.openid4vp.credentials.authenticator;

import lombok.extern.slf4j.Slf4j;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWEAlgorithm;
import com.nimbusds.jose.JWEObject;
import com.nimbusds.jose.crypto.ECDHDecrypter;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.util.JSONObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.openid4vp.client.OpenId4VpClient;
import org.pac4j.openid4vp.credentials.VerifiablePresentationCredentials;
import org.pac4j.openid4vp.dcql.DcqlQuery;
import org.pac4j.openid4vp.dcql.DcqlValidator;
import org.pac4j.openid4vp.exceptions.OpenId4VpException;
import org.pac4j.openid4vp.request.OpenId4VpRequestObjectBuilder;
import org.pac4j.openid4vp.transaction.VpTransaction;
import org.pac4j.openid4vp.verifier.VerifiedCredential;

import java.text.ParseException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static org.pac4j.openid4vp.util.OpenId4VpConstants.*;

/**
 * Validates the response envelope and DCQL against the saved authorization request. Credential signature,
 * issuer trust, status and transaction-bound holder proof are delegated to each format's verifier.
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@RequiredArgsConstructor
@Slf4j
public class OpenId4VpAuthenticator implements Authenticator {

    private final OpenId4VpClient client;

    private final DcqlValidator dcqlValidator = new DcqlValidator();

    /** {@inheritDoc} */
    @Override
    public Optional<Credentials> validate(final CallContext ctx, final Credentials credentials) {
        val vpCredentials = (VerifiablePresentationCredentials) credentials;
        // Never leave previously or partially verified data available after a failed validation.
        vpCredentials.setVpToken(new LinkedHashMap<>());
        vpCredentials.setVerifiedCredentials(new LinkedHashMap<>());
        val transaction = vpCredentials.getTransaction();
        if (transaction == null || (transaction.getRawResponse() == null && transaction.getRawVpToken() == null)) {
            LOGGER.debug("no wallet response available for validation");
            return Optional.empty();
        }
        LOGGER.debug("validating wallet response for transaction {}: response mode={}", transaction.getId(), transaction.getResponseMode());
        if (transaction.getExpiresAt() == null || !Instant.now().isBefore(transaction.getExpiresAt())) {
            LOGGER.debug("response rejected for transaction {}: expired transaction", transaction.getId());
            throw new OpenId4VpException("the presentation transaction has expired");
        }
        if (transaction.getRequestParameters() == null || transaction.getDcqlQuery() == null
            || transaction.getResponseMode() == null || transaction.getNonce() == null) {
            LOGGER.debug("response rejected for transaction {}: incomplete saved authorization request", transaction.getId());
            throw new OpenId4VpException("the presentation transaction has no saved authorization request");
        }
        if (transaction.getError() != null) {
            LOGGER.debug("response rejected for transaction {}: wallet error", transaction.getId());
            throw new OpenId4VpException("the wallet returned an error");
        }
        val query = DcqlQuery.parse(transaction.getDcqlQuery());
        query.check();
        val presentations = readVpToken(transaction);
        LOGGER.debug("response envelope checked for transaction {}: {} credential query entries",
            transaction.getId(), presentations.size());
        val verified = new LinkedHashMap<String, List<VerifiedCredential>>();
        for (val entry : presentations.entrySet()) {
            val credentialQuery = query.findCredential(entry.getKey())
                .orElseThrow(() -> {
                    LOGGER.debug("response rejected for transaction {}: unknown credential query identifier", transaction.getId());
                    return new OpenId4VpException("no credential query for the presentation: " + entry.getKey());
                });
            if (!Boolean.TRUE.equals(credentialQuery.getMultiple()) && entry.getValue().size() != 1) {
                LOGGER.debug("response rejected for transaction {}: query {} does not allow multiple presentations",
                    transaction.getId(), credentialQuery.getId());
                throw new OpenId4VpException("multiple presentations are not allowed for: " + entry.getKey());
            }
            val verifier = client.getConfiguration().getCredentialVerifiers().get(credentialQuery.getFormat());
            if (verifier == null) {
                LOGGER.debug("response rejected for transaction {}: no verifier for {}", transaction.getId(), credentialQuery.getFormat());
                throw new OpenId4VpException("no credential verifier registered for: " + credentialQuery.getFormat().getValue());
            }
            val results = new ArrayList<VerifiedCredential>();
            for (val raw : entry.getValue()) {
                LOGGER.debug("verifying presentation {}/{} for transaction {}, query {}, format={}, verifier={}",
                    results.size() + 1, entry.getValue().size(), transaction.getId(), credentialQuery.getId(),
                    credentialQuery.getFormat(), verifier.getClass().getSimpleName());
                try {
                    val result = verifier.verify(raw, transaction, client.getConfiguration());
                    dcqlValidator.validateCredential(credentialQuery, result);
                    results.add(result);
                } catch (final RuntimeException e) {
                    LOGGER.debug("credential verification failed for transaction {}, query {}: {}",
                        transaction.getId(), credentialQuery.getId(), e.getClass().getSimpleName());
                    throw e;
                }
                LOGGER.debug("presentation {}/{} verified for transaction {}, query {}", results.size(), entry.getValue().size(),
                    transaction.getId(), credentialQuery.getId());
            }
            verified.put(entry.getKey(), List.copyOf(results));
        }
        LOGGER.debug("checking DCQL credential selection for transaction {}", transaction.getId());
        dcqlValidator.validateSelection(query, verified.keySet());
        vpCredentials.setVpToken(presentations);
        vpCredentials.setVerifiedCredentials(verified);
        LOGGER.debug("wallet response validated for transaction {}: {} credential query results published",
            transaction.getId(), verified.size());
        return Optional.of(vpCredentials);
    }

    /**
     * Read a clear response or decrypt the response using only the key and algorithms of this request.
     *
     * @param transaction the answered transaction
     * @return presentations indexed by query identifier
     */
    protected Map<String, List<String>> readVpToken(final VpTransaction transaction) {
        val encrypted = transaction.getResponseMode().isEncrypted();
        val hasEncryptedResponse = transaction.getRawResponse() != null;
        val hasClearResponse = transaction.getRawVpToken() != null;
        if (hasEncryptedResponse != encrypted || hasClearResponse == encrypted) {
            LOGGER.debug("response rejected for transaction {}: unexpected response mode", transaction.getId());
            throw new OpenId4VpException("the wallet response does not match the requested response mode");
        }
        try {
            val request = JSONObjectUtils.parse(transaction.getRequestParameters());
            if (!encrypted) {
                checkState(transaction, request, transaction.getResponseState());
                return parseVpToken(transaction.getRawVpToken());
            }
            val response = decryptResponse(transaction, request);
            checkState(transaction, request, response.get(STATE));
            if (response.containsKey(ERROR)) {
                LOGGER.debug("response rejected for transaction {}: encrypted wallet error", transaction.getId());
                throw new OpenId4VpException("the wallet returned an encrypted error response");
            }
            if (!(response.get(VP_TOKEN) instanceof Map<?, ?>)) {
                LOGGER.debug("response rejected for transaction {}: missing or invalid encrypted vp_token", transaction.getId());
                throw new OpenId4VpException("the encrypted response must contain a vp_token object");
            }
            return parseVpToken(JSONObjectUtils.toJSONString(JSONObjectUtils.getJSONObject(response, VP_TOKEN)));
        } catch (final ParseException | JOSEException e) {
            LOGGER.debug("response decoding or decryption failed for transaction {}: {}",
                transaction.getId(), e.getClass().getSimpleName());
            throw new OpenId4VpException("invalid wallet response", e);
        }
    }

    private Map<String, Object> decryptResponse(final VpTransaction transaction, final Map<String, Object> request)
        throws ParseException, JOSEException {
        if (transaction.getEncryptionKey() == null) {
            LOGGER.debug("response rejected for transaction {}: missing decryption key", transaction.getId());
            throw new OpenId4VpException("the transaction has no response decryption key");
        }
        val key = ECKey.parse(transaction.getEncryptionKey());
        val jwe = JWEObject.parse(transaction.getRawResponse());
        val header = jwe.getHeader();
        LOGGER.debug("checking response encryption headers for transaction {}", transaction.getId());
        val metadata = JSONObjectUtils.getJSONObject(request, CLIENT_METADATA);
        val allowedEncryption = metadata.containsKey(ENCRYPTED_RESPONSE_ENC_VALUES_SUPPORTED)
            ? JSONObjectUtils.getStringList(metadata, ENCRYPTED_RESPONSE_ENC_VALUES_SUPPORTED) : List.of("A128GCM");
        if (!JWEAlgorithm.ECDH_ES.equals(header.getAlgorithm()) || !header.getAlgorithm().equals(key.getAlgorithm())
            || !Curve.P_256.equals(key.getCurve()) || !key.isPrivate()
            || !OpenId4VpRequestObjectBuilder.ACCEPTED_ENC_VALUES.contains(header.getEncryptionMethod().getName())
            || !allowedEncryption.contains(header.getEncryptionMethod().getName())) {
            LOGGER.debug("response rejected for transaction {}: unadvertised encryption algorithm or incompatible key",
                transaction.getId());
            throw new OpenId4VpException("the response uses an unadvertised encryption algorithm");
        }
        if (key.getKeyID() != null && !key.getKeyID().equals(header.getKeyID())) {
            LOGGER.debug("response rejected for transaction {}: encryption key identifier mismatch", transaction.getId());
            throw new OpenId4VpException("the response encryption key identifier does not match the request");
        }
        if (header.getCompressionAlgorithm() != null || (header.getCriticalParams() != null && !header.getCriticalParams().isEmpty())) {
            LOGGER.debug("response rejected for transaction {}: compression or critical JWE headers", transaction.getId());
            throw new OpenId4VpException("unsupported compressed response or critical JWE header");
        }
        jwe.decrypt(new ECDHDecrypter(key));
        LOGGER.debug("response decrypted and authentication tag verified for transaction {}: alg={}, enc={}",
            transaction.getId(), header.getAlgorithm(), header.getEncryptionMethod());
        return JSONObjectUtils.parse(jwe.getPayload().toString());
    }

    private void checkState(final VpTransaction transaction, final Map<String, Object> request, final Object returnedState) {
        LOGGER.debug("checking response state for transaction {}: DC API={}, state requested={}", transaction.getId(),
            transaction.getResponseMode().isOverDcApi(), request.containsKey(STATE));
        // DC API has no state parameter; its request is bound through the browser session and holder proof.
        if (!transaction.getResponseMode().isOverDcApi() && request.containsKey(STATE)
            && !Objects.equals(request.get(STATE), returnedState)) {
            LOGGER.debug("response rejected for transaction {}: state mismatch", transaction.getId());
            throw new OpenId4VpException("the response state does not match the request");
        }
    }

    /**
     * Read the array of presentations for every query. Both currently supported formats use strings.
     *
     * @param vpToken the serialized vp_token
     * @return the presentations
     */
    protected Map<String, List<String>> parseVpToken(final String vpToken) {
        try {
            val presentations = new LinkedHashMap<String, List<String>>();
            for (val entry : JSONObjectUtils.parse(vpToken).entrySet()) {
                if (!(entry.getValue() instanceof List<?> items) || items.isEmpty()) {
                    LOGGER.debug("vp_token rejected: an entry is not a non-empty array");
                    throw new OpenId4VpException("the vp_token entry " + entry.getKey() + " must be a non-empty array of presentations");
                }
                val values = new ArrayList<String>();
                for (val item : items) {
                    if (!(item instanceof String value) || value.isBlank()) {
                        LOGGER.debug("vp_token rejected: a presentation is not a non-blank string");
                        throw new OpenId4VpException("a presentation must be a non-empty string");
                    }
                    values.add(value);
                }
                presentations.put(entry.getKey(), List.copyOf(values));
            }
            return presentations;
        } catch (final ParseException e) {
            LOGGER.debug("vp_token rejected: malformed JSON object");
            throw new OpenId4VpException("the vp_token is not a JSON object", e);
        }
    }
}
