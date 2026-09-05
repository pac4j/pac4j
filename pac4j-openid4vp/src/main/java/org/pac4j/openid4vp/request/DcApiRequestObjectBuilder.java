package org.pac4j.openid4vp.request;

import lombok.val;
import org.pac4j.core.context.CallContext;
import org.pac4j.openid4vp.client.OpenId4VpClient;
import org.pac4j.openid4vp.config.OpenId4VpDcApiConfiguration;
import org.pac4j.openid4vp.transaction.VpTransaction;

import java.util.Map;

import static org.pac4j.openid4vp.util.OpenId4VpConstants.EXPECTED_ORIGINS;

/**
 * Builds the request object handed to the digital credentials API of the browser.
 *
 * <p>It is the very same signed request object as the one a wallet fetches by URL, save for the claims which
 * depend on how the wallet is reached: no response URI, since the answer comes back through the browser, and
 * the origins the request may legitimately come from.</p>
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
public class DcApiRequestObjectBuilder extends RequestObjectBuilder {

    /**
     * <p>Constructor for DcApiRequestObjectBuilder.</p>
     *
     * @param client a {@link OpenId4VpClient} object
     */
    public DcApiRequestObjectBuilder(final OpenId4VpClient client) {
        super(client);
    }

    /** {@inheritDoc} */
    @Override
    protected void addBindingParameters(final CallContext ctx, final VpTransaction transaction,
                                        final Map<String, Object> parameters) {
        val configuration = (OpenId4VpDcApiConfiguration) client.getConfiguration();
        // the state is not defined over this binding, and a wallet ignores it
        parameters.put(EXPECTED_ORIGINS, configuration.getExpectedOrigins());
    }
}
