package org.pac4j.oauth.profile.converter;

import org.junit.Test;
import org.pac4j.core.profile.converter.AttributeConverter;
import org.pac4j.oauth.profile.facebook.FacebookRelationshipStatus;
import org.pac4j.oauth.profile.facebook.converter.FacebookRelationshipStatusConverter;

import static org.junit.Assert.*;

/**
 * This class test the {@link FacebookRelationshipStatusConverter} class.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class FacebookRelationshipStatusConverterTests {

    private final AttributeConverter converter = new FacebookRelationshipStatusConverter();

    @Test
    public void testNull() {
        assertNull(this.converter.convert(null));
    }

    @Test
    public void testNotAString() {
        assertNull(this.converter.convert(Boolean.TRUE));
    }

    @Test
    public void testSingle() {
        assertEquals(FacebookRelationshipStatus.SINGLE, this.converter.convert("Single"));
    }

    @Test
    public void testInARelationship() {
        assertEquals(FacebookRelationshipStatus.IN_A_RELATIONSHIP, this.converter.convert("In a relationship"));
    }

    @Test
    public void testEngaged() {
        assertEquals(FacebookRelationshipStatus.ENGAGED, this.converter.convert("Engaged"));
    }

    @Test
    public void testMarried() {
        assertEquals(FacebookRelationshipStatus.MARRIED, this.converter.convert("Married"));
    }

    @Test
    public void testItsComplicated() {
        assertEquals(FacebookRelationshipStatus.ITS_COMPLICATED, this.converter.convert("It's complicated"));
    }

    @Test
    public void testInAnOpenRelationship() {
        assertEquals(FacebookRelationshipStatus.IN_AN_OPEN_RELATIONSHIP,
                     this.converter.convert("In an open relationship"));
    }

    @Test
    public void testWidowed() {
        assertEquals(FacebookRelationshipStatus.WIDOWED, this.converter.convert("Widowed"));
    }

    @Test
    public void testSeparated() {
        assertEquals(FacebookRelationshipStatus.SEPARATED, this.converter.convert("Separated"));
    }

    @Test
    public void testDivorced() {
        assertEquals(FacebookRelationshipStatus.DIVORCED, this.converter.convert("Divorced"));
    }

    @Test
    public void testInACivilUnion() {
        assertEquals(FacebookRelationshipStatus.IN_A_CIVIL_UNION, this.converter.convert("In a civil union"));
    }

    @Test
    public void testInADomesticPartnership() {
        assertEquals(FacebookRelationshipStatus.IN_A_DOMESTIC_PARTNERSHIP,
                     this.converter.convert("In a domestic partnership"));
    }

    @Test
    public void testSingleEnum() {
        assertEquals(FacebookRelationshipStatus.SINGLE,
                     this.converter.convert(FacebookRelationshipStatus.SINGLE.toString()));
    }

    @Test
    public void testInARelationshipEnum() {
        assertEquals(FacebookRelationshipStatus.IN_A_RELATIONSHIP,
                     this.converter.convert(FacebookRelationshipStatus.IN_A_RELATIONSHIP.toString()));
    }

    @Test
    public void testEngagedEnum() {
        assertEquals(FacebookRelationshipStatus.ENGAGED,
                     this.converter.convert(FacebookRelationshipStatus.ENGAGED.toString()));
    }

    @Test
    public void testMarriedEnum() {
        assertEquals(FacebookRelationshipStatus.MARRIED,
                     this.converter.convert(FacebookRelationshipStatus.MARRIED.toString()));
    }

    @Test
    public void testItsComplicatedEnum() {
        assertEquals(FacebookRelationshipStatus.ITS_COMPLICATED,
                     this.converter.convert(FacebookRelationshipStatus.ITS_COMPLICATED.toString()));
    }

    @Test
    public void testInAnOpenRelationshipEnum() {
        assertEquals(FacebookRelationshipStatus.IN_AN_OPEN_RELATIONSHIP,
                     this.converter.convert(FacebookRelationshipStatus.IN_AN_OPEN_RELATIONSHIP.toString()));
    }

    @Test
    public void testWidowedEnum() {
        assertEquals(FacebookRelationshipStatus.WIDOWED,
                     this.converter.convert(FacebookRelationshipStatus.WIDOWED.toString()));
    }

    @Test
    public void testSeparatedEnum() {
        assertEquals(FacebookRelationshipStatus.SEPARATED,
                     this.converter.convert(FacebookRelationshipStatus.SEPARATED.toString()));
    }

    @Test
    public void testDivorcedEnum() {
        assertEquals(FacebookRelationshipStatus.DIVORCED,
                     this.converter.convert(FacebookRelationshipStatus.DIVORCED.toString()));
    }

    @Test
    public void testInACivilUnionEnum() {
        assertEquals(FacebookRelationshipStatus.IN_A_CIVIL_UNION,
                     this.converter.convert(FacebookRelationshipStatus.IN_A_CIVIL_UNION.toString()));
    }

    @Test
    public void testInADomesticPartnershipEnum() {
        assertEquals(FacebookRelationshipStatus.IN_A_DOMESTIC_PARTNERSHIP,
                     this.converter.convert(FacebookRelationshipStatus.IN_A_DOMESTIC_PARTNERSHIP.toString()));
    }
}
