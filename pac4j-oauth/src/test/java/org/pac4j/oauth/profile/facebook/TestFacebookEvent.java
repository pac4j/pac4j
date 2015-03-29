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

/**
 * This class tests the {@link FacebookEvent} class.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class TestFacebookEvent extends TestCase implements TestsConstants {
    
    private static final String LOCATION = "Paris, France";
    
    private static final String START_TIME = "2012-05-07T02:00:00";
    
    private static final String END_TIME = "2012-05-08T02:00:00";
    
    private static final String RSVP_STATUS = "attending";
    
    private static final String GOOD_JSON = "{\"id\": \"" + STRING_ID + "\", \"name\": \"" + NAME + "\", \"location\" : \""
                                            + LOCATION + "\", \"rsvp_status\": \"" + RSVP_STATUS
                                            + "\", \"start_time\": \"" + START_TIME + "\", \"end_time\": \"" + END_TIME
                                            + "\" }";
    
    public void testNull() {
        final FacebookEvent facebookEvent = new FacebookEvent();
        facebookEvent.buildFrom(null);
        assertNull(facebookEvent.getId());
        assertNull(facebookEvent.getName());
        assertNull(facebookEvent.getLocation());
        assertNull(facebookEvent.getRsvpStatus());
        assertNull(facebookEvent.getStartTime());
        assertNull(facebookEvent.getEndTime());
    }
    
    public void testBadJson() {
        final FacebookEvent facebookEvent = new FacebookEvent();
        facebookEvent.buildFrom(BAD_JSON);
        assertNull(facebookEvent.getId());
        assertNull(facebookEvent.getName());
        assertNull(facebookEvent.getLocation());
        assertNull(facebookEvent.getRsvpStatus());
        assertNull(facebookEvent.getStartTime());
        assertNull(facebookEvent.getEndTime());
    }
    
    public void testGoodJson() {
        final FacebookEvent facebookEvent = new FacebookEvent();
        facebookEvent.buildFrom(GOOD_JSON);
        assertEquals(STRING_ID, facebookEvent.getId());
        assertEquals(NAME, facebookEvent.getName());
        assertEquals(LOCATION, facebookEvent.getLocation());
        assertEquals(RSVP_STATUS, facebookEvent.getRsvpStatus());
        assertEquals(FacebookConverters.eventDateConverter.convert(START_TIME).toString(), facebookEvent.getStartTime()
            .toString());
        assertEquals(FacebookConverters.eventDateConverter.convert(END_TIME).toString(), facebookEvent.getEndTime()
            .toString());
    }
}
