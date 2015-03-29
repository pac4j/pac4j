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
package org.pac4j.oauth.profile.google2;

import junit.framework.TestCase;

import org.pac4j.core.util.TestsConstants;

/**
 * This class tests the {@link Google2Email} class.
 *
 * @author Nate Williams
 * @since 1.6.1
 */
public final class TestGoogle2Email extends TestCase implements TestsConstants {

    private static final String EMAIL_ADDRESS = "user@example.com";
    private static final String EMAIL_TYPE = "account";

    private static final String GOOD_JSON = "{ \"value\": \"" + EMAIL_ADDRESS + "\", \"type\": \"" + EMAIL_TYPE + "\" }";

    public void testNull() {
        final Google2Email google2Email = new Google2Email();
        google2Email.buildFrom(null);
        assertNull(google2Email.getEmail());
        assertNull(google2Email.getType());
    }

    public void testBadJson() {
        final Google2Email google2Email = new Google2Email();
        google2Email.buildFrom(BAD_JSON);
        assertNull(google2Email.getEmail());
        assertNull(google2Email.getType());
    }

    public void testGoodJson() {
        final Google2Email google2Email = new Google2Email();
        google2Email.buildFrom(GOOD_JSON);
        assertEquals(EMAIL_ADDRESS, google2Email.getEmail());
        assertEquals(EMAIL_TYPE, google2Email.getType());
    }
}
