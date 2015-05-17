package org.pac4j.saml.util;

import com.sun.org.apache.xerces.internal.util.SecurityManager;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.xml.BasicParserPool;
import net.shibboleth.utilities.java.support.xml.ParserPool;
import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.config.InitializationException;
import org.opensaml.core.config.InitializationService;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistry;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallerFactory;
import org.opensaml.core.xml.io.UnmarshallerFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Misagh Moayyed
 */
public final class Configuration {
    private static BasicParserPool parserPool;

    private Configuration() {}

    static {
        bootstrap();
    }

    public static void bootstrap() {
        parserPool = new BasicParserPool();
        parserPool.setMaxPoolSize(100);
        parserPool.setCoalescing(true);
        parserPool.setIgnoreComments(true);
        parserPool.setNamespaceAware(true);

        Map<String, Object> builderAttributes = new HashMap<String, Object>();
        SecurityManager secMgmr = new SecurityManager();
        builderAttributes.put("http://apache.org/xml/properties/security-manager", secMgmr);
        parserPool.setBuilderAttributes(builderAttributes);

        Map<String, Boolean> features = new HashMap<String, Boolean>();
        features.put("http://apache.org/xml/features/disallow-doctype-decl", Boolean.TRUE);
        features.put("http://apache.org/xml/features/validation/schema/normalized-value", Boolean.FALSE);
        features.put("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE);
        parserPool.setBuilderFeatures(features);

        try {
            parserPool.initialize();
        } catch (ComponentInitializationException e) {
            throw new RuntimeException("Exception initializing parserPool", e);
        }


        try {
            InitializationService.initialize();
        } catch (InitializationException e) {
            throw new RuntimeException("Exception initializing OpenSAML", e);
        }

        XMLObjectProviderRegistry registry;
        synchronized(ConfigurationService.class) {
            registry = ConfigurationService.get(XMLObjectProviderRegistry.class);
            if (registry == null) {
                registry = new XMLObjectProviderRegistry();
                ConfigurationService.register(XMLObjectProviderRegistry.class, registry);
            }
        }

        registry.setParserPool(parserPool);
    }

    public static ParserPool getParserPool () {
        return parserPool;
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
}
