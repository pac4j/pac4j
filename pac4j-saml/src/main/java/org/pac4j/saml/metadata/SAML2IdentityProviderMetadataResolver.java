package org.pac4j.saml.metadata;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.annotation.Nullable;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.DOMMetadataResolver;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.io.Resource;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.client.SAML2ClientConfiguration;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.util.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import net.shibboleth.utilities.java.support.xml.SerializeSupport;
import net.shibboleth.utilities.java.support.xml.XMLParserException;

/**
 * @author Misagh Moayyed
 * @since 1.7
 */
public class SAML2IdentityProviderMetadataResolver implements SAML2MetadataResolver {
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected final static String HTTP_PREFIX = "http";
	protected final static String FILE_PREFIX = "file:";

    private final Resource idpMetadataResource;
	private String idpEntityId;
	private DOMMetadataResolver idpMetadataProvider;

	public SAML2IdentityProviderMetadataResolver(final String idpMetadataPath, @Nullable final String idpEntityId) {
		this(null, idpMetadataPath, idpEntityId);
	}

	public SAML2IdentityProviderMetadataResolver(final SAML2ClientConfiguration configuration,
			final String idpEntityId) {
		this(configuration.getIdentityProviderMetadataResource(), configuration.getIdentityProviderMetadataPath(),
				idpEntityId);
	}

	public SAML2IdentityProviderMetadataResolver(final Resource idpMetadataResource, final String idpMetadataPath,
			@Nullable final String idpEntityId) {
		CommonHelper.assertTrue(idpMetadataResource != null || CommonHelper.isNotBlank(idpMetadataPath),
				"Either IdpMetadataResource or idpMetadataPath must be provided");
		if (idpMetadataResource != null) {
			this.idpMetadataResource = idpMetadataResource;
		} else {
			this.idpMetadataResource = CommonHelper.getResource(idpMetadataPath);
		}
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

            try (final InputStream in = this.idpMetadataResource.getInputStream()) {
				final Document inCommonMDDoc = Configuration.getParserPool().parse(in);
				final Element metadataRoot = inCommonMDDoc.getDocumentElement();
				idpMetadataProvider = new DOMMetadataResolver(metadataRoot);
				idpMetadataProvider.setParserPool(Configuration.getParserPool());
				idpMetadataProvider.setFailFastInitialization(true);
				idpMetadataProvider.setRequireValidMetadata(true);
				idpMetadataProvider.setId(idpMetadataProvider.getClass().getCanonicalName());
				idpMetadataProvider.initialize();
			} catch (final FileNotFoundException e) {
				throw new TechnicalException("Error loading idp Metadata. The path must be a " + "valid https url, "
						+ CommonHelper.INVALID_PATH_MESSAGE, e);
			}

            // If no idpEntityId declared, select first EntityDescriptor entityId as our IDP entityId
			if (this.idpEntityId == null) {
				final Iterator<EntityDescriptor> it = idpMetadataProvider.iterator();

				while (it.hasNext()) {
					final EntityDescriptor entityDescriptor = it.next();
					if (SAML2IdentityProviderMetadataResolver.this.idpEntityId == null) {
						SAML2IdentityProviderMetadataResolver.this.idpEntityId = entityDescriptor.getEntityID();
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
			for (final EntityDescriptor entity : ((EntitiesDescriptor) md).getEntityDescriptors()) {
				return entity.getEntityID();
			}
		} else if (md instanceof EntityDescriptor) {
			return ((EntityDescriptor) md).getEntityID();
		}
		throw new SAMLException("No idp entityId found");
	}

	@Override
	public String getMetadataPath() {
        return idpMetadataResource.getFilename();
	}

	@Override
	public String getMetadata() {
        if (getEntityDescriptorElement() != null
                && getEntityDescriptorElement().getDOM() != null) {
            return SerializeSupport.nodeToString(getEntityDescriptorElement().getDOM());
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
