package org.pac4j.saml.sso.artifact;

import java.util.Collections;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.Resolver;
import org.pac4j.saml.metadata.SAML2MetadataResolver;

/**
 * A resolver for the entity id specified by the given
 * {@link SAML2MetadataResolver}.
 * 
 * @since 3.8.0
 */
public class FixedEntityIdResolver implements Resolver<String, CriteriaSet> {
    private SAML2MetadataResolver metadataResolver;

    public FixedEntityIdResolver(SAML2MetadataResolver metadataResolver) {
        this.metadataResolver = metadataResolver;
    }

    @Override
    @Nonnull
    public Iterable<String> resolve(@Nullable CriteriaSet criteria) {
        return Collections.singletonList(metadataResolver.getEntityId());
    }

    @Override
    @Nullable
    public String resolveSingle(@Nullable CriteriaSet criteria) {
        return metadataResolver.getEntityId();
    }
}
