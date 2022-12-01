package org.pac4j.saml.metadata;

import lombok.val;
import org.opensaml.saml.metadata.resolver.impl.AbstractMetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.FilesystemMetadataResolver;
import org.pac4j.core.util.CommonHelper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

/**
 * Generates metadata object with standard values and overridden user defined values.
 *
 * @author Misagh Moayyed
 * @since 4.0.1
 */
public class SAML2FileSystemMetadataGenerator extends BaseSAML2MetadataGenerator {

    private final Resource metadataResource;

    public SAML2FileSystemMetadataGenerator(Resource metadataResource) {
        this.metadataResource = metadataResource;
    }

    @Override
    protected AbstractMetadataResolver createMetadataResolver() throws Exception {
        return new FilesystemMetadataResolver(metadataResource.getFile());
    }

    @Override
    public boolean storeMetadata(final String metadata, final boolean force) throws Exception {
        if (metadataResource == null || CommonHelper.isBlank(metadata)) {
            logger.info("No metadata or resource is provided");
            return false;
        }
        if (!(metadataResource instanceof WritableResource)) {
            logger.warn("Unable to store metadata, as resource is not writable");
            return false;
        }

        if (metadataResource.exists() && !force) {
            logger.info("Metadata file already exists at {}.", metadataResource.getFile());
        } else {
            logger.info("Writing metadata to {}", metadataResource.getFilename());
            val parent = metadataResource.getFile().getParentFile();
            if (parent != null) {
                logger.debug("Attempting to create directory structure for: {}", parent.getCanonicalPath());
                if (!parent.exists() && !parent.mkdirs()) {
                    logger.warn("Could not construct the directory structure for metadata: {}", parent.getCanonicalPath());
                }
            }
            val transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            val result = new StreamResult(new StringWriter());
            val source = new StreamSource(new StringReader(metadata));
            transformer.transform(source, result);

            val destination = WritableResource.class.cast(metadataResource);
            try (var spMetadataOutputStream = destination.getOutputStream()) {
                spMetadataOutputStream.write(result.getWriter().toString().getBytes(StandardCharsets.UTF_8));
            }
            if (destination.exists()) {
                if (isSignMetadata()) {
                    getMetadataSigner().sign(metadataResource.getFile());
                }
                return true;
            }
        }
        return false;
    }
}
