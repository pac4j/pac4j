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
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.metadata.SAML2MetadataResolver;
import org.pac4j.saml.storage.SAMLMessageStorageFactory;
import org.pac4j.saml.transport.DefaultPac4jSAMLResponse;
import org.pac4j.saml.transport.Pac4jSAMLResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
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

    protected final MetadataResolver metadata;

    protected final SAML2MetadataResolver idpEntityId;

    protected final SAML2MetadataResolver spEntityId;

    protected final SAMLMessageStorageFactory samlMessageStorageFactory;

    public SAML2ContextProvider(final MetadataResolver metadata,
                                final SAML2MetadataResolver idpEntityId,
                                final SAML2MetadataResolver spEntityId,
                                @Nullable final SAMLMessageStorageFactory samlMessageStorageFactory) {
        this.metadata = metadata;
        this.idpEntityId = idpEntityId;
        this.spEntityId = spEntityId;
        this.samlMessageStorageFactory = samlMessageStorageFactory;
    }

    @Override
    public final SAML2MessageContext buildServiceProviderContext(final WebContext webContext) {
        final SAML2MessageContext context = new SAML2MessageContext();
        addTransportContext(webContext, context);
        addSPContext(context);
        return context;
    }

    @Override
    public SAML2MessageContext buildContext(final WebContext webContext) {
        final SAML2MessageContext context = buildServiceProviderContext(webContext);
        addIDPContext(context);
        context.setWebContext(webContext);
        return context;
    }

    protected final void addTransportContext(final WebContext webContext, final SAML2MessageContext context) {
        final ProfileRequestContext profile = context.getProfileRequestContext();
        profile.setOutboundMessageContext(prepareOutboundMessageContext(webContext));
        context.getSAMLProtocolContext().setProtocol(SAMLConstants.SAML20P_NS);

        final ProfileRequestContext request = context.getProfileRequestContext();
        request.setProfileId(SAML2_WEBSSO_PROFILE_URI);

        if (this.samlMessageStorageFactory != null) {
            logger.debug("Creating message storage by {}", this.samlMessageStorageFactory.getClass().getName());
            context.setSAMLMessageStorage(this.samlMessageStorageFactory.getMessageStorage(webContext));
        }
    }

    protected MessageContext<Pac4jSAMLResponse> prepareOutboundMessageContext(final WebContext webContext) {
        final Pac4jSAMLResponse outTransport = new DefaultPac4jSAMLResponse(webContext);
        final MessageContext<Pac4jSAMLResponse> outCtx = new MessageContext<>();
        outCtx.setMessage(outTransport);
        return outCtx;
    }

    protected final void addSPContext(final SAML2MessageContext context) {
        final SAMLSelfEntityContext selfContext = context.getSAMLSelfEntityContext();
        selfContext.setEntityId(this.spEntityId.getEntityId());
        selfContext.setRole(SPSSODescriptor.DEFAULT_ELEMENT_NAME);
        addContext(this.spEntityId, selfContext, SPSSODescriptor.DEFAULT_ELEMENT_NAME);
    }

    protected final void addIDPContext(final SAML2MessageContext context) {
        final SAMLPeerEntityContext peerContext = context.getSAMLPeerEntityContext();
        peerContext.setEntityId(this.idpEntityId.getEntityId());
        peerContext.setRole(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
        addContext(this.idpEntityId, peerContext, IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
    }

    protected final void addContext(final SAML2MetadataResolver entityId, final BaseContext parentContext,
                                    final QName elementName) {
        final EntityDescriptor entityDescriptor;
        final RoleDescriptor roleDescriptor;
        try {
            final CriteriaSet set = new CriteriaSet();
            set.add(new EntityIdCriterion(entityId.getEntityId()));

            entityDescriptor = this.metadata.resolveSingle(set);
            if (entityDescriptor == null) {
                throw new SAMLException("Cannot find entity " + entityId + " in metadata provider");
            }
            final List<RoleDescriptor> list = entityDescriptor.getRoleDescriptors(elementName,
                    SAMLConstants.SAML20P_NS);
            roleDescriptor = CommonHelper.isNotEmpty(list) ? list.get(0) : null;

            if (roleDescriptor == null) {
                throw new SAMLException("Cannot find entity " + entityId + " or role "
                        + elementName + " in metadata provider");
            }

        } catch (final ResolverException e) {
            throw new SAMLException("An error occured while getting IDP descriptors", e);
        }
        final SAMLMetadataContext mdCtx = parentContext.getSubcontext(SAMLMetadataContext.class, true);
        mdCtx.setEntityDescriptor(entityDescriptor);
        mdCtx.setRoleDescriptor(roleDescriptor);
    }
}
