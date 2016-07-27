package org.pac4j.cas.client.direct;

import org.junit.Test;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

/**
 * Tests the {@link DirectCasClient}.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public final class DirectCasClientTests implements TestsConstants {

    @Test
    public void testInitOk() {
        final CasConfiguration configuration = new CasConfiguration();
        configuration.setLoginUrl(LOGIN_URL);
        final DirectCasClient client = new DirectCasClient(configuration);
        client.init(MockWebContext.create());
    }

    @Test
    public void testInitMissingConfiguration() {
        final DirectCasClient client = new DirectCasClient();
        TestsHelper.expectException(() -> client.init(MockWebContext.create()), TechnicalException.class, "configuration cannot be null");
    }

    @Test
    public void testInitGatewayForbidden() {
        final CasConfiguration configuration = new CasConfiguration();
        configuration.setLoginUrl(LOGIN_URL);
        configuration.setGateway(true);
        final DirectCasClient client = new DirectCasClient(configuration);
        TestsHelper.expectException(() -> client.init(MockWebContext.create()), TechnicalException.class, "the DirectCasClient can not support gateway to avoid infinite loops");
    }
}
