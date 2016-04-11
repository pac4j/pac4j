package org.pac4j.oauth.profile;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.oauth.profile.facebook.FacebookObject;

import static org.junit.Assert.*;

/**
 * This class tests the {@link JsonHelper} class.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class JsonHelperTests implements TestsConstants {
    
    private static final String GOOD_TEXT_JSON = "{ \"" + KEY + "\" : \"" + VALUE + "\" }";
    
    private static final String GOOD_BOOLEAN_JSON = "{ \"" + KEY + "\" : " + Boolean.TRUE + " }";
    
    private static final String GOOD_NUMBER_JSON = "{ \"" + KEY + "\" : 1 }";
    
    private static final String GOOD_NODE_JSON = "{ \"" + KEY + "\" : " + GOOD_TEXT_JSON + " }";
    
    private static final String BAD_JSON = "this_is_definitively_not_a_json_text";

    @Test
    public void testGetFirstNodeOk() {
        assertNotNull(JsonHelper.getFirstNode(GOOD_TEXT_JSON));
    }

    @Test
    public void testGetFirstNodeKo() {
        assertNull(JsonHelper.getFirstNode(BAD_JSON));
    }

    @Test
    public void testGetText() {
        assertEquals(VALUE, JsonHelper.getElement(JsonHelper.getFirstNode(GOOD_TEXT_JSON), KEY));
    }

    @Test
    public void testGetNull() {
        assertNull(JsonHelper.getElement(null, KEY));
    }

    @Test
    public void testGetBadKey() {
        assertNull(JsonHelper.getElement(JsonHelper.getFirstNode(GOOD_TEXT_JSON), "bad" + KEY));
    }

    @Test
    public void testGetBoolean() {
        assertEquals(Boolean.TRUE, JsonHelper.getElement(JsonHelper.getFirstNode(GOOD_BOOLEAN_JSON), KEY));
    }

    @Test
    public void testGetNumber() {
        assertEquals(1, JsonHelper.getElement(JsonHelper.getFirstNode(GOOD_NUMBER_JSON), KEY));
    }

    @Test
    public void testGetNode() {
        assertEquals(JsonHelper.getFirstNode(GOOD_TEXT_JSON),
                     JsonHelper.getElement(JsonHelper.getFirstNode(GOOD_NODE_JSON), KEY));
    }

    @Test
    public void testGetAsType() {
        final JsonNode node = JsonHelper.getFirstNode("{ \"id\": \"x\", \"name\": \"y\" }");
        final FacebookObject object = JsonHelper.getAsType(node, FacebookObject.class);
        assertNotNull(object);
        assertNotNull(object);
        assertEquals("x", object.getId());
        assertEquals("y", object.getName());
    }

    @Test
    public void testToJSONString() {
        final FacebookObject object = new FacebookObject();
        object.setId(ID);
        object.setName(NAME);
        assertEquals("\"{\\\"id\\\":\\\"id\\\",\\\"name\\\":\\\"name\\\"}\"", JsonHelper.toJSONString(JsonHelper.toJSONString(object)));
    }
}
