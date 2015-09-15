package org.pac4j.cas.client.rest;

import junit.framework.TestCase;

/**
 * General test cases for HttpTGTProfile.
 *
 * @author  Jacob Severson
 * @since   1.8.0
 */
public class TestHttpTGTProfile extends TestCase {

    public void testClearProfile() {
        final HttpTGTProfile profile = new HttpTGTProfile("testId", "testUser");
        profile.clear();
        assertNull(profile.getTicketGrantingTicketId());
    }
}
