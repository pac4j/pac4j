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
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import javax.annotation.Nullable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Misagh Moayyed
 * @since 1.7
 */
public class SAML2IdentityProviderMetadataResolver implements SAML2MetadataResolver {
    private static final long NO_LAST_MODIFIED = -1;

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final Resource idpMetadataResource;

    private String idpEntityId;

    private DOMMetadataResolver idpMetadataProvider;

    private long lastModified = NO_LAST_MODIFIED;

    private ReentrantLock lock = new ReentrantLock();

    public SAML2IdentityProviderMetadataResolver(final SAML2Configuration configuration) {
        this(configuration.getIdentityProviderMetadataResource(), configuration.getIdentityProviderEntityId());
    }

    public SAML2IdentityProviderMetadataResolver(final Resource idpMetadataResource, @Nullable final String idpEntityId) {
        CommonHelper.assertNotNull("identityProviderMetadataResource", idpMetadataResource);
        this.idpMetadataResource = idpMetadataResource;
        this.idpEntityId = idpEntityId;
    }

    public void init() {
        this.idpMetadataProvider = buildMetadata();
        if (idpMetadataResource instanceof FileSystemResource) {
            hasChanged();
        }
    }

    @Override
    public final MetadataResolver resolve() {
        if (idpMetadataResource instanceof FileSystemResource && lock.tryLock()) {
            try {
                if (hasChanged()) {
                    this.idpMetadataProvider = buildMetadata();
                }
            } finally {
                lock.unlock();
            }
        }
        return idpMetadataProvider;
    }

    protected boolean hasChanged() {
        long newLastModified;
        try {
            newLastModified = this.idpMetadataResource.lastModified();
        } catch (final IOException e) {
            newLastModified = NO_LAST_MODIFIED;
        }
        final var hasChanged = lastModified != newLastModified;
        logger.debug("lastModified: {} / newLastModified: {} -> hasChanged: {}", lastModified, newLastModified, hasChanged);
        lastModified = newLastModified;
        return hasChanged;
    }

    protected DOMMetadataResolver buildMetadata() {
        try {
            final DOMMetadataResolver resolver;
            try (var in = this.idpMetadataResource.getInputStream()) {
                final var inCommonMDDoc = Configuration.getParserPool().parse(in);
                final var metadataRoot = inCommonMDDoc.getDocumentElement();
                resolver = new DOMMetadataResolver(metadataRoot);
                resolver.setIndexes(Collections.singleton(new RoleMetadataIndex()));
                resolver.setParserPool(Configuration.getParserPool());
                resolver.setFailFastInitialization(true);
                resolver.setRequireValidMetadata(true);
                resolver.setId(resolver.getClass().getCanonicalName());
                resolver.initialize();
            } catch (final FileNotFoundException e) {
                if (logger.isDebugEnabled()) {
                    logger.error(e.getMessage(), e);
                }
                throw new TechnicalException("Error loading idp Metadata: " + e.getMessage());
            }
            // If no idpEntityId declared, select first EntityDescriptor entityId as our IDP entityId
            if (this.idpEntityId == null) {
                final var it = resolver.iterator();

                while (it.hasNext()) {
                    final var entityDescriptor = it.next();
                    if (this.idpEntityId == null) {
                        this.idpEntityId = entityDescriptor.getEntityID();
                    }
                }
            }

            if (this.idpEntityId == null) {
                throw new SAMLException("No idp entityId found");
            }

            return resolver;

        } catch (final ComponentInitializationException e) {
            throw new SAMLException("Error initializing idpMetadataProvider", e);
        } catch (final XMLParserException e) {
            throw new TechnicalException("Error parsing idp Metadata", e);
        } catch (final IOException e) {
            throw new TechnicalException("Error getting idp Metadata resource", e);
        }
    }

    @Override
    public String getEntityId() {
        final var md = getEntityDescriptorElement();
        if (md instanceof EntitiesDescriptor) {
            return ((EntitiesDescriptor) md).getEntityDescriptors().get(0).getEntityID();
        }
        if (md instanceof EntityDescriptor) {
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
