package org.pac4j.core.util;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.io.Resource;
import org.pac4j.core.io.WritableResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Date;

/**
 * This class gathers all the utilities methods.
 *
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class CommonHelper {

    private static final Logger logger = LoggerFactory.getLogger(CommonHelper.class);

    public static final String RESOURCE_PREFIX = "resource";
    public static final String CLASSPATH_PREFIX = "classpath";

    protected static final String FILE_PREFIX = "file:";

    public static final String INVALID_PATH_MESSAGE = "begin with '" + RESOURCE_PREFIX + ":', '" + CLASSPATH_PREFIX
            + ":', '" + HttpConstants.SCHEME_HTTP + ":', '" + HttpConstants.SCHEME_HTTPS + ":' or it must be a physical readable non-empty local file "
            + "at the path specified.";

    /**
     * Return if the String is not blank.
     *
     * @param s string
     * @return if the String is not blank
     */
    public static boolean isNotBlank(final String s) {
        if (s == null) {
            return false;
        }
        return s.trim().length() > 0;
    }

    /**
     * Return if the String is blank.
     *
     * @param s string
     * @return if the String is blank
     */
    public static boolean isBlank(final String s) {
        return !isNotBlank(s);
    }

    /**
     * Compare two String to see if they are equals (both null is ok).
     *
     * @param s1 string
     * @param s2 string
     * @return if two String are equals
     */
    public static boolean areEquals(final String s1, final String s2) {
        return s1 == null ? s2 == null : s1.equals(s2);
    }

    /**
     * Compare two String to see if they are equals ignoring the case and the blank spaces (both null is ok).
     *
     * @param s1 string
     * @param s2 string
     * @return if two String are equals ignoring the case and the blank spaces
     */
    public static boolean areEqualsIgnoreCaseAndTrim(final String s1, final String s2) {
        if (s1 == null && s2 == null) {
            return true;
        } else if (s1 != null && s2 != null) {
            return s1.trim().equalsIgnoreCase(s2.trim());
        } else {
            return false;
        }
    }

    /**
     * Compare two String to see if they are not equals.
     *
     * @param s1 string
     * @param s2 string
     * @return if two String are not equals
     */
    public static boolean areNotEquals(final String s1, final String s2) {
        return !areEquals(s1, s2);
    }

    /**
     * Return if a collection is empty.
     *
     * @param coll a collection
     * @return whether it is empty
     */
    public static boolean isEmpty(final Collection<?> coll) {
        return coll == null || coll.isEmpty();
    }

    /**
     * Return if a collection is not empty.
     *
     * @param coll a collection
     * @return whether it is not empty
     */
    public static boolean isNotEmpty(final Collection<?> coll) {
        return !isEmpty(coll);
    }

    /**
     * Verify that a boolean is true otherwise throw a {@link TechnicalException}.
     *
     * @param value   the value to be checked for truth
     * @param message the message to include in the exception if the value is false
     */
    public static void assertTrue(final boolean value, final String message) {
        if (!value) {
            throw new TechnicalException(message);
        }
    }

    /**
     * Verify that a String is not blank otherwise throw a {@link TechnicalException}.
     *
     * @param name  name if the string
     * @param value value of the string
     */
    public static void assertNotBlank(final String name, final String value) {
        assertTrue(!isBlank(value), name + " cannot be blank");
    }

    /**
     * Verify that an Object is not <code>null</code> otherwise throw a {@link TechnicalException}.
     *
     * @param name name of the object
     * @param obj  object
     */
    public static void assertNotNull(final String name, final Object obj) {
        assertTrue(obj != null, name + " cannot be null");
    }

    /**
     * Verify that an Object is <code>null</code> otherwise throw a {@link TechnicalException}.
     *
     * @param name name of the object
     * @param obj  object
     */
    public static void assertNull(final String name, final Object obj) {
        assertTrue(obj == null, name + " must be null");
    }

    /**
     * Add a new parameter to an url.
     *
     * @param url   url
     * @param name  name of the parameter
     * @param value value of the parameter
     * @return the new url with the parameter appended
     */
    public static String addParameter(final String url, final String name, final String value) {
        if (url != null) {
            final StringBuilder sb = new StringBuilder();
            sb.append(url);
            if (name != null) {
                if (url.indexOf("?") >= 0) {
                    sb.append("&");
                } else {
                    sb.append("?");
                }
                sb.append(name);
                sb.append("=");
                if (value != null) {
                    sb.append(urlEncode(value));
                }
            }
            return sb.toString();
        }
        return null;
    }

    /**
     * URL encode a text using UTF-8.
     *
     * @param text text to encode
     * @return the encoded text
     */
    public static String urlEncode(final String text) {
        try {
            return URLEncoder.encode(text, HttpConstants.UTF8_ENCODING);
        } catch (final UnsupportedEncodingException e) {
            final String message = "Unable to encode text : " + text;
            throw new TechnicalException(message, e);
        }
    }

    /**
     * Build a normalized "toString" text for an object.
     *
     * @param clazz class
     * @param args  arguments
     * @return a normalized "toString" text
     */
    public static String toString(final Class<?> clazz, final Object... args) {
        final StringBuilder sb = new StringBuilder();
        sb.append("#");
        sb.append(clazz.getSimpleName());
        sb.append("# |");
        boolean b = true;
        for (final Object arg : args) {
            if (b) {
                sb.append(" ");
                sb.append(arg);
                sb.append(":");
            } else {
                sb.append(" ");
                sb.append(arg);
                sb.append(" |");
            }
            b = !b;
        }
        return sb.toString();
    }

    /**
     * Extract the prefix of the name.
     *
     * @param name the name
     * @return the prefix
     */
    protected static String extractPrefix(final String name) {
        String prefix = null;
        if (name != null) {
            int prefixEnd = name.indexOf(":");
            if (prefixEnd != -1) {
                prefix = name.substring(0, prefixEnd);
            }
        }
        return prefix;
    }

    /**
     * Add a slash at the beginning of a path if missing.
     *
     * @param path the path
     * @return the completed path
     */
    protected static String startWithSlash(final String path) {
        if (!path.startsWith("/")) {
            return "/" + path;
        } else {
            return path;
        }
    }

    /**
     * Returns an {@link InputStream} from given name depending on its format:
     * - loads from the classloader of this class if name starts with "resource:" (add a slash a the beginning if absent)
     * - loads from the classloader of the current thread if name starts with "classpath:"
     * - loads from the given url if name starts with "http:" or "https:"
     * - loads as {@link FileInputStream} otherwise
     * <p>
     * Caller is responsible for closing inputstream
     *
     * @param name name of the resource
     * @return the input stream
     */
    public static InputStream getInputStreamFromName(String name) {
        String path = name;
        final String prefix = extractPrefix(name);
        if (prefix != null) {
            path = name.substring(prefix.length() + 1);
        }
        if (CommonHelper.isEmpty(prefix)) {
            try {
                return new FileInputStream(path);
            } catch (FileNotFoundException e) {
                throw new TechnicalException(e);
            }
        }

        switch (prefix) {
            case RESOURCE_PREFIX:
                return CommonHelper.class.getResourceAsStream(startWithSlash(path));
            case CLASSPATH_PREFIX:
                return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
            case HttpConstants.SCHEME_HTTP:
                logger.warn("file is retrieved from an insecure http endpoint [{}]", path);
                return getInputStreamViaHttp(name);
            case HttpConstants.SCHEME_HTTPS:
                return getInputStreamViaHttp(name);
            default:
                throw new TechnicalException("prefix is not handled:" + prefix);
        }

    }

    private static InputStream getInputStreamViaHttp(String name) {
        URLConnection con = null;
        try {
            URL url = new URL(name);
            con = url.openConnection();
            return con.getInputStream();
        } catch (IOException ex) {
            // Close the HTTP connection (if applicable).
            if (con instanceof HttpURLConnection) {
                ((HttpURLConnection) con).disconnect();
            }
            throw new TechnicalException(ex);
        }
    }

    public static Resource getResource(final String filePath) {
        return new WritableResource() {

            @Override
            public File getFile() {
                final String filename = getFilename();
                if (filename != null) {
                    return new File(filename);
                }
                logger.warn("This filePath: {} is not a file. Returning null in the getFile() method", filePath);
                return null;
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return getInputStreamFromName(filePath);
            }

            @Override
            public String getFilename() {
                String filename = null;
                final String prefix = extractPrefix(filePath);
                if (prefix == null) {
                    filename = filePath;
                } else if (prefix.equals(RESOURCE_PREFIX) || prefix.equals(CLASSPATH_PREFIX)) {
                    final String path = filePath.substring(prefix.length() + 1);
                    final URL url;
                    if (prefix.equals(RESOURCE_PREFIX)) {
                        url = CommonHelper.class.getResource(startWithSlash(path));
                    } else {
                        url = Thread.currentThread().getContextClassLoader().getResource(path);
                    }
                    if (url == null || url.toString() == null) {
                        throw new TechnicalException("Do not use the resource: or classpath: prefix for non-existing files. Use a direct path (relative or absolute, no prefix)");
                    }
                    final String sUrl = url.toString();
                    // create filename from url if we know the prefix (we remove it)
                    if (sUrl.startsWith(FILE_PREFIX)) {
                        filename = sUrl.substring(FILE_PREFIX.length());
                    } else {
                        throw new TechnicalException("Unsupported resource format: " + sUrl + ". Use a relative or absolute path");
                    }
                }
                logger.debug("filepath: {} -> filename: {}", filePath, filename);
                return filename;
            }

            @Override
            public boolean exists() {
                final File f = getFile();
                if (f != null) {
                    return f.exists();
                }
                // if we get there, it means that this is not a file, so it's a URL and we assume it exists
                return true;
            }

            @Override
            public OutputStream getOutputStream() throws IOException {
                final String filename = getFilename();
                if (filename != null) {
                    return new FileOutputStream(filePath);
                }
                return null;
            }
        };
    }

    /**
     * Return a random string of a certain size.
     *
     * @param size the size
     * @return the random size
     */
    public static String randomString(final int size) {
        return java.util.UUID.randomUUID().toString().replace("-", "").substring(0, size);
    }

    /**
     * Copy a date.
     *
     * @param original original date
     * @return date copy
     */
    public static Date newDate(final Date original) {
        return original != null ? new Date(original.getTime()) : null;
    }

    /**
     * Taken from commons-lang3
     */

    private static final String EMPTY = "";
    private static final int INDEX_NOT_FOUND = -1;

    public static String substringBetween(final String str, final String open, final String close) {
        if (str == null || open == null || close == null) {
            return null;
        }
        int start = str.indexOf(open);
        if (start != INDEX_NOT_FOUND) {
            int end = str.indexOf(close, start + open.length());
            if (end != INDEX_NOT_FOUND) {
                return str.substring(start + open.length(), end);
            }
        }
        return null;
    }

    public static String substringAfter(final String str, final String separator) {
        if (isEmpty(str)) {
            return str;
        }
        if (separator == null) {
            return EMPTY;
        }
        int pos = str.indexOf(separator);
        if (pos == INDEX_NOT_FOUND) {
            return EMPTY;
        }
        return str.substring(pos + separator.length());
    }

    public static String substringBefore(final String str, final String separator) {
        if (isEmpty(str) || separator == null) {
            return str;
        }
        if (separator.length() == 0) {
            return EMPTY;
        }
        int pos = str.indexOf(separator);
        if (pos == INDEX_NOT_FOUND) {
            return str;
        }
        return str.substring(0, pos);
    }

    private static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }
}
