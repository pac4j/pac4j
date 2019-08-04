package org.pac4j.saml.util;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.xml.BasicParserPool;
import net.shibboleth.utilities.java.support.xml.ParserPool;
import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import org.opensaml.core.xml.config.XMLObjectProviderRegistry;
import org.opensaml.saml.common.SAMLRuntimeException;

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
            throw new SAMLRuntimeException("Exception initializing OpenSAML", e);
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
            throw new SAMLRuntimeException("Exception initializing parserPool", e);
        }
    }
}
