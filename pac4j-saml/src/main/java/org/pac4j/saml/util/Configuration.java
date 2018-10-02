package org.pac4j.saml.util;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.xml.BasicParserPool;
import net.shibboleth.utilities.java.support.xml.ParserPool;
import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistry;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import javax.annotation.Priority;

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

    private static int compareManagers(Object obj1, Object obj2) {
        int p1 = 100;
        int p2 = 100;
        Priority p1a = obj1.getClass().getAnnotation(Priority.class);
        if (p1a != null) {
            p1 = p1a.value();
        }
        Priority p2a = obj2.getClass().getAnnotation(Priority.class);
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
        ServiceLoader<ConfigurationManager> configurationManagers = ServiceLoader.load(ConfigurationManager.class);
        List<ConfigurationManager> configurationManagerList = new ArrayList();
        configurationManagers.forEach(configurationManagerList::add);
        if (configurationManagerList.size() > 0) {
            configurationManagerList.sort(Configuration::compareManagers);
            configurationManagerList.get(0).configure();
        }

        for (ConfigurationManager configurationManager : configurationManagers) {
            configurationManager.configure();
        }
    }

    public static class DefaultConfigurationManager implements ConfigurationManager {
        @Override
        public void configure() {
            XMLObjectProviderRegistry registry;
            synchronized (ConfigurationService.class) {
                registry = ConfigurationService.get(XMLObjectProviderRegistry.class);
                if (registry == null) {
                    registry = new XMLObjectProviderRegistry();
                    ConfigurationService.register(XMLObjectProviderRegistry.class, registry);
                }
            }

            try {
                InitializationService.initialize();
            } catch (final InitializationException e) {
                throw new RuntimeException("Exception initializing OpenSAML", e);
            }

            ParserPool parserPool = initParserPool();
            registry.setParserPool(parserPool);
        }

        private static ParserPool initParserPool() {

            try {
                BasicParserPool parserPool = new BasicParserPool();
                parserPool.setMaxPoolSize(100);
                parserPool.setCoalescing(true);
                parserPool.setIgnoreComments(true);
                parserPool.setNamespaceAware(true);
                parserPool.setExpandEntityReferences(false);
                parserPool.setXincludeAware(false);
                parserPool.setIgnoreElementContentWhitespace(true);

                final Map<String, Object> builderAttributes = new HashMap<String, Object>();
                parserPool.setBuilderAttributes(builderAttributes);

                final Map<String, Boolean> features = new HashMap<>();
                features.put("http://apache.org/xml/features/disallow-doctype-decl", Boolean.TRUE);
                features.put("http://apache.org/xml/features/validation/schema/normalized-value", Boolean.FALSE);
                features.put("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE);
                features.put("http://xml.org/sax/features/external-general-entities", Boolean.FALSE);
                features.put("http://xml.org/sax/features/external-parameter-entities", Boolean.FALSE);

                parserPool.setBuilderFeatures(features);
                parserPool.initialize();
                return parserPool;
            } catch (final ComponentInitializationException e) {
                throw new RuntimeException("Exception initializing parserPool", e);
            }
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
        final StringWriter writer = new StringWriter();
        try {
            final Marshaller marshaller = getMarshallerFactory().getMarshaller(samlObject.getElementQName());
            if (marshaller != null) {
                final Element element = marshaller.marshall(samlObject);
                final DOMSource domSource = new DOMSource(element);

                final StreamResult result = new StreamResult(writer);
                final TransformerFactory tf = TransformerFactory.newInstance();
                final Transformer transformer = tf.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                transformer.transform(domSource, result);
            }
        } catch (final Exception e) {
            throw new SAMLException(e.getMessage(), e);
        }
        return writer;
    }
}
