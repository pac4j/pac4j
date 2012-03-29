/*
  Copyright 2012 Jerome Leleu

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
package org.scribe.up.test.profile.converter;

import junit.framework.TestCase;

import org.scribe.up.profile.JsonHelper;
import org.scribe.up.profile.converter.JsonObjectConverter;

/**
 * This class tests the {@link org.scribe.up.profile.converter.JsonObjectConverter} class.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class TestJsonObjectConverter extends TestCase {
    
    private JsonObjectConverter converter = new JsonObjectConverter(MockJsonObject.class);
    
    private static final String ELEMENT = "element";
    
    private final static String EMPTY_JSON = "\"" + ELEMENT + "\"";
    
    public void testNull() {
        assertNull(converter.convert(null));
    }
    
    public void testNotAStringNotAJsonNode() {
        assertNull(converter.convert(1));
    }
    
    public void testJsonString() {
        Object object = converter.convert(EMPTY_JSON);
        assertEquals(MockJsonObject.class, object.getClass());
        MockJsonObject mock = (MockJsonObject) object;
        assertEquals(ELEMENT, mock.getValue());
        assertEquals(EMPTY_JSON, mock.toString());
    }
    
    public void testJsonNode() {
        Object object = converter.convert(JsonHelper.getFirstNode(EMPTY_JSON));
        assertEquals(MockJsonObject.class, object.getClass());
        MockJsonObject mock = (MockJsonObject) object;
        assertEquals(ELEMENT, mock.getValue());
        assertEquals(EMPTY_JSON, mock.toString());
    }
}
