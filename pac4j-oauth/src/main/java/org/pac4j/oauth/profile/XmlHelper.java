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

import org.pac4j.core.profile.converter.AttributeConverter;

/**
 * Some XML helper mirroring the {@link JsonHelper}.
 * 
 * @author Jerome Leleu
 * @since 1.4.1
 */
public final class XmlHelper {
    
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
