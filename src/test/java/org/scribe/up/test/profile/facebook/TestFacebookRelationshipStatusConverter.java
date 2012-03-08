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

import org.scribe.up.profile.facebook.FacebookRelationshipStatus;
import org.scribe.up.profile.facebook.FacebookRelationshipStatusConverter;

/**
 * This class test the {@link org.scribe.up.profile.facebook.FacebookRelationshipStatus} class.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class TestFacebookRelationshipStatusConverter extends TestCase {
    
    private FacebookRelationshipStatusConverter converter = new FacebookRelationshipStatusConverter();
    
    public void testNull() {
        assertNull(converter.convert(null));
    }
    
    public void testBadInput() {
        assertNull(converter.convert(Boolean.TRUE));
    }
    
    public void testSingle() {
        assertEquals(FacebookRelationshipStatus.SINGLE, converter.convert("Single"));
    }
    
    public void testInARelationship() {
        assertEquals(FacebookRelationshipStatus.IN_A_RELATIONSHIP, converter.convert("In a relationship"));
    }
    
    public void testEngaged() {
        assertEquals(FacebookRelationshipStatus.ENGAGED, converter.convert("Engaged"));
    }
    
    public void testMarried() {
        assertEquals(FacebookRelationshipStatus.MARRIED, converter.convert("Married"));
    }
}
