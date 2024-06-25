package org.pac4j.saml.metadata.s3;

import com.google.common.net.MediaType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.opensaml.saml.metadata.resolver.impl.AbstractMetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.DOMMetadataResolver;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.metadata.BaseSAML2MetadataGenerator;
import org.pac4j.saml.util.Configuration;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ChecksumAlgorithm;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListBucketsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * This is {@link SAML2S3MetadataGenerator}
 * that stores service provider metadata in AWS S3 Buckets.
 *
 * @author Misagh Moayyed
 * @since 6.0.4
 */
@RequiredArgsConstructor
@Getter
@Setter
@Slf4j
public class SAML2S3MetadataGenerator extends BaseSAML2MetadataGenerator {
    /**
     * Bucket name prefix.
     */
    static final String BUCKET_NAME_PREFIX = "pac4j-saml-metadata";

    private final S3Client s3Client;

    private final String entityId;

    private boolean createBucketIfNecessary = true;

    private ChecksumAlgorithm checksumAlgorithm = ChecksumAlgorithm.CRC32;

    @Override
    public AbstractMetadataResolver createMetadataResolver() throws Exception {
        var bucketName = buildBucketName();
        var result = s3Client.listObjectsV2(ListObjectsV2Request.builder().bucket(bucketName).build());
        if (!result.hasContents()) {
            throw new SAMLException("No metadata bucket with valid contents can be found for " + bucketName);
        }
        var objects = result.contents();
        LOGGER.debug("Located {} S3 object(s) from bucket {}", objects.size(), bucketName);
        if (objects.isEmpty()) {
            throw new SAMLException("No metadata objects could be found in bucket " + bucketName);
        }
        val objectKey = this.entityId;
        LOGGER.debug("Fetching object {} from bucket {}", objectKey, bucketName);
        var object = s3Client.getObject(GetObjectRequest.builder().bucket(bucketName).key(objectKey).build());
        if (object != null) {
            return buildMetadataResolver(object);
        }
        throw new SAMLException("Unable to locate metadata document for key " + objectKey);
    }

    protected AbstractMetadataResolver buildMetadataResolver(final ResponseInputStream<GetObjectResponse> response) throws Exception {
        try (var is = new ByteArrayInputStream(response.readAllBytes())) {
            var document = Configuration.getParserPool().parse(is);
            var root = document.getDocumentElement();
            return new DOMMetadataResolver(root);
        }
    }

    @Override
    public boolean storeMetadata(final String metadata, final boolean force) {
        if (CommonHelper.isBlank(metadata)) {
            logger.info("No metadata is provided");
            return false;
        }

        var metadataToUse = isSignMetadata() ? getMetadataSigner().sign(metadata) : metadata;
        CommonHelper.assertNotBlank("metadata", metadataToUse);

        var entityDescriptor = Configuration.deserializeSamlObject(metadataToUse)
            .map(EntityDescriptor.class::cast)
            .orElseThrow();
        var metadataEntityId = entityDescriptor.getEntityID();
        if (!Objects.requireNonNull(metadataEntityId).equals(this.entityId)) {
            throw new SAMLException("Entity id from metadata " + metadataEntityId
                + " does not match supplied entity id " + this.entityId);
        }
        createMetadataBucketIfNecessary();
        return putMetadataInBucket(entityDescriptor, metadataToUse);
    }

    protected void createMetadataBucketIfNecessary() {
        val bucketNameToUse = buildBucketName();
        if (createBucketIfNecessary && s3Client.listBuckets(ListBucketsRequest.builder().build())
            .buckets().stream().noneMatch(b -> b.name().equalsIgnoreCase(bucketNameToUse))) {
            LOGGER.debug("Bucket {} does not exist. Creating...", bucketNameToUse);
            var bucket = s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketNameToUse).build());
            LOGGER.debug("Created bucket {} with name {}", bucket.location(), bucketNameToUse);
        }
    }

    protected boolean putMetadataInBucket(final EntityDescriptor entityDescriptor, final String metadataToUse) {
        val request = buildPutRequest(entityDescriptor);
        LOGGER.debug("Saving metadata {} in bucket {}", metadataToUse, request.bucket());
        var putResponse = s3Client.putObject(request, RequestBody.fromString(metadataToUse));
        return putResponse != null && putResponse.sdkHttpResponse().isSuccessful();
    }

    protected PutObjectRequest buildPutRequest(final EntityDescriptor entityDescriptor) {
        var bucketMetadata = buildBucketMetadata(entityDescriptor);
        val bucketNameToUse = buildBucketName();
        val builder = PutObjectRequest.builder()
            .key(entityDescriptor.getEntityID())
            .bucket(bucketNameToUse)
            .contentType(MediaType.XML_UTF_8.toString())
            .metadata(bucketMetadata)
            .checksumAlgorithm(this.checksumAlgorithm);
        return customizePutRequest(builder, entityDescriptor);
    }

    protected PutObjectRequest customizePutRequest(final PutObjectRequest.Builder builder,
                                                   final EntityDescriptor entityDescriptor) {
        return builder.build();
    }

    protected Map<String, String> buildBucketMetadata(final EntityDescriptor entityDescriptor) {
        var bucketMetadata = new HashMap<String, String>();
        bucketMetadata.put("entityId", entityDescriptor.getEntityID());
        return bucketMetadata;
    }

    protected String buildBucketName() {
        return BUCKET_NAME_PREFIX;
    }
}
