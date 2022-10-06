package org.pac4j.saml.sso.artifact;

import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.Resolver;
import org.pac4j.saml.metadata.SAML2MetadataResolver;

import java.util.Collections;

/**
 * A resolver for the entity id specified by the given
 * {@link SAML2MetadataResolver}.
 *
 * @since 3.8.0
 */
public class FixedEntityIdResolver implements Resolver<String, CriteriaSet> {
    private SAML2MetadataResolver metadataResolver;

    public FixedEntityIdResolver(final SAML2MetadataResolver metadataResolver) {
        this.metadataResolver = metadataResolver;
    }

    @Override
    public Iterable<String> resolve(final CriteriaSet criteria) {
        return Collections.singletonList(metadataResolver.getEntityId());
    }

    @Override
    public String resolveSingle(final CriteriaSet criteria) {
        return metadataResolver.getEntityId();
    }
}
