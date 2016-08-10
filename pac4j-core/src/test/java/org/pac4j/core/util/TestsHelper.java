package org.pac4j.core.util;

import org.junit.Assert;
import org.pac4j.core.context.HttpConstants;
import org.pac4j.core.context.MockWebContext;
import org.pac4j.core.exception.TechnicalException;

import java.io.*;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This class is an helper for tests: get a basic web client, parameters from an url, a formatted date, etc.
 *
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class TestsHelper {

    public static Map<String, String> getParametersFromUrl(String url) {
        int pos = url.indexOf("?");
        if (pos >= 0) {
            url = url.substring(pos + 1);
        } else {
            pos = url.indexOf("#");
            if (pos >= 0) {
                url = url.substring(pos + 1);
            }
        }
        final Map<String, String> parameters = new HashMap<>();
        final StringTokenizer st = new StringTokenizer(url, "&");
        while (st.hasMoreTokens()) {
            final String keyValue = st.nextToken();
            final String[] parts = keyValue.split("=");
            if (parts != null && parts.length >= 2) {
                try {
                    parameters.put(parts[0], URLDecoder.decode(parts[1], HttpConstants.UTF8_ENCODING));
                } catch (final UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return parameters;
    }

    public static String getFormattedDate(final long l, final String format, final Locale locale) {
        final Date d = new Date(l);
        SimpleDateFormat simpleDateFormat;
        if (locale == null) {
            simpleDateFormat = new SimpleDateFormat(format);
        } else {
            simpleDateFormat = new SimpleDateFormat(format, locale);
        }
        return simpleDateFormat.format(d);
    }

    public static void initShouldFail(final InitializableObject obj, final String message) {
        expectException(() -> obj.init(), TechnicalException.class, message);
    }

    public static void initShouldFail(final InitializableWebObject obj, final String message) {
        expectException(() -> obj.init(MockWebContext.create()), TechnicalException.class, message);
    }

    public static Exception expectException(final Executable executable) {
        try {
            executable.execute();
        } catch (final Exception e) {
            return e;
        }
        return null;
    }

    public static void expectException(final Executable executable, final Class<? extends Exception> clazz, final String message) {
        final Exception e = expectException(executable);
        Assert.assertTrue(clazz.isAssignableFrom(e.getClass()));
        Assert.assertEquals(message, e.getMessage());
    }
}
