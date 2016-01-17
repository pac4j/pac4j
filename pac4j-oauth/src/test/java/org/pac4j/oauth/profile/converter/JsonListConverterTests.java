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

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.facebook.FacebookObject;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests the {@link JsonListConverter}.
 * 
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class JsonListConverterTests implements TestsConstants {
    
    private final JsonListConverter converter = new JsonListConverter(FacebookObject.class, FacebookObject[].class);

    private final static String ONE_JSON = "{ \"id\": \"x\", \"name\": \"y\" }";
    private final static String JSON = "[" + ONE_JSON + "]";

    @Test
    public void testNull() {
        assertNull(this.converter.convert(null));
    }

    @Test
    public void testBadType() {
        assertNull(this.converter.convert(1));
    }

    @Test
    public void testString() {
        final FacebookObject[] objects = (FacebookObject[]) converter.convert(JSON);
        assertNotNull(objects);
        assertEquals(1, objects.length);
        final FacebookObject object = objects[0];
        assertEquals("x", object.getId());
        assertEquals("y", object.getName());
    }

    @Test
    public void testJsonNode() {
        final JsonNode node = JsonHelper.getFirstNode(JSON);
        final FacebookObject[] objects = (FacebookObject[]) converter.convert(node);
        assertNotNull(objects);
        assertEquals(1, objects.length);
        final FacebookObject object = objects[0];
        assertEquals("x", object.getId());
        assertEquals("y", object.getName());
    }

    @Test
    public void testListJson() {
        final List<String> list = new ArrayList<>();
        list.add(ONE_JSON);
        final List<FacebookObject> objects = (List<FacebookObject>) converter.convert(list);
        assertNotNull(objects);
        assertEquals(1, objects.size());
        final FacebookObject object = objects.get(0);
        assertEquals("x", object.getId());
        assertEquals("y", object.getName());
    }
}
