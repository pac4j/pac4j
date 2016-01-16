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
package org.pac4j.oauth.profile.vk;

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
public final class TestVkBooleanConverter {

	private final VkBooleanConverter converter = new VkBooleanConverter();

	@Test
	public void testNull() {
		assertNull(this.converter.convert(null));
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
