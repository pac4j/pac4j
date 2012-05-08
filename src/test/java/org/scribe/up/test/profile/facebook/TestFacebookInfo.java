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
package org.scribe.up.test.profile.facebook;

import junit.framework.TestCase;

import org.scribe.up.profile.converter.Converters;
import org.scribe.up.profile.facebook.FacebookInfo;

/**
 * This class tests the {@link org.scribe.up.profile.facebook.FacebookInfo} class.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class TestFacebookInfo extends TestCase {
    
    private static final String ID = "12345";
    
    private static final String CATEGORY = "cat";
    
    private static final String NAME = "name";
    
    private static final String CREATED_TIME = "2012-02-23T20:52:30+0000";
    
    private static final String GOOD_JSON = "{\"id\": \"" + ID + "\", \"category\": \"" + CATEGORY + "\", \"name\": \""
                                            + NAME + "\", \"created_time\": \"" + CREATED_TIME + "\" }";
    
    private static final String BAD_JSON = "{ }";
    
    public void testNull() {
        FacebookInfo facebookInfo = new FacebookInfo(null);
        assertNull(facebookInfo.getId());
        assertNull(facebookInfo.getCategory());
        assertNull(facebookInfo.getName());
        assertNull(facebookInfo.getCreatedTime());
    }
    
    public void testBadJson() {
        FacebookInfo facebookInfo = new FacebookInfo(BAD_JSON);
        assertNull(facebookInfo.getId());
        assertNull(facebookInfo.getCategory());
        assertNull(facebookInfo.getName());
        assertNull(facebookInfo.getCreatedTime());
    }
    
    public void testGoodJson() {
        FacebookInfo facebookInfo = new FacebookInfo(GOOD_JSON);
        assertEquals(ID, facebookInfo.getId());
        assertEquals(CATEGORY, facebookInfo.getCategory());
        assertEquals(NAME, facebookInfo.getName());
        assertEquals(Converters.dateConverter.convert(CREATED_TIME).toString(), facebookInfo.getCreatedTime()
            .toString());
    }
}
