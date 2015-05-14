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

import org.opensaml.saml.security.impl.MetadataCredentialResolver;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;
import org.opensaml.xmlsec.signature.support.impl.ExplicitKeySignatureTrustEngine;

/**
 * Provider returning well configured {@link SignatureTrustEngine} instances.
 * 
 * @author Michael Remond
 * @since 1.5.0
 */
public class SignatureTrustEngineProvider {

    private final MetadataProvider metadataProvider;

    public SignatureTrustEngineProvider(final MetadataProvider metadataProvider) {
        this.metadataProvider = metadataProvider;
    }

    public SignatureTrustEngine build() {
        MetadataCredentialResolver metadataResolver = new MetadataCredentialResolver(this.metadataProvider);
        return new ExplicitKeySignatureTrustEngine(metadataResolver, Configuration.getGlobalSecurityConfiguration()
                .getDefaultKeyInfoCredentialResolver());
    }
}
