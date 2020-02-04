package org.pac4j.saml.metadata;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import net.shibboleth.utilities.java.support.xml.XMLParserException;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.DOMMetadataResolver;
import org.opensaml.saml.metadata.resolver.index.impl.RoleMetadataIndex;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.util.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.annotation.Nullable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Iterator;

/**
 * @author Misagh Moayyed
 * @since 1.7
 */
public class SAML2IdentityProviderMetadataResolver implements SAML2MetadataResolver {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final Resource idpMetadataResource;
    private String idpEntityId;
    private DOMMetadataResolver idpMetadataProvider;

    public SAML2IdentityProviderMetadataResolver(final SAML2Configuration configuration) {
        this(configuration.getIdentityProviderMetadataResource(), configuration.getIdentityProviderEntityId());
    }

    public SAML2IdentityProviderMetadataResolver(final Resource idpMetadataResource, @Nullable final String idpEntityId) {
        CommonHelper.assertNotNull("idpMetadataResource", idpMetadataResource);
        this.idpMetadataResource = idpMetadataResource;
        this.idpEntityId = idpEntityId;
    }

    @Override
    public final MetadataResolver resolve() {

        // No locks are used since saml2client's init does in turn invoke resolve and idpMetadataProvider is set.
        // idpMetadataProvider is initialized by Saml2Client::internalInit->MetadataResolver::initIdentityProviderMetadataResolve->resolve
        // Usage of locks will adversly impact performance.
        if (idpMetadataProvider != null) {
            return idpMetadataProvider;
        }

        try {

            if (this.idpMetadataResource == null) {
                throw new XMLParserException("idp metadata cannot be resolved from " + this.idpMetadataResource);
            }

            try (InputStream in = this.idpMetadataResource.getInputStream()) {
                final Document inCommonMDDoc = Configuration.getParserPool().parse(in);
                final Element metadataRoot = inCommonMDDoc.getDocumentElement();
                idpMetadataProvider = new DOMMetadataResolver(metadataRoot);
                idpMetadataProvider.setIndexes(Collections.singleton(new RoleMetadataIndex()));
                idpMetadataProvider.setParserPool(Configuration.getParserPool());
                idpMetadataProvider.setFailFastInitialization(true);
                idpMetadataProvider.setRequireValidMetadata(true);
                idpMetadataProvider.setId(idpMetadataProvider.getClass().getCanonicalName());
                idpMetadataProvider.initialize();
            } catch (final FileNotFoundException e) {
                throw new TechnicalException("Error loading idp Metadata");
            }
            // If no idpEntityId declared, select first EntityDescriptor entityId as our IDP entityId
            if (this.idpEntityId == null) {
                final Iterator<EntityDescriptor> it = idpMetadataProvider.iterator();

                while (it.hasNext()) {
                    final EntityDescriptor entityDescriptor = it.next();
                    if (this.idpEntityId == null) {
                        this.idpEntityId = entityDescriptor.getEntityID();
                    }
                }
            }

            if (this.idpEntityId == null) {
                throw new SAMLException("No idp entityId found");
            }

        } catch (final ComponentInitializationException e) {
            throw new SAMLException("Error initializing idpMetadataProvider", e);
        } catch (final XMLParserException e) {
            throw new TechnicalException("Error parsing idp Metadata", e);
        } catch (final IOException e) {
            throw new TechnicalException("Error getting idp Metadata resource", e);
        }
        return idpMetadataProvider;
    }

    @Override
    public String getEntityId() {
        final XMLObject md = getEntityDescriptorElement();
        if (md instanceof EntitiesDescriptor) {
            return ((EntitiesDescriptor) md).getEntityDescriptors().get(0).getEntityID();
        } else if (md instanceof EntityDescriptor) {
            return ((EntityDescriptor) md).getEntityID();
        }
        throw new SAMLException("No idp entityId found");
    }

    @Override
    public String getMetadata() {
        if (getEntityDescriptorElement() != null) {
            return Configuration.serializeSamlObject(getEntityDescriptorElement()).toString();
        }
        throw new TechnicalException("Metadata cannot be retrieved because entity descriptor is null");
    }

    @Override
    public final XMLObject getEntityDescriptorElement() {
        try {
            return resolve().resolveSingle(new CriteriaSet(new EntityIdCriterion(this.idpEntityId)));
        } catch (final ResolverException e) {
            throw new SAMLException("Error initializing idpMetadataProvider", e);
        }
    }
}
