package org.pac4j.oidc.client;

import com.nimbusds.oauth2.sdk.id.State;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.exception.http.FoundAction;
import org.pac4j.core.exception.http.HttpAction;
import org.pac4j.core.exception.http.RedirectionAction;
import org.pac4j.core.exception.http.StatusAction;
import org.pac4j.core.http.ajax.AjaxRequestResolver;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.oidc.config.OidcConfiguration;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

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

    private OidcClient<OidcConfiguration> getClient() throws URISyntaxException {
    
        OIDCProviderMetadata providerMetadata = mock(OIDCProviderMetadata.class);
        when(providerMetadata.getAuthorizationEndpointURI()).thenReturn(new java.net.URI("http://localhost:8080/auth"));
    
        OidcConfiguration configuration = new OidcConfiguration();
        configuration.setClientId("testClient");
        configuration.setSecret("secret");
        configuration.setProviderMetadata(providerMetadata);
    
        final OidcClient<OidcConfiguration> client = new OidcClient<>();
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
            public boolean isAjax(WebContext context) {
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
            public HttpAction buildAjaxResponse(RedirectionAction action, WebContext context) {
                return new StatusAction(401);
            }
        });
        
        MockWebContext context = MockWebContext.create();
        
        final FoundAction firstRequestAction = (FoundAction) client.redirect(context);
        String state = splitQuery(new URL(firstRequestAction.getLocation())).get("state");
    
        try {
            //noinspection ThrowableNotThrown
            client.redirect(context);
            fail("Ajax request should throw exception");
        } catch (Exception e) {
            State stateAfterAjax = (State) context.getSessionStore().get(context, OidcConfiguration.STATE_SESSION_ATTRIBUTE);
            assertEquals("subsequent ajax request should not override the state in the session store", state, stateAfterAjax.toString());
        }
    
    }
    
    static Map<String, String> splitQuery(URL url) {
        Map<String, String> query_pairs = new LinkedHashMap<>();
        String query = url.getQuery();
        String[] pairs = query.split("&", -1);
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(CommonHelper.urlEncode(pair.substring(0, idx)), CommonHelper.urlEncode(pair.substring(idx + 1)));
        }
        return query_pairs;
    }
    
}


