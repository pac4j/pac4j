package org.pac4j.oauth.run;

import com.esotericsoftware.kryo.Kryo;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.run.RunClient;
import org.pac4j.oauth.client.OrcidClient;
import org.pac4j.oauth.profile.orcid.OrcidProfile;

/**
 * Run manually a test for the {@link OrcidClient}. Doesn't work for a public API, requires a member API.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class RunOrcidClient extends RunClient {

    public static void main(String[] args) throws Exception {
        new RunOrcidClient().run();
    }

    @Override
    protected String getLogin() {
        return "testpac4j@gmail.com";
    }

    @Override
    protected String getPassword() {
        return "testpwdpac4j";
    }

    @Override
    protected IndirectClient getClient() {
        final OrcidClient client = new OrcidClient();
        client.setKey("APP-IVZK2KU3UNHH2AH0");
        client.setSecret("852f8210-ff83-45ff-9a04-a52a39b41abd");
        client.setCallbackUrl(PAC4J_URL);
        return client;
    }

    @Override
    protected void registerForKryo(final Kryo kryo) {
        kryo.register(OrcidProfile.class);
    }

    @Override
    protected void verifyProfile(CommonProfile userProfile) {
    }
}
