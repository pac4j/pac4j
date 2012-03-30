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

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.scribe.up.profile.JsonHelper;
import org.scribe.up.profile.JsonList;
import org.scribe.up.profile.converter.JsonListConverter;

/**
 * This class tests the {@link org.scribe.up.profile.converter.JsonListConverter} class.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class TestJsonListConverter extends TestCase {
    
    private JsonListConverter converterForString = new JsonListConverter(String.class);
    
    private JsonListConverter converterForMockJsonObject = new JsonListConverter(MockJsonObject.class);
    
    private final static String ELEMENT1 = "element1";
    
    private final static String ELEMENT2 = "element2";
    
    private final static String JSON_LIST = "[\"" + ELEMENT1 + "\",\"" + ELEMENT2 + "\"]";
    
    private final static String JSON_EMPTY_LIST = "[]";
    
    public void testNull() {
        assertNull(converterForString.convert(null));
        assertNull(converterForMockJsonObject.convert(null));
    }
    
    public void testNotAStringNotAJsonNode() {
        assertNull(converterForString.convert(1));
        assertNull(converterForMockJsonObject.convert(1));
    }
    
    public void testJsonString() {
        Object object = converterForString.convert(JSON_LIST);
        assertEquals(JsonList.class, object.getClass());
        @SuppressWarnings("unchecked")
        JsonList<String> list = (JsonList<String>) object;
        assertEquals(2, list.size());
        assertEquals(ELEMENT1, list.get(0));
        assertEquals(ELEMENT2, list.get(1));
    }
    
    public void testJsonStringMockJsonObject() {
        Object object = converterForMockJsonObject.convert(JSON_LIST);
        assertEquals(JsonList.class, object.getClass());
        @SuppressWarnings("unchecked")
        JsonList<MockJsonObject> list = (JsonList<MockJsonObject>) object;
        assertEquals(2, list.size());
        assertEquals(ELEMENT1, list.get(0).getValue());
        assertEquals(ELEMENT2, list.get(1).getValue());
    }
    
    public void testJsonNode() {
        Object object = converterForString.convert(JsonHelper.getFirstNode(JSON_LIST));
        assertEquals(JsonList.class, object.getClass());
        @SuppressWarnings("unchecked")
        JsonList<String> list = (JsonList<String>) object;
        assertEquals(2, list.size());
        assertEquals(ELEMENT1, list.get(0));
        assertEquals(ELEMENT2, list.get(1));
    }
    
    public void testEmptyList() {
        Object object = converterForString.convert(JsonHelper.getFirstNode(JSON_EMPTY_LIST));
        assertEquals(JsonList.class, object.getClass());
        @SuppressWarnings("unchecked")
        JsonList<String> list = (JsonList<String>) object;
        assertEquals(0, list.size());
    }
    
    public void testList() {
        List<String> list = new ArrayList<String>();
        list.add(ELEMENT1);
        list.add(ELEMENT2);
        Object object = converterForString.convert(list);
        assertEquals(JsonList.class, object.getClass());
        @SuppressWarnings("unchecked")
        JsonList<String> jsonList = (JsonList<String>) object;
        assertEquals(2, jsonList.size());
        assertEquals(ELEMENT1, jsonList.get(0));
        assertEquals(ELEMENT2, jsonList.get(1));
    }
}
