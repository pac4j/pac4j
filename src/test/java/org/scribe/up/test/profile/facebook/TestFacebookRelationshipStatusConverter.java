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
    
    public void testNotAString() {
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
    
    public void testItsComplicated() {
        assertEquals(FacebookRelationshipStatus.ITS_COMPLICATED, converter.convert("It's complicated"));
    }
    
    public void testInAnOpenRelationship() {
        assertEquals(FacebookRelationshipStatus.IN_AN_OPEN_RELATIONSHIP, converter.convert("In an open relationship"));
    }
    
    public void testWidowed() {
        assertEquals(FacebookRelationshipStatus.WIDOWED, converter.convert("Widowed"));
    }
    
    public void testSeparated() {
        assertEquals(FacebookRelationshipStatus.SEPARATED, converter.convert("Separated"));
    }
    
    public void testDivorced() {
        assertEquals(FacebookRelationshipStatus.DIVORCED, converter.convert("Divorced"));
    }
    
    public void testInACivilUnion() {
        assertEquals(FacebookRelationshipStatus.IN_A_CIVIL_UNION, converter.convert("In a civil union"));
    }
    
    public void testInADomesticPartnership() {
        assertEquals(FacebookRelationshipStatus.IN_A_DOMESTIC_PARTNERSHIP,
                     converter.convert("In a domestic partnership"));
    }
    
    public void testSingleEnum() {
        assertEquals(FacebookRelationshipStatus.SINGLE, converter.convert(FacebookRelationshipStatus.SINGLE.toString()));
    }
    
    public void testInARelationshipEnum() {
        assertEquals(FacebookRelationshipStatus.IN_A_RELATIONSHIP,
                     converter.convert(FacebookRelationshipStatus.IN_A_RELATIONSHIP.toString()));
    }
    
    public void testEngagedEnum() {
        assertEquals(FacebookRelationshipStatus.ENGAGED,
                     converter.convert(FacebookRelationshipStatus.ENGAGED.toString()));
    }
    
    public void testMarriedEnum() {
        assertEquals(FacebookRelationshipStatus.MARRIED,
                     converter.convert(FacebookRelationshipStatus.MARRIED.toString()));
    }
    
    public void testItsComplicatedEnum() {
        assertEquals(FacebookRelationshipStatus.ITS_COMPLICATED,
                     converter.convert(FacebookRelationshipStatus.ITS_COMPLICATED.toString()));
    }
    
    public void testInAnOpenRelationshipEnum() {
        assertEquals(FacebookRelationshipStatus.IN_AN_OPEN_RELATIONSHIP,
                     converter.convert(FacebookRelationshipStatus.IN_AN_OPEN_RELATIONSHIP.toString()));
    }
    
    public void testWidowedEnum() {
        assertEquals(FacebookRelationshipStatus.WIDOWED,
                     converter.convert(FacebookRelationshipStatus.WIDOWED.toString()));
    }
    
    public void testSeparatedEnum() {
        assertEquals(FacebookRelationshipStatus.SEPARATED,
                     converter.convert(FacebookRelationshipStatus.SEPARATED.toString()));
    }
    
    public void testDivorcedEnum() {
        assertEquals(FacebookRelationshipStatus.DIVORCED,
                     converter.convert(FacebookRelationshipStatus.DIVORCED.toString()));
    }
    
    public void testInACivilUnionEnum() {
        assertEquals(FacebookRelationshipStatus.IN_A_CIVIL_UNION,
                     converter.convert(FacebookRelationshipStatus.IN_A_CIVIL_UNION.toString()));
    }
    
    public void testInADomesticPartnershipEnum() {
        assertEquals(FacebookRelationshipStatus.IN_A_DOMESTIC_PARTNERSHIP,
                     converter.convert(FacebookRelationshipStatus.IN_A_DOMESTIC_PARTNERSHIP.toString()));
    }
}
