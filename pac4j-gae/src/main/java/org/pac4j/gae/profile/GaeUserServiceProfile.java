package org.pac4j.gae.profile;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.gae.client.GaeUserServiceClient;

/**
 * <p>This class is the user profile for Google using UserService with appropriate getters.</p>
 * <p>It is returned by the {@link GaeUserServiceClient}.</p>
 *
 * @see org.pac4j.gae.client.GaeUserServiceClient
 * @author Patrice de Saint Steban
 * @since 1.6.0
 */
public class GaeUserServiceProfile extends CommonProfile {

    private static final long serialVersionUID = 7866288887408897456L;

    public final static String PAC4J_GAE_GLOBAL_ADMIN_ROLE = "GLOBAL_ADMIN";
}
