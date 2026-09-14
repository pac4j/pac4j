package org.pac4j.core.util;

import lombok.val;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Canonicalizes a raw request URI path so that it can safely be compared with configured paths.
 *
 * <p>A raw request URI (as returned by {@code HttpServletRequest#getRequestURI()}) is neither decoded nor canonicalized:
 * {@code /public/../admin}, {@code /public/%2e%2e/admin} or {@code /admin;x=y} all route to the same servlet
 * but are different strings. Comparing them with a configured path (see {@code PathMatcher}) without canonicalization
 * lets a request bypass the security rules that apply to the resource it actually reaches.</p>
 *
 * <p>The canonicalization follows the rules a servlet container applies before servlet mapping
 * (Jakarta Servlet 6.0 specification, section 3.5.2 "URI Path Canonicalization"):</p>
 * <ol>
 *     <li>path parameters ({@code ;name=value} up to the next {@code /}) are removed from every segment (step 1)</li>
 *     <li>percent-encoded characters are decoded exactly once, as UTF-8 (step 2)</li>
 *     <li>empty segments and {@code .} segments are removed, a {@code ..} segment removes the previous segment (steps 3 and 4)</li>
 * </ol>
 *
 * <p>Where the specification requires the container to reject the request (a {@code ..} segment climbing above the root,
 * an encoded {@code /} or an encoded dot segment), this class instead returns the most restrictive interpretation:
 * a {@code ..} above the root is ignored, an encoded {@code /} is a separator and encoded dots are dots.
 * The resulting path is therefore never broader than the path the container would match.</p>
 *
 * @author Jerome Leleu
 * @since 6.6.0
 */
public final class PathNormalizer {

    private PathNormalizer() {}

    /**
     * Canonicalizes a raw request URI path.
     *
     * @param rawPath the raw (undecoded, non canonical) path, may be {@code null}
     * @return the canonical path: {@code ""} for a {@code null} or empty path, otherwise a path starting with {@code /}
     */
    public static String normalize(final String rawPath) {
        if (rawPath == null || rawPath.isEmpty()) {
            return Pac4jConstants.EMPTY_STRING;
        }

        // Servlet 6.0 §3.5.2 step 1: path parameters are removed before decoding (an encoded ';' is not a delimiter)
        val decoded = percentDecode(removePathParameters(rawPath));

        // Servlet 6.0 §3.5.2 steps 3 and 4: empty and "." segments are dropped, ".." removes the previous segment
        // (a ".." above the root is ignored instead of being rejected: the result is never broader)
        final Deque<String> segments = new ArrayDeque<>();
        for (val segment : decoded.split("/", -1)) {
            if (segment.isEmpty() || ".".equals(segment)) {
                continue;
            }
            if ("..".equals(segment)) {
                segments.pollLast();
                continue;
            }
            segments.addLast(segment);
        }

        val path = new StringBuilder();
        for (val segment : segments) {
            path.append('/').append(segment);
        }
        // a trailing slash is significant for servlet mapping and is kept ("/dir/" and "/dir" are different paths)
        val trailingSlash = decoded.endsWith("/") || decoded.endsWith("/.") || decoded.endsWith("/..");
        if (path.length() == 0 || trailingSlash) {
            path.append('/');
        }
        return path.toString();
    }

    private static String removePathParameters(final String path) {
        if (path.indexOf(';') < 0) {
            return path;
        }
        val result = new StringBuilder(path.length());
        var inParameter = false;
        for (var i = 0; i < path.length(); i++) {
            val c = path.charAt(i);
            if (c == '/') {
                inParameter = false;
            } else if (c == ';') {
                inParameter = true;
            }
            if (!inParameter) {
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * Decodes {@code %XX} sequences once, as UTF-8. Unlike {@code URLDecoder}, a {@code +} is left untouched
     * (it is not a space in a path) and a malformed sequence is kept as is instead of failing.
     */
    private static String percentDecode(final String path) {
        if (path.indexOf('%') < 0) {
            return path;
        }
        val bytes = new ByteArrayOutputStream(path.length());
        var i = 0;
        while (i < path.length()) {
            val next = path.indexOf('%', i);
            if (next < 0) {
                bytes.writeBytes(path.substring(i).getBytes(StandardCharsets.UTF_8));
                break;
            }
            bytes.writeBytes(path.substring(i, next).getBytes(StandardCharsets.UTF_8));
            i = next;
            val high = i + 1 < path.length() ? hexDigit(path.charAt(i + 1)) : -1;
            val low = i + 2 < path.length() ? hexDigit(path.charAt(i + 2)) : -1;
            if (high >= 0 && low >= 0) {
                bytes.write((high << 4) + low);
                i += 3;
            } else {
                bytes.write('%');
                i++;
            }
        }
        return bytes.toString(StandardCharsets.UTF_8);
    }

    /**
     * Only ASCII hexadecimal digits are valid in a percent-encoded octet (RFC 3986, section 2.1),
     * whereas {@link Character#digit(char, int)} also accepts other Unicode digits.
     */
    private static int hexDigit(final char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c >= 'a' && c <= 'f') {
            return c - 'a' + 10;
        }
        if (c >= 'A' && c <= 'F') {
            return c - 'A' + 10;
        }
        return -1;
    }
}
