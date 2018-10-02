package org.pac4j.saml.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;

/**
 * Unit test for class {@link SAML2Utils}.
 * 
 * @author jkacer
 * @since 1.8.0
 */
public final class SAML2UtilsTests {

    @Test
    public void twoNullUrisMustEqual() {
        assertTrue(SAML2Utils.urisEqualAfterPortNormalization(null, null));
    }
    
    @Test
    public void nullUriAndNonNullUriMustNotEqual() throws URISyntaxException {
        final URI uri = new URI("http://somewhere/something");
        assertFalse(SAML2Utils.urisEqualAfterPortNormalization(uri, null));
        assertFalse(SAML2Utils.urisEqualAfterPortNormalization(null, uri));
    }
    
    @Test
    public void uriMustEqualItself() throws URISyntaxException {
        final URI uri = new URI("http://somewhere/something");
        assertTrue(SAML2Utils.urisEqualAfterPortNormalization(uri, uri));
    }
    
    @Test
    public void twoSameUrisMustEqual() throws URISyntaxException {
        final URI uri1 = new URI("http://somewhere/something");
        final URI uri2 = new URI("http://somewhere/something");
        assertTrue(SAML2Utils.urisEqualAfterPortNormalization(uri1, uri2));
        assertTrue(SAML2Utils.urisEqualAfterPortNormalization(uri2, uri1));
    }
    
    @Test
    public void twoDifferntUrisMustNotEqual() throws URISyntaxException {
        final URI uri1 = new URI("http://somewhere/something1");
        final URI uri2 = new URI("http://somewhere/something2");
        assertFalse(SAML2Utils.urisEqualAfterPortNormalization(uri1, uri2));
        assertFalse(SAML2Utils.urisEqualAfterPortNormalization(uri2, uri1));
    }
    
    @Test
    public void sameUrisWithImplicitAndExplicitHttpPortMustEqual() throws URISyntaxException {
        final URI uri1 = new URI("http://somewhere:80/something");
        final URI uri2 = new URI("http://somewhere/something");
        assertTrue(SAML2Utils.urisEqualAfterPortNormalization(uri1, uri2));
        assertTrue(SAML2Utils.urisEqualAfterPortNormalization(uri2, uri1));
    }

    @Test
    public void sameUrisWithImplicitAndExplicitHttpsPortMustEqual() throws URISyntaxException {
        final URI uri1 = new URI("https://somewhere:443/something");
        final URI uri2 = new URI("https://somewhere/something");
        assertTrue(SAML2Utils.urisEqualAfterPortNormalization(uri1, uri2));
        assertTrue(SAML2Utils.urisEqualAfterPortNormalization(uri2, uri1));
    }
    
    @Test
    public void differentUrisWithImplicitAndExplicitHttpPortMustNotEqual() throws URISyntaxException {
        final URI uri1 = new URI("http://somewhere:80/something1");
        final URI uri2 = new URI("http://somewhere/something2");
        assertFalse(SAML2Utils.urisEqualAfterPortNormalization(uri1, uri2));
        assertFalse(SAML2Utils.urisEqualAfterPortNormalization(uri2, uri1));
    }

    @Test
    public void differentUrisWithImplicitAndExplicitHttpsPortMustNotEqual() throws URISyntaxException {
        final URI uri1 = new URI("https://somewhere:443/something1");
        final URI uri2 = new URI("https://somewhere/something2");
        assertFalse(SAML2Utils.urisEqualAfterPortNormalization(uri1, uri2));
        assertFalse(SAML2Utils.urisEqualAfterPortNormalization(uri2, uri1));
    }
}
