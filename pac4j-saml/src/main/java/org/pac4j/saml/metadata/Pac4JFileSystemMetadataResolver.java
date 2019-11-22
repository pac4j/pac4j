package org.pac4j.saml.metadata;

import java.io.File;
import java.io.IOException;

import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.metadata.resolver.filter.MetadataFilter;
import org.opensaml.saml.metadata.resolver.impl.FilesystemMetadataResolver;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

public class Pac4JFileSystemMetadataResolver implements Pac4JMetadataResolver {
    protected static final Logger logger = LoggerFactory.getLogger(Pac4JFileSystemMetadataResolver.class);

    private MetadataResolver delegateResolver;
    private Resource metadataResource;

    public Pac4JFileSystemMetadataResolver(Resource metadataResource) throws Exception {
        delegateResolver = new FilesystemMetadataResolver(metadataResource.getFile());
        this.metadataResource = metadataResource;
    }

    public String getId() {
        return delegateResolver.getId();
    }

    public MetadataFilter getMetadataFilter() {
        return delegateResolver.getMetadataFilter();
    }

    public boolean isRequireValidMetadata() {
        return delegateResolver.isRequireValidMetadata();
    }

    public Iterable<EntityDescriptor> resolve(CriteriaSet arg0) throws ResolverException {
        return delegateResolver.resolve(arg0);
    }

    public EntityDescriptor resolveSingle(CriteriaSet arg0) throws ResolverException {
        return delegateResolver.resolveSingle(arg0);
    }

    public void setMetadataFilter(MetadataFilter arg0) {
        delegateResolver.setMetadataFilter(arg0);
    }

    public void setRequireValidMetadata(boolean arg0) {
        delegateResolver.setRequireValidMetadata(arg0);
    }

    @Override
    public void createParentDirectories() throws IOException {
        File resourceFile;
        try {
            resourceFile = metadataResource.getFile();
            if (resourceFile != null) {
                final File parent = resourceFile.getParentFile();
                if (parent != null) {
                    logger.info("Attempting to create directory structure for: {}", parent.getCanonicalPath());
                    if (!parent.exists() && !parent.mkdirs()) {
                        logger.warn("Could not construct the directory structure for SP metadata: {}",
                               parent.getCanonicalPath());
                    }   
                }
            }
        } catch (UnsupportedOperationException e) {
            // do nothing since likely a resource that doesn't have a filesystem
            logger.warn("no filesystem", e);
        }
    }

}
