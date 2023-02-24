package org.pac4j.saml.util;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.shibboleth.shared.xml.ParserPool;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallerFactory;
import org.opensaml.core.xml.io.UnmarshallerFactory;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.pac4j.core.adapter.FrameworkAdapter;
import org.pac4j.saml.exceptions.SAMLException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;

/**
 * OpenSAML configuration bean to bootstrap the parser pool.
 *
 * Uses the Java service API to find an instance of {@link org.pac4j.saml.util.ConfigurationManager} to do the actual configuration. Will
 * use the implementation with the lowest javax|jakarta.annotation.Priority annotation. If none are found, a relatively sane
 * implementation, {@link org.pac4j.saml.util.DefaultConfigurationManager}, will be used. The default priority is 100.
 *
 * @see ServiceLoader
 * @author Misagh Moayyed
 * @since 1.7
 */
@Slf4j
public final class Configuration {

    private Configuration() {
    }

    static {
        LOGGER.info("Bootstrapping OpenSAML configuration via Pac4j...");
        bootstrap();
    }

    private static void bootstrap() {
        var configurationManagers = ServiceLoader.load(ConfigurationManager.class, Configuration.class.getClassLoader());
        if (configurationManagers.findFirst().isEmpty())
            configurationManagers = ServiceLoader.load(ConfigurationManager.class);
        final List<ConfigurationManager> configurationManagerList = new ArrayList<>();
        configurationManagers.forEach(configurationManagerList::add);
        if (!configurationManagerList.isEmpty()) {
            configurationManagerList.sort(FrameworkAdapter.INSTANCE::compareManagers);
            configurationManagerList.get(0).configure();
        }
    }

    /**
     * <p>getParserPool.</p>
     *
     * @return a {@link net.shibboleth.shared.xml.ParserPool} object
     */
    public static ParserPool getParserPool() {
        return XMLObjectProviderRegistrySupport.getParserPool();
    }

    /**
     * <p>getBuilderFactory.</p>
     *
     * @return a {@link org.opensaml.core.xml.XMLObjectBuilderFactory} object
     */
    public static XMLObjectBuilderFactory getBuilderFactory() {
        return XMLObjectProviderRegistrySupport.getBuilderFactory();
    }

    /**
     * <p>getMarshallerFactory.</p>
     *
     * @return a {@link org.opensaml.core.xml.io.MarshallerFactory} object
     */
    public static MarshallerFactory getMarshallerFactory() {
        return XMLObjectProviderRegistrySupport.getMarshallerFactory();
    }

    /**
     * <p>getUnmarshallerFactory.</p>
     *
     * @return a {@link org.opensaml.core.xml.io.UnmarshallerFactory} object
     */
    public static UnmarshallerFactory getUnmarshallerFactory() {
        return XMLObjectProviderRegistrySupport.getUnmarshallerFactory();
    }

    /**
     * <p>serializeSamlObject.</p>
     *
     * @param samlObject a {@link org.opensaml.core.xml.XMLObject} object
     * @return a {@link java.io.StringWriter} object
     */
    public static StringWriter serializeSamlObject(final XMLObject samlObject) {
        val writer = new StringWriter();
        try {
            val marshaller = getMarshallerFactory().getMarshaller(samlObject.getElementQName());
            if (marshaller != null) {
                val element = marshaller.marshall(samlObject);
                val domSource = new DOMSource(element);

                val result = new StreamResult(writer);
                val tf = TransformerFactory.newInstance();
                val transformer = tf.newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
                transformer.transform(domSource, result);
            }
        } catch (final Exception e) {
            throw new SAMLException(e.getMessage(), e);
        }
        return writer;
    }

    /**
     * <p>deserializeSamlObject.</p>
     *
     * @param obj a {@link java.lang.String} object
     * @return a {@link java.util.Optional} object
     */
    public static Optional<XMLObject> deserializeSamlObject(final String obj) {
        try (val reader = new StringReader(obj)) {
            return Optional.of(XMLObjectSupport.unmarshallFromReader(Configuration.getParserPool(), reader));
        } catch (final Exception e) {
            LOGGER.error("Error unmarshalling message from input stream", e);
            return Optional.empty();
        }
    }
}
