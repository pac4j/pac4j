package org.pac4j.saml.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.junit.Test;

public class VelocityEngineFactoryTest {

    @Test
    public void defaultProperties()  {
        final var engine = VelocityEngineFactory.getEngine();
        assertNotNull(engine);
        assertEquals("org.apache.velocity.runtime.resource.loader.StringResourceLoader",
            engine.getProperty("resource.loader.string.class"));
        assertEquals("org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader",
            engine.getProperty("resource.loader.classpath.class"));
        assertEquals(vector("classpath"), engine.getProperty("resource.loaders"));
        assertEquals("UTF-8", engine.getProperty(RuntimeConstants.INPUT_ENCODING));
    }

    private static List<String> vector(final String... values) {
        return new ArrayList<>(Arrays.asList(values));
    }
}
