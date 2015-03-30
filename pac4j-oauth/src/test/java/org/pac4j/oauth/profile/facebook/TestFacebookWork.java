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

import org.pac4j.core.util.TestsConstants;
import org.pac4j.oauth.profile.JsonHelper;

/**
 * This class tests the {@link FacebookWork} class.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class TestFacebookWork extends TestCase implements TestsConstants {
    
    private static final String FACEBOOK_OBJECT = "{\"id\": \"" + STRING_ID + "\", \"name\": \"" + NAME + "\"}";
    
    private static final String DESCRIPTION = "description";
    
    private static final String DATE = "2012-01";
    
    private static final String GOOD_JSON = "{\"employer\": " + FACEBOOK_OBJECT + ", \"location\": " + FACEBOOK_OBJECT
                                            + ", \"position\": " + FACEBOOK_OBJECT + ", \"description\": \""
                                            + DESCRIPTION + "\", \"start_date\" : \"" + DATE + "\", \"end_date\" : \""
                                            + DATE + "\" }";
    
    public void testNull() {
        final FacebookWork facebookWork = new FacebookWork();
        facebookWork.buildFrom(null);
        assertNull(facebookWork.getEmployer());
        assertNull(facebookWork.getLocation());
        assertNull(facebookWork.getPosition());
        assertNull(facebookWork.getDescription());
        assertNull(facebookWork.getStartDate());
        assertNull(facebookWork.getEndDate());
    }
    
    public void testBadJson() {
        final FacebookWork facebookWork = new FacebookWork();
        facebookWork.buildFrom(BAD_JSON);
        assertNull(facebookWork.getEmployer());
        assertNull(facebookWork.getLocation());
        assertNull(facebookWork.getPosition());
        assertNull(facebookWork.getDescription());
        assertNull(facebookWork.getStartDate());
        assertNull(facebookWork.getEndDate());
    }
    
    public void testGoodJson() {
        final FacebookWork facebookWork = new FacebookWork();
        facebookWork.buildFrom(GOOD_JSON);
        final FacebookObject facebookObject = new FacebookObject();
        facebookObject.buildFrom(JsonHelper.getFirstNode(FACEBOOK_OBJECT));
        assertEquals(facebookObject.toString(), facebookWork.getEmployer().toString());
        assertEquals(facebookObject.toString(), facebookWork.getLocation().toString());
        assertEquals(facebookObject.toString(), facebookWork.getPosition().toString());
        assertEquals(DESCRIPTION, facebookWork.getDescription());
        final String d = FacebookConverters.workDateConverter.convert(DATE).toString();
        assertEquals(d, facebookWork.getStartDate().toString());
        assertEquals(d, facebookWork.getEndDate().toString());
    }
}
