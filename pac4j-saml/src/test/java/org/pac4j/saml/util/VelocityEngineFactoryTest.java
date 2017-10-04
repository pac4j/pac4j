package org.pac4j.saml.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Vector;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.junit.Test;

public class VelocityEngineFactoryTest {

    @Test
    public void defaultProperties()  {
        VelocityEngine engine = VelocityEngineFactory.getEngine();
        assertNotNull(engine);
        assertEquals("org.apache.velocity.runtime.resource.loader.StringResourceLoader",
            engine.getProperty("string.resource.loader.class"));
        assertEquals("org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader",
            engine.getProperty("classpath.resource.loader.class"));
        assertEquals(vector("classpath"), engine.getProperty("resource.loader"));
        assertEquals("UTF-8", engine.getProperty(RuntimeConstants.INPUT_ENCODING));
        assertEquals("UTF-8", engine.getProperty(RuntimeConstants.OUTPUT_ENCODING));
        assertEquals("net.shibboleth.utilities.java.support.velocity.SLF4JLogChute",
            engine.getProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS));
    }

    private Vector<String> vector(final String... values) {
        return new Vector<>(Arrays.asList(values));
    }
}
