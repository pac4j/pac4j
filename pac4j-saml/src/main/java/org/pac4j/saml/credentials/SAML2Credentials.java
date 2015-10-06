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

package org.pac4j.saml.credentials;

import java.util.List;

import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.NameID;
import org.pac4j.core.credentials.Credentials;

/**
 * Credentials containing the nameId of the SAML subject and all of its attributes.
 * 
 * @author Michael Remond
 * @since 1.5.0
 */
public class SAML2Credentials extends Credentials {

    private static final long serialVersionUID = 5040516205957826527L;

    private NameID nameId;

    private List<Attribute> attributes;
    
    private Conditions conditions;

    public SAML2Credentials(final NameID nameId, final List<Attribute> attributes, final Conditions conditions,
                            final String clientName) {
        this.nameId = nameId;
        this.attributes = attributes;
        this.conditions = conditions;
        setClientName(clientName);
    }

    public final NameID getNameId() {
        return this.nameId;
    }

    public final List<Attribute> getAttributes() {
        return this.attributes;
    }
    
    public Conditions getConditions() {
        return this.conditions;
    }

    @Override
    public final String toString() {
        return "SAMLCredential [nameId=" + this.nameId + ", attributes=" + this.attributes + "]";
    }

    @Override
    public void clear() {
        this.conditions = null;
        this.nameId = null;
        this.attributes = null;
        this.setClientName(null);
    }
}
