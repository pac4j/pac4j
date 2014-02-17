/*
  Copyright 2012 - 2014 Jerome Leleu

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
package org.pac4j.core.profile.converter;

import org.pac4j.core.profile.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class converts a String into a Color.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class ColorConverter implements AttributeConverter<Color> {
    
    private static final Logger logger = LoggerFactory.getLogger(ColorConverter.class);
    
    public Color convert(final Object attribute) {
        if (attribute != null && attribute instanceof String) {
            final String s = (String) attribute;
            if (s.length() == 6) {
                try {
                    String hex = s.substring(0, 2);
                    final int r = Integer.parseInt(hex, 16);
                    hex = s.substring(2, 4);
                    final int g = Integer.parseInt(hex, 16);
                    hex = s.substring(4, 6);
                    final int b = Integer.parseInt(hex, 16);
                    return new Color(r, g, b);
                } catch (final NumberFormatException e) {
                    logger.error("Cannot convert " + s + " into color", e);
                }
            }
        }
        return null;
    }
}
