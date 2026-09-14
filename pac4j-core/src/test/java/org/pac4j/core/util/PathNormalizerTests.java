package org.pac4j.core.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@link PathNormalizer}.
 *
 * @author Jerome Leleu
 * @since 6.6.0
 */
public final class PathNormalizerTests {

    @Test
    public void testNull() {
        assertEquals(Pac4jConstants.EMPTY_STRING, PathNormalizer.normalize(null));
    }

    @Test
    public void testEmpty() {
        assertEquals(Pac4jConstants.EMPTY_STRING, PathNormalizer.normalize(Pac4jConstants.EMPTY_STRING));
    }

    @Test
    public void testRoot() {
        assertEquals("/", PathNormalizer.normalize("/"));
    }

    @Test
    public void testCanonicalPathIsUnchanged() {
        assertEquals("/ctx/admin/users", PathNormalizer.normalize("/ctx/admin/users"));
    }

    @Test
    public void testTrailingSlashIsKept() {
        assertEquals("/ctx/admin/", PathNormalizer.normalize("/ctx/admin/"));
    }

    @Test
    public void testMissingLeadingSlashIsAdded() {
        assertEquals("/ctx/admin", PathNormalizer.normalize("ctx/admin"));
    }

    @Test
    public void testEmptySegmentsAreRemoved() {
        assertEquals("/ctx/admin", PathNormalizer.normalize("//ctx///admin"));
    }

    @Test
    public void testDotSegmentsAreRemoved() {
        assertEquals("/ctx/admin", PathNormalizer.normalize("/./ctx/./admin"));
        assertEquals("/ctx/admin/", PathNormalizer.normalize("/ctx/admin/."));
    }

    @Test
    public void testDotDotSegmentRemovesPreviousSegment() {
        assertEquals("/admin", PathNormalizer.normalize("/public/../admin"));
        assertEquals("/admin", PathNormalizer.normalize("/a/b/../../admin"));
    }

    @Test
    public void testDotDotAtTheEndKeepsATrailingSlash() {
        assertEquals("/ctx/", PathNormalizer.normalize("/ctx/admin/.."));
    }

    @Test
    public void testDotDotAboveTheRootIsIgnored() {
        assertEquals("/admin", PathNormalizer.normalize("/../admin"));
        assertEquals("/admin", PathNormalizer.normalize("/a/../../../admin"));
        assertEquals("/", PathNormalizer.normalize("/.."));
    }

    @Test
    public void testPercentEncodedCharactersAreDecoded() {
        assertEquals("/ctx/café/a b", PathNormalizer.normalize("/ctx/caf%C3%A9/a%20b"));
    }

    @Test
    public void testEncodedDotSegmentsAreCanonicalized() {
        assertEquals("/admin", PathNormalizer.normalize("/public/%2e%2e/admin"));
        assertEquals("/admin", PathNormalizer.normalize("/public/%2E%2E/admin"));
        assertEquals("/admin", PathNormalizer.normalize("/public/.%2e/admin"));
    }

    @Test
    public void testEncodedSlashIsASeparator() {
        assertEquals("/admin", PathNormalizer.normalize("/public%2F..%2Fadmin"));
        assertEquals("/public/admin", PathNormalizer.normalize("/public%2fadmin"));
    }

    @Test
    public void testDecodingHappensOnlyOnce() {
        // a double-encoded ".." is a literal "%2e%2e" segment for the container as well, not a traversal
        assertEquals("/public/%2e%2e/admin", PathNormalizer.normalize("/public/%252e%252e/admin"));
    }

    @Test
    public void testPlusIsNotASpace() {
        assertEquals("/a+b", PathNormalizer.normalize("/a+b"));
    }

    @Test
    public void testMalformedPercentSequenceIsKept() {
        assertEquals("/a%zz/b%", PathNormalizer.normalize("/a%zz/b%"));
        assertEquals("/a%2", PathNormalizer.normalize("/a%2"));
    }

    @Test
    public void testNonAsciiDigitsAreNotHexadecimalDigits() {
        // U+0663 (Arabic-Indic three) and U+FF21 (fullwidth A) are accepted by Character.digit() but not by containers
        assertEquals("/a%\u0663\u0663/b%\uff21\uff21", PathNormalizer.normalize("/a%\u0663\u0663/b%\uff21\uff21"));
    }

    @Test
    public void testPathParametersAreRemoved() {
        assertEquals("/ctx/admin", PathNormalizer.normalize("/ctx/admin;jsessionid=ABC123"));
        assertEquals("/ctx/admin/users", PathNormalizer.normalize("/ctx;a=b/admin;c=d;e=f/users;x"));
        assertEquals("/ctx/admin/", PathNormalizer.normalize("/ctx/admin/;jsessionid=ABC123"));
    }

    @Test
    public void testEncodedSemicolonIsNotAPathParameterDelimiter() {
        assertEquals("/ctx/admin;x=y", PathNormalizer.normalize("/ctx/admin%3Bx=y"));
    }

    @Test
    public void testPathParameterHidingATraversal() {
        assertEquals("/admin", PathNormalizer.normalize("/public;x=y/..;z=t/admin"));
    }
}
