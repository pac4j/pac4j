package org.pac4j.core.profile.converter;

import junit.framework.TestCase;

import org.pac4j.core.profile.Gender;

/**
 * This class tests the {@link GenderIntegerConverter} class.
 * 
 * @author indvdum (gotoindvdum[at]gmail[dot]com)
 * @since 1.5
 * 
 */
public final class TestGenderIntegerConverter extends TestCase {

	private static final Integer MALE = 2;

	private static final Integer FEMALE = 1;

	private static final Integer UNSPECIFIED = 0;

	private final GenderIntegerConverter converter = new GenderIntegerConverter(MALE, FEMALE);

	public void testNull() {
		assertNull(this.converter.convert(null));
	}

	public void testNotAString() {
		assertNull(this.converter.convert(Boolean.TRUE));
	}

	public void testMale() {
		assertEquals(Gender.MALE, this.converter.convert(MALE));
	}

	public void testFemale() {
		assertEquals(Gender.FEMALE, this.converter.convert(FEMALE));
	}

	public void testUnspecified() {
		assertEquals(Gender.UNSPECIFIED, this.converter.convert(UNSPECIFIED));
	}

}
