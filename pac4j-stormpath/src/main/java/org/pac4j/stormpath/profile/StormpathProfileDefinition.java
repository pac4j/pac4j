package org.pac4j.stormpath.profile;

import org.pac4j.core.profile.definition.CommonProfileDefinition;

/**
 * Profile definition for Stormpath.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public class StormpathProfileDefinition extends CommonProfileDefinition<StormpathProfile> {

    public static final String FULL_NAME = "fullName";
    public static final String GIVEN_NAME = "givenName";
    public static final String MIDDLE_NAME = "middleName";
    public static final String SUR_NAME = "surName";
    public static final String GROUPS = "groups";
    public static final String GROUP_MEMBERSHIPS = "groupMemberships";
    public static final String STATUS = "status";

    public StormpathProfileDefinition() {
        super();
        setProfileFactory(parameters -> new StormpathProfile());
    }
}
