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

import org.scribe.up.profile.facebook.FacebookConverters;
import org.scribe.up.profile.facebook.FacebookEvent;

/**
 * This class tests the {@link org.scribe.up.profile.facebook.FacebookEvent} class.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class TestFacebookEvent extends TestCase {
    
    private static final String ID = "12345";
    
    private static final String NAME = "name";
    
    private static final String LOCATION = "Paris, France";
    
    private static final String START_TIME = "2012-05-07T02:00:00";
    
    private static final String END_TIME = "2012-05-08T02:00:00";
    
    private static final String RSVP_STATUS = "attending";
    
    private static final String GOOD_JSON = "{\"id\": \"" + ID + "\", \"name\": \"" + NAME + "\", \"location\" : \""
                                            + LOCATION + "\", \"rsvp_status\": \"" + RSVP_STATUS
                                            + "\", \"start_time\": \"" + START_TIME + "\", \"end_time\": \"" + END_TIME
                                            + "\" }";
    
    private static final String BAD_JSON = "{ }";
    
    public void testNull() {
        FacebookEvent facebookEvent = new FacebookEvent(null);
        assertNull(facebookEvent.getId());
        assertNull(facebookEvent.getName());
        assertNull(facebookEvent.getLocation());
        assertNull(facebookEvent.getRsvpStatus());
        assertNull(facebookEvent.getStartTime());
        assertNull(facebookEvent.getEndTime());
    }
    
    public void testBadJson() {
        FacebookEvent facebookEvent = new FacebookEvent(BAD_JSON);
        assertNull(facebookEvent.getId());
        assertNull(facebookEvent.getName());
        assertNull(facebookEvent.getLocation());
        assertNull(facebookEvent.getRsvpStatus());
        assertNull(facebookEvent.getStartTime());
        assertNull(facebookEvent.getEndTime());
    }
    
    public void testGoodJson() {
        FacebookEvent facebookEvent = new FacebookEvent(GOOD_JSON);
        assertEquals(ID, facebookEvent.getId());
        assertEquals(NAME, facebookEvent.getName());
        assertEquals(LOCATION, facebookEvent.getLocation());
        assertEquals(RSVP_STATUS, facebookEvent.getRsvpStatus());
        assertEquals(FacebookConverters.eventDateConverter.convert(START_TIME).toString(), facebookEvent.getStartTime()
            .toString());
        assertEquals(FacebookConverters.eventDateConverter.convert(END_TIME).toString(), facebookEvent.getEndTime()
            .toString());
    }
}
