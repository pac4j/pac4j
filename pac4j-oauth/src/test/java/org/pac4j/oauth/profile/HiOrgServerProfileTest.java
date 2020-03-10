package org.pac4j.oauth.profile;

import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.oauth.profile.hiorgserver.HiOrgServerProfile;
import org.pac4j.oauth.profile.hiorgserver.HiOrgServerProfileDefinition;

/**
 * This class tests the {@link HiOrgServerProfile} class.
 *
 * @author Martin Böhmer
 * @since 3.2.0
 */
public class HiOrgServerProfileTest {

    @Test
    public void testGetRoles() {
        final int rolesAsInt = 1 + 2 + 4 + 8 + 16 + 32 + 64 + 128 + 256 + 512 + 1024;
        final String body = "{ \"" + HiOrgServerProfileDefinition.USER_ID + "\": 12345, \""
                + HiOrgServerProfileDefinition.ROLES + "\": " + rolesAsInt + " }";
        final HiOrgServerProfileDefinition profileDefinition = new HiOrgServerProfileDefinition();
        final HiOrgServerProfile profile = profileDefinition.extractUserProfile(body);
        final Set<String> roles = profile.getRoles();
        Assert.assertEquals(rolesAsInt, profile.getRolesAsInteger());
        for (int i = 0; i <= 10; i++) {
            final int groupId = (int) Math.pow(2, i);
            final String groupIdAsString = String.valueOf(groupId);
            Assert.assertTrue(roles.contains(groupIdAsString));
            Assert.assertTrue(profile.hasRole(groupId));
        }
    }

}
