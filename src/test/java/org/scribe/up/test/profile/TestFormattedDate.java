/*
  Copyright 2012 Jerome Leleu

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
package org.scribe.up.test.profile;

import java.util.Date;
import java.util.Locale;

import junit.framework.TestCase;

import org.scribe.up.profile.FormattedDate;

/**
 * This class tests the {@link org.scribe.up.profile.FormattedDate} class.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class TestFormattedDate extends TestCase {
    
    public void test() {
        FormattedDate d = new FormattedDate(new Date(0), "EEE MMM dd HH:mm:ss Z yyyy", Locale.FRANCE);
        assertEquals("jeu. janv. 01 01:00:00 +0100 1970", d.toString());
    }
}
