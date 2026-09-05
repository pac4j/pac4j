package org.pac4j.openid4vp.redirect;

import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.pac4j.core.config.properties.JwksProperties;

import org.pac4j.core.context.CallContext;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.openid4vp.client.OpenId4VpClient;
import org.pac4j.openid4vp.config.ClientIdPrefix;
import org.pac4j.openid4vp.config.OpenId4VpConfiguration;
import org.pac4j.openid4vp.transaction.VpTransaction;
import org.pac4j.openid4vp.verifier.SdJwtVcVerifier;
import org.pac4j.test.context.MockWebContext;
import org.pac4j.test.context.session.MockSessionStore;


import static org.junit.jupiter.api.Assertions.*;
import static org.pac4j.openid4vp.util.OpenId4VpConstants.*;

/**
 * Tests the transaction opened when a presentation starts, and the URL handed over to invoke a wallet.
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
class OpenId4VpRedirectionActionBuilderTests {

    private static final String CALLBACK_URL = "https://app.example.org/callback";
    private static final String CLIENT = "https://app.example.org/callback";
    private static final String DCQL = "{\"credentials\":[{\"id\":\"pid\",\"format\":\"dc+sd-jwt\"}]}";

    @TempDir
    private java.nio.file.Path directory;

    private OpenId4VpClient client;
    private OpenId4VpConfiguration configuration;

    @BeforeEach
    void setUp() throws Exception {
        configuration = new OpenId4VpConfiguration();
        configuration.setJwks(new JwksProperties()
            .setJwksPath(directory.resolve("keys.jwks").toString()).setKid("key-1"));
        configuration.setClientId(CLIENT);
        configuration.setClientIdPrefix(ClientIdPrefix.DECENTRALIZED_IDENTIFIER);
        configuration.setDcqlQuery(DCQL);
        configuration.addCredentialVerifier(new SdJwtVcVerifier());

        client = new OpenId4VpClient(configuration);
        client.setName("EudiWallet");
        client.setCallbackUrl(CALLBACK_URL);
    }

    private VpTransaction openTransaction() {
        val ctx = new CallContext(MockWebContext.create(), new MockSessionStore());
        val action = assertInstanceOf(FoundAction.class, client.getRedirectionAction(ctx).get());
        // the wallet is invoked with the custom scheme, carrying the verifier and where to fetch the request
        assertTrue(action.getLocation().startsWith("openid4vp://"));
        assertTrue(action.getLocation().contains(CLIENT_ID + "="));
        assertTrue(action.getLocation().contains(REQUEST_URI + "="));

        val transactionId = ctx.sessionStore().get(ctx.webContext(), SESSION_TRANSACTION_ID).get().toString();
        return configuration.getTransactionStore().get(transactionId).get();
    }

    @Test
    void testEachTransactionHasItsOwnKeyAndNonce() {
        val first = openTransaction();
        val second = openTransaction();

        assertTrue(!first.getNonce().equals(second.getNonce()));
        assertTrue(!first.getEncryptionKey().equals(second.getEncryptionKey()));
    }
}
