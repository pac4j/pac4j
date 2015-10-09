/*
  Copyright 2012 -2015 pac4j organization
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
       http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
 package org.pac4j.saml.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;


/**
 * Unit test for class {@link UriUtils}.
 * 
 * @author jkacer
 * @since 1.8.0
 */
public class UriUtilsTest {

    @Test
    public void twoNullUrisMustEqual() {
        assertTrue(UriUtils.urisEqualAfterPortNormalization(null, null));
    }
    
    @Test
    public void nullUriAndNonNullUriMustNotEqual() throws URISyntaxException {
        final URI uri = new URI("http://somewhere/something");
        assertFalse(UriUtils.urisEqualAfterPortNormalization(uri, null));
        assertFalse(UriUtils.urisEqualAfterPortNormalization(null, uri));
    }
    
    @Test
    public void uriMustEqualItself() throws URISyntaxException {
        final URI uri = new URI("http://somewhere/something");
        assertTrue(UriUtils.urisEqualAfterPortNormalization(uri, uri));
    }
    
    @Test
    public void twoSameUrisMustEqual() throws URISyntaxException {
        final URI uri1 = new URI("http://somewhere/something");
        final URI uri2 = new URI("http://somewhere/something");
        assertTrue(UriUtils.urisEqualAfterPortNormalization(uri1, uri2));
        assertTrue(UriUtils.urisEqualAfterPortNormalization(uri2, uri1));
    }
    
    @Test
    public void twoDifferntUrisMustNotEqual() throws URISyntaxException {
        final URI uri1 = new URI("http://somewhere/something1");
        final URI uri2 = new URI("http://somewhere/something2");
        assertFalse(UriUtils.urisEqualAfterPortNormalization(uri1, uri2));
        assertFalse(UriUtils.urisEqualAfterPortNormalization(uri2, uri1));
    }
    
    @Test
    public void sameUrisWithImplicitAndExplicitHttpPortMustEqual() throws URISyntaxException {
        final URI uri1 = new URI("http://somewhere:80/something");
        final URI uri2 = new URI("http://somewhere/something");
        assertTrue(UriUtils.urisEqualAfterPortNormalization(uri1, uri2));
        assertTrue(UriUtils.urisEqualAfterPortNormalization(uri2, uri1));
    }

    @Test
    public void sameUrisWithImplicitAndExplicitHttpsPortMustEqual() throws URISyntaxException {
        final URI uri1 = new URI("https://somewhere:443/something");
        final URI uri2 = new URI("https://somewhere/something");
        assertTrue(UriUtils.urisEqualAfterPortNormalization(uri1, uri2));
        assertTrue(UriUtils.urisEqualAfterPortNormalization(uri2, uri1));
    }
    
    @Test
    public void differentUrisWithImplicitAndExplicitHttpPortMustNotEqual() throws URISyntaxException {
        final URI uri1 = new URI("http://somewhere:80/something1");
        final URI uri2 = new URI("http://somewhere/something2");
        assertFalse(UriUtils.urisEqualAfterPortNormalization(uri1, uri2));
        assertFalse(UriUtils.urisEqualAfterPortNormalization(uri2, uri1));
    }

    @Test
    public void differentUrisWithImplicitAndExplicitHttpsPortMustNotEqual() throws URISyntaxException {
        final URI uri1 = new URI("https://somewhere:443/something1");
        final URI uri2 = new URI("https://somewhere/something2");
        assertFalse(UriUtils.urisEqualAfterPortNormalization(uri1, uri2));
        assertFalse(UriUtils.urisEqualAfterPortNormalization(uri2, uri1));
    }
    
}
