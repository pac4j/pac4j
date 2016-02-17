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
package org.pac4j.core.credentials.extractor;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.CredentialsException;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.credentials.TokenCredentials;

import static org.junit.Assert.*;

/**
 * This class tests the {@link HeaderExtractor}.
 * 
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class HeaderExtractorTests implements TestsConstants {

    private final static String GOOD_HEADER = "goodHeader";
    private final static String BAD_HEADER = "badHeader";

    private final static String GOOD_PREFIX = "goodPrefix ";
    private final static String BAD_PREFIX = "badPrefix ";

    private final static HeaderExtractor extractor = new HeaderExtractor(GOOD_HEADER, GOOD_PREFIX, CLIENT_NAME);

    @Test
    public void testRetrieveHeaderOk() {
        final MockWebContext context = MockWebContext.create().addRequestHeader(GOOD_HEADER, GOOD_PREFIX + VALUE);
        final TokenCredentials credentials = extractor.extract(context);
        assertEquals(VALUE, credentials.getToken());
    }

    @Test
    public void testBadHeader() {
        final MockWebContext context = MockWebContext.create().addRequestHeader(BAD_HEADER, GOOD_PREFIX + VALUE);
        final TokenCredentials credentials = extractor.extract(context);
        assertNull(credentials);
    }

    @Test
    public void testBadPrefix() {
        try {
            final MockWebContext context = MockWebContext.create().addRequestHeader(GOOD_HEADER, BAD_PREFIX + VALUE);
            final TokenCredentials credentials = extractor.extract(context);
            fail("Should fail");
        } catch (final CredentialsException e) {
            assertEquals("Wrong prefix for header: " + GOOD_HEADER, e.getMessage());
        }
    }
}
