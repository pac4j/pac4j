package org.pac4j.oauth.profile.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.val;
import org.junit.Test;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.facebook.FacebookObject;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests the {@link JsonConverter}.
 *
 * @author Jerome Leleu
 * @since 1.9.0
 */
public final class JsonConverterTests implements TestsConstants {

    private final static JsonConverter OBJECT_CONVERTER = new JsonConverter(FacebookObject.class);
    private final static JsonConverter LIST_CONVERTER = new JsonConverter(List.class, new TypeReference<List<FacebookObject>>(){});

    private final static String ONE_JSON = "{ \"id\": \"x\", \"name\": \"y\" }";
    private final static String JSON = "[" + ONE_JSON + "]";

    @Test
    public void testNull() {
        assertNull(OBJECT_CONVERTER.convert(null));
        assertNull(LIST_CONVERTER.convert(null));
    }

    @Test
    public void testBadType() {
        assertNull(OBJECT_CONVERTER.convert(1));
        assertNull(LIST_CONVERTER.convert(1));
    }

    @Test
    public void testString() {
        val object = (FacebookObject) OBJECT_CONVERTER.convert(ONE_JSON);
        assertNotNull(object);
        assertEquals("x", object.getId());
        assertEquals("y", object.getName());
    }

    @Test
    public void testJsonNode() {
        val node = JsonHelper.getFirstNode(ONE_JSON);
        val object = (FacebookObject) OBJECT_CONVERTER.convert(node);
        assertNotNull(object);
        assertEquals("x", object.getId());
        assertEquals("y", object.getName());
    }

    @Test
    public void testObject() {
        assertNotNull(OBJECT_CONVERTER.convert(new FacebookObject()));
    }

    @Test
    public void testStringForListConverter() {
        val objects = (List<FacebookObject>) LIST_CONVERTER.convert(JSON);
        assertNotNull(objects);
        assertEquals(1, objects.size());
        val object = objects.get(0);
        assertEquals("x", object.getId());
        assertEquals("y", object.getName());
    }

    @Test
    public void testJsonNodeForListConverter() {
        val node = JsonHelper.getFirstNode(JSON);
        val objects = (List<FacebookObject>) LIST_CONVERTER.convert(node);
        assertNotNull(objects);
        assertEquals(1, objects.size());
        val object = objects.get(0);
        assertEquals("x", object.getId());
        assertEquals("y", object.getName());
    }

    @Test
    public void testListObjectForListConverter() {
        final List<FacebookObject> list = new ArrayList<>();
        list.add(new FacebookObject());
        val objects = (List<FacebookObject>) LIST_CONVERTER.convert(list);
        assertNotNull(objects);
        assertEquals(1, objects.size());
        assertNotNull(objects.get(0));
    }
}
