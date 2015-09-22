/*
  Copyright 2012 - 2015 pac4j organization

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
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
