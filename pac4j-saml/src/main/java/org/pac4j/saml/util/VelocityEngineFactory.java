
package org.pac4j.saml.util;

import lombok.val;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.pac4j.core.exception.TechnicalException;

import java.util.Properties;

/**
 * Factory returning a well configured {@link VelocityEngine} instance required for
 * generating an HTML form used to POST SAML messages.
 *
 * @author Michael Remond
 */
public class VelocityEngineFactory {

    /**
     * <p>getEngine.</p>
     *
     * @return a {@link VelocityEngine} object
     */
    public static VelocityEngine getEngine() {

        try {
            val props = new Properties();
            props.setProperty(RuntimeConstants.INPUT_ENCODING, "UTF-8");
            props.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
            props.setProperty("resource.loader.string.class", StringResourceLoader.class.getName());
            props.setProperty("resource.loader.classpath.class", ClasspathResourceLoader.class.getName());
            props.setProperty(RuntimeConstants.RESOURCE_LOADERS, "classpath,string");
            val engine = new VelocityEngine();
            engine.init(props);
            return engine;
        } catch (final Exception e) {
            throw new TechnicalException("Error configuring velocity", e);
        }

    }

}
