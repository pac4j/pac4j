package org.pac4j.oauth.profile.vk;

import junit.framework.TestCase;

import org.pac4j.oauth.profile.vk.converter.VkBooleanConverter;

/**
 * This class tests the {@link VkBooleanConverter} class.
 * 
 * @author indvdum (gotoindvdum[at]gmail[dot]com)
 * @since 1.5
 * 
 */
public final class TestVkBooleanConverter extends TestCase {

	private final VkBooleanConverter converter = new VkBooleanConverter();

	public void testNull() {
		assertNull(this.converter.convert(null));
	}

	public void testBooleanFalse() {
		assertEquals(Boolean.FALSE, this.converter.convert(Boolean.FALSE));
	}

	public void testBooleanTrue() {
		assertEquals(Boolean.TRUE, this.converter.convert(Boolean.TRUE));
	}

	public void testIntegerFalse() {
		assertEquals(Boolean.FALSE, this.converter.convert(0));
	}

	public void testIntegerTrue() {
		assertEquals(Boolean.TRUE, this.converter.convert(1));
	}

	public void testFalse() {
		assertEquals(Boolean.FALSE, this.converter.convert("not a 1"));
	}

	public void testTrue() {
		assertEquals(Boolean.TRUE, this.converter.convert("1"));
	}
}
