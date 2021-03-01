package org.pac4j.saml.context;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.messaging.context.BaseContext;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.messaging.context.SAMLMetadataContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.context.session.SessionStore;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.client.SAML2Client;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.metadata.SAML2MetadataResolver;
import org.pac4j.saml.store.SAMLMessageStoreFactory;
import org.pac4j.saml.transport.DefaultPac4jSAMLResponse;
import org.pac4j.saml.transport.Pac4jSAMLResponse;
import org.pac4j.saml.util.SAML2Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import javax.xml.namespace.QName;

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

    protected static final Logger logger = LoggerFactory.getLogger(SAML2ContextProvider.class);

    protected final SAML2MetadataResolver idpEntityId;

    protected final SAML2MetadataResolver spEntityId;

    protected final SAMLMessageStoreFactory samlMessageStoreFactory;

    public SAML2ContextProvider(final SAML2MetadataResolver idpEntityId,
                                final SAML2MetadataResolver spEntityId,
                                @Nullable final SAMLMessageStoreFactory samlMessageStoreFactory) {
        this.idpEntityId = idpEntityId;
        this.spEntityId = spEntityId;
        this.samlMessageStoreFactory = samlMessageStoreFactory;
    }

    @Override
    public final SAML2MessageContext buildServiceProviderContext(final SAML2Client client,
                                                                 final WebContext webContext,
                                                                 final SessionStore sessionStore) {
        final var context = new SAML2MessageContext();
        context.setSaml2Configuration(client.getConfiguration());
        addTransportContext(webContext, sessionStore, context);
        addSPContext(context);
        return context;
    }

    @Override
    public SAML2MessageContext buildContext(final SAML2Client client, final WebContext webContext, final SessionStore sessionStore) {
        final var context = buildServiceProviderContext(client, webContext, sessionStore);
        addIDPContext(context);
        context.setWebContext(webContext);
        context.setSessionStore(sessionStore);
        return context;
    }

    protected final void addTransportContext(final WebContext webContext, final SessionStore sessionStore,
                                             final SAML2MessageContext context) {
        final var profile = context.getProfileRequestContext();
        profile.setOutboundMessageContext(prepareOutboundMessageContext(webContext));
        context.getSAMLProtocolContext().setProtocol(SAMLConstants.SAML20P_NS);

        final var request = context.getProfileRequestContext();
        request.setProfileId(SAML2_WEBSSO_PROFILE_URI);

        if (this.samlMessageStoreFactory != null) {
            logger.debug("Creating message store by {}", this.samlMessageStoreFactory.getClass().getName());
            context.setSAMLMessageStore(this.samlMessageStoreFactory.getMessageStore(webContext, sessionStore));
        }
    }

    protected MessageContext prepareOutboundMessageContext(final WebContext webContext) {
        final Pac4jSAMLResponse outTransport = new DefaultPac4jSAMLResponse(webContext);
        final var outCtx = new MessageContext();
        outCtx.setMessage(outTransport);
        return outCtx;
    }

    protected final void addSPContext(final SAML2MessageContext context) {
        final var selfContext = context.getSAMLSelfEntityContext();
        selfContext.setEntityId(this.spEntityId.getEntityId());
        selfContext.setRole(SPSSODescriptor.DEFAULT_ELEMENT_NAME);
        addContext(this.spEntityId, selfContext, SPSSODescriptor.DEFAULT_ELEMENT_NAME);
    }

    protected final void addIDPContext(final SAML2MessageContext context) {
        final var peerContext = context.getSAMLPeerEntityContext();
        peerContext.setEntityId(this.idpEntityId.getEntityId());
        peerContext.setRole(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
        addContext(this.idpEntityId, peerContext, IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
    }

    protected final void addContext(final SAML2MetadataResolver metadata, final BaseContext parentContext,
                                    final QName elementName) {
        final EntityDescriptor entityDescriptor;
        final RoleDescriptor roleDescriptor;
        try {
            final var set = new CriteriaSet();
            final var entityId = metadata.getEntityId();
            set.add(new EntityIdCriterion(entityId));

            entityDescriptor = SAML2Utils.buildChainingMetadataResolver(this.idpEntityId, this.spEntityId).resolveSingle(set);
            if (entityDescriptor == null) {
                throw new SAMLException("Cannot find entity " + entityId + " in metadata provider");
            }
            final var list = entityDescriptor.getRoleDescriptors(elementName,
                    SAMLConstants.SAML20P_NS);
            roleDescriptor = CommonHelper.isNotEmpty(list) ? list.get(0) : null;

            if (roleDescriptor == null) {
                throw new SAMLException("Cannot find entity " + entityId + " or role "
                        + elementName + " in metadata provider");
            }

        } catch (final ResolverException e) {
            throw new SAMLException("An error occurred while getting IDP descriptors", e);
        }
        final var mdCtx = parentContext.getSubcontext(SAMLMetadataContext.class, true);
        mdCtx.setEntityDescriptor(entityDescriptor);
        mdCtx.setRoleDescriptor(roleDescriptor);
    }
}
