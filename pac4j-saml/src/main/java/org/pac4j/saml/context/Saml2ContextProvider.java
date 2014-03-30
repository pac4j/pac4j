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

package org.pac4j.saml.context;

import org.opensaml.common.binding.BasicSAMLMessageContext;
import org.opensaml.common.xml.SAMLConstants;
import org.opensaml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml2.metadata.RoleDescriptor;
import org.opensaml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml2.metadata.provider.MetadataProvider;
import org.opensaml.saml2.metadata.provider.MetadataProviderException;
import org.pac4j.core.context.WebContext;
import org.pac4j.saml.exceptions.SamlException;
import org.pac4j.saml.transport.SimpleRequestAdapter;
import org.pac4j.saml.transport.SimpleResponseAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Responsible for building a {@link ExtendedSAMLMessageContext} from given SAML2 properties (idpEntityId and metadata
 * manager) and current {@link WebContext}.
 * 
 * @author Michael Remond
 * @since 1.5.0
 */
@SuppressWarnings("rawtypes")
public class Saml2ContextProvider {

    protected final static Logger logger = LoggerFactory.getLogger(Saml2ContextProvider.class);

    protected MetadataProvider metadata;

    protected String idpEntityId;

    protected String spEntityId;

    public Saml2ContextProvider(final MetadataProvider metadata, final String idpEntityId, final String spEntityId) {
        this.metadata = metadata;
        this.idpEntityId = idpEntityId;
        this.spEntityId = spEntityId;
    }

    public ExtendedSAMLMessageContext buildSpContext(final WebContext webContext) {

        ExtendedSAMLMessageContext context = new ExtendedSAMLMessageContext();
        context.setMetadataProvider(this.metadata);
        addTransportContext(webContext, context);
        addSPContext(context);

        return context;
    }

    public ExtendedSAMLMessageContext buildSpAndIdpContext(final WebContext webContext) {

        ExtendedSAMLMessageContext context = new ExtendedSAMLMessageContext();
        context.setMetadataProvider(this.metadata);
        addTransportContext(webContext, context);
        addSPContext(context);
        addIDPContext(context);

        return context;
    }

    protected void addTransportContext(final WebContext webContext, final BasicSAMLMessageContext context) {

        SimpleRequestAdapter inTransport = new SimpleRequestAdapter(webContext);
        SimpleResponseAdapter outTransport = new SimpleResponseAdapter();

        context.setInboundMessageTransport(inTransport);
        context.setOutboundMessageTransport(outTransport);
    }

    protected void addSPContext(final BasicSAMLMessageContext context) {
        context.setLocalEntityId(this.spEntityId);
        context.setLocalEntityRole(SPSSODescriptor.DEFAULT_ELEMENT_NAME);

        EntityDescriptor entityDescriptor = null;
        RoleDescriptor roleDescriptor = null;
        try {
            entityDescriptor = this.metadata.getEntityDescriptor(this.spEntityId);
            roleDescriptor = this.metadata.getRole(this.spEntityId, SPSSODescriptor.DEFAULT_ELEMENT_NAME,
                    SAMLConstants.SAML20P_NS);
        } catch (MetadataProviderException e) {
            throw new SamlException("An error occured while getting SP descriptors", e);
        }

        if (entityDescriptor == null || roleDescriptor == null) {
            throw new SamlException("Cannot find entity " + this.spEntityId + " or role "
                    + SPSSODescriptor.DEFAULT_ELEMENT_NAME + " in metadata provider");
        }

        context.setLocalEntityMetadata(entityDescriptor);
        context.setLocalEntityRoleMetadata(roleDescriptor);
    }

    protected void addIDPContext(final BasicSAMLMessageContext context) {

        context.setPeerEntityId(this.idpEntityId);
        context.setPeerEntityRole(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);

        EntityDescriptor entityDescriptor = null;
        RoleDescriptor roleDescriptor = null;
        try {
            entityDescriptor = this.metadata.getEntityDescriptor(this.idpEntityId);
            roleDescriptor = this.metadata.getRole(this.idpEntityId, IDPSSODescriptor.DEFAULT_ELEMENT_NAME,
                    SAMLConstants.SAML20P_NS);
        } catch (MetadataProviderException e) {
            throw new SamlException("An error occured while getting IDP descriptors", e);
        }

        if (entityDescriptor == null || roleDescriptor == null) {
            throw new SamlException("Cannot find entity " + this.idpEntityId + " or role "
                    + IDPSSODescriptor.DEFAULT_ELEMENT_NAME + " in metadata provider");
        }

        context.setPeerEntityMetadata(entityDescriptor);
        context.setPeerEntityRoleMetadata(roleDescriptor);
    }

}
