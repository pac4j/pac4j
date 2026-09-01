package org.pac4j.saml.util;

import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.xmlsec.config.DecryptionParserPool;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link DefaultConfigurationManager}, and in particular the fallback on the JDK parser when the
 * XML parser in use rejects the {@code jdk.xml.*} attributes set by the OpenSAML initializers.
 *
 * @since 6.6.0
 * @author Jerome LELEU
 */
class DefaultConfigurationManagerTests {

    private static final String DOCUMENT_BUILDER_FACTORY_PROPERTY = "javax.xml.parsers.DocumentBuilderFactory";

    @AfterEach
    void clearParserFactory() {
        System.clearProperty(DOCUMENT_BUILDER_FACTORY_PROPERTY);
    }

    @Test
    void testOpenSamlIsInitializedWithTheJdkParser() {
        new DefaultConfigurationManager().configure();

        assertParserPoolsAreUsable();
        assertNull(System.getProperty(DOCUMENT_BUILDER_FACTORY_PROPERTY),
            "the JDK parser supports the attributes, so no override should have been left behind");
    }

    @Test
    void testOpenSamlIsInitializedWhenTheParserRejectsTheJdkAttributes() {
        val factory = JdkAttributeRejectingDocumentBuilderFactory.class.getName();
        System.setProperty(DOCUMENT_BUILDER_FACTORY_PROPERTY, factory);

        new DefaultConfigurationManager().configure();

        assertParserPoolsAreUsable();
        assertEquals(factory, System.getProperty(DOCUMENT_BUILDER_FACTORY_PROPERTY),
            "the parser configured by the application must be restored once OpenSAML is initialized");
    }

    private static void assertParserPoolsAreUsable() {
        val parserPool = XMLObjectProviderRegistrySupport.getParserPool();
        assertNotNull(parserPool);

        val decryptionParserPool = ConfigurationService.get(DecryptionParserPool.class);
        assertNotNull(decryptionParserPool, "DecryptionParserPool must be registered, Decrypter requires it");

        try {
            val xml = new ByteArrayInputStream("<test/>".getBytes(StandardCharsets.UTF_8));
            assertNotNull(parserPool.parse(xml).getDocumentElement());
            assertNotNull(decryptionParserPool.getParserPool().parse(
                new ByteArrayInputStream("<test/>".getBytes(StandardCharsets.UTF_8))).getDocumentElement());
        } catch (final Exception e) {
            throw new AssertionError("the parser pools built during initialization must be usable", e);
        }
    }

    /**
     * A parser factory that refuses the {@code jdk.xml.*} attributes, the way a standalone Apache Xerces does.
     */
    public static class JdkAttributeRejectingDocumentBuilderFactory extends DocumentBuilderFactory {

        private final DocumentBuilderFactory delegate = newDefaultInstance();

        @Override
        public void setAttribute(final String name, final Object value) {
            if (name.startsWith("jdk.xml.")) {
                throw new IllegalArgumentException("Property '" + name + "' is not recognized.");
            }
            delegate.setAttribute(name, value);
        }

        @Override
        public Object getAttribute(final String name) {
            return delegate.getAttribute(name);
        }

        @Override
        public void setFeature(final String name, final boolean value) throws ParserConfigurationException {
            delegate.setFeature(name, value);
        }

        @Override
        public boolean getFeature(final String name) throws ParserConfigurationException {
            return delegate.getFeature(name);
        }

        @Override
        public DocumentBuilder newDocumentBuilder() throws ParserConfigurationException {
            return delegate.newDocumentBuilder();
        }

        @Override
        public void setNamespaceAware(final boolean value) {
            delegate.setNamespaceAware(value);
        }

        @Override
        public boolean isNamespaceAware() {
            return delegate.isNamespaceAware();
        }

        @Override
        public void setValidating(final boolean value) {
            delegate.setValidating(value);
        }

        @Override
        public boolean isValidating() {
            return delegate.isValidating();
        }

        @Override
        public void setIgnoringElementContentWhitespace(final boolean value) {
            delegate.setIgnoringElementContentWhitespace(value);
        }

        @Override
        public boolean isIgnoringElementContentWhitespace() {
            return delegate.isIgnoringElementContentWhitespace();
        }

        @Override
        public void setExpandEntityReferences(final boolean value) {
            delegate.setExpandEntityReferences(value);
        }

        @Override
        public boolean isExpandEntityReferences() {
            return delegate.isExpandEntityReferences();
        }

        @Override
        public void setIgnoringComments(final boolean value) {
            delegate.setIgnoringComments(value);
        }

        @Override
        public boolean isIgnoringComments() {
            return delegate.isIgnoringComments();
        }

        @Override
        public void setCoalescing(final boolean value) {
            delegate.setCoalescing(value);
        }

        @Override
        public boolean isCoalescing() {
            return delegate.isCoalescing();
        }

        @Override
        public void setXIncludeAware(final boolean value) {
            delegate.setXIncludeAware(value);
        }

        @Override
        public boolean isXIncludeAware() {
            return delegate.isXIncludeAware();
        }

        @Override
        public void setSchema(final Schema schema) {
            delegate.setSchema(schema);
        }

        @Override
        public Schema getSchema() {
            return delegate.getSchema();
        }
    }
}
