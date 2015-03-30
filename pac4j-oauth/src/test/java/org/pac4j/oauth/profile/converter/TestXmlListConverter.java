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

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.pac4j.core.util.TestsConstants;
import org.pac4j.oauth.profile.XmlList;

/**
 * This class tests the {@link XmlListConverter} class.
 * 
 * @author Jerome Leleu
 * @since 1.4.1
 */
@SuppressWarnings("unchecked")
public final class TestXmlListConverter extends TestCase implements TestsConstants {
    
    private final XmlListConverter converterForString = new XmlListConverter(String.class);
    
    private final XmlListConverter converterForMockXmlObject = new XmlListConverter(MockXmlObject.class);
    
    private final static String XML_LIST = "<el>" + ELEMENT + "</el><el>" + ELEMENT2 + "</el>";
    
    private final static String XML_SPACED_LIST = "  <el>" + ELEMENT + "</el>    <el>" + ELEMENT2 + "</el>   ";
    
    private final static String XML_EMPTY_LIST = "";
    
    public void testNull() {
        assertNull(this.converterForString.convert(null));
        assertNull(this.converterForMockXmlObject.convert(null));
    }
    
    public void testNotAString() {
        assertNull(this.converterForString.convert(1));
        assertNull(this.converterForMockXmlObject.convert(1));
    }
    
    public void testList() {
        final Object object = this.converterForString.convert(XML_LIST);
        assertEquals(XmlList.class, object.getClass());
        final XmlList<String> list = (XmlList<String>) object;
        assertEquals(2, list.size());
        assertEquals(ELEMENT, list.get(0));
        assertEquals(ELEMENT2, list.get(1));
    }
    
    public void testSpacedList() {
        final Object object = this.converterForString.convert(XML_SPACED_LIST);
        assertEquals(XmlList.class, object.getClass());
        final XmlList<String> list = (XmlList<String>) object;
        assertEquals(2, list.size());
        assertEquals(ELEMENT, list.get(0));
        assertEquals(ELEMENT2, list.get(1));
    }
    
    public void testMockXmlObject() {
        final Object object = this.converterForMockXmlObject.convert(XML_LIST);
        assertEquals(XmlList.class, object.getClass());
        final XmlList<MockXmlObject> list = (XmlList<MockXmlObject>) object;
        assertEquals(2, list.size());
        assertEquals(ELEMENT, list.get(0).getValue());
        assertEquals(ELEMENT2, list.get(1).getValue());
    }
    
    public void testEmptyList() {
        final Object object = this.converterForString.convert(XML_EMPTY_LIST);
        assertEquals(XmlList.class, object.getClass());
        final XmlList<String> list = (XmlList<String>) object;
        assertEquals(0, list.size());
    }
    
    public void testBuildList() {
        final List<String> list = new ArrayList<String>();
        list.add(ELEMENT);
        list.add(ELEMENT2);
        final Object object = this.converterForString.convert(list);
        assertNull(object);
    }
}
