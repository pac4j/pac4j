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
package org.pac4j.core.profile;

import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.profile.converter.AttributeConverter;
import org.pac4j.core.util.TestsConstants;

import static org.junit.Assert.*;

/**
 * Tests the {@link AttributesDefinition}.
 *
 * @author Jerome Leleu
 * @since 1.8.0
 */
public class AttributesDefinitionTests implements TestsConstants {

    private AttributesDefinition definition;

    private class FakeConverter implements AttributeConverter<String> {

        public String convert(final Object value) {
            return FAKE_VALUE;
        }
    }

    @Before
    public void setUp() {
        definition = new AttributesDefinition();
    }

    @Test
    public void testNoConverterNoEnforcement() {
        assertEquals(VALUE, definition.convert(NAME, VALUE));
    }

    @Test
    public void testNoConverterEnforcement() {
        ProfileHelper.setEnforceProfileDefinition(true);
        try {
            assertNull(definition.convert(NAME, VALUE));
        } finally {
            ProfileHelper.setEnforceProfileDefinition(false);
        }
    }

    @Test
    public void testConverterNoEnforcement() {
        definition.addAttribute(NAME, new FakeConverter());
        assertEquals(FAKE_VALUE, definition.convert(NAME, VALUE));
    }

    @Test
    public void testConverterEnforcement() {
        ProfileHelper.setEnforceProfileDefinition(true);
        try {
            definition.addAttribute(NAME, new FakeConverter());
            assertEquals(FAKE_VALUE, definition.convert(NAME, VALUE));
        } finally {
            ProfileHelper.setEnforceProfileDefinition(false);
        }
    }
}
