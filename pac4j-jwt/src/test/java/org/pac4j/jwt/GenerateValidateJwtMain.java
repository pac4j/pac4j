package org.pac4j.jwt;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.val;
import org.pac4j.core.credentials.TokenCredentials;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.jwt.config.signature.RSASignatureConfiguration;
import org.pac4j.jwt.credentials.authenticator.JwtAuthenticator;
import org.pac4j.jwt.util.JWKHelper;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

public class GenerateValidateJwtMain {

    private static final String JWT = Pac4jConstants.EMPTY_STRING;
    private static final String JWK = Pac4jConstants.EMPTY_STRING;

    public static void main(String... args) throws Exception {
        generate();
        validate();
    }

    public static void generate() throws Exception {
        val keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        final KeyPair pair = keyGen.generateKeyPair();
        var config = new RSASignatureConfiguration();
        var pub = (RSAPublicKey) pair.getPublic();
        var priv = (RSAPrivateKey) pair.getPrivate();
        config.setPublicKey(pub);
        config.setPrivateKey(priv);
        var claims = new JWTClaimsSet.Builder().subject("me").build();
        val signedJWT = config.sign(claims);
        System.out.println(signedJWT.serialize());
        System.out.println("verified: " + config.verify(signedJWT));
        JWK jwk = new RSAKey.Builder(pub)
            .privateKey(priv)
            .keyUse(KeyUse.SIGNATURE)
            .keyID("thekey")
            .build();
        //System.out.println(jwk);
        System.out.println(jwk.toPublicJWK());
    }

    private static void validate() throws Exception {
        var pair = JWKHelper.buildRSAKeyPairFromJwk(JWK);
        var config = new RSASignatureConfiguration();
        var pub = (RSAPublicKey) pair.getPublic();
        config.setPublicKey(pub);

        var authenticator = new JwtAuthenticator(config);
        authenticator.validate(null, new TokenCredentials(JWT));
        System.out.println("verified");
    }
}
