package org.pac4j.cas.credentials;

import junit.framework.TestCase;
import org.pac4j.core.util.TestsConstants;

/**
 * General test cases for CasCredentials
 *
 * @author  Jacob Severson
 * @since   1.8.0
 */
public class TestCasCredentials extends TestCase implements TestsConstants {

    public void testClearCredentials() {
        final CasCredentials casCredentials = new CasCredentials(
                "testServiceTicket",
                "TestClientName"
        );
        casCredentials.clear();
        assertNull(casCredentials.getClientName());
        assertNull(casCredentials.getServiceTicket());
    }

}
