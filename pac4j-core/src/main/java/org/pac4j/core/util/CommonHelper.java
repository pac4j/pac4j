package org.pac4j.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Date;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.exception.TechnicalException;
import org.pac4j.core.io.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	public static final String HTTP_PREFIX = "http";
	public static final String HTTPS_PREFIX = "https";

    public static final String INVALID_PATH_MESSAGE = "begin with '" + RESOURCE_PREFIX + ":', '" + CLASSPATH_PREFIX
			+ ":', '" + HTTP_PREFIX + ":' or it must be a physical readable non-empty local file "
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
        } else if (s1 != null && s2 !=  null) {
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
    public static boolean isEmpty(final Collection coll) {
        return coll == null || coll.isEmpty();
    }

    /**
     * Return if a collection is not empty.
     *
     * @param coll a collection
     * @return whether it is not empty
     */
    public static boolean isNotEmpty(final Collection coll) {
        return !isEmpty(coll);
    }

    /**
     * Verify that a boolean is true otherwise throw a {@link TechnicalException}.
     * 
     * @param value the value to be checked for truth
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
     * @param name name if the string
     * @param value value of the string
     */
    public static void assertNotBlank(final String name, final String value) {
        assertTrue(!isBlank(value), name + " cannot be blank");
    }

    /**
     * Verify that an Object is not <code>null</code> otherwise throw a {@link TechnicalException}.
     * 
     * @param name name of the object
     * @param obj object
     */
    public static void assertNotNull(final String name, final Object obj) {
        assertTrue(obj != null, name + " cannot be null");
    }

    /**
     * Verify that an Object is <code>null</code> otherwise throw a {@link TechnicalException}.
     *
     * @param name name of the object
     * @param obj object
     */
    public static void assertNull(final String name, final Object obj) {
        assertTrue(obj == null, name + " must be null");
    }

    /**
     * Add a new parameter to an url.
     * 
     * @param url url
     * @param name name of the parameter
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
            String message = "Unable to encode text : " + text;
            throw new TechnicalException(message, e);
        }
    }

    /**
     * Build a normalized "toString" text for an object.
     * 
     * @param clazz class
     * @param args arguments
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
     * Returns an {@link InputStream} from given name depending on its format:
     * - loads from the classloader if name starts with "resource:"
     * - loads as {@link FileInputStream} otherwise
     * 
     * @param name name of the resource
     * @return the input stream
     */
    public static InputStream getInputStreamFromName(String name) {
		int prefixEnd = name.indexOf(":");
		String prefix = null;
		String path = name;
		if (prefixEnd != -1) {
			prefix = name.substring(0, prefixEnd);
			path = name.substring(prefixEnd + 1);
		}

		switch (prefix) {
		case RESOURCE_PREFIX:
			if (!path.startsWith("/")) {
				path = "/" + path;
			}
			// The choice here was to keep legacy behavior and remove / prior to
			// calling classloader.getResourceAsStream.. or make it work exactly
			// as it did before but have different behavior for resource: and
			// classpath:
			// My decision was to keep legacy working the same.
			return CommonHelper.class.getResourceAsStream(path);
		case CLASSPATH_PREFIX:
			return Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
		case HTTP_PREFIX:
			logger.warn("IdP metadata is retrieved from an insecure http endpoint [{}]", path);
		case HTTPS_PREFIX:
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
		default:
			try {
				return new FileInputStream(path);
			} catch (FileNotFoundException e) {
				throw new TechnicalException(e);
			}
		}

	}

	public static Resource getResource(final String idpMetadataPath) {
		return new Resource() {

			@Override
			public InputStream getInputStream() throws IOException {
				return getInputStreamFromName(idpMetadataPath);
			}

			@Override
			public long lastModified() throws IOException {
				throw new UnsupportedOperationException("not implemented");
			}

			@Override
			public boolean isReadable() {
				throw new UnsupportedOperationException("not implemented");
			}

			@Override
			public boolean isOpen() {
				throw new UnsupportedOperationException("not implemented");
			}

			@Override
			public URL getURL() throws IOException {
				throw new UnsupportedOperationException("not implemented");
			}

			@Override
			public URI getURI() throws IOException {
				throw new UnsupportedOperationException("not implemented");
			}

			@Override
			public String getFilename() {
				throw new UnsupportedOperationException("not implemented");
			}

			@Override
			public File getFile() throws IOException {
				throw new UnsupportedOperationException("not implemented");
			}

			@Override
			public String getDescription() {
				throw new UnsupportedOperationException("not implemented");
			}

			@Override
			public boolean exists() {
				throw new UnsupportedOperationException("not implemented");
			}

			@Override
			public Resource createRelative(String relativePath) throws IOException {
				throw new UnsupportedOperationException("not implemented");
			}

			@Override
			public long contentLength() throws IOException {
				throw new UnsupportedOperationException("not implemented");
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

    public static String substringBetween(String str, String open, String close) {
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

    public static String substringAfter(String str, String separator) {
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

    private static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }
}
