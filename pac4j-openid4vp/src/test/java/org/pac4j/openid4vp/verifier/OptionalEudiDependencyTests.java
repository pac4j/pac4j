package org.pac4j.openid4vp.verifier;

import lombok.val;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Exercises the real class-loading boundary without EUDI or its Kotlin runtime.
 *
 * @author Jerome LELEU
 * @since 6.6.0
 */
class OptionalEudiDependencyTests {

    @Test
    void configurationAndCustomVerifierWorkWithoutEudiOrKotlin() throws Exception {
        val urls = new ArrayList<URL>();
        val classpath = System.getProperty("surefire.test.class.path", System.getProperty("java.class.path"));
        for (val path : classpath.split(File.pathSeparator)) {
            urls.add(new File(path).toURI().toURL());
        }
        try (val loader = new URLClassLoader(urls.toArray(URL[]::new), ClassLoader.getPlatformClassLoader()) {
            @Override
            protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException {
                if (name.startsWith("eu.europa.ec.eudi.") || name.startsWith("kotlin.") || name.startsWith("kotlinx.")) {
                    throw new ClassNotFoundException(name);
                }
                return super.loadClass(name, resolve);
            }
        }) {
            val configType = loader.loadClass("org.pac4j.openid4vp.config.OpenId4VpConfiguration");
            val config = configType.getConstructor().newInstance();
            val verifiers = (Map<?, ?>) configType.getMethod("getCredentialVerifiers").invoke(config);
            val verifier = verifiers.values().iterator().next();
            val transactionType = loader.loadClass("org.pac4j.openid4vp.transaction.VpTransaction");
            val verify = verifier.getClass().getMethod("verify", String.class, transactionType, configType);
            val error = assertThrows(InvocationTargetException.class, () -> verify.invoke(verifier, "presentation", null, config));
            assertEquals("org.pac4j.openid4vp.exceptions.OpenId4VpException", error.getCause().getClass().getName());
            assertTrue(error.getCause().getMessage().contains("eu.europa.ec.eudi:eudi-lib-jvm-sdjwt-kt:0.20.1"));
            assertTrue(error.getCause().getMessage().contains("Add it explicitly"));

            val format = verifier.getClass().getMethod("getFormat").invoke(verifier);
            val verifierType = loader.loadClass("org.pac4j.openid4vp.verifier.CredentialVerifier");
            val resultType = loader.loadClass("org.pac4j.openid4vp.verifier.VerifiedCredential");
            val expected = resultType.getConstructor().newInstance();
            val custom = Proxy.newProxyInstance(loader, new Class<?>[] {verifierType},
                (proxy, method, args) -> "getFormat".equals(method.getName()) ? format : expected);
            configType.getMethod("addCredentialVerifier", verifierType).invoke(config, custom);
            assertSame(custom, verifiers.get(format));
            assertSame(expected, verifierType.getMethod("verify", String.class, transactionType, configType)
                .invoke(custom, "presentation", null, config));
        }
    }
}
