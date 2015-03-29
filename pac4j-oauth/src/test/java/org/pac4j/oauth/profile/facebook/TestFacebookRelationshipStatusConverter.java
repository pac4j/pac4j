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

import org.pac4j.oauth.profile.facebook.FacebookRelationshipStatus;
import org.pac4j.oauth.profile.facebook.converter.FacebookRelationshipStatusConverter;

/**
 * This class test the {@link org.pac4j.oauth.profile.facebook.converter.FacebookRelationshipStatusConverter} class.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class TestFacebookRelationshipStatusConverter extends TestCase {
    
    private final FacebookRelationshipStatusConverter converter = new FacebookRelationshipStatusConverter();
    
    public void testNull() {
        assertNull(this.converter.convert(null));
    }
    
    public void testNotAString() {
        assertNull(this.converter.convert(Boolean.TRUE));
    }
    
    public void testSingle() {
        assertEquals(FacebookRelationshipStatus.SINGLE, this.converter.convert("Single"));
    }
    
    public void testInARelationship() {
        assertEquals(FacebookRelationshipStatus.IN_A_RELATIONSHIP, this.converter.convert("In a relationship"));
    }
    
    public void testEngaged() {
        assertEquals(FacebookRelationshipStatus.ENGAGED, this.converter.convert("Engaged"));
    }
    
    public void testMarried() {
        assertEquals(FacebookRelationshipStatus.MARRIED, this.converter.convert("Married"));
    }
    
    public void testItsComplicated() {
        assertEquals(FacebookRelationshipStatus.ITS_COMPLICATED, this.converter.convert("It's complicated"));
    }
    
    public void testInAnOpenRelationship() {
        assertEquals(FacebookRelationshipStatus.IN_AN_OPEN_RELATIONSHIP,
                     this.converter.convert("In an open relationship"));
    }
    
    public void testWidowed() {
        assertEquals(FacebookRelationshipStatus.WIDOWED, this.converter.convert("Widowed"));
    }
    
    public void testSeparated() {
        assertEquals(FacebookRelationshipStatus.SEPARATED, this.converter.convert("Separated"));
    }
    
    public void testDivorced() {
        assertEquals(FacebookRelationshipStatus.DIVORCED, this.converter.convert("Divorced"));
    }
    
    public void testInACivilUnion() {
        assertEquals(FacebookRelationshipStatus.IN_A_CIVIL_UNION, this.converter.convert("In a civil union"));
    }
    
    public void testInADomesticPartnership() {
        assertEquals(FacebookRelationshipStatus.IN_A_DOMESTIC_PARTNERSHIP,
                     this.converter.convert("In a domestic partnership"));
    }
    
    public void testSingleEnum() {
        assertEquals(FacebookRelationshipStatus.SINGLE,
                     this.converter.convert(FacebookRelationshipStatus.SINGLE.toString()));
    }
    
    public void testInARelationshipEnum() {
        assertEquals(FacebookRelationshipStatus.IN_A_RELATIONSHIP,
                     this.converter.convert(FacebookRelationshipStatus.IN_A_RELATIONSHIP.toString()));
    }
    
    public void testEngagedEnum() {
        assertEquals(FacebookRelationshipStatus.ENGAGED,
                     this.converter.convert(FacebookRelationshipStatus.ENGAGED.toString()));
    }
    
    public void testMarriedEnum() {
        assertEquals(FacebookRelationshipStatus.MARRIED,
                     this.converter.convert(FacebookRelationshipStatus.MARRIED.toString()));
    }
    
    public void testItsComplicatedEnum() {
        assertEquals(FacebookRelationshipStatus.ITS_COMPLICATED,
                     this.converter.convert(FacebookRelationshipStatus.ITS_COMPLICATED.toString()));
    }
    
    public void testInAnOpenRelationshipEnum() {
        assertEquals(FacebookRelationshipStatus.IN_AN_OPEN_RELATIONSHIP,
                     this.converter.convert(FacebookRelationshipStatus.IN_AN_OPEN_RELATIONSHIP.toString()));
    }
    
    public void testWidowedEnum() {
        assertEquals(FacebookRelationshipStatus.WIDOWED,
                     this.converter.convert(FacebookRelationshipStatus.WIDOWED.toString()));
    }
    
    public void testSeparatedEnum() {
        assertEquals(FacebookRelationshipStatus.SEPARATED,
                     this.converter.convert(FacebookRelationshipStatus.SEPARATED.toString()));
    }
    
    public void testDivorcedEnum() {
        assertEquals(FacebookRelationshipStatus.DIVORCED,
                     this.converter.convert(FacebookRelationshipStatus.DIVORCED.toString()));
    }
    
    public void testInACivilUnionEnum() {
        assertEquals(FacebookRelationshipStatus.IN_A_CIVIL_UNION,
                     this.converter.convert(FacebookRelationshipStatus.IN_A_CIVIL_UNION.toString()));
    }
    
    public void testInADomesticPartnershipEnum() {
        assertEquals(FacebookRelationshipStatus.IN_A_DOMESTIC_PARTNERSHIP,
                     this.converter.convert(FacebookRelationshipStatus.IN_A_DOMESTIC_PARTNERSHIP.toString()));
    }
}
