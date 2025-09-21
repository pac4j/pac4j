package org.pac4j.oidc.redirect;

import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.pac4j.core.context.CallContext;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.profile.factory.ProfileManagerFactory;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.metadata.OidcOpMetadataResolver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OidcRedirectionActionBuilderTests implements TestsConstants {

    private static OidcClient getClient() throws Exception {
        var providerMetadata = mock(OIDCProviderMetadata.class);
        when(providerMetadata.getAuthorizationEndpointURI()).thenReturn(new java.net.URI("http://localhost:8080/auth"));
        val configuration = new OidcConfiguration();
        configuration.setClientId("testClient");
        configuration.setSecret("secret");
        configuration.setScope("openid,profile,email");
        val metadataResolver = mock(OidcOpMetadataResolver.class);
        when(metadataResolver.load()).thenReturn(providerMetadata);
        configuration.setOpMetadataResolver(metadataResolver);

        val client = new OidcClient();
        client.setConfiguration(configuration);
        client.setCallbackUrl(CALLBACK_URL);
        return client;
    }

    @Test
    public void testOidcRedirectionScopes() throws Exception {
        var builder = new OidcRedirectionActionBuilder(getClient());
        val webContext = MockWebContext.create();
        val sessionStore = new MockSessionStore();
        val ctx = new CallContext(webContext, sessionStore, ProfileManagerFactory.DEFAULT);
        var action = builder.getRedirectionAction(ctx).orElseThrow();
        assertEquals(302, action.getCode());
    }
}
