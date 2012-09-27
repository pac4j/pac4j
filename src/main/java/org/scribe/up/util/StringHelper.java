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

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is an helper to work with strings.
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
    public static String substringBetween(final String text, final String s1, final String s2) {
        if (text != null && s1 != null && s2 != null) {
            final int begin = text.indexOf(s1);
            if (begin >= 0) {
                final int end = text.indexOf(s2, begin);
                if (end >= 0) {
                    final String extract = text.substring(begin + s1.length(), end);
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
    public static boolean isNotBlank(final String s) {
        return s != null && !"".equals(s.trim());
    }
    
    /**
     * Test if a string is blank.
     * 
     * @param s
     * @return if the string is blank
     */
    public static boolean isBlank(final String s) {
        return !isNotBlank(s);
    }
    
    /**
     * <p>
     * Creates a random string whose length is the number of characters specified.
     * </p>
     * <p>
     * Characters will be chosen from the set of alpha-numeric characters.
     * </p>
     * 
     * @param count the length of random string to create
     * @return the random string
     */
    public static String randomAlphanumeric(int count) {
        final Random random = new Random();
        final int start = ' ';
        final int end = 'z' + 1;
        
        if (count == 0) {
            return "";
        } else if (count < 0) {
            throw new IllegalArgumentException("Requested random string length " + count + " is less than 0.");
        }
        final char[] buffer = new char[count];
        final int gap = end - start;
        
        while (count-- != 0) {
            char ch;
            ch = (char) (random.nextInt(gap) + start);
            if (Character.isLetter(ch) || Character.isDigit(ch)) {
                if (ch >= 56320 && ch <= 57343) {
                    if (count == 0) {
                        count++;
                    } else {
                        // low surrogate, insert high surrogate after putting it in
                        buffer[count] = ch;
                        count--;
                        buffer[count] = (char) (55296 + random.nextInt(128));
                    }
                } else if (ch >= 55296 && ch <= 56191) {
                    if (count == 0) {
                        count++;
                    } else {
                        // high surrogate, insert low surrogate before putting it in
                        buffer[count] = (char) (56320 + random.nextInt(128));
                        count--;
                        buffer[count] = ch;
                    }
                } else if (ch >= 56192 && ch <= 56319) {
                    // private high surrogate, no effing clue, so skip it
                    count++;
                } else {
                    buffer[count] = ch;
                }
            } else {
                count++;
            }
        }
        return new String(buffer);
    }
}
