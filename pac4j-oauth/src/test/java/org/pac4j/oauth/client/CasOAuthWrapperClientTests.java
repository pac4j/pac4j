package org.pac4j.oauth.client;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.junit.Test;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.oauth.profile.casoauthwrapper.CasOAuthWrapperProfileDefinition;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Tests the profile parsing from the {@link CasOAuthWrapperClient}.
 *
 * @author Jerome Leleu
 * @since 1.9.2
 */
public final class CasOAuthWrapperClientTests implements TestsConstants {

    @Test
    public void testParsingAttributesCASServerV4_2AndBefore() throws IOException {
        val jsonFactory = new JsonFactory(new ObjectMapper());
        final Map<String, Object> attributes = new HashMap<>();
        attributes.put(KEY, VALUE);
        attributes.put(NAME, TOKEN);
        val writer = new StringWriter();
        try (var jsonGenerator = jsonFactory.createJsonGenerator(writer)) {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("id", ID);
            jsonGenerator.writeArrayFieldStart("attributes");
            for (val entry : attributes.entrySet()) {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeObjectField(entry.getKey(), entry.getValue());
                jsonGenerator.writeEndObject();
            }
            jsonGenerator.writeEndArray();
            jsonGenerator.writeEndObject();
        }
        val body = writer.toString();
        val client = new CasOAuthWrapperClient();
        client.setKey(KEY);
        client.setSecret(SECRET);
        client.setCasOAuthUrl(CALLBACK_URL);
        client.setCallbackUrl(CALLBACK_URL);
        client.init();
        val profile = new CasOAuthWrapperProfileDefinition().extractUserProfile(body);
        assertEquals(ID, profile.getId());
        assertEquals(2, profile.getAttributes().size());
        assertEquals(VALUE, profile.getAttribute(KEY));
        assertEquals(TOKEN, profile.getAttribute(NAME));
    }

    @Test
    public void testParsingAttributesCASServerV5() throws IOException {
        final Map<String, Object> attributes = new HashMap<>();
        attributes.put(KEY, VALUE);
        attributes.put(NAME, TOKEN);
        final Map<String, Object> map = new HashMap<>();
        map.put("id", ID);
        map.put("attributes", attributes);
        val body = new ObjectMapper()
                .writer()
                .withDefaultPrettyPrinter()
                .writeValueAsString(map);
        val client = new CasOAuthWrapperClient();
        client.setKey(KEY);
        client.setSecret(SECRET);
        client.setCasOAuthUrl(CALLBACK_URL);
        client.setCallbackUrl(CALLBACK_URL);
        client.init();
        val profile = new CasOAuthWrapperProfileDefinition().extractUserProfile(body);
        assertEquals(ID, profile.getId());
        assertEquals(2, profile.getAttributes().size());
        assertEquals(VALUE, profile.getAttribute(KEY));
        assertEquals(TOKEN, profile.getAttribute(NAME));
    }
}
