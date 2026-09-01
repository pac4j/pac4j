package org.pac4j.oidc.config;

import org.junit.jupiter.api.Test;
import org.pac4j.test.context.MockWebContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @Test
    public void shouldResolveLoginHintFromRequest() {
        var webContext = MockWebContext.create();
        webContext.setRequestAttribute(OidcConfiguration.LOGIN_HINT, "user@example.org");
        var oidcConfiguration = new OidcConfiguration();
        oidcConfiguration.setLoginHint("user@pac4j.org");
        var oidcConfigurationContext = new OidcConfigurationContext(webContext, oidcConfiguration);
        var result = oidcConfigurationContext.getLoginHint();
        assertEquals("user@example.org", result);
        webContext.setRequestAttribute(OidcConfiguration.LOGIN_HINT, null);
        result = oidcConfigurationContext.getLoginHint();
        assertEquals("user@pac4j.org", result);
    }
}
