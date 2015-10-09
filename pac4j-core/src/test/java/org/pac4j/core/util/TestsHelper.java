/*
  Copyright 2012 - 2015 pac4j organization

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
package org.pac4j.core.util;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.KryoException;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import org.junit.Assert;
import org.pac4j.core.exception.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * This class is an helper fo tests : to get a basic web client, parameters from an url, a formatted date or to serialize and deserialize
 * objects.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class TestsHelper {

    private static final Logger logger = LoggerFactory.getLogger(TestsHelper.class);

    private static final int BUFFERS_INITIAL_CAPACITY = 5 * 1024;

    private static final int BUFFERS_MAXIMAL_CAPACITY = 1024 * 20;

    public static WebClient newWebClient(final boolean isJavascriptEnabled) {
        final WebClient webClient = new WebClient();
        webClient.getOptions().setRedirectEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getOptions().setJavaScriptEnabled(isJavascriptEnabled);
        if (isJavascriptEnabled) {
            webClient.setAjaxController(new NicelyResynchronizingAjaxController());
        }
        return webClient;
    }

    public static Map<String, String> getParametersFromUrl(String url) {
        int pos = url.indexOf("?");
        if (pos >= 0) {
            url = url.substring(pos + 1);
        }
        pos = url.indexOf("#");
        if (pos >= 0) {
            url = url.substring(0, pos);
        }
        final Map<String, String> parameters = new HashMap<String, String>();
        final StringTokenizer st = new StringTokenizer(url, "&");
        while (st.hasMoreTokens()) {
            final String keyValue = st.nextToken();
            final String[] parts = keyValue.split("=");
            if (parts != null && parts.length >= 2) {
                try {
                    parameters.put(parts[0], URLDecoder.decode(parts[1], "UTF-8"));
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

    public static byte[] serialize(final Object o) {
        byte[] bytes = null;
        try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final ObjectOutputStream oos = new ObjectOutputStream(baos);
            try {
                oos.writeObject(o);
                oos.flush();
                bytes = baos.toByteArray();
            } finally {
                try {
                    oos.close();
                } finally {
                    baos.close();
                }
            }
        } catch (final IOException e) {
            logger.warn("cannot serialize object", e);
        }
        return bytes;
    }

    public static Object unserialize(final byte[] bytes) {
        Object o = null;
        try {
            final ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            final ObjectInputStream ois = new ObjectInputStream(bais);
            try {
                o = ois.readObject();
            } finally {
                try {
                    ois.close();
                } finally {
                    bais.close();
                }
            }
        } catch (final IOException e) {
            logger.warn("cannot deserialize object", e);
        } catch (final ClassNotFoundException e) {
            logger.warn("cannot deserialize object", e);
        }
        return o;
    }

    public static byte[] serializeKryo(final Kryo kryo, final Object object) {
        Output output = new Output(BUFFERS_INITIAL_CAPACITY, BUFFERS_MAXIMAL_CAPACITY);
        try {
            kryo.writeClassAndObject(output, object);
        } catch (final KryoException e) {
            logger.error("serialize exception with Kryo on object : {}", object, e);
        }
        return output.toBytes();
    }

    public static Object unserializeKryo(final Kryo kryo, final byte[] bytes) {
        Input input = new Input(bytes);
        try {
            return kryo.readClassAndObject(input);
        } catch (final KryoException e) {
            logger.error("unserialize exception with Kryo", e);
        }
        return null;
    }

    public static void initShouldFail(final InitializableObject obj, final String message) {
        try {
            obj.init();
            Assert.fail("init should fail");
        } catch (final TechnicalException e) {
            Assert.assertEquals(message, e.getMessage());
        }
    }
}
