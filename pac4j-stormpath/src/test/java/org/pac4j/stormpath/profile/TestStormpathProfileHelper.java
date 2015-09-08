package org.pac4j.stormpath.profile;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.TestCaseProfileHelper;

/**
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
