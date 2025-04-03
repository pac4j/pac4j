package org.pac4j.saml.metadata;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.saml.metadata.resolver.impl.AbstractBatchMetadataResolver;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.pac4j.saml.util.Configuration;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

@Slf4j
@Getter
public class SAML2EntityDescriptorMetadataResolver extends AbstractBatchMetadataResolver {
    private final EntityDescriptor entityDescriptor;

    public SAML2EntityDescriptorMetadataResolver(final EntityDescriptor entityDescriptor) {
        this.entityDescriptor = entityDescriptor;
        setId(Objects.requireNonNull(entityDescriptor.getEntityID()));
        setRequireValidMetadata(true);
        setParserPool(Configuration.getParserPool());
        setFailFastInitialization(true);
    }

    @Nonnull
    @Override
    protected Iterable<EntityDescriptor> doResolve(final CriteriaSet criteria) throws ResolverException {
        var entityIdCriterion = criteria != null ? criteria.get(EntityIdCriterion.class) : null;
        if (entityIdCriterion == null || entityIdCriterion.getEntityId().equals(entityDescriptor.getEntityID())) {
            return List.of(entityDescriptor);
        }
        return List.of();
    }
}
