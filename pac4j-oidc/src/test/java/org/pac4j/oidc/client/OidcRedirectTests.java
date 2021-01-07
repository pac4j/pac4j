package org.pac4j.oidc.client;

import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.MockSessionStore;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.exception.http.StatusAction;
import org.pac4j.core.http.ajax.AjaxRequestResolver;
import org.pac4j.core.redirect.RedirectionActionBuilder;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;
import org.pac4j.oidc.config.OidcConfiguration;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This class tests client redirects.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class OidcRedirectTests implements TestsConstants {

    private OidcClient getClient() throws URISyntaxException {

        OIDCProviderMetadata providerMetadata = mock(OIDCProviderMetadata.class);
        when(providerMetadata.getAuthorizationEndpointURI()).thenReturn(new java.net.URI("http://localhost:8080/auth"));

        OidcConfiguration configuration = new OidcConfiguration();
        configuration.setClientId("testClient");
        configuration.setSecret("secret");
        configuration.setProviderMetadata(providerMetadata);

        final OidcClient client = new OidcClient();
        client.setConfiguration(configuration);
        client.setCallbackUrl(CALLBACK_URL);

        return client;
    }

    @Test
    public void testAjaxRequestAfterStandardRequestShouldNotOverrideState() throws MalformedURLException, URISyntaxException {
        OidcClient client = getClient();
        client.setCallbackUrl(CALLBACK_URL);
        client.setAjaxRequestResolver(new AjaxRequestResolver() {
            boolean first = true;
            @Override
            public boolean isAjax(final WebContext context, final SessionStore sessionStore) {
                /*
                 * Considers that the first request is not ajax, all the subsequent ones are
                 */
                if (first) {
                    first = false;
                    return false;
                } else {
                    return true;
                }
            }
            @Override
            public HttpAction buildAjaxResponse(final WebContext context, final SessionStore sessionStore,
                                                final RedirectionActionBuilder redirectionActionBuilder) {
                return new StatusAction(401);
            }
        });

        MockWebContext context = MockWebContext.create();
        final SessionStore sessionStore = new MockSessionStore();

        final FoundAction firstRequestAction = (FoundAction) client.getRedirectionAction(context, sessionStore).orElse(null);
        String state = TestsHelper.splitQuery(new URL(firstRequestAction.getLocation())).get("state");

        try {
            //noinspection ThrowableNotThrown
            client.getRedirectionAction(context, sessionStore);
            fail("Ajax request should throw exception");
        } catch (Exception e) {
            State stateAfterAjax = (State) sessionStore.get(context, client.getStateSessionAttributeName()).orElse(null);
            assertEquals("subsequent ajax request should not override the state in the session store", state, stateAfterAjax.toString());
        }

    }
}
