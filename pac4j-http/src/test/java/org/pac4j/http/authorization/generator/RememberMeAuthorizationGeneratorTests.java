package org.pac4j.http.authorization.generator;

import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.TestsConstants;

import static org.junit.Assert.*;

/**
 * Tests the {@link RememberMeAuthorizationGenerator}.
 *
 * @author Jerome Leleu
 * @since 2.0.0
 */
public final class RememberMeAuthorizationGeneratorTests implements TestsConstants {

    private CommonProfile profile;

    @Before
    public void setUp() {
        profile = new CommonProfile();
    }

    @Test
    public void testNoRme() {
        final RememberMeAuthorizationGenerator ag = new RememberMeAuthorizationGenerator();
        ag.generate(MockWebContext.create(), profile);
        assertFalse(profile.isRemembered());
    }

    @Test
    public void testBadRmeValue() {
        final RememberMeAuthorizationGenerator ag = new RememberMeAuthorizationGenerator();
        final MockWebContext context = MockWebContext.create().addRequestParameter("rme", "no");
        ag.generate(context, profile);
        assertFalse(profile.isRemembered());
    }

    @Test
    public void testGoodRmeValue() {
        final RememberMeAuthorizationGenerator ag = new RememberMeAuthorizationGenerator();
        final MockWebContext context = MockWebContext.create().addRequestParameter("rme", "true");
        ag.generate(context, profile);
        assertTrue(profile.isRemembered());
    }

    @Test
    public void testGoodSpecialRmeValue() {
        final RememberMeAuthorizationGenerator ag = new RememberMeAuthorizationGenerator("r", "y");
        final MockWebContext context = MockWebContext.create().addRequestParameter("r", "y");
        ag.generate(context, profile);
        assertTrue(profile.isRemembered());
    }

    @Test(expected = TechnicalException.class)
    public void testBlankValues() {
        new RememberMeAuthorizationGenerator("", "");
    }
}
