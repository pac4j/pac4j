/*
  Copyright 2012 -2014 Michael Remond

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

import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.BasicRoleDescriptorResolver;
import org.opensaml.saml.security.impl.MetadataCredentialResolver;
import org.opensaml.xmlsec.config.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;
import org.opensaml.xmlsec.signature.support.impl.ExplicitKeySignatureTrustEngine;

/**
 * Provider returning well configured {@link SignatureTrustEngine} instances.
 * 
 * @author Michael Remond
 * @since 1.5.0
 */
public class SignatureTrustEngineProvider {

    private final MetadataResolver metadataResolver;

    public SignatureTrustEngineProvider(final MetadataResolver metadataResolver) {
        this.metadataResolver = metadataResolver;
    }

    public SignatureTrustEngine build() {
        MetadataCredentialResolver metadataCredentialResolver = new MetadataCredentialResolver();
        BasicRoleDescriptorResolver roleResolver = new BasicRoleDescriptorResolver(metadataResolver);

        KeyInfoCredentialResolver keyResolver =
                DefaultSecurityConfigurationBootstrap.buildBasicInlineKeyInfoCredentialResolver();

        metadataCredentialResolver.setKeyInfoCredentialResolver(keyResolver);
        metadataCredentialResolver.setRoleDescriptorResolver(roleResolver);

        return new ExplicitKeySignatureTrustEngine(metadataCredentialResolver, keyResolver);
    }
}
