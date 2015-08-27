package org.pac4j.cas.client.rest;

import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.TestCaseProfileHelper;

/**
 * The {@link TestHttpTGTProfileHelper} is responsible for
 * executing tests against {@link CasRestFormClient}.
 *
 * @author Misagh Moayyed
 */
public class TestHttpTGTProfileHelper extends TestCaseProfileHelper {

    @Override
    protected Class<? extends CommonProfile> getProfileClass() {
        return HttpTGTProfile.class;
    }

    @Override
    protected String getProfileType() {
        return HttpTGTProfile.class.getSimpleName();
    }

    @Override
    protected String getAttributeName() {
        return "nothing";
    }
}
