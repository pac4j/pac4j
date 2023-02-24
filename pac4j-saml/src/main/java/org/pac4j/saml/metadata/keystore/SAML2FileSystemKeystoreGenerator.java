package org.pac4j.saml.metadata.keystore;

import lombok.val;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.saml.config.SAML2Configuration;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.regex.Pattern;

/**
 * This is {@link org.pac4j.saml.metadata.keystore.SAML2FileSystemKeystoreGenerator}.
 *
 * @author Misagh Moayyed
 * @since 4.0.1
 */
public class SAML2FileSystemKeystoreGenerator extends BaseSAML2KeystoreGenerator {
    private static final Pattern NORMALIZE_PATTERN = Pattern.compile("[^a-zA-Z0-9-_\\.]");

    /**
     * <p>Constructor for SAML2FileSystemKeystoreGenerator.</p>
     *
     * @param configuration a {@link org.pac4j.saml.config.SAML2Configuration} object
     */
    public SAML2FileSystemKeystoreGenerator(final SAML2Configuration configuration) {
        super(configuration);
    }

    private void writeEncodedCertificateToFile(final File file, final byte[] certificate) {
        if (file.exists()) {
            val res = file.delete();
            logger.debug("Deleted file [{}]:{}", file, res);
        }
        try (var pemWriter = new PemWriter(
            new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            val pemObject = new PemObject(file.getName(), certificate);
            pemWriter.writeObject(pemObject);
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void writeBinaryCertificateToFile(final File file, final byte[] certificate) {
        if (file.exists()) {
            val res = file.delete();
            logger.debug("Deleted file [{}]:{}", file, res);
        }
        try (OutputStream fos = new FileOutputStream(file)) {
            fos.write(certificate);
            fos.flush();
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public boolean shouldGenerate() {
        validate();
        val keystoreFile = saml2Configuration.getKeystoreResource();
        return keystoreFile != null && !keystoreFile.exists() || super.shouldGenerate();
    }

    /** {@inheritDoc} */
    @Override
    public InputStream retrieve() throws Exception {
        validate();
        logger.debug("Retrieving keystore from {}", saml2Configuration.getKeystoreResource());
        return saml2Configuration.getKeystoreResource().getInputStream();
    }

    private void validate() {
        CommonHelper.assertNotNull("keystoreResource", saml2Configuration.getKeystoreResource());
        CommonHelper.assertNotBlank("keystorePassword", saml2Configuration.getKeystorePassword());
    }

    /** {@inheritDoc} */
    @Override
    protected void store(final KeyStore ks, final X509Certificate certificate,
                         final PrivateKey privateKey) throws Exception {
        validate();

        val keystoreFile = saml2Configuration.getKeystoreResource().getFile();
        val parentFile = keystoreFile.getParentFile();
        if (parentFile != null && !parentFile.exists() && !parentFile.mkdirs()) {
            logger.warn("Could not construct the directory structure for keystore: {}", keystoreFile.getCanonicalPath());
        }
        val password = saml2Configuration.getKeystorePassword().toCharArray();
        try (OutputStream fos = new FileOutputStream(keystoreFile.getCanonicalPath())) {
            ks.store(fos, password);
            fos.flush();
        }

        val signingCertEncoded = getSigningBase64CertificatePath();
        writeEncodedCertificateToFile(signingCertEncoded, certificate.getEncoded());

        val signingCertBinary = getSigningBinaryCertificatePath();
        writeBinaryCertificateToFile(signingCertBinary, certificate.getEncoded());

        val signingKeyEncoded = getSigningKeyFile();
        writeEncodedCertificateToFile(signingKeyEncoded, privateKey.getEncoded());
    }

    /**
     * Sanitize String to use it as fileName for Signing Certificate Names
     */
    private String getNormalizedCertificateName() {
        val certName = new StringBuilder(CERTIFICATES_PREFIX);
        if (CommonHelper.isNotBlank(saml2Configuration.getCertificateNameToAppend())) {
            certName.append('-');
            certName.append(NORMALIZE_PATTERN.matcher(saml2Configuration.getCertificateNameToAppend())
                .replaceAll(Pac4jConstants.EMPTY_STRING));
        }
        return certName.toString();
    }

    private File getSigningBinaryCertificatePath() throws IOException {
        return new File(saml2Configuration.getKeystoreResource().getFile().getParentFile(), getNormalizedCertificateName() + ".crt");
    }

    private File getSigningBase64CertificatePath() throws IOException {
        return new File(saml2Configuration.getKeystoreResource().getFile().getParentFile(), getNormalizedCertificateName() + ".pem");
    }

    private File getSigningKeyFile() throws IOException {
        return new File(saml2Configuration.getKeystoreResource().getFile().getParentFile(), getNormalizedCertificateName() + ".key");
    }
}
