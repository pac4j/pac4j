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
package org.pac4j.http.authorization;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.context.WebContext;

import static org.junit.Assert.*;

/**
 * Tests {@link DefaultCsrfTokenGenerator}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class DefaultCsrfTokenGeneratorTests {

    private final DefaultCsrfTokenGenerator generator = new DefaultCsrfTokenGenerator();

    @Test
    public void test() {
        final WebContext context = MockWebContext.create();
        final String token = generator.get(context);
        assertNotNull(token);
        final String token2 = generator.get(context);
        assertEquals(token, token2);
    }
}
