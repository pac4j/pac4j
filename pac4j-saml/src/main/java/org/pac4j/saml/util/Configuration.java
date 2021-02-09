package org.pac4j.saml.util;

import java.io.StringReader;
import net.shibboleth.utilities.java.support.xml.ParserPool;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.Marshaller;
import org.opensaml.core.xml.io.MarshallerFactory;
import org.opensaml.core.xml.io.UnmarshallerFactory;
import org.pac4j.saml.exceptions.SAMLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;

import javax.annotation.Priority;
import org.opensaml.core.xml.util.XMLObjectSupport;

/**
 * OpenSAML configuration bean to bootstrap the parser pool.
 *
 * Uses the Java service API to find an instance of {@link ConfigurationManager} to do the actual configuration. Will
 * use the implementation with the lowest {@link Priority} annotation. If none are found, a relatively sane
 * implementation, {@link DefaultConfigurationManager}, will be used. The default priority is 100.
 *
 * @see ServiceLoader
 *
 * @author Misagh Moayyed
 * @since 1.7
 */
public final class Configuration {
    protected static final Logger logger = LoggerFactory.getLogger(Configuration.class);

    private Configuration() {
    }

    static {
        logger.info("Bootstrapping OpenSAML configuration via Pac4j...");
        bootstrap();
    }

    private static int compareManagers(final Object obj1, final Object obj2) {
        var p1 = 100;
        var p2 = 100;
        final var p1a = obj1.getClass().getAnnotation(Priority.class);
        if (p1a != null) {
            p1 = p1a.value();
        }
        final var p2a = obj2.getClass().getAnnotation(Priority.class);
        if (p2a != null) {
            p2 = p2a.value();
        }
        if (p1 < p2) {
            return -1;
        } else if (p1 > p2) {
            return 1;
        } else {
            return obj2.getClass().getSimpleName().compareTo(obj1.getClass().getSimpleName());
        }
    }

    private static void bootstrap() {
        final var configurationManagers = ServiceLoader.load(ConfigurationManager.class);
        final List<ConfigurationManager> configurationManagerList = new ArrayList<>();
        configurationManagers.forEach(configurationManagerList::add);
        if (!configurationManagerList.isEmpty()) {
            configurationManagerList.sort(Configuration::compareManagers);
            configurationManagerList.get(0).configure();
        }
    }


    public static ParserPool getParserPool() {
        return XMLObjectProviderRegistrySupport.getParserPool();
    }

    public static XMLObjectBuilderFactory getBuilderFactory() {
        return XMLObjectProviderRegistrySupport.getBuilderFactory();
    }

    public static MarshallerFactory getMarshallerFactory() {
        return XMLObjectProviderRegistrySupport.getMarshallerFactory();
    }

    public static UnmarshallerFactory getUnmarshallerFactory() {
        return XMLObjectProviderRegistrySupport.getUnmarshallerFactory();
    }

    public static StringWriter serializeSamlObject(final XMLObject samlObject) {
        final var writer = new StringWriter();
        try {
            final var marshaller = getMarshallerFactory().getMarshaller(samlObject.getElementQName());
            if (marshaller != null) {
                final var element = marshaller.marshall(samlObject);
                final var domSource = new DOMSource(element);

                final var result = new StreamResult(writer);
                final var tf = TransformerFactory.newInstance();
                final var transformer = tf.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                transformer.transform(domSource, result);
            }
        } catch (final Exception e) {
            throw new SAMLException(e.getMessage(), e);
        }
        return writer;
    }

    public static Optional<XMLObject> deserializeSamlObject(final String obj) {
        try (var reader = new StringReader(obj)) {
            return Optional.of(XMLObjectSupport.unmarshallFromReader(Configuration.getParserPool(), reader));
        } catch (final Exception e) {
            logger.error("Error unmarshalling message from input stream", e);
            return Optional.empty();
        }
    }
}
