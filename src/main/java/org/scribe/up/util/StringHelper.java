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
package org.scribe.up.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is an helper to work on strings.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class StringHelper {
    
    private static final Logger logger = LoggerFactory.getLogger(StringHelper.class);
    
    private StringHelper() {
    }
    
    /**
     * Return the text between the two strings specified. Return null if no string is found.
     * 
     * @param text
     * @param s1
     * @param s2
     * @return the text between the two strings specified in input
     */
    public static String substringBetween(String text, String s1, String s2) {
        if (text != null && s1 != null && s2 != null) {
            int begin = text.indexOf(s1);
            if (begin >= 0) {
                int end = text.indexOf(s2, begin);
                if (end >= 0) {
                    String extract = text.substring(begin + s1.length(), end);
                    logger.trace("String extracted between {} and {} in {} : {}", new Object[] {
                        s1, s2, text, extract
                    });
                    return extract;
                }
            }
        }
        return null;
    }
    
    /**
     * Test if a string is not blank : should not be null and trimed value should not be equals to empty string.
     * 
     * @param s
     * @return if the string is not blank
     */
    public static boolean isNotBlank(String s) {
        return s != null && !"".equals(s.trim());
    }
    
    /**
     * Test if a string is blank.
     * 
     * @param s
     * @return if the string is blank
     */
    public static boolean isBlank(String s) {
        return !isNotBlank(s);
    }
}
