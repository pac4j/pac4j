package org.pac4j.oidc.config;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;

import static org.junit.Assert.assertEquals;

public class OidcConfigurationContextTest {
    @Test
    public void shouldResolveScopeWhenOverriddenFromRequest() {
        var webContext = MockWebContext.create();
        webContext.setRequestAttribute(OidcConfiguration.SCOPE, "openid profile email phone");

        var oidcConfiguration = new OidcConfiguration();

        var oidcConfigurationContext = new OidcConfigurationContext(webContext, oidcConfiguration);

        var result = oidcConfigurationContext.getScope();

        assertEquals("openid profile email phone", result);
    }

    @Test
    public void shouldResolveScopeWhenConfiguredProgrammatically() {
        var webContext = MockWebContext.create();

        var oidcConfiguration = new OidcConfiguration();
        oidcConfiguration.setScope("openid profile email products");

        var oidcConfigurationContext = new OidcConfigurationContext(webContext, oidcConfiguration);

        var result = oidcConfigurationContext.getScope();

        assertEquals("openid profile email products", result);
    }

    @Test
    public void shouldResolveScopeFromDefaultValues() {
        var webContext = MockWebContext.create();

        var oidcConfiguration = new OidcConfiguration();

        var oidcConfigurationContext = new OidcConfigurationContext(webContext, oidcConfiguration);

        var result = oidcConfigurationContext.getScope();

        assertEquals("openid profile email", result);
    }
}
