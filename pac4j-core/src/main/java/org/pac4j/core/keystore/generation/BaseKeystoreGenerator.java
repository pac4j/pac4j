package org.pac4j.core.keystore.generation;

import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.asn1.x509.V3TBSCertificateGenerator;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.pac4j.core.config.properties.KeystoreProperties;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.chrono.ChronoLocalDateTime;
import java.util.Date;

/**
 * This is {@link BaseKeystoreGenerator}.
 *
 * @author Jérôme LELEU
 * @since 6.4.0
 */
public abstract class BaseKeystoreGenerator implements KeystoreGenerator {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final KeystoreProperties keystore;

    /**
     * <p>Constructor for BaseKeystoreGenerator.</p>
     *
     * @param keystore a {@link KeystoreProperties} object
     */
    public BaseKeystoreGenerator(final KeystoreProperties keystore) {
        this.keystore = keystore;
    }

    /** {@inheritDoc} */
    @Override
    public boolean shouldGenerate() {
        return keystore.isForceKeystoreGeneration();
    }

    /** {@inheritDoc} */
    @Override
    public void generate() {
        try {
            if (StringUtils.isBlank(keystore.getKeyStoreAlias())) {
                keystore.setKeyStoreAlias(getClass().getSimpleName());
                logger.info("Defaulting keystore alias {}", keystore.getKeyStoreAlias());
            }

            if (StringUtils.isBlank(keystore.getKeyStoreType())) {
                keystore.setKeyStoreType(KeyStore.getDefaultType());
                logger.info("Defaulting keystore type {}", keystore.getKeyStoreType());
            }

            validate();

            val ks = KeyStore.getInstance(keystore.getKeyStoreType());
            val password = keystore.getKeystorePassword().toCharArray();
            ks.load(null, password);

            val kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(keystore.getPrivateKeySize());
            val kp = kpg.genKeyPair();

            val sigAlg = keystore.getCertificateSignatureAlg();
            val sigAlgID = new DefaultSignatureAlgorithmIdentifierFinder().find(sigAlg);
            val dn = InetAddress.getLocalHost().getHostName();
            val certificate = createSelfSignedCert(new X500Name("CN=" + dn), sigAlg, sigAlgID, kp);

            val keyPassword = keystore.getPrivateKeyPassword().toCharArray();
            val signingKey = kp.getPrivate();
            ks.setKeyEntry(keystore.getKeyStoreAlias(), signingKey, keyPassword, new Certificate[]{certificate});

            store(ks, certificate, signingKey);
            logger.info("Created keystore {} with key alias {}",
                keystore.getKeystoreResource(),
                ks.aliases().nextElement());
        } catch (final Exception e) {
            throw new TechnicalException("Could not create keystore", e);
        }
    }

    /**
     * <p>store.</p>
     *
     * @param ks a {@link KeyStore} object
     * @param certificate a {@link X509Certificate} object
     * @param privateKey a {@link PrivateKey} object
     * @throws Exception if any.
     */
    protected abstract void store(KeyStore ks, X509Certificate certificate,
                                  PrivateKey privateKey) throws Exception;

    private static Time time(final ChronoLocalDateTime<LocalDate> localDateTime) {
        return new Time(Date.from(localDateTime.toInstant(ZoneOffset.UTC)));
    }

    /**
     * Generate a self-signed certificate for dn using the provided signature algorithm and key pair.
     *
     * @param dn       X.500 name to associate with certificate issuer/subject.
     * @param sigName  name of the signature algorithm to use.
     * @param sigAlgID algorithm ID associated with the signature algorithm name.
     * @param keyPair  the key pair to associate with the certificate.
     * @return an X509Certificate containing the public key in keyPair.
     * @throws Exception
     */
    private X509Certificate createSelfSignedCert(final X500Name dn, final String sigName,
                                                 final AlgorithmIdentifier sigAlgID,
                                                 final KeyPair keyPair) throws Exception {
        val certGen = new V3TBSCertificateGenerator();

        certGen.setSerialNumber(new ASN1Integer(BigInteger.valueOf(1)));
        certGen.setIssuer(dn);
        certGen.setSubject(dn);

        val startDate = LocalDateTime.now(Clock.systemUTC()).minusSeconds(1);
        certGen.setStartDate(time(startDate));

        val endDate = startDate.plus(keystore.getCertificateExpirationPeriod());
        certGen.setEndDate(time(endDate));

        certGen.setSignature(sigAlgID);
        certGen.setSubjectPublicKeyInfo(SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded()));

        val sig = Signature.getInstance(sigName);

        sig.initSign(keyPair.getPrivate());

        sig.update(certGen.generateTBSCertificate().getEncoded(ASN1Encoding.DER));

        val tbsCert = certGen.generateTBSCertificate();

        val v = new ASN1EncodableVector();

        v.add(tbsCert);
        v.add(sigAlgID);
        v.add(new DERBitString(sig.sign()));

        val cert = (X509Certificate) CertificateFactory.getInstance("X.509")
            .generateCertificate(new ByteArrayInputStream(new DERSequence(v).getEncoded(ASN1Encoding.DER)));

        // check the certificate - this will confirm the encoded sig algorithm ID is correct.
        cert.verify(keyPair.getPublic());

        return cert;
    }

    private void validate() {
        CommonHelper.assertNotBlank("keystoreAlias", keystore.getKeyStoreAlias());
        CommonHelper.assertNotBlank("keystoreType", keystore.getKeyStoreType());
        CommonHelper.assertNotBlank("privateKeyPassword", keystore.getPrivateKeyPassword());
    }
}
