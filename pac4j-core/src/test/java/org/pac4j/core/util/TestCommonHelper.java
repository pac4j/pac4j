/*
  Copyright 2012 - 2014 Jerome Leleu

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
package org.pac4j.core.util;

import junit.framework.TestCase;

import org.pac4j.core.exception.TechnicalException;

/**
 * This class tests the {@link CommonHelper} class.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class TestCommonHelper extends TestCase {
    
    private static final String URL_WITHOUT_PARAMETER = "http://host/app";
    
    private static final String URL_WITH_PARAMETER = "http://host/app?param=value";
    
    private static final String NAME = "name";
    
    private static final String VALUE = "va+l+ue";
    
    private static final String ENCODED_VALUE = "va%2Bl%2Bue";
    
    private static final Class<?> CLAZZ = String.class;
    
    private static final String CLASS_NAME = String.class.getSimpleName();
    
    public void testIsNotBlankNull() {
        assertFalse(CommonHelper.isNotBlank(null));
    }
    
    public void testIsNotBlankEmply() {
        assertFalse(CommonHelper.isNotBlank(""));
    }
    
    public void testIsNotBlankBlank() {
        assertFalse(CommonHelper.isNotBlank("     "));
    }
    
    public void testIsNotBlankNotBlank() {
        assertTrue(CommonHelper.isNotBlank(NAME));
    }
    
    public void testAssertNotBlankBlank() {
        try {
            CommonHelper.assertNotBlank(NAME, "");
            fail("must throw an ClientException");
        } catch (final TechnicalException e) {
            assertEquals(NAME + " cannot be blank", e.getMessage());
        }
    }
    
    public void testAssertNotBlankNotBlank() {
        CommonHelper.assertNotBlank(NAME, VALUE);
    }
    
    public void testAssertNotNullNull() {
        try {
            CommonHelper.assertNotNull(NAME, null);
            fail("must throw an ClientException");
        } catch (final TechnicalException e) {
            assertEquals(NAME + " cannot be null", e.getMessage());
        }
    }
    
    public void testAssertNotNullNotNull() {
        CommonHelper.assertNotNull(NAME, VALUE);
    }
    
    public void testAddParameterNullUrl() {
        assertNull(CommonHelper.addParameter(null, NAME, VALUE));
    }
    
    public void testAddParameterNullName() {
        assertEquals(URL_WITH_PARAMETER, CommonHelper.addParameter(URL_WITH_PARAMETER, null, VALUE));
    }
    
    public void testAddParameterNullValue() {
        assertEquals(URL_WITH_PARAMETER + "&" + NAME + "=", CommonHelper.addParameter(URL_WITH_PARAMETER, NAME, null));
    }
    
    public void testAddParameterWithParameter() {
        assertEquals(URL_WITH_PARAMETER + "&" + NAME + "=" + ENCODED_VALUE,
                     CommonHelper.addParameter(URL_WITH_PARAMETER, NAME, VALUE));
    }
    
    public void testAddParameterWithoutParameter() {
        assertEquals(URL_WITHOUT_PARAMETER + "?" + NAME + "=" + ENCODED_VALUE,
                     CommonHelper.addParameter(URL_WITHOUT_PARAMETER, NAME, VALUE));
    }
    
    public void testToStringNoParameter() {
        assertEquals("<" + CLASS_NAME + "> |", CommonHelper.toString(CLAZZ));
    }
    
    public void testToStringWithParameter() {
        assertEquals("<" + CLASS_NAME + "> | " + NAME + ": " + VALUE + " |", CommonHelper.toString(CLAZZ, NAME, VALUE));
    }
    
    public void testToStringWithParameters() {
        assertEquals("<" + CLASS_NAME + "> | " + NAME + ": " + VALUE + " | " + NAME + ": " + VALUE + " |",
                     CommonHelper.toString(CLAZZ, NAME, VALUE, NAME, VALUE));
    }
    
    public void testAreEqualsBothNull() {
        assertTrue(CommonHelper.areEquals(null, null));
    }
    
    public void testAreEqualsOneIsNull() {
        assertFalse(CommonHelper.areEquals(VALUE, null));
    }
    
    public void testAreEqualsDifferentValue() {
        assertFalse(CommonHelper.areEquals(NAME, VALUE));
    }
    
    public void testAreEqualsSameValue() {
        assertTrue(CommonHelper.areEquals(VALUE, VALUE));
    }
}
