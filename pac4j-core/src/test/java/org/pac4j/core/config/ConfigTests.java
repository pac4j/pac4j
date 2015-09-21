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
package org.pac4j.core.config;

import org.junit.Test;
import org.pac4j.core.authorization.Authorizer;
import org.pac4j.core.authorization.RequireAnyRoleAuthorizer;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.TestsConstants;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests the {@link Config}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class ConfigTests implements TestsConstants {

    @Test(expected = TechnicalException.class)
    public void testNullAuthorizersSetter() {
        final Config config = new Config();
        config.setAuthorizers(null);
    }

    @Test(expected = TechnicalException.class)
    public void testNullAuthorizersConstructor() {
        final Config config = new Config((Map<String, Authorizer >) null);
    }

    @Test
    public void testAddAuthorizer() {
        final Config config = new Config();
        final RequireAnyRoleAuthorizer authorizer = new RequireAnyRoleAuthorizer();
        config.addAuthorizer(NAME, authorizer);
        assertEquals(authorizer, config.getAuthorizers().get(NAME));
    }
}
