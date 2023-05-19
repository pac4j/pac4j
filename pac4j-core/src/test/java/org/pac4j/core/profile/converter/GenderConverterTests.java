package org.pac4j.core.profile.converter;

import org.junit.Test;
import org.pac4j.core.profile.Gender;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * This class tests the {@link GenderConverter} class.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class GenderConverterTests {

    private final AttributeConverter converter = new GenderConverter();
    private final AttributeConverter converterNumber = new GenderConverter("2", "1");
    private final AttributeConverter converterChinese = new GenderConverter("男", "女");

    @Test
    public void testNull() {
        assertNull(this.converter.convert(null));
    }

    @Test
    public void testNotAString() {
        assertEquals(Gender.UNSPECIFIED, this.converter.convert(Boolean.TRUE));
    }

    @Test
    public void testMale() {
        assertEquals(Gender.MALE, this.converter.convert("m"));
    }

    @Test
    public void testFemale() {
        assertEquals(Gender.FEMALE, this.converter.convert("f"));
    }

    @Test
    public void testMaleNumber() {
        assertEquals(Gender.MALE, this.converterNumber.convert(2));
    }

    @Test
    public void testFemaleNumber() {
        assertEquals(Gender.FEMALE, this.converterNumber.convert(1));
    }

    @Test
    public void testUnspecified() {
        assertEquals(Gender.UNSPECIFIED, this.converter.convert("unspecified"));
    }

    @Test
    public void testMaleEnum() {
        assertEquals(Gender.MALE, this.converter.convert(Gender.MALE.toString()));
    }

    @Test
    public void testFemaleEnum() {
        assertEquals(Gender.FEMALE, this.converter.convert(Gender.FEMALE.toString()));
    }

    @Test
    public void testUnspecifiedEnum() {
        assertEquals(Gender.UNSPECIFIED, this.converter.convert(Gender.UNSPECIFIED.toString()));
    }

    @Test
    public void testMaleChinese() {
        assertEquals(Gender.MALE, this.converterChinese.convert("男"));
    }

    @Test
    public void testFemaleChinese() {
        assertEquals(Gender.FEMALE, this.converterChinese.convert("女"));
    }

    @Test
    public void testUnspecifiedChinese() {
        assertEquals(Gender.UNSPECIFIED, this.converterChinese.convert("其他"));
    }
}
