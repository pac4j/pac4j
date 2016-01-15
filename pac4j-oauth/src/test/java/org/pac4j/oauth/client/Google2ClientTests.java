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
package org.pac4j.oauth.client;

import org.junit.Test;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.RequiresHttpAction;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.core.util.TestsHelper;

/**
 * This class tests the {@link Google2Client}.
 *
 * @author Jerome Leleu
 * @since 1.2.0
 */
public class Google2ClientTests implements TestsConstants {

    @Test
    public void testMissingScope() {
        final Google2Client client = new Google2Client(KEY, SECRET);
        client.setCallbackUrl(CALLBACK_URL);
        client.setScope(null);
        TestsHelper.initShouldFail(client, "scope cannot be null");
    }

    @Test
    public void testDefaultScope() throws RequiresHttpAction {
        final Google2Client google2Client = new Google2Client();
        google2Client.setKey(KEY);
        google2Client.setSecret(SECRET);
        google2Client.setCallbackUrl(CALLBACK_URL);
        google2Client.redirect(MockWebContext.create());
    }
}
