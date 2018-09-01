package org.pac4j.oauth.run;

import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.run.RunClient;
import org.pac4j.oauth.client.HiOrgServerClient;
import org.pac4j.oauth.profile.hiorgserver.HiOrgServerProfile;
import static org.junit.Assert.*;

/**
 * Run manually a test for the {@link HiOrgServerClient}.
 *
 * @author Martin Böhmer
 * @since 3.1.1
 */
public class RunHiOrgServerClient extends RunClient {

    public static void main(String[] args) {
        new RunHiOrgServerClient().run();
    }

    @Override
    protected String getLogin() {
        return "testscribeup@gmail.com";
    }

    @Override
    protected String getPassword() {
        return "testpwdscribeup";
    }

    @Override
    protected boolean canCancel() {
        return true;
    }

    @Override
    protected IndirectClient getClient() {
        final HiOrgServerClient client = new HiOrgServerClient();
        client.setKey("your client id");
        client.setSecret("your secret");
        client.setCallbackUrl(PAC4J_BASE_URL);
        return client;
    }

    @Override
    protected void verifyProfile(CommonProfile userProfile) {
        final HiOrgServerProfile profile = (HiOrgServerProfile) userProfile;
        assertEquals("1a396c7895f10eac304a81eef63ca0e2", profile.getId());
        assertEquals("doej", profile.getUsername().toLowerCase());
        assertEquals("John", profile.getFirstName());
        assertEquals("Doe", profile.getFamilyName());
        assertEquals("John Doe", profile.getDisplayName());
        assertEquals("erk", profile.getOrganisationId());
        assertEquals("DRK im Ennepe-Ruhr-Kreis Kreisverband e. V.", profile.getOrganisationName());
        assertEquals("Kreisrotkreuzleiter", profile.getPosition());
        assertEquals(Boolean.TRUE, profile.isLeader());
    }

}
