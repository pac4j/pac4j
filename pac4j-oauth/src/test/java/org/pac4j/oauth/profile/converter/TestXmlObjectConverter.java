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
package org.pac4j.oauth.profile.converter;

import junit.framework.TestCase;

import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.util.TestsConstants;

/**
 * This class tests the {@link XmlObjectConverter} class.
 * 
 * @author Jerome Leleu
 * @since 1.4.1
 */
public final class TestXmlObjectConverter extends TestCase implements TestsConstants {
    
    private final XmlObjectConverter converter = new XmlObjectConverter(MockXmlObject.class);
    
    private final static String SIMPLE_XML = ELEMENT;
    
    public void testNull() {
        assertNull(this.converter.convert(null));
    }
    
    public void testNotAString() {
        assertNull(this.converter.convert(1));
    }
    
    public void testString() {
        final Object object = this.converter.convert(SIMPLE_XML);
        assertEquals(MockXmlObject.class, object.getClass());
        final MockXmlObject mock = (MockXmlObject) object;
        assertEquals(ELEMENT, mock.getValue());
        assertEquals("", mock.toString());
    }
    
    public void testStringKeepRawData() {
        ProfileHelper.setKeepRawData(true);
        final Object object = this.converter.convert(SIMPLE_XML);
        assertEquals(MockXmlObject.class, object.getClass());
        final MockXmlObject mock = (MockXmlObject) object;
        assertEquals(ELEMENT, mock.getValue());
        assertEquals(SIMPLE_XML, mock.toString());
        ProfileHelper.setKeepRawData(false);
    }
}
