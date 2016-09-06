package org.pac4j.oauth.profile.converter;

import org.junit.Test;
import org.pac4j.oauth.profile.vk.converter.VkBooleanConverter;

import static org.junit.Assert.*;

/**
 * This class tests the {@link VkBooleanConverter} class.
 * 
 * @author indvdum (gotoindvdum[at]gmail[dot]com)
 * @since 1.5
 * 
 */
public final class VkBooleanConverterTests {

	private final VkBooleanConverter converter = new VkBooleanConverter();

	@Test
	public void testNull() {
		assertFalse(this.converter.convert(null));
	}

	@Test
	public void testBooleanFalse() {
		assertEquals(Boolean.FALSE, this.converter.convert(Boolean.FALSE));
	}

	@Test
	public void testBooleanTrue() {
		assertEquals(Boolean.TRUE, this.converter.convert(Boolean.TRUE));
	}

	@Test
	public void testIntegerFalse() {
		assertEquals(Boolean.FALSE, this.converter.convert(0));
	}

	@Test
	public void testIntegerTrue() {
		assertEquals(Boolean.TRUE, this.converter.convert(1));
	}

	@Test
	public void testFalse() {
		assertEquals(Boolean.FALSE, this.converter.convert("not a 1"));
	}

	@Test
	public void testTrue() {
		assertEquals(Boolean.TRUE, this.converter.convert("1"));
	}
}
