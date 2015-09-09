package org.pac4j.saml.credentials;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.impl.ConditionsBuilder;
import org.opensaml.saml.saml2.core.impl.NameIDBuilder;

/**
 * General test cases for SAML2Credentials.
 *
 * @author Jacob Severson
 * @since  1.8.0
 */
public class TestSAML2Credentials extends TestCase {

    public void testClearSAML2Credentials() {
        SAML2Credentials credentials = new SAML2Credentials (
                new NameIDBuilder().buildObject(),
                new ArrayList<Attribute>(),
                new ConditionsBuilder().buildObject(),
                "testClient"
        );
        credentials.clear();
        assertNull(credentials.getClientName());
        assertNull(credentials.getAttributes());
        assertNull(credentials.getConditions());
        assertNull(credentials.getNameId());
    }
}
