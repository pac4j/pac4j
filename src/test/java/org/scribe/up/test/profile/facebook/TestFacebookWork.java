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

import org.scribe.up.profile.DateConverter;
import org.scribe.up.profile.JsonHelper;
import org.scribe.up.profile.facebook.FacebookObject;
import org.scribe.up.profile.facebook.FacebookWork;

/**
 * This class tests the {@link org.scribe.up.profile.facebook.FacebookWork} class.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class TestFacebookWork extends TestCase {
    
    private static DateConverter dateConverter = new DateConverter("yyyy-MM");
    
    private static final String ID = "12345";
    
    private static final String NAME = "name";
    
    private static final String FACEBOOK_OBJECT = "{\"id\": \"" + ID + "\", \"name\": \"" + NAME + "\"}";
    
    private static final String DESCRIPTION = "description";
    
    private static final String DATE = "2012-01";
    
    private static final String GOOD_JSON = "{\"employer\": " + FACEBOOK_OBJECT + ", \"location\": " + FACEBOOK_OBJECT
                                            + ", \"position\": " + FACEBOOK_OBJECT + ", \"description\": \""
                                            + DESCRIPTION + "\", \"start_date\" : \"" + DATE + "\", \"end_date\" : \""
                                            + DATE + "\" }";
    
    private static final String BAD_JSON = "{ }";
    
    public void testNull() {
        FacebookWork facebookWork = new FacebookWork(null);
        assertNull(facebookWork.getEmployer());
        assertNull(facebookWork.getLocation());
        assertNull(facebookWork.getPosition());
        assertNull(facebookWork.getDescription());
        assertNull(facebookWork.getStartDate());
        assertNull(facebookWork.getEndDate());
    }
    
    public void testBadJson() {
        FacebookWork facebookWork = new FacebookWork(JsonHelper.getFirstNode(BAD_JSON));
        FacebookObject facebookObject = facebookWork.getEmployer();
        assertNull(facebookObject.getId());
        assertNull(facebookObject.getName());
        facebookObject = facebookWork.getLocation();
        assertNull(facebookObject.getId());
        assertNull(facebookObject.getName());
        facebookObject = facebookWork.getPosition();
        assertNull(facebookObject.getId());
        assertNull(facebookObject.getName());
        assertNull(facebookWork.getDescription());
        assertNull(facebookWork.getStartDate());
        assertNull(facebookWork.getEndDate());
    }
    
    public void testGoodJson() {
        FacebookWork facebookWork = new FacebookWork(JsonHelper.getFirstNode(GOOD_JSON));
        FacebookObject facebookObject = new FacebookObject(JsonHelper.getFirstNode(FACEBOOK_OBJECT));
        assertEquals(facebookObject.toString(), facebookWork.getEmployer().toString());
        assertEquals(facebookObject.toString(), facebookWork.getLocation().toString());
        assertEquals(facebookObject.toString(), facebookWork.getPosition().toString());
        assertEquals(DESCRIPTION, facebookWork.getDescription());
        String d = dateConverter.convert(DATE).toString();
        assertEquals(d, facebookWork.getStartDate().toString());
        assertEquals(d, facebookWork.getEndDate().toString());
    }
}
