package org.pac4j.oidc.run;

import com.esotericsoftware.kryo.Kryo;
import com.nimbusds.oauth2.sdk.token.AccessTokenType;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.run.RunClient;
import org.pac4j.oidc.client.OidcClient;
import org.pac4j.oidc.config.OidcConfiguration;
import org.pac4j.oidc.kryo.AccessTokenTypeSerializer;
import org.pac4j.oidc.profile.OidcProfile;

import static org.junit.Assert.assertEquals;

/**
 * Run a manual test for the CAS OpenID Connect wrapper support.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public class RunCasOidcWrapper extends RunClient {

    public static void main(final String[] args) throws Exception {
        new RunCasOidcWrapper().run();
    }

    @Override
    protected String getLogin() {
        return "jleleu";
    }

    @Override
    protected String getPassword() {
        return "jleleu";
    }

    @Override
    protected IndirectClient getClient() {
        final OidcConfiguration configuration = new OidcConfiguration();
        configuration.setClientId("testoidc");
        configuration.setSecret("secret");
        //configuration.setDiscoveryURI("https://casserverpac4j.herokuapp.com/oidc/.well-known/openid-configuration");
        configuration.setDiscoveryURI("http://localhost:8888/cas/oidc/.well-known/openid-configuration");
        final OidcClient client = new OidcClient(configuration);
        client.setCallbackUrl(PAC4J_BASE_URL);
        return client;
    }

    @Override
    protected void registerForKryo(final Kryo kryo) {
        kryo.register(OidcProfile.class);
        kryo.register(AccessTokenType.class, new AccessTokenTypeSerializer());
    }

    @Override
    protected void verifyProfile(final CommonProfile userProfile) {
        final OidcProfile profile = (OidcProfile) userProfile;
        assertEquals("", profile.getId());
        //final DefaultIdTokenProfile idTokenProfile = (DefaultIdTokenProfile) profile.getIdTokenProfile().get();
    }
}
