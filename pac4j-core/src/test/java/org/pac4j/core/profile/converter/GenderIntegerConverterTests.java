package org.pac4j.core.profile.converter;

import org.junit.Test;
import org.pac4j.core.profile.Gender;

import static org.junit.Assert.*;

/**
 * This class tests the {@link GenderIntegerConverter} class.
 * 
 * @author indvdum (gotoindvdum[at]gmail[dot]com)
 * @since 1.5
 * 
 */
public final class GenderIntegerConverterTests {

	private static final Integer MALE = 2;

	private static final Integer FEMALE = 1;

	private static final Integer UNSPECIFIED = 0;

	private final GenderIntegerConverter converter = new GenderIntegerConverter(MALE, FEMALE);

	@Test
	public void testNull() {
		assertNull(this.converter.convert(null));
	}

	@Test
	public void testNotAString() {
		assertNull(this.converter.convert(Boolean.TRUE));
	}

	@Test
	public void testMale() {
		assertEquals(Gender.MALE, this.converter.convert(MALE));
	}

	@Test
	public void testFemale() {
		assertEquals(Gender.FEMALE, this.converter.convert(FEMALE));
	}

	@Test
	public void testUnspecified() {
		assertEquals(Gender.UNSPECIFIED, this.converter.convert(UNSPECIFIED));
	}
}
