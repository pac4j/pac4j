package org.pac4j.saml.util;


import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.xml.ParserPool;
import net.shibboleth.shared.xml.impl.BasicParserPool;
import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import org.opensaml.core.xml.config.XMLObjectProviderRegistry;

import javax.xml.parsers.DocumentBuilderFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * The default {@link ConfigurationManager}, bootstrapping OpenSAML and its parser pool.
 *
 * @since 3.3.0
 * @author Jerome LELEU
 */
@Slf4j
public class DefaultConfigurationManager implements ConfigurationManager {

    private static final String DOCUMENT_BUILDER_FACTORY_PROPERTY = "javax.xml.parsers.DocumentBuilderFactory";

    private static final String[] JDK_PARSER_ATTRIBUTES = {"jdk.xml.elementAttributeLimit", "jdk.xml.maxElementDepth"};

    /** {@inheritDoc} */
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

        initializeOpenSaml();

        val parserPool = initParserPool();
        registry.setParserPool(parserPool);
    }

    /**
     * Runs the OpenSAML initializers, falling back on the JDK built-in XML parser if the parser in use
     * does not support them.
     *
     * Since OpenSAML 5.2.2, {@code GlobalParserPoolInitializer} and {@code DecryptionParserPoolInitializer}
     * configure their parser pools with the {@code jdk.xml.elementAttributeLimit} and
     * {@code jdk.xml.maxElementDepth} attributes, which only the JDK built-in parser accepts: a standalone
     * Apache Xerces rejects them and the whole initialization fails. A parser pool keeps the factory it
     * resolves when initialized, so the override only needs to last for the duration of this method.
     */
    protected void initializeOpenSaml() {
        synchronized (ConfigurationService.class) {
            val factory = DocumentBuilderFactory.newInstance();
            if (supportsJdkParserAttributes(factory)) {
                initializeServices();
                return;
            }

            val jdkFactory = DocumentBuilderFactory.newDefaultInstance().getClass().getName();
            LOGGER.warn("The XML parser in use ({}) rejects the attributes set by the OpenSAML initializers; "
                + "falling back on the JDK parser ({}) to initialize OpenSAML", factory.getClass().getName(), jdkFactory);

            val previousFactory = System.getProperty(DOCUMENT_BUILDER_FACTORY_PROPERTY);
            System.setProperty(DOCUMENT_BUILDER_FACTORY_PROPERTY, jdkFactory);
            try {
                initializeServices();
            } finally {
                if (previousFactory == null) {
                    System.clearProperty(DOCUMENT_BUILDER_FACTORY_PROPERTY);
                } else {
                    System.setProperty(DOCUMENT_BUILDER_FACTORY_PROPERTY, previousFactory);
                }
            }
        }
    }

    private static boolean supportsJdkParserAttributes(final DocumentBuilderFactory factory) {
        for (val attribute : JDK_PARSER_ATTRIBUTES) {
            try {
                factory.setAttribute(attribute, 30);
            } catch (final IllegalArgumentException e) {
                LOGGER.debug("The XML parser {} does not support the {} attribute", factory.getClass().getName(), attribute);
                return false;
            }
        }
        return true;
    }

    private static void initializeServices() {
        try {
            InitializationService.initialize();
        } catch (final InitializationException e) {
            throw new RuntimeException("Exception initializing OpenSAML", e);
        }
    }

    private static ParserPool initParserPool() {

        try {
            val parserPool = new BasicParserPool();
            parserPool.setMaxPoolSize(100);
            parserPool.setCoalescing(true);
            parserPool.setIgnoreComments(true);
            parserPool.setNamespaceAware(true);
            parserPool.setExpandEntityReferences(false);
            parserPool.setXincludeAware(false);
            parserPool.setIgnoreElementContentWhitespace(true);

            final Map<String, Object> builderAttributes = new HashMap<>();
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
