/*
  Copyright 2012 -2014 pac4j organization

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
package org.pac4j.saml.metadata;

import net.shibboleth.ext.spring.resource.ResourceHelper;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import net.shibboleth.utilities.java.support.resource.Resource;
import net.shibboleth.utilities.java.support.xml.XMLParserException;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.DOMMetadataResolver;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.xml.util.XMLHelper;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.util.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.UrlResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.annotation.Nullable;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;


/**
 * @author Misagh Moayyed
 * @since 1.7
 */
public class SAML2IdentityProviderMetadataResolver implements SAML2MetadataResolver {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final static String HTTP_PREFIX = "http";
    protected final static String FILE_PREFIX = "file:";

    private final String idpMetadataPath;
    private String idpEntityId;
    private DOMMetadataResolver idpMetadataProvider;

    public SAML2IdentityProviderMetadataResolver(final String idpMetadataPath,
                                                 @Nullable final String idpEntityId) {
        this.idpMetadataPath = idpMetadataPath;
        this.idpEntityId = idpEntityId;
    }

    @Override
    public  final MetadataResolver resolve() {
    	
    	// No locks are used since saml2client's init does in turn invoke resolve and idpMetadataProvider is set.
    	// idpMetadataProvider is initialized by Saml2Client::internalInit->MetadataResolver::initIdentityProviderMetadataResolve->resolve
    	// Usage of locks will adversly impact performance.
    	if(idpMetadataProvider != null) {
    		return idpMetadataProvider;
    	}
    	
        try {
            Resource resource = null;
            if (this.idpMetadataPath.startsWith(CommonHelper.RESOURCE_PREFIX)) {
                String path = this.idpMetadataPath.substring(CommonHelper.RESOURCE_PREFIX.length());
                if (!path.startsWith("/")) {
                    path = "/" + path;
                }
                resource = ResourceHelper.of(new ClassPathResource(path));
            }  else if (this.idpMetadataPath.startsWith(HTTP_PREFIX)) {
                final UrlResource urlResource = new UrlResource(this.idpMetadataPath);
                if (urlResource.getURL().getProtocol().equalsIgnoreCase(HTTP_PREFIX)) {
                    logger.warn("IdP metadata is retrieved from an insecure http endpoint [{}]",
                            urlResource.getURL());
                }
                resource = ResourceHelper.of(urlResource);
            // for backward compatibility
            } else if (this.idpMetadataPath.startsWith(FILE_PREFIX)) {
                resource = ResourceHelper.of(new FileSystemResource(this.idpMetadataPath.substring(FILE_PREFIX.length())));
            } else {
                resource = ResourceHelper.of(new FileSystemResource(this.idpMetadataPath));
            }

            if (resource == null) {
                throw new XMLParserException("idp metadata cannot be resolved from " + this.idpMetadataPath);
            }

            try (final InputStream in = resource.getInputStream()) {
                final Document inCommonMDDoc = Configuration.getParserPool().parse(in);
                final Element metadataRoot = inCommonMDDoc.getDocumentElement();
                idpMetadataProvider = new DOMMetadataResolver(metadataRoot);
                idpMetadataProvider.setParserPool(Configuration.getParserPool());
                idpMetadataProvider.setFailFastInitialization(true);
                idpMetadataProvider.setRequireValidMetadata(true);
                idpMetadataProvider.setId(idpMetadataProvider.getClass().getCanonicalName());
                idpMetadataProvider.initialize();
            } catch (final FileNotFoundException e) {
                throw new TechnicalException("Error loading idp Metadata. The path must be a "
                        + "valid https url, begin with '" + CommonHelper.RESOURCE_PREFIX
                        + "' or it must be a physical readable non-empty local file "
                        + "at the path specified.", e);
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
        return idpMetadataPath;
    }

    @Override
    public String getMetadata() {
        if (getEntityDescriptorElement() != null
                && getEntityDescriptorElement().getDOM() != null) {
            return XMLHelper.nodeToString(getEntityDescriptorElement().getDOM());
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
