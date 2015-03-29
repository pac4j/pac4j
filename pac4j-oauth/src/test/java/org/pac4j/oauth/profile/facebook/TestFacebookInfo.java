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
package org.pac4j.oauth.profile.facebook;

import junit.framework.TestCase;

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.core.util.TestsConstants;

/**
 * This class tests the {@link FacebookInfo} class.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class TestFacebookInfo extends TestCase implements TestsConstants {
    
    private static final String CATEGORY = "cat";
    
    private static final String CREATED_TIME = "2012-02-23T20:52:30+0000";
    
    private static final String GOOD_JSON = "{\"id\": \"" + STRING_ID + "\", \"category\": \"" + CATEGORY + "\", \"name\": \""
                                            + NAME + "\", \"created_time\": \"" + CREATED_TIME + "\" }";
    
    public void testNull() {
        final FacebookInfo facebookInfo = new FacebookInfo();
        facebookInfo.buildFrom(null);
        assertNull(facebookInfo.getId());
        assertNull(facebookInfo.getCategory());
        assertNull(facebookInfo.getName());
        assertNull(facebookInfo.getCreatedTime());
    }
    
    public void testBadJson() {
        final FacebookInfo facebookInfo = new FacebookInfo();
        facebookInfo.buildFrom(BAD_JSON);
        assertNull(facebookInfo.getId());
        assertNull(facebookInfo.getCategory());
        assertNull(facebookInfo.getName());
        assertNull(facebookInfo.getCreatedTime());
    }
    
    public void testGoodJson() {
        final FacebookInfo facebookInfo = new FacebookInfo();
        facebookInfo.buildFrom(GOOD_JSON);
        assertEquals(STRING_ID, facebookInfo.getId());
        assertEquals(CATEGORY, facebookInfo.getCategory());
        assertEquals(NAME, facebookInfo.getName());
        assertEquals(Converters.dateConverter.convert(CREATED_TIME).toString(), facebookInfo.getCreatedTime()
            .toString());
    }
}
