package org.pac4j.oidc.run;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.pac4j.core.client.IndirectClient;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileHelper;
import org.pac4j.core.run.RunClient;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.core.util.Pac4jConstants;
import org.pac4j.oidc.client.AppleClient;
import org.pac4j.oidc.config.AppleOidcConfiguration;
import org.pac4j.oidc.profile.apple.AppleProfile;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Objects;

import static org.junit.Assert.*;
import static org.pac4j.core.util.Pac4jConstants.DEFAULT_CLIENT_NAME_PARAMETER;

/**
 * Run a manual test for the {@link AppleClient}.
 *
 * @author Charley Wu
 * @since 5.0.0
 */
public class RunAppleClient extends RunClient {

    public static void main(final String[] args){ new RunAppleClient().run(); }

    @Override
    protected String getLogin() {
        return "YOUR_APPLE_ID";
    }

    @Override
    protected String getPassword() {
        return "YOUR_PASSWORD";
    }

    @Override
    protected IndirectClient getClient() {
        final AppleOidcConfiguration configuration = new AppleOidcConfiguration();
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(Objects.requireNonNull(classLoader.getResource("apple.pem")).getFile());
        ECPrivateKey privateKey = readPrivateKey(file);
        assert privateKey != null;
        configuration.setClientId("org.pac4j.test");
        configuration.setTeamID("67D9XQG2LJ");
        configuration.setPrivateKeyID("VB4MYGJ3TQ");
        configuration.setPrivateKey(privateKey);
        configuration.setResponseType("code id_token");
        configuration.setResponseMode("form_post");
        configuration.setScope("openid name email");
        configuration.setUseNonce(true); // Required in the implicit and hybrid flows
        final AppleClient client = new AppleClient(configuration);
        // MUST begin with https://
        client.setCallbackUrl(CommonHelper.addParameter("https://www.pac4j.org/test.html",
            DEFAULT_CLIENT_NAME_PARAMETER, client.getName()));
        logger.warn("Please copy/paste the form post data instead of query string after signed in");
        return client;
    }

    @Override
    protected void verifyProfile(CommonProfile userProfile) {
        final AppleProfile profile = (AppleProfile) userProfile;
        final String id = profile.getId();
        assertNotNull(id);
        assertEquals(AppleProfile.class.getName() + Pac4jConstants.TYPED_ID_SEPARATOR + id,
            profile.getTypedId());
        assertTrue(ProfileHelper.isTypedIdOf(profile.getTypedId(), AppleProfile.class));
        assertNotNull(profile.getAccessToken());
        assertNotNull(profile.getIdToken());
        assertNotNull(profile.getRefreshToken());
        assertNotNull(profile.getIdTokenString());
        assertNotNull(profile.getEmail());
        assertTrue(profile.getEmailVerified());
        assertEquals("https://appleid.apple.com", profile.getIssuer());
        assertEquals(1, profile.getAudience().size());
        assertNotNull(profile.getExpirationDate());
        assertNotNull(profile.getIssuedAt());
        assertNotNull(profile.getAttribute("auth_time"));
    }

    private ECPrivateKey readPrivateKey(File file) {
        KeyFactory keyFactory;
        try {
            keyFactory = KeyFactory.getInstance("EC");
        } catch (NoSuchAlgorithmException e) {
            throw new TechnicalException(e);
        }

        try (FileReader keyReader = new FileReader(file, StandardCharsets.UTF_8);
             PemReader pemReader = new PemReader(keyReader)) {
            PemObject pemObject = pemReader.readPemObject();
            byte[] content = pemObject.getContent();
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(content);
            return (ECPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (InvalidKeySpecException | IOException e) {
            throw new TechnicalException(e);
        }
    }
}
