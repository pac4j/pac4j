package org.pac4j.oauth.profile;

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
        var rolesAsInt = 1 + 2 + 4 + 8 + 16 + 32 + 64 + 128 + 256 + 512 + 1024;
        var body = "{ \"" + HiOrgServerProfileDefinition.USER_ID + "\": 12345, \""
                + HiOrgServerProfileDefinition.ROLES + "\": " + rolesAsInt + " }";
        final var profileDefinition = new HiOrgServerProfileDefinition();
        final var profile = profileDefinition.extractUserProfile(body);
        final var roles = profile.getRoles();
        Assert.assertEquals(rolesAsInt, profile.getRolesAsInteger());
        for (var i = 0; i <= 10; i++) {
            var groupId = (int) Math.pow(2, i);
            var groupIdAsString = String.valueOf(groupId);
            Assert.assertTrue(roles.contains(groupIdAsString));
            Assert.assertTrue(profile.hasRole(groupId));
        }
    }

}
