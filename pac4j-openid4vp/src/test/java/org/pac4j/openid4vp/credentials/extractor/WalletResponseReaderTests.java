package org.pac4j.openid4vp.credentials.extractor;

import lombok.val;
import org.junit.jupiter.api.Test;
import org.pac4j.openid4vp.config.ResponseMode;
import org.pac4j.openid4vp.exceptions.OpenId4VpException;
import org.pac4j.openid4vp.transaction.VpTransaction;
import org.pac4j.test.context.MockWebContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.pac4j.openid4vp.util.OpenId4VpConstants.*;

/**
 * Reject ambiguous response parameters before changing the transaction.
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
class WalletResponseReaderTests {

    @Test
    void rejectsMixedResponsesWithoutKeepingPartialData() {
        for (val pair : List.of(List.of(RESPONSE, VP_TOKEN), List.of(RESPONSE, ERROR), List.of(VP_TOKEN, ERROR))) {
            val transaction = new VpTransaction();
            val context = MockWebContext.create().addRequestParameter(pair.get(0), "value")
                .addRequestParameter(pair.get(1), "value");
            assertThrows(OpenId4VpException.class, () -> WalletResponseReader.read(context, transaction, ResponseMode.DIRECT_POST_JWT));
            assertFalse(transaction.isAnswered());
            assertEquals(VpTransaction.Status.CREATED, transaction.getStatus());
        }
    }

    @Test
    void rejectsMissingOrBlankResponses() {
        val transaction = new VpTransaction();
        assertThrows(OpenId4VpException.class,
            () -> WalletResponseReader.read(MockWebContext.create(), transaction, ResponseMode.DIRECT_POST_JWT));
        for (val name : List.of(RESPONSE, VP_TOKEN, ERROR)) {
            val context = MockWebContext.create().addRequestParameter(name, " ");
            assertThrows(OpenId4VpException.class, () -> WalletResponseReader.read(context, transaction, ResponseMode.DIRECT_POST_JWT));
        }
        assertFalse(transaction.isAnswered());
    }

    @Test
    void usesTheTransactionModeEvenIfConfigurationChanged() {
        val transaction = new VpTransaction().setResponseMode(ResponseMode.DIRECT_POST_JWT);
        val context = MockWebContext.create().addRequestParameter(VP_TOKEN, "{}");
        assertThrows(OpenId4VpException.class, () -> WalletResponseReader.read(context, transaction, ResponseMode.DIRECT_POST));
        transaction.setResponseMode(ResponseMode.DIRECT_POST);
        val encrypted = MockWebContext.create().addRequestParameter(RESPONSE, "encrypted");
        assertThrows(OpenId4VpException.class,
            () -> WalletResponseReader.read(encrypted, transaction, ResponseMode.DIRECT_POST_JWT));
    }

    @Test
    void keepsClearStateForValidationAndAllowsUnencryptedErrors() {
        val transaction = new VpTransaction();
        val context = MockWebContext.create().addRequestParameter(VP_TOKEN, "{}").addRequestParameter(STATE, "state");
        WalletResponseReader.read(context, transaction, ResponseMode.DIRECT_POST);
        assertEquals("state", transaction.getResponseState());
        val error = new VpTransaction();
        WalletResponseReader.read(MockWebContext.create().addRequestParameter(ERROR, "access_denied"),
            error, ResponseMode.DIRECT_POST_JWT);
        assertEquals("access_denied", error.getError());
    }
}
