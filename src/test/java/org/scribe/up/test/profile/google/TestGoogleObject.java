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
package org.scribe.up.test.profile.google;

import junit.framework.TestCase;

import org.scribe.up.profile.JsonHelper;
import org.scribe.up.profile.google.GoogleObject;

/**
 * This class tests the {@link org.scribe.up.profile.google.GoogleObject} class.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class TestGoogleObject extends TestCase {
    
    private static final String VALUE = "value";
    
    private static final String TYPE = "type";
    
    private static final String GOOD_JSON = "{\"value\" : \"" + VALUE + "\", \"type\" : \"" + TYPE + "\"}";
    
    private static final String BAD_JSON = "{ }";
    
    public void testNull() {
        final GoogleObject googleObject = new GoogleObject();
        googleObject.buildFrom(null);
        assertNull(googleObject.getValue());
        assertNull(googleObject.getType());
    }
    
    public void testBadJson() {
        final GoogleObject googleObject = new GoogleObject();
        googleObject.buildFrom(JsonHelper.getFirstNode(BAD_JSON));
        assertNull(googleObject.getValue());
        assertNull(googleObject.getType());
    }
    
    public void testGoodJson() {
        final GoogleObject googleObject = new GoogleObject();
        googleObject.buildFrom(JsonHelper.getFirstNode(GOOD_JSON));
        assertEquals(VALUE, googleObject.getValue());
        assertEquals(TYPE, googleObject.getType());
    }
}
