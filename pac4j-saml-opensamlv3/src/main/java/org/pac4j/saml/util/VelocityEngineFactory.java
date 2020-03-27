
package org.pac4j.saml.util;

import net.shibboleth.utilities.java.support.velocity.SLF4JLogChute;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.pac4j.core.exception.TechnicalException;

import java.util.Properties;

/**
 * Factory returning a well configured {@link VelocityEngine} instance required for
 * generating an HTML form used to POST SAML messages.
 *
 * @author Michael Remond
 *
 */
public class VelocityEngineFactory {

    public static VelocityEngine getEngine() {

        try {

            final Properties props =
                    new Properties();
            props.putAll(net.shibboleth.utilities.java.support.velocity.VelocityEngine.getDefaultProperties());
            props.setProperty(RuntimeConstants.INPUT_ENCODING, "UTF-8");
            props.setProperty(RuntimeConstants.OUTPUT_ENCODING, "UTF-8");
            props.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
            props.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, SLF4JLogChute.class.getName());

            final VelocityEngine velocityEngine =
                    net.shibboleth.utilities.java.support.velocity.VelocityEngine
                    .newVelocityEngine(props);
            return velocityEngine;
        } catch (final Exception e) {
            throw new TechnicalException("Error configuring velocity", e);
        }

    }

}
