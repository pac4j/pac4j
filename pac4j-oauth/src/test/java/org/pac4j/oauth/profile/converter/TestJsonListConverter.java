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
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.JsonList;

/**
 * This class tests the {@link JsonListConverter} class.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class TestJsonListConverter extends TestCase implements TestsConstants {
    
    private final JsonListConverter converterForString = new JsonListConverter(String.class);
    
    private final JsonListConverter converterForMockJsonObject = new JsonListConverter(MockJsonObject.class);
    
    private final static String JSON_LIST = "[\"" + ELEMENT + "\",\"" + ELEMENT2 + "\"]";
    
    private final static String JSON_EMPTY_LIST = "[]";
    
    public void testNull() {
        assertNull(this.converterForString.convert(null));
        assertNull(this.converterForMockJsonObject.convert(null));
    }
    
    public void testNotAStringNotAJsonNode() {
        assertNull(this.converterForString.convert(1));
        assertNull(this.converterForMockJsonObject.convert(1));
    }
    
    public void testJsonString() {
        final Object object = this.converterForString.convert(JSON_LIST);
        assertEquals(JsonList.class, object.getClass());
        @SuppressWarnings("unchecked")
        final JsonList<String> list = (JsonList<String>) object;
        assertEquals(2, list.size());
        assertEquals(ELEMENT, list.get(0));
        assertEquals(ELEMENT2, list.get(1));
    }
    
    public void testJsonStringMockJsonObject() {
        final Object object = this.converterForMockJsonObject.convert(JSON_LIST);
        assertEquals(JsonList.class, object.getClass());
        @SuppressWarnings("unchecked")
        final JsonList<MockJsonObject> list = (JsonList<MockJsonObject>) object;
        assertEquals(2, list.size());
        assertEquals(ELEMENT, list.get(0).getValue());
        assertEquals(ELEMENT2, list.get(1).getValue());
    }
    
    public void testJsonNode() {
        final Object object = this.converterForString.convert(JsonHelper.getFirstNode(JSON_LIST));
        assertEquals(JsonList.class, object.getClass());
        @SuppressWarnings("unchecked")
        final JsonList<String> list = (JsonList<String>) object;
        assertEquals(2, list.size());
        assertEquals(ELEMENT, list.get(0));
        assertEquals(ELEMENT2, list.get(1));
    }
    
    public void testEmptyList() {
        final Object object = this.converterForString.convert(JsonHelper.getFirstNode(JSON_EMPTY_LIST));
        assertEquals(JsonList.class, object.getClass());
        @SuppressWarnings("unchecked")
        final JsonList<String> list = (JsonList<String>) object;
        assertEquals(0, list.size());
    }
    
    public void testList() {
        final List<String> list = new ArrayList<String>();
        list.add(ELEMENT);
        list.add(ELEMENT2);
        final Object object = this.converterForString.convert(list);
        assertEquals(JsonList.class, object.getClass());
        @SuppressWarnings("unchecked")
        final JsonList<String> jsonList = (JsonList<String>) object;
        assertEquals(2, jsonList.size());
        assertEquals(ELEMENT, jsonList.get(0));
        assertEquals(ELEMENT2, jsonList.get(1));
    }
}
