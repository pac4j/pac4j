package org.pac4j.openid4vp.credentials.extractor;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.pac4j.core.context.WebContext;
import org.pac4j.openid4vp.config.ResponseMode;
import org.pac4j.openid4vp.exceptions.OpenId4VpException;
import org.pac4j.openid4vp.transaction.VpTransaction;

import static org.pac4j.openid4vp.util.OpenId4VpConstants.*;

/**
 * Reads what the wallet answered, whichever way it came: encrypted, in the {@code response} parameter; in
 * clear, as the {@code vp_token} parameter; or as an error.
 *
 * <p>The encrypted response modes carry the whole response in one JWE, "the Wallet adds the response
 * parameter containing the JWT", while the clear ones carry the response parameters themselves, "the
 * Response URI receives all Authorization Response parameters as defined by the respective Response Type",
 * that is {@code vp_token} and, when the request had one, {@code state}. A clear answer to a request which
 * asked for an encrypted one is refused: the credentials would have travelled unprotected.</p>
 *
 * <p>An error follows RFC 6749: {@code error} and an optional {@code error_description}. "If a Wallet is
 * unable to generate an encrypted response, it MAY send an error response without encryption", so an error
 * is accepted in clear whatever the response mode.</p>
 *
 * @see <a href="https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#response_mode_post">
 *     OpenID4VP 1.0, response mode direct_post</a>
 * @see <a href="https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#response_encryption">
 *     OpenID4VP 1.0, encrypted responses</a>
 * @see <a href="https://openid.net/specs/openid-4-verifiable-presentations-1_0.html#section-8.5">
 *     OpenID4VP 1.0, error response</a>
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
@UtilityClass
@Slf4j
public class WalletResponseReader {

    /**
     * <p>Whether the request carries an answer of the wallet, of any of the three kinds.</p>
     *
     * @param webContext the web context
     * @return a boolean
     */
    public boolean carriesAnswer(final WebContext webContext) {
        return webContext.getRequestParameter(RESPONSE).isPresent()
            || webContext.getRequestParameter(VP_TOKEN).isPresent()
            || webContext.getRequestParameter(ERROR).isPresent();
    }

    /**
     * <p>The message of a refusal, for the leg where it surfaces.</p>
     *
     * @param transaction the transaction the wallet refused
     * @return the message
     */
    public String refusalMessage(final VpTransaction transaction) {
        val description = transaction.getErrorDescription() != null ? " (" + transaction.getErrorDescription() + ")" : "";
        return "the wallet refused the presentation of the transaction " + transaction.getId() + ": "
            + transaction.getError() + description;
    }

    /**
     * <p>Store the answer of the wallet in the transaction.</p>
     *
     * @param webContext the web context
     * @param transaction the transaction answered
     * @param responseMode the response mode the request asked for
     */
    public void read(final WebContext webContext, final VpTransaction transaction, final ResponseMode responseMode) {
        val id = transaction.getId();
        val response = webContext.getRequestParameter(RESPONSE).orElse(null);
        val vpToken = webContext.getRequestParameter(VP_TOKEN).orElse(null);
        val error = webContext.getRequestParameter(ERROR).orElse(null);
        val mode = transaction.getResponseMode() == null ? responseMode : transaction.getResponseMode();
        LOGGER.debug("reading wallet response for transaction {}: expected mode={}, saved mode={}", id, mode,
            transaction.getResponseMode() != null);
        if ((response != null ? 1 : 0) + (vpToken != null ? 1 : 0) + (error != null ? 1 : 0) != 1) {
            LOGGER.debug("wallet response rejected for transaction {}: missing or mixed response parameters", id);
            throw new OpenId4VpException("exactly one of response, vp_token or error must be posted by the wallet");
        }
        if ((response != null && response.isBlank()) || (vpToken != null && vpToken.isBlank())
            || (error != null && error.isBlank())) {
            LOGGER.debug("wallet response rejected for transaction {}: blank response parameter", id);
            throw new OpenId4VpException("the wallet response cannot be blank");
        }
        if (response != null) {
            if (!mode.isEncrypted()) {
                LOGGER.debug("wallet response rejected for transaction {}: unexpected encryption", id);
                throw new OpenId4VpException("an encrypted response was not requested: " + id);
            }
            transaction.setRawResponse(response);
            LOGGER.debug("the wallet posted its encrypted response for the transaction: {} ({} characters)", id, response.length());
        } else if (vpToken != null) {
            if (mode.isEncrypted()) {
                LOGGER.debug("wallet response rejected for transaction {}: encryption required", id);
                throw new OpenId4VpException("the wallet answered in clear a request asking for an encrypted response ("
                    + mode.getValue() + "): " + id);
            }
            transaction.setRawVpToken(vpToken);
            LOGGER.debug("the wallet posted its response in clear for the transaction: {} ({} characters)", id, vpToken.length());
        } else if (error != null) {
            transaction.setError(error);
            transaction.setErrorDescription(webContext.getRequestParameter(ERROR_DESCRIPTION).orElse(null));
            LOGGER.debug("the wallet answered transaction {} with an error; description present={}", id,
                transaction.getErrorDescription() != null);
        } else {
            throw new OpenId4VpException("no response, vp_token or error posted by the wallet for the transaction: " + id);
        }
        transaction.setResponseState(webContext.getRequestParameter(STATE).orElse(null));
        transaction.setStatus(VpTransaction.Status.RESPONSE_RECEIVED);
        LOGGER.debug("wallet response read for transaction {}: status={}, state present={}", id, transaction.getStatus(),
            transaction.getResponseState() != null);
    }
}
