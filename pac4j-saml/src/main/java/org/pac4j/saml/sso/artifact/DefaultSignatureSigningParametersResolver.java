package org.pac4j.saml.sso.artifact;

import java.util.Collections;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import org.opensaml.saml.criterion.RoleDescriptorCriterion;
import org.opensaml.saml.saml2.metadata.SSODescriptor;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.SignatureSigningParametersResolver;
import org.pac4j.saml.crypto.SignatureSigningParametersProvider;

public class DefaultSignatureSigningParametersResolver implements SignatureSigningParametersResolver {
    private SignatureSigningParametersProvider provider;

    public DefaultSignatureSigningParametersResolver(SignatureSigningParametersProvider provider) {
        this.provider = provider;
    }

    @Override
    @Nonnull
    public Iterable<SignatureSigningParameters> resolve(@Nullable CriteriaSet criteria) throws ResolverException {
        SignatureSigningParameters ret = resolveSingle(criteria);
        return ret == null ? Collections.emptySet() : Collections.singleton(ret);
    }

    @Override
    @Nullable
    public SignatureSigningParameters resolveSingle(@Nullable CriteriaSet criteria) throws ResolverException {
        if (criteria == null) {
            throw new ResolverException("CriteriaSet was null");
        }
        RoleDescriptorCriterion role = criteria.get(RoleDescriptorCriterion.class);
        if (role == null) {
            throw new ResolverException("No RoleDescriptorCriterion specified");
        }
        return provider.build((SSODescriptor) role.getRole());
    }
}
