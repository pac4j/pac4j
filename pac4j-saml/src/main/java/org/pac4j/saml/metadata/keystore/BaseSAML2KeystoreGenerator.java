package org.pac4j.saml.metadata.keystore;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Encoding;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERNull;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.TBSCertificate;
import org.bouncycastle.asn1.x509.Time;
import org.bouncycastle.asn1.x509.V3TBSCertificateGenerator;
import org.joda.time.DateTime;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.saml.config.SAML2Configuration;
import org.pac4j.saml.exceptions.SAMLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.net.InetAddress;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;

/**
 * This is {@link BaseSAML2KeystoreGenerator}.
 *
 * @author Misagh Moayyed
 * @since 4.0.1
 */
public abstract class BaseSAML2KeystoreGenerator implements SAML2KeystoreGenerator {
    protected static final String CERTIFICATES_PREFIX = "saml-signing-cert";

    protected final Logger logger = LoggerFactory.getLogger(BaseSAML2KeystoreGenerator.class);

    protected final SAML2Configuration saml2Configuration;

    public BaseSAML2KeystoreGenerator(final SAML2Configuration saml2Configuration) {
        this.saml2Configuration = saml2Configuration;
    }

    @Override
    public boolean shouldGenerate() {
        return saml2Configuration.isForceKeystoreGeneration();
    }

    @Override
    public void generate() {
        try {
            if (CommonHelper.isBlank(saml2Configuration.getKeyStoreAlias())) {
                saml2Configuration.setKeystoreAlias(getClass().getSimpleName());
                logger.warn("Defaulting keystore alias {}", saml2Configuration.getKeyStoreAlias());
            }

            if (CommonHelper.isBlank(saml2Configuration.getKeyStoreType())) {
                saml2Configuration.setKeystoreType(KeyStore.getDefaultType());
                logger.warn("Defaulting keystore type {}", saml2Configuration.getKeyStoreType());
            }

            final KeyStore ks = KeyStore.getInstance(saml2Configuration.getKeyStoreType());
            final char[] password = saml2Configuration.getKeystorePassword().toCharArray();
            ks.load(null, password);

            final KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(saml2Configuration.getPrivateKeySize());
            final KeyPair kp = kpg.genKeyPair();

            final AlgorithmIdentifier sigAlgID = new AlgorithmIdentifier(PKCSObjectIdentifiers.sha1WithRSAEncryption, DERNull.INSTANCE);
            final String dn = InetAddress.getLocalHost().getHostName();
            final X509Certificate certificate = createSelfSignedCert(new X500Name("CN=" + dn),
                saml2Configuration.getCertificateSignatureAlg(), sigAlgID, kp);

            final char[] keyPassword = saml2Configuration.getPrivateKeyPassword().toCharArray();
            final PrivateKey signingKey = kp.getPrivate();
            ks.setKeyEntry(saml2Configuration.getKeyStoreAlias(), signingKey, keyPassword, new Certificate[]{certificate});

            store(ks, certificate, signingKey);
            logger.info("Created keystore {} with key alias {}",
                saml2Configuration.getKeystoreResource(),
                ks.aliases().nextElement());
        } catch (final Exception e) {
            throw new SAMLException("Could not create keystore", e);
        }
    }

    protected abstract void store(KeyStore ks, X509Certificate certificate,
                                  PrivateKey privateKey) throws Exception;

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
        final V3TBSCertificateGenerator certGen = new V3TBSCertificateGenerator();

        certGen.setSerialNumber(new ASN1Integer(BigInteger.valueOf(1)));
        certGen.setIssuer(dn);
        certGen.setSubject(dn);

        certGen.setStartDate(new Time(new Date(System.currentTimeMillis() - 1000L)));

        final Date expiration = DateTime.now().plusDays(
            saml2Configuration.getCertificateExpirationPeriod().getDays()).toDate();
        certGen.setEndDate(new Time(expiration));

        certGen.setSignature(sigAlgID);
        certGen.setSubjectPublicKeyInfo(SubjectPublicKeyInfo.getInstance(keyPair.getPublic().getEncoded()));

        final Signature sig = Signature.getInstance(sigName);

        sig.initSign(keyPair.getPrivate());

        sig.update(certGen.generateTBSCertificate().getEncoded(ASN1Encoding.DER));

        final TBSCertificate tbsCert = certGen.generateTBSCertificate();

        final ASN1EncodableVector v = new ASN1EncodableVector();

        v.add(tbsCert);
        v.add(sigAlgID);
        v.add(new DERBitString(sig.sign()));

        final X509Certificate cert = (X509Certificate) CertificateFactory.getInstance("X.509")
            .generateCertificate(new ByteArrayInputStream(new DERSequence(v).getEncoded(ASN1Encoding.DER)));

        // check the certificate - this will confirm the encoded sig algorithm ID is correct.
        cert.verify(keyPair.getPublic());

        return cert;
    }
}
