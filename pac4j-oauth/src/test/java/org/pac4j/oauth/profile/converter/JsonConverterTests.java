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

import static org.junit.Assert.*;

/**
 * Tests the {@link JsonConverter}.
 * 
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class JsonConverterTests implements TestsConstants {
    
    private final JsonConverter converter = new JsonConverter(FacebookObject.class);
    
    private final static String JSON = "{ \"id\": \"x\", \"name\": \"y\" }";

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
        final FacebookObject object = (FacebookObject) converter.convert(JSON);
        assertNotNull(object);
        assertEquals("x", object.getId());
        assertEquals("y", object.getName());
    }

    @Test
    public void testJsonNode() {
        final JsonNode node = JsonHelper.getFirstNode(JSON);
        final FacebookObject object = (FacebookObject) converter.convert(node);
        assertNotNull(object);
        assertEquals("x", object.getId());
        assertEquals("y", object.getName());
    }
}
