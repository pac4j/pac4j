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

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.messaging.context.InOutOperationContext;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.messaging.context.SAMLMetadataContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLSelfEntityContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.pac4j.core.context.WebContext;
import org.pac4j.saml.exceptions.SamlException;
import org.pac4j.saml.transport.SimpleRequestAdapter;
import org.pac4j.saml.transport.SimpleResponseAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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

    protected MetadataResolver metadata;

    protected String idpEntityId;

    protected String spEntityId;

    public Saml2ContextProvider(final MetadataResolver metadata, final String idpEntityId, final String spEntityId) {
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

    protected void addTransportContext(final WebContext webContext, final MessageContext<SAMLObject> context) {

        SimpleRequestAdapter inTransport = new SimpleRequestAdapter(webContext);
        MessageContext<SimpleRequestAdapter> inCtx = new MessageContext<SimpleRequestAdapter>();
        inCtx.setMessage(inTransport);

        SimpleResponseAdapter outTransport = new SimpleResponseAdapter();
        MessageContext<SimpleResponseAdapter> outCtx = new MessageContext<SimpleResponseAdapter>();
        outCtx.setMessage(outTransport);

        context.getSubcontext(InOutOperationContext.class).setInboundMessageContext(inCtx);
        context.getSubcontext(InOutOperationContext.class).setInboundMessageContext(outCtx);
    }

    protected void addSPContext(final MessageContext<SAMLObject> context) {
        SAMLSelfEntityContext selfContext = context.getSubcontext(SAMLSelfEntityContext.class);
        selfContext.setEntityId(this.spEntityId);
        selfContext.setRole(SPSSODescriptor.DEFAULT_ELEMENT_NAME);

        EntityDescriptor entityDescriptor = null;
        RoleDescriptor roleDescriptor = null;
        try {
            CriteriaSet set = new CriteriaSet();
            set.add(new EntityIdCriterion(this.spEntityId));

            entityDescriptor = this.metadata.resolveSingle(set);

            List<RoleDescriptor> list = entityDescriptor.getRoleDescriptors(SPSSODescriptor.DEFAULT_ELEMENT_NAME,
                    SAMLConstants.SAML20P_NS);
            roleDescriptor = !list.isEmpty() ? list.get(0) : null;

        } catch (ResolverException e) {
            throw new SamlException("An error occured while getting SP descriptors", e);
        }

        if (entityDescriptor == null || roleDescriptor == null) {
            throw new SamlException("Cannot find entity " + this.idpEntityId + " or role "
                    + IDPSSODescriptor.DEFAULT_ELEMENT_NAME + " in metadata provider");
        }


        selfContext.getSubcontext(SAMLMetadataContext.class).setEntityDescriptor(entityDescriptor);
        selfContext.getSubcontext(SAMLMetadataContext.class).setRoleDescriptor(roleDescriptor);
    }

    protected void addIDPContext(final MessageContext<SAMLObject> context) {

        SAMLPeerEntityContext peerContext = context.getSubcontext(SAMLPeerEntityContext.class);
        peerContext.setEntityId(this.idpEntityId);
        peerContext.setRole(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);

        EntityDescriptor entityDescriptor = null;
        RoleDescriptor roleDescriptor = null;
        try {
            CriteriaSet set = new CriteriaSet();
            set.add(new EntityIdCriterion(this.idpEntityId));

            entityDescriptor = this.metadata.resolveSingle(set);

            List<RoleDescriptor> list = entityDescriptor.getRoleDescriptors(IDPSSODescriptor.DEFAULT_ELEMENT_NAME,
                    SAMLConstants.SAML20P_NS);
            roleDescriptor = !list.isEmpty() ? list.get(0) : null;

        } catch (ResolverException e) {
            throw new SamlException("An error occured while getting IDP descriptors", e);
        }

        if (entityDescriptor == null || roleDescriptor == null) {
            throw new SamlException("Cannot find entity " + this.idpEntityId + " or role "
                    + IDPSSODescriptor.DEFAULT_ELEMENT_NAME + " in metadata provider");
        }

        peerContext.getSubcontext(SAMLMetadataContext.class).setEntityDescriptor(entityDescriptor);
        peerContext.getSubcontext(SAMLMetadataContext.class).setRoleDescriptor(roleDescriptor);
    }

}
