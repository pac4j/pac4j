package org.pac4j.saml.context;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import net.shibboleth.shared.resolver.CriteriaSet;
import net.shibboleth.shared.resolver.ResolverException;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.messaging.context.BaseContext;
import org.opensaml.messaging.context.MessageContext;
import org.opensaml.saml.common.messaging.context.SAMLMetadataContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.RoleDescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.pac4j.core.context.CallContext;
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

import javax.xml.namespace.QName;

/**
 * Responsible for building a {@link org.pac4j.saml.context.SAML2MessageContext} from given SAML2 properties (idpEntityId and metadata
 * manager) and current {@link org.pac4j.core.context.WebContext}.
 *
 * @author Michael Remond
 * @author Misagh Moayyed
 * @since 1.7
 */
@SuppressWarnings("rawtypes")
@Slf4j
public class SAML2ContextProvider implements SAMLContextProvider {
    private static final String SAML2_WEBSSO_PROFILE_URI = "urn:oasis:names:tc:SAML:2.0:profiles:SSO:browser";

    protected final SAML2MetadataResolver idpEntityId;

    protected final SAML2MetadataResolver spEntityId;

    protected final SAMLMessageStoreFactory samlMessageStoreFactory;

    /**
     * <p>Constructor for SAML2ContextProvider.</p>
     *
     * @param idpEntityId a {@link org.pac4j.saml.metadata.SAML2MetadataResolver} object
     * @param spEntityId a {@link org.pac4j.saml.metadata.SAML2MetadataResolver} object
     * @param samlMessageStoreFactory a {@link org.pac4j.saml.store.SAMLMessageStoreFactory} object
     */
    public SAML2ContextProvider(final SAML2MetadataResolver idpEntityId,
                                final SAML2MetadataResolver spEntityId,
                                final SAMLMessageStoreFactory samlMessageStoreFactory) {
        this.idpEntityId = idpEntityId;
        this.spEntityId = spEntityId;
        this.samlMessageStoreFactory = samlMessageStoreFactory;
    }

    /** {@inheritDoc} */
    @Override
    public final SAML2MessageContext buildServiceProviderContext(final CallContext ctx, final SAML2Client client) {
        val context = new SAML2MessageContext(ctx);
        context.setSaml2Configuration(client.getConfiguration());
        addTransportContext(ctx.webContext(), ctx.sessionStore(), context);
        addSPContext(context);
        return context;
    }

    /** {@inheritDoc} */
    @Override
    public SAML2MessageContext buildContext(final CallContext ctx, final SAML2Client client) {
        val context = buildServiceProviderContext(ctx, client);
        addIDPContext(context);
        return context;
    }

    /**
     * <p>addTransportContext.</p>
     *
     * @param webContext a {@link org.pac4j.core.context.WebContext} object
     * @param sessionStore a {@link org.pac4j.core.context.session.SessionStore} object
     * @param context a {@link org.pac4j.saml.context.SAML2MessageContext} object
     */
    protected final void addTransportContext(final WebContext webContext, final SessionStore sessionStore,
                                             final SAML2MessageContext context) {
        val profile = context.getProfileRequestContext();
        profile.setOutboundMessageContext(prepareOutboundMessageContext(webContext));
        context.getSAMLProtocolContext().setProtocol(SAMLConstants.SAML20P_NS);

        val request = context.getProfileRequestContext();
        request.setProfileId(SAML2_WEBSSO_PROFILE_URI);

        if (this.samlMessageStoreFactory != null) {
            LOGGER.debug("Creating message store by {}", this.samlMessageStoreFactory.getClass().getName());
            context.setSamlMessageStore(this.samlMessageStoreFactory.getMessageStore(webContext, sessionStore));
        }
    }

    /**
     * <p>prepareOutboundMessageContext.</p>
     *
     * @param webContext a {@link org.pac4j.core.context.WebContext} object
     * @return a {@link org.opensaml.messaging.context.MessageContext} object
     */
    protected MessageContext prepareOutboundMessageContext(final WebContext webContext) {
        final Pac4jSAMLResponse outTransport = new DefaultPac4jSAMLResponse(webContext);
        val outCtx = new MessageContext();
        outCtx.setMessage(outTransport);
        return outCtx;
    }

    /**
     * <p>addSPContext.</p>
     *
     * @param context a {@link org.pac4j.saml.context.SAML2MessageContext} object
     */
    protected final void addSPContext(final SAML2MessageContext context) {
        val selfContext = context.getSAMLSelfEntityContext();
        selfContext.setEntityId(this.spEntityId.getEntityId());
        selfContext.setRole(SPSSODescriptor.DEFAULT_ELEMENT_NAME);
        addContext(this.spEntityId, selfContext, SPSSODescriptor.DEFAULT_ELEMENT_NAME);
    }

    /**
     * <p>addIDPContext.</p>
     *
     * @param context a {@link org.pac4j.saml.context.SAML2MessageContext} object
     */
    protected final void addIDPContext(final SAML2MessageContext context) {
        val peerContext = context.getSAMLPeerEntityContext();
        peerContext.setEntityId(this.idpEntityId.getEntityId());
        peerContext.setRole(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
        addContext(this.idpEntityId, peerContext, IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
    }

    /**
     * <p>addContext.</p>
     *
     * @param metadata a {@link org.pac4j.saml.metadata.SAML2MetadataResolver} object
     * @param parentContext a {@link org.opensaml.messaging.context.BaseContext} object
     * @param elementName a {@link javax.xml.namespace.QName} object
     */
    protected final void addContext(final SAML2MetadataResolver metadata, final BaseContext parentContext,
                                    final QName elementName) {
        final EntityDescriptor entityDescriptor;
        final RoleDescriptor roleDescriptor;
        try {
            val set = new CriteriaSet();
            val entityId = metadata.getEntityId();
            set.add(new EntityIdCriterion(entityId));

            entityDescriptor = SAML2Utils.buildChainingMetadataResolver(this.idpEntityId, this.spEntityId).resolveSingle(set);
            if (entityDescriptor == null) {
                throw new SAMLException("Cannot find entity " + entityId + " in metadata provider");
            }
            val list = entityDescriptor.getRoleDescriptors(elementName,
                    SAMLConstants.SAML20P_NS);
            roleDescriptor = CommonHelper.isNotEmpty(list) ? list.get(0) : null;

            if (roleDescriptor == null) {
                throw new SAMLException("Cannot find entity " + entityId + " or role "
                        + elementName + " in metadata provider");
            }

        } catch (final ResolverException e) {
            throw new SAMLException("An error occurred while getting IDP descriptors", e);
        }
        val mdCtx = parentContext.getSubcontext(SAMLMetadataContext.class, true);
        mdCtx.setEntityDescriptor(entityDescriptor);
        mdCtx.setRoleDescriptor(roleDescriptor);
    }
}
