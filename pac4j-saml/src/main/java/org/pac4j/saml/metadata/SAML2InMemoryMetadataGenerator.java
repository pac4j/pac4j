package org.pac4j.saml.metadata;

import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;
import org.opensaml.saml.metadata.resolver.impl.AbstractMetadataResolver;
import org.springframework.core.io.ByteArrayResource;

import java.nio.charset.StandardCharsets;

/**
 * Generate SAML metadata into a byte array resource.
 *
 * @author Misagh Moayyed
 * @since 6.1.2
 */
@Getter
public class SAML2InMemoryMetadataGenerator extends BaseSAML2MetadataGenerator {
    private ByteArrayResource metadataResource = new ByteArrayResource(ArrayUtils.EMPTY_BYTE_ARRAY);

    @Override
    protected AbstractMetadataResolver createMetadataResolver() throws Exception {
        return null;
    }

    @Override
    public boolean storeMetadata(String metadata, boolean force) throws Exception {
        metadataResource = new ByteArrayResource(metadata.getBytes(StandardCharsets.UTF_8));
        return metadataResource.exists();
    }
}
