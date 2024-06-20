package org.pac4j.saml.metadata.s3;

import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.pac4j.core.util.TestsConstants;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.exceptions.SAMLException;
import org.pac4j.saml.util.DefaultConfigurationManager;
import org.springframework.core.io.ClassPathResource;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.http.SdkHttpResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.CreateBucketResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.ListBucketsRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.InputStream;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * This is {@link SAML2S3MetadataGeneratorIT}.
 *
 * @author Misagh Moayyed
 * @since 6.0.4
 */
public class SAML2S3MetadataGeneratorIT implements TestsConstants {
    private static final String ENTITY_ID = "org:pac4j:example";

    private String metadata;
    private final SAML2Configuration configuration = new SAML2Configuration();

    @Before
    public void setUp() throws Exception {
        var metadataGenerator = new SAML2S3MetadataGenerator(getClient(), ENTITY_ID);
        var mgr = new DefaultConfigurationManager();
        mgr.configure();

        configuration.setForceKeystoreGeneration(true);
        configuration.setKeystorePath("target/keystore.jks");
        configuration.setKeystorePassword("pac4j");
        configuration.setPrivateKeyPassword("pac4j");
        configuration.setSignMetadata(true);
        configuration.setServiceProviderEntityId(ENTITY_ID);
        configuration.setIdentityProviderMetadataResource(new ClassPathResource("idp-metadata.xml"));
        configuration.setMetadataGenerator(metadataGenerator);
        configuration.init();
    }

    private static S3Client getClient() {
        var client = mock(S3Client.class);
        var response = ListBucketsResponse.builder().build();
        when(client.listBuckets(any(ListBucketsRequest.class))).thenReturn(response);
        when(client.createBucket(any(CreateBucketRequest.class))).thenReturn(CreateBucketResponse.builder().build());

        var sdkResponse = SdkHttpResponse.builder().statusCode(200).build();
        when(client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
            .thenReturn(PutObjectResponse.builder().applyMutation(r -> r.sdkHttpResponse(sdkResponse)).build());

        when(client.listObjectsV2(any(ListObjectsV2Request.class))).thenReturn(
            ListObjectsV2Response.builder()
                .contents(S3Object.builder().key(UUID.randomUUID().toString()).build())
                .build());
        when(client.getObject(any(GetObjectRequest.class))).thenReturn(
            new ResponseInputStream<>(GetObjectResponse.builder().build(), InputStream.nullInputStream()));
        return client;
    }

    @Test
    public void testMetadata() throws Exception {
        var metadataGenerator = configuration.toMetadataGenerator();
        val entity = metadataGenerator.buildEntityDescriptor();
        assertNotNull(entity);
        var metadata = metadataGenerator.getMetadata(entity);
        assertNotNull(metadata);

        assertTrue(metadataGenerator.storeMetadata(metadata, true));
        assertThrows(SAMLException.class, metadataGenerator::buildMetadataResolver);
    }
}
