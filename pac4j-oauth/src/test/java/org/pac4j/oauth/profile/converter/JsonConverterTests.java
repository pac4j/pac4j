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
