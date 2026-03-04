package org.pac4j.core.keystore.generation;

import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemObjectGenerator;
import org.bouncycastle.util.io.pem.PemWriter;
import org.pac4j.core.config.properties.KeystoreProperties;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.regex.Pattern;

/**
 * This is {@link FileSystemKeystoreGenerator}.
 *
 * @author Jérôme LELEU
 * @since 6.4.0
 */
public class FileSystemKeystoreGenerator extends BaseKeystoreGenerator {

    private static final Pattern NORMALIZE_PATTERN = Pattern.compile("[^a-zA-Z0-9-_\\.]");

    /**
     * <p>Constructor for FileSystemKeystoreGenerator.</p>
     *
     * @param keystore a {@link KeystoreProperties} object
     */
    public FileSystemKeystoreGenerator(final KeystoreProperties keystore) {
        super(keystore);
    }

    private void writeEncodedCertificateToFile(final File file, final byte[] certificate) {
        if (file.exists()) {
            val res = file.delete();
            logger.debug("Deleted file [{}]:{}", file, res);
        }
        try (var pemWriter = new PemWriter(
            new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            PemObjectGenerator pemObject = new PemObject(file.getName(), certificate);
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
        val keystoreFile = keystore.getKeystoreResource();
        return keystoreFile != null && !keystoreFile.exists() || super.shouldGenerate();
    }

    /** {@inheritDoc} */
    @Override
    public InputStream retrieve() throws Exception {
        validate();
        logger.debug("Retrieving keystore from {}", keystore.getKeystoreResource());
        return keystore.getKeystoreResource().getInputStream();
    }

    private void validate() {
        CommonHelper.assertNotNull("keystoreResource", keystore.getKeystoreResource());
        CommonHelper.assertNotBlank("keystorePassword", keystore.getKeystorePassword());
        CommonHelper.assertNotBlank("certificatePrefix", keystore.getCertificatePrefix());
    }

    /** {@inheritDoc} */
    @Override
    protected void store(final KeyStore ks, final X509Certificate certificate,
                         final PrivateKey privateKey) throws Exception {
        validate();

        val keystoreFile = keystore.getKeystoreResource().getFile();
        val parentFile = keystoreFile.getParentFile();
        if (parentFile != null && !parentFile.exists() && !parentFile.mkdirs()) {
            logger.warn("Could not construct the directory structure for keystore: {}", keystoreFile.getCanonicalPath());
        }
        val password = keystore.getKeystorePassword().toCharArray();
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
        val certName = new StringBuilder(keystore.getCertificatePrefix());
        if (StringUtils.isNotBlank(keystore.getCertificateNameToAppend())) {
            certName.append('-');
            certName.append(NORMALIZE_PATTERN.matcher(keystore.getCertificateNameToAppend())
                .replaceAll(Pac4jConstants.EMPTY_STRING));
        }
        return certName.toString();
    }

    private File getSigningBinaryCertificatePath() throws IOException {
        return new File(keystore.getKeystoreResource().getFile().getParentFile(), getNormalizedCertificateName() + ".crt");
    }

    private File getSigningBase64CertificatePath() throws IOException {
        return new File(keystore.getKeystoreResource().getFile().getParentFile(), getNormalizedCertificateName() + ".pem");
    }

    private File getSigningKeyFile() throws IOException {
        return new File(keystore.getKeystoreResource().getFile().getParentFile(), getNormalizedCertificateName() + ".key");
    }
}
