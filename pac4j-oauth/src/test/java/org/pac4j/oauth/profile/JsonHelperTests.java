package org.pac4j.oauth.profile;

import lombok.val;
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
    public void testToJSONString() {
        val object = new FacebookObject();
        object.setId(ID);
        object.setName(NAME);
        String objectstr = JsonHelper.toJSONString(object);
        assertTrue(objectstr.equals("{\"id\":\"id\",\"name\":\"name\"}") || objectstr.equals("{\"name\":\"name\",\"id\":\"id\"}"));
    }
}
