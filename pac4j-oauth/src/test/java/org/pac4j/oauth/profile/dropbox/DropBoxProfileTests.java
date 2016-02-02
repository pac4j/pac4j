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
package org.pac4j.oauth.profile.dropbox;

import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.util.TestsConstants;

/**
 * General test cases for the {@link DropBoxProfile}.
 *
 * @author Jerome Leleu
 * @since  1.9.0
 */
public class DropBoxProfileTests implements TestsConstants {

    @Test
    public void testClear() {
        DropBoxProfile profile = new DropBoxProfile();
        profile.setAccessToken(VALUE);
        profile.setAccessSecret(VALUE);
        profile.clearSensitiveData();
        Assert.assertNull(profile.getAccessToken());
        Assert.assertNull(profile.getAccessSecret());
    }
}
