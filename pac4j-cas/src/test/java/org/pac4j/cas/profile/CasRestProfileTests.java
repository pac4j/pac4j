package org.pac4j.cas.profile;

import org.junit.Test;
import org.pac4j.core.util.TestsConstants;

import static org.junit.Assert.*;

/**
 * General test cases for {@link CasRestProfile}.
 *
 * @author Jacob Severson
 * @since 1.8.0
 */
public final class CasRestProfileTests implements TestsConstants {

    @Test
    public void testClearProfile() {
        final CasRestProfile profile = new CasRestProfile(ID, USERNAME);
        profile.removeLoginData();
        assertNull(profile.getTicketGrantingTicketId());
    }
}
