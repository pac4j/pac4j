package org.pac4j.saml.metadata;

import org.opensaml.saml.metadata.resolver.impl.AbstractBatchMetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.FilesystemMetadataResolver;
import org.springframework.core.io.Resource;

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

}
