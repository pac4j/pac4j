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
package org.pac4j.oauth.profile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.pac4j.core.profile.converter.AttributeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Some XML helper mirroring the {@link JsonHelper}.
 * 
 * @author Jerome Leleu
 * @since 1.4.1
 */
public final class XmlHelper {

    private static final Logger logger = LoggerFactory.getLogger(XmlHelper.class);

    private static XmlMapper mapper;

    static {
        mapper = new XmlMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Return an Object from a XML text.
     *
     * @param xml a XML text
     * @return the parsed object
     */
    public static <T extends Object> T getAsType(final String xml, final Class<T> clazz) {
        try {
            return mapper.readValue(xml, clazz);
        } catch (final IOException e) {
            logger.error("Cannot get as type", e);
        }
        return null;
    }

    /**
     * Get a sub-text between tags from a text.
     * 
     * @param text the text.
     * @param tag the tag.
     * @return a sub-text between tags from a text.
     */
    public static String get(final String text, final String tag) {
        XmlMatch match = get(text, tag, 0);
        if (match != null) {
            return match.getText();
        }
        return null;
    }

    /**
     * Returns the XML string for the object.
     *
     * @param obj the object
     * @return the XML string
     */
    public static String toXMLString(final Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (final JsonProcessingException e) {
            logger.error("Cannot to XML string", e);
        }
        return null;
    }

    /**
     * Get a XML match between tags from a text, starting at a certain position.
     * 
     * @param text the text.
     * @param tag the tag.
     * @param startPos the position to start.
     * @return a XML match.
     */
    public static XmlMatch get(final String text, final String tag, final int startPos) {
        String startTag = "<" + tag;
        int pos1 = text.indexOf(startTag, startPos);
        if (pos1 >= 0) {
            int pos2 = text.indexOf(">", pos1);
            if (pos2 > pos1) {
                String endTag = "</" + tag + ">";
                int pos3 = text.indexOf(endTag, pos2);
                if (pos3 > pos2) {
                    return new XmlMatch(text.substring(pos2 + 1, pos3), pos1);
                }
            }
        }
        return null;
    }
    
    /**
     * Convert a XML attribute.
     * 
     * @param converter converter
     * @param xml xml
     * @param name attribute name
     * @return the converted XML attribute
     */
    public static Object convert(final AttributeConverter<? extends Object> converter, final String xml,
                                 final String name) {
        return converter.convert(get(xml, name));
    }
}
