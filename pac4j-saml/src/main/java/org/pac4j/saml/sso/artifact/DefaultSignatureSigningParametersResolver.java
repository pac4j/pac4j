package org.pac4j.saml.sso.artifact;


import lombok.val;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;
import org.opensaml.saml.criterion.RoleDescriptorCriterion;
import org.opensaml.saml.saml2.metadata.SSODescriptor;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.SignatureSigningParametersResolver;
import org.pac4j.saml.crypto.SignatureSigningParametersProvider;

import java.util.Collections;

/**
 * A {@link SignatureSigningParametersResolver} that resolves the
 * {@link SignatureSigningParameters} from the pac4j
 * {@link SignatureSigningParametersProvider}.
 *
 * @since 3.8.0
 */
public class DefaultSignatureSigningParametersResolver implements SignatureSigningParametersResolver {
    private SignatureSigningParametersProvider provider;

    public DefaultSignatureSigningParametersResolver(final SignatureSigningParametersProvider provider) {
        this.provider = provider;
    }

    @Override
    public Iterable<SignatureSigningParameters> resolve(final CriteriaSet criteria) throws ResolverException {
        val ret = resolveSingle(criteria);
        return ret == null ? Collections.emptySet() : Collections.singleton(ret);
    }

    @Override
    public SignatureSigningParameters resolveSingle(final CriteriaSet criteria) throws ResolverException {
        if (criteria == null) {
            throw new ResolverException("CriteriaSet was null");
        }
        val role = criteria.get(RoleDescriptorCriterion.class);
        if (role == null) {
            throw new ResolverException("No RoleDescriptorCriterion specified");
        }
        return provider.build((SSODescriptor) role.getRole());
    }
}
