package org.pac4j.saml.metadata;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.shibboleth.shared.resolver.CriteriaSet;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.AbstractMetadataResolver;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.util.Configuration;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

/**
 * A metadata resolver that delegates to an existing one.
 * @author Misagh Moayyed
 * @since 6.1.2
 */
@RequiredArgsConstructor
@Getter
public class SAML2DelegatingMetadataResolver implements SAML2MetadataResolver {
    private final EntityDescriptor entityDescriptorElement;
    private AbstractMetadataResolver metadataResolver;

    @Override
    public MetadataResolver resolve(final boolean force) {
        if (metadataResolver == null) {
            try {
                metadataResolver = new EntityDescriptorMetadataResolver(entityDescriptorElement);
                metadataResolver.initialize();
            } catch (Exception e) {
                throw new SAMLException("Unable to initialize metadata resolver for " + entityDescriptorElement.getEntityID(), e);
            }
        }
        return metadataResolver;
    }

    @Override
    public String getEntityId() {
        return entityDescriptorElement.getEntityID();
    }

    @Override
    public String getMetadata() {
        return Configuration.serializeSamlObject(entityDescriptorElement).toString();
    }

    private static class EntityDescriptorMetadataResolver extends AbstractMetadataResolver {
        private final EntityDescriptor entityDescriptor;

        public EntityDescriptorMetadataResolver(final EntityDescriptor entityDescriptor) {
            this.entityDescriptor = entityDescriptor;
            setId(Objects.requireNonNull(entityDescriptor.getEntityID()));
            setRequireValidMetadata(true);
            setParserPool(Configuration.getParserPool());
            setFailFastInitialization(true);
        }

        @Nonnull
        @Override
        protected Iterable<EntityDescriptor> doResolve(final CriteriaSet criteria) {
            return List.of(entityDescriptor);
        }
    }

}
