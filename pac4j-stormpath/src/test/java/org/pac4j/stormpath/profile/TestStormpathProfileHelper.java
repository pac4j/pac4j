package org.pac4j.stormpath.profile;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.profile.TestCaseProfileHelper;

/**
 * This class tests the {@link ProfileHelper} class for the {@link StormpathProfile}.
 * @author Misagh Moayyed
 * @since 1.8
 */
public class TestStormpathProfileHelper extends TestCaseProfileHelper {

    @Override
    protected Class<? extends CommonProfile> getProfileClass() {
        return StormpathProfile.class;
    }

    @Override
    protected String getProfileType() {
        return "StormpathProfile";
    }

    @Override
    protected String getAttributeName() {
        return "stormpathAttributeName";
    }
}
