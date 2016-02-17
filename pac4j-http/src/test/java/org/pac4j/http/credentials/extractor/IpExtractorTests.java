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
package org.pac4j.http.credentials.extractor;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.credentials.TokenCredentials;

import static org.junit.Assert.*;

/**
 * This class tests the {@link IpExtractor}.
 * 
 * @author Jerome Leleu
 * @since 1.8.0
 */
public final class IpExtractorTests implements TestsConstants {

    private final static String GOOD_IP = "1.2.3.4";

    private final static IpExtractor extractor = new IpExtractor(CLIENT_NAME);

    @Test
    public void testRetrieveIpOk() {
        final MockWebContext context = MockWebContext.create().setRemoteAddress(GOOD_IP);
        final TokenCredentials credentials = extractor.extract(context);
        assertEquals(GOOD_IP, credentials.getToken());
    }

    @Test
    public void testNoIp() {
        final MockWebContext context = MockWebContext.create();
        final TokenCredentials credentials = extractor.extract(context);
        assertNull(credentials);
    }
}
