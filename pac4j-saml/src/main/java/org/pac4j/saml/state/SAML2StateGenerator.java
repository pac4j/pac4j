package org.pac4j.saml.state;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.util.generator.ValueGenerator;
import org.pac4j.saml.client.SAML2Client;

/**
 * State generator for SAML 2.
 *
 * @author Jerome Leleu
 * @since 3.3.0
 */
@RequiredArgsConstructor
public class SAML2StateGenerator implements ValueGenerator {

    /** Constant <code>SAML_RELAY_STATE_ATTRIBUTE="samlRelayState"</code> */
    public static final String SAML_RELAY_STATE_ATTRIBUTE = "samlRelayState";

    private final SAML2Client client;

    /** {@inheritDoc} */
    @Override
    public String generateValue(final CallContext ctx) {
        val webContext = ctx.webContext();
        val sessionStore = ctx.sessionStore();

        val relayState = sessionStore.get(webContext, SAML_RELAY_STATE_ATTRIBUTE);
        // clean from session after retrieving it
        if (relayState.isPresent()) {
            sessionStore.set(webContext, SAML_RELAY_STATE_ATTRIBUTE, null);
        }
        return relayState.isPresent() ? (String) relayState.get() : client.computeFinalCallbackUrl(webContext);
    }
}
