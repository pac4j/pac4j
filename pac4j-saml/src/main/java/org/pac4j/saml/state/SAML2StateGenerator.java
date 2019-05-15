package org.pac4j.saml.state;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.state.StateGenerator;
import org.pac4j.saml.client.SAML2Client;

import java.util.Optional;

/**
 * State generator for SAML 2.
 *
 * @author Jerome Leleu
 * @since 3.3.0
 */
public class SAML2StateGenerator implements StateGenerator {

    public static final String SAML_RELAY_STATE_ATTRIBUTE = "samlRelayState";

    private final SAML2Client client;

    public SAML2StateGenerator(final SAML2Client client) {
        this.client = client;
    }

    @Override
    public String generateState(final WebContext webContext) {
        final Optional<String> relayState = (Optional<String>) webContext.getSessionStore().get(webContext, SAML_RELAY_STATE_ATTRIBUTE);
        // clean from session after retrieving it
        if (relayState.isPresent()) {
            webContext.getSessionStore().set(webContext, SAML_RELAY_STATE_ATTRIBUTE, "");
        }
        return relayState.isPresent() ? relayState.get() : client.computeFinalCallbackUrl(webContext);
    }
}
