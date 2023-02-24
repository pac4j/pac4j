package org.pac4j.saml.metadata.keystore;

import lombok.val;
import org.bouncycastle.asn1.*;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.asn1.x509.V3TBSCertificateGenerator;
import org.bouncycastle.operator.DefaultSignatureAlgorithmIdentifierFinder;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.exceptions.SAMLException;
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
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * This is {@link org.pac4j.saml.metadata.keystore.BaseSAML2KeystoreGenerator}.
 *
 * @author Misagh Moayyed
 * @since 4.0.1
 */
public abstract class BaseSAML2KeystoreGenerator implements SAML2KeystoreGenerator {
    /** Constant <code>CERTIFICATES_PREFIX="saml-signing-cert"</code> */
    protected static final String CERTIFICATES_PREFIX = "saml-signing-cert";

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final SAML2Configuration saml2Configuration;

    /**
     * <p>Constructor for BaseSAML2KeystoreGenerator.</p>
     *
     * @param saml2Configuration a {@link org.pac4j.saml.config.SAML2Configuration} object
     */
    public BaseSAML2KeystoreGenerator(final SAML2Configuration saml2Configuration) {
        this.saml2Configuration = saml2Configuration;
    }

    /** {@inheritDoc} */
    @Override
    public boolean shouldGenerate() {
        return saml2Configuration.isForceKeystoreGeneration();
    }

    /** {@inheritDoc} */
    @Override
    public void generate() {
        try {
            if (CommonHelper.isBlank(saml2Configuration.getKeyStoreAlias())) {
                saml2Configuration.setKeyStoreAlias(getClass().getSimpleName());
                logger.warn("Defaulting keystore alias {}", saml2Configuration.getKeyStoreAlias());
            }

            if (CommonHelper.isBlank(saml2Configuration.getKeyStoreType())) {
                saml2Configuration.setKeyStoreType(KeyStore.getDefaultType());
                logger.warn("Defaulting keystore type {}", saml2Configuration.getKeyStoreType());
            }

            validate();

            val ks = KeyStore.getInstance(saml2Configuration.getKeyStoreType());
            val password = saml2Configuration.getKeystorePassword().toCharArray();
            ks.load(null, password);

            val kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(saml2Configuration.getPrivateKeySize());
            val kp = kpg.genKeyPair();

            val sigAlg = saml2Configuration.getCertificateSignatureAlg();
            val sigAlgID = new DefaultSignatureAlgorithmIdentifierFinder().find(sigAlg);
            val dn = InetAddress.getLocalHost().getHostName();
            val certificate = createSelfSignedCert(new X500Name("CN=" + dn), sigAlg, sigAlgID, kp);

            val keyPassword = saml2Configuration.getPrivateKeyPassword().toCharArray();
            val signingKey = kp.getPrivate();
            ks.setKeyEntry(saml2Configuration.getKeyStoreAlias(), signingKey, keyPassword, new Certificate[]{certificate});

            store(ks, certificate, signingKey);
            logger.info("Created keystore {} with key alias {}",
                saml2Configuration.getKeystoreResource(),
                ks.aliases().nextElement());
        } catch (final Exception e) {
            throw new SAMLException("Could not create keystore", e);
        }
    }

    /**
     * <p>store.</p>
     *
     * @param ks a {@link java.security.KeyStore} object
     * @param certificate a {@link java.security.cert.X509Certificate} object
     * @param privateKey a {@link java.security.PrivateKey} object
     * @throws java.lang.Exception if any.
     */
    protected abstract void store(KeyStore ks, X509Certificate certificate,
                                  PrivateKey privateKey) throws Exception;

    private static Time time(final LocalDateTime localDateTime) {
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

        val endDate = startDate.plus(saml2Configuration.getCertificateExpirationPeriod());
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
        CommonHelper.assertNotBlank("keystoreAlias", saml2Configuration.getKeyStoreAlias());
        CommonHelper.assertNotBlank("keystoreType", saml2Configuration.getKeyStoreType());
        CommonHelper.assertNotBlank("privateKeyPassword", saml2Configuration.getPrivateKeyPassword());
    }
}
