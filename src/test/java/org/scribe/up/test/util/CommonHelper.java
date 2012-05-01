/*
  Copyright 2012 Jerome Leleu

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
package org.scribe.up.test.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.WebClient;

/**
 * This class is an helper to get a basic web client, parameters from an url, a formatted date or to serialize and deserialize objects.
 * 
 * @author Jerome Leleu
 * @since 1.0.0
 */
public final class CommonHelper {
    
    private static final Logger logger = LoggerFactory.getLogger(CommonHelper.class);
    
    private CommonHelper() {
    }
    
    public static WebClient newWebClient(boolean isJavascriptEnabled) {
        WebClient webClient = new WebClient();
        webClient.setRedirectEnabled(true);
        webClient.setCssEnabled(false);
        webClient.setJavaScriptEnabled(isJavascriptEnabled);
        return webClient;
    }
    
    public static Map<String, String[]> getParametersFromUrl(String url) {
        Map<String, String[]> parameters = new HashMap<String, String[]>();
        int pos = url.indexOf("?");
        if (pos > 0) {
            url = url.substring(pos + 1);
        }
        StringTokenizer st = new StringTokenizer(url, "&");
        while (st.hasMoreTokens()) {
            String keyValue = st.nextToken();
            String[] parts = keyValue.split("=");
            String[] values = new String[1];
            values[0] = parts[1];
            parameters.put(parts[0], values);
        }
        return parameters;
    }
    
    public static String getFormattedDate(long l, String format, Locale locale) {
        Date d = new Date(l);
        SimpleDateFormat simpleDateFormat;
        if (locale == null) {
            simpleDateFormat = new SimpleDateFormat(format);
        } else {
            simpleDateFormat = new SimpleDateFormat(format, locale);
        }
        return simpleDateFormat.format(d);
    }
    
    public static byte[] serialize(Object o) {
        byte[] bytes = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
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
        } catch (IOException e) {
            logger.warn("cannot serialize object", e);
        }
        return bytes;
    }
    
    public static Object unserialize(byte[] bytes) {
        Object o = null;
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bais);
            try {
                o = ois.readObject();
            } finally {
                try {
                    ois.close();
                } finally {
                    bais.close();
                }
            }
        } catch (IOException e) {
            logger.warn("cannot deserialize object", e);
        } catch (ClassNotFoundException e) {
            logger.warn("cannot deserialize object", e);
        }
        return o;
    }
}
