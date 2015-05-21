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
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

/**
 * @author Misagh Moayyed
 * @since 1.7
 */
public class SAML2IdentityProviderMetadataResolver implements SAML2MetadataResolver {

    private final String idpMetadataPath;
    private String idpEntityId;
    private DOMMetadataResolver idpMetadataProvider;

    public SAML2IdentityProviderMetadataResolver(final String idpMetadataPath,
                                                 @Nullable final String idpEntityId) {
        this.idpMetadataPath = idpMetadataPath;
        this.idpEntityId = idpEntityId;
    }

    @Override
    public MetadataResolver resolve() {

        try {
            Resource resource;
            if (this.idpMetadataPath.startsWith(CommonHelper.RESOURCE_PREFIX)) {
                String path = this.idpMetadataPath.substring(CommonHelper.RESOURCE_PREFIX.length());
                if (!path.startsWith("/")) {
                    path = "/" + path;
                }
                resource = ResourceHelper.of(new ClassPathResource(path));
            } else {
                resource = ResourceHelper.of(new FileSystemResource(this.idpMetadataPath));
            }
            final InputStream in = resource.getInputStream();
            final Document inCommonMDDoc = Configuration.getParserPool().parse(in);
            final Element metadataRoot = inCommonMDDoc.getDocumentElement();
            idpMetadataProvider = new DOMMetadataResolver(metadataRoot);

            idpMetadataProvider.setParserPool(Configuration.getParserPool());
            idpMetadataProvider.setFailFastInitialization(true);
            idpMetadataProvider.setRequireValidMetadata(true);
            idpMetadataProvider.setId(idpMetadataProvider.getClass().getCanonicalName());
            idpMetadataProvider.initialize();


            // If no idpEntityId declared, select first EntityDescriptor entityId as our IDP entityId
            if (this.idpEntityId == null) {
                idpMetadataProvider.forEach(new Consumer<EntityDescriptor>() {
                    @Override
                    public void accept(final EntityDescriptor entityDescriptor) {
                        if (SAML2IdentityProviderMetadataResolver.this.idpEntityId == null) {
                            SAML2IdentityProviderMetadataResolver.this.idpEntityId = entityDescriptor.getEntityID();
                        }
                    }
                });
            }

            if (this.idpEntityId == null) {
                throw new SAMLException("No idp entityId found");
            }

        } catch (ComponentInitializationException e) {
            throw new SAMLException("Error initializing idpMetadataProvider", e);
        } catch (XMLParserException e) {
            throw new TechnicalException("Error parsing idp Metadata", e);
        } catch (IOException e) {
            throw new TechnicalException("Error getting idp Metadata resource", e);
        }
        return idpMetadataProvider;
    }

    @Override
    public String getEntityId() {
        final XMLObject md = getEntityDescriptorElement();
        if (md instanceof EntitiesDescriptor) {
            for (EntityDescriptor entity : ((EntitiesDescriptor) md).getEntityDescriptors()) {
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
        return XMLHelper.nodeToString(getEntityDescriptorElement().getDOM());
    }

    @Override
    public XMLObject getEntityDescriptorElement() {
        try {
            return resolve().resolveSingle(new CriteriaSet(new EntityIdCriterion(this.idpEntityId)));
        } catch (ResolverException e) {
            throw new SAMLException("Error initializing idpMetadataProvider", e);
        }
    }

}
