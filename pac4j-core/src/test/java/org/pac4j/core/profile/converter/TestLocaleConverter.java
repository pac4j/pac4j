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
package org.pac4j.core.profile.converter;

import java.util.Locale;

import junit.framework.TestCase;

import org.pac4j.core.profile.converter.LocaleConverter;

/**
 * This class tests the {@link org.pac4j.core.profile.converter.LocaleConverter} class.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class TestLocaleConverter extends TestCase {
    
    private final LocaleConverter converter = new LocaleConverter();
    
    public void testNull() {
        assertNull(this.converter.convert(null));
    }
    
    public void testNotAString() {
        assertNull(this.converter.convert(Boolean.TRUE));
    }
    
    public void testLanguage() {
        final Locale locale = this.converter.convert("fr");
        assertEquals("fr", locale.getLanguage());
    }
    
    public void testLanguageCountry() {
        final Locale locale = this.converter.convert(Locale.FRANCE.toString());
        assertEquals(Locale.FRANCE.getLanguage(), locale.getLanguage());
        assertEquals(Locale.FRANCE.getCountry(), locale.getCountry());
    }
    
    public void testBadLocale() {
        assertNull(this.converter.convert("1_2_3"));
    }
}
