/*
  Copyright 2012 - 2015 pac4j organization

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.pac4j.saml.crypto;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import org.opensaml.saml.criterion.RoleDescriptorCriterion;
import org.opensaml.saml.saml2.metadata.SSODescriptor;
import org.opensaml.saml.security.impl.SAMLMetadataSignatureSigningParametersResolver;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.SignatureSigningConfiguration;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.config.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.criterion.SignatureSigningConfigurationCriterion;
import org.opensaml.xmlsec.impl.BasicSignatureSigningConfiguration;
import org.pac4j.saml.exceptions.SAMLException;

import java.util.ArrayList;
import java.util.List;

/**
 * Provide the signature parameters required for signing.
 * @author Misagh Moayyed
 * @since 1.7
 */
public class DefaultSignatureSigningParametersProvider implements SignatureSigningParametersProvider {

    private final CredentialProvider credentialProvider;

    public DefaultSignatureSigningParametersProvider(final CredentialProvider credentialProvider) {
        this.credentialProvider = credentialProvider;
    }

    @Override
    public SignatureSigningParameters build(final SSODescriptor descriptor) {
        try {
            final CriteriaSet criteria = new CriteriaSet();
            criteria.add(new SignatureSigningConfigurationCriterion(
                    getSignatureSigningConfiguration()));
            criteria.add(new RoleDescriptorCriterion(descriptor));
            final SAMLMetadataSignatureSigningParametersResolver resolver =
                    new SAMLMetadataSignatureSigningParametersResolver();

            final SignatureSigningParameters params = resolver.resolveSingle(criteria);
            if (params == null) {
                throw new SAMLException("Could not determine the signature parameters");
            }


            return params;
        } catch (final Exception e) {
            throw new SAMLException(e);
        }
    }

    private SignatureSigningConfiguration getSignatureSigningConfiguration() {
        final BasicSignatureSigningConfiguration config =
                DefaultSecurityConfigurationBootstrap.buildDefaultSignatureSigningConfiguration();
        final List<Credential> creds = new ArrayList<Credential>();
        creds.add(this.credentialProvider.getCredential());
        config.setSigningCredentials(creds);
        return config;
    }
}
