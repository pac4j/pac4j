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
 * This class tests the {@link FacebookEducation} class.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class TestFacebookEducation extends TestCase implements TestsConstants {
    
    private static final String FACEBOOK_OBJECT = "{\"id\": \"" + STRING_ID + "\", \"name\": \"" + NAME + "\"}";
    
    private static final String GOOD_JSON = "{\"school\": " + FACEBOOK_OBJECT + ", \"degree\": " + FACEBOOK_OBJECT
                                            + ", \"year\": " + FACEBOOK_OBJECT + ", \"concentration\": ["
                                            + FACEBOOK_OBJECT + "],\"type\": \"" + TYPE + "\" }";
    
    public void testNull() {
        final FacebookEducation facebookEducation = new FacebookEducation();
        facebookEducation.buildFrom(null);
        assertNull(facebookEducation.getSchool());
        assertNull(facebookEducation.getDegree());
        assertNull(facebookEducation.getYear());
        assertNull(facebookEducation.getConcentration());
        assertNull(facebookEducation.getType());
    }
    
    public void testBadJson() {
        final FacebookEducation facebookEducation = new FacebookEducation();
        facebookEducation.buildFrom(BAD_JSON);
        assertNull(facebookEducation.getSchool());
        assertNull(facebookEducation.getDegree());
        assertNull(facebookEducation.getYear());
        assertNull(facebookEducation.getConcentration());
        assertNull(facebookEducation.getType());
    }
    
    public void testGoodJson() {
        final FacebookEducation facebookEducation = new FacebookEducation();
        facebookEducation.buildFrom(GOOD_JSON);
        final FacebookObject facebookObject = new FacebookObject();
        facebookObject.buildFrom(JsonHelper.getFirstNode(FACEBOOK_OBJECT));
        assertEquals(facebookObject.toString(), facebookEducation.getSchool().toString());
        assertEquals(facebookObject.toString(), facebookEducation.getDegree().toString());
        assertEquals(facebookObject.toString(), facebookEducation.getConcentration().get(0).toString());
        assertEquals(facebookObject.toString(), facebookEducation.getYear().toString());
        assertEquals(TYPE, facebookEducation.getType());
    }
}
