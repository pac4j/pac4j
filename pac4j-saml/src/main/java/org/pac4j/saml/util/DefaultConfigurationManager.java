package org.pac4j.saml.util;


import net.shibboleth.shared.component.ComponentInitializationException;
import net.shibboleth.shared.xml.ParserPool;
import net.shibboleth.shared.xml.impl.BasicParserPool;
import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import org.opensaml.core.xml.config.XMLObjectProviderRegistry;

import java.util.HashMap;
import java.util.Map;

public class DefaultConfigurationManager implements ConfigurationManager {
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

        final var parserPool = initParserPool();
        registry.setParserPool(parserPool);
    }

    private static ParserPool initParserPool() {

        try {
            final var parserPool = new BasicParserPool();
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
