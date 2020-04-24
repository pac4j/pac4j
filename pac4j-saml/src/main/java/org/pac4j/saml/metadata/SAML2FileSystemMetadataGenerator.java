package org.pac4j.saml.metadata;

import org.opensaml.saml.metadata.resolver.impl.AbstractBatchMetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.FilesystemMetadataResolver;
import org.pac4j.core.util.CommonHelper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

/**
 * Generates metadata object with standard values and overriden user defined values.
 *
 * @author Michael Remond
 * @since 1.5.0
 */
public class SAML2FileSystemMetadataGenerator extends BaseSAML2MetadataGenerator {

    @Override
    protected AbstractBatchMetadataResolver createMetadataResolver(final Resource metadataResource) throws Exception {
        return new FilesystemMetadataResolver(metadataResource.getFile());
    }

    @Override
    public void storeMetadata(final String metadata, final WritableResource metadataResource, final boolean force) throws Exception {
        if (metadataResource == null || CommonHelper.isBlank(metadata)) {
            logger.info("No metadata or resource is provided");
            return;
        }

        if (metadataResource.exists() && !force) {
            logger.info("Metadata file already exists at {}.", metadataResource.getFile());
        } else {
            logger.info("Writing metadata to {}", metadataResource.getFilename());
            final File parent = metadataResource.getFile().getParentFile();
            if (parent != null) {
                logger.debug("Attempting to create directory structure for: {}", parent.getCanonicalPath());
                if (!parent.exists() && !parent.mkdirs()) {
                    logger.warn("Could not construct the directory structure for metadata: {}", parent.getCanonicalPath());
                }
            }
            final Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            final StreamResult result = new StreamResult(new StringWriter());
            final StreamSource source = new StreamSource(new StringReader(metadata));
            transformer.transform(source, result);
            try (OutputStream spMetadataOutputStream = metadataResource.getOutputStream()) {
                spMetadataOutputStream.write(result.getWriter().toString().getBytes(StandardCharsets.UTF_8));
            }
        }
    }
}
