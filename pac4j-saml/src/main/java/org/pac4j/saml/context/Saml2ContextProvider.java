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
import org.opensaml.messaging.context.BaseContext;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.profile.context.ProfileRequestContext;
import org.opensaml.saml.common.messaging.context.SAMLMetadataContext;
import org.opensaml.saml.common.messaging.context.SAMLPeerEntityContext;
import org.opensaml.saml.common.messaging.context.SAMLSelfEntityContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.metadata.SAML2MetadataResolver;
import org.pac4j.saml.transport.SimpleRequestAdapter;
import org.pac4j.saml.transport.SimpleResponseAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.namespace.QName;
import java.util.List;

/**
 * Responsible for building a {@link SAML2MessageContext} from given SAML2 properties (idpEntityId and metadata
 * manager) and current {@link WebContext}.
 * 
 * @author Michael Remond
 * @author Misagh Moayyed
 * @since 1.7
 */
@SuppressWarnings("rawtypes")
public class SAML2ContextProvider implements SAMLContextProvider {
    private static final String SAML2_WEBSSO_PROFILE_URI = "urn:oasis:names:tc:SAML:2.0:profiles:SSO:browser";

    protected final static Logger logger = LoggerFactory.getLogger(SAML2ContextProvider.class);

    protected MetadataResolver metadata;

    protected SAML2MetadataResolver idpEntityId;

    protected SAML2MetadataResolver spEntityId;

    public SAML2ContextProvider(final MetadataResolver metadata,
                                final SAML2MetadataResolver idpEntityId,
                                final SAML2MetadataResolver spEntityId) {
        this.metadata = metadata;
        this.idpEntityId = idpEntityId;
        this.spEntityId = spEntityId;
    }

    @Override
    public SAML2MessageContext buildServiceProviderContext(final WebContext webContext) {
        final SAML2MessageContext context = new SAML2MessageContext();
        context.setMetadataProvider(this.metadata);
        addTransportContext(webContext, context);
        addSPContext(context);
        return context;
    }

    @Override
    public SAML2MessageContext buildContext(final WebContext webContext) {
        final SAML2MessageContext context = buildServiceProviderContext(webContext);
        addIDPContext(context);
        return context;
    }

    protected void addTransportContext(final WebContext webContext, final SAML2MessageContext context) {

        final J2EContext j2EContext = (J2EContext) webContext;
        final SimpleRequestAdapter inTransport = new SimpleRequestAdapter(j2EContext);
        final MessageContext<SimpleRequestAdapter> inCtx = new MessageContext<SimpleRequestAdapter>();
        inCtx.setMessage(inTransport);

        final SimpleResponseAdapter outTransport = new SimpleResponseAdapter(j2EContext);
        final MessageContext<SimpleResponseAdapter> outCtx = new MessageContext<SimpleResponseAdapter>();
        outCtx.setMessage(outTransport);

        final ProfileRequestContext profile = context.getProfileRequestContext();
        profile.setInboundMessageContext(inCtx);
        profile.setOutboundMessageContext(outCtx);
        context.getSAMLProtocolContext().setProtocol(SAMLConstants.SAML20P_NS);

        final ProfileRequestContext request = context.getProfileRequestContext();
        request.setProfileId(SAML2_WEBSSO_PROFILE_URI);
    }

    protected void addSPContext(final SAML2MessageContext context) {
        final SAMLSelfEntityContext selfContext = context.getSAMLSelfEntityContext();
        selfContext.setEntityId(this.spEntityId.getEntityId());
        selfContext.setRole(SPSSODescriptor.DEFAULT_ELEMENT_NAME);
        addContext(this.spEntityId, selfContext, SPSSODescriptor.DEFAULT_ELEMENT_NAME);
    }

    protected void addIDPContext(final SAML2MessageContext context) {
        final SAMLPeerEntityContext peerContext = context.getSAMLPeerEntityContext();
        peerContext.setEntityId(this.idpEntityId.getEntityId());
        peerContext.setRole(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
        addContext(this.idpEntityId, peerContext, IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
    }

    protected void addContext(final SAML2MetadataResolver entityId, final BaseContext parentContext,
                              final QName elementName) {
        EntityDescriptor entityDescriptor;
        RoleDescriptor roleDescriptor;
        try {
            final CriteriaSet set = new CriteriaSet();
            set.add(new EntityIdCriterion(entityId.getEntityId()));

            entityDescriptor = this.metadata.resolveSingle(set);
            if (entityDescriptor == null) {
                throw new SAMLException("Cannot find entity " + entityId + " in metadata provider");
            }
            final List<RoleDescriptor> list = entityDescriptor.getRoleDescriptors(elementName,
                    SAMLConstants.SAML20P_NS);
            roleDescriptor = (list != null && !list.isEmpty()) ? list.get(0) : null;

            if (roleDescriptor == null) {
                throw new SAMLException("Cannot find entity " + entityId + " or role "
                        + elementName + " in metadata provider");
            }

        } catch (ResolverException e) {
            throw new SAMLException("An error occured while getting IDP descriptors", e);
        }
        final SAMLMetadataContext mdCtx = parentContext.getSubcontext(SAMLMetadataContext.class, true);
        mdCtx.setEntityDescriptor(entityDescriptor);
        mdCtx.setRoleDescriptor(roleDescriptor);
    }
}
