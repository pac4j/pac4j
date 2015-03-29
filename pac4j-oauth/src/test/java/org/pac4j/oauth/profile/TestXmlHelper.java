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

import junit.framework.TestCase;

import org.pac4j.core.profile.converter.Converters;

/**
 * Test {@link XmlHelper}.
 * 
 * @author Jerome Leleu
 * @since 1.4.1
 */
public final class TestXmlHelper extends TestCase {
    
    private static final String TEXT = "text";
    
    private static final String TEXT2 = "text2";
    
    private static final int NUMBER = 1234;
    
    private static final String TAG1 = "tag1";
    
    private static final String OPEN_TAG1 = "<" + TAG1 + ">";
    
    private static final String OPEN_TAG1_WITH_ATTRIBUTE = "<" + TAG1 + " attr=\"v\">";
    
    private static final String CLOSE_TAG1 = "</" + TAG1 + ">";
    
    private static final String CLOSE_TAG2 = "</tag2>";
    
    public void testGoodTextEvenIfAttribute() {
        assertEquals(TEXT, XmlHelper.get(OPEN_TAG1_WITH_ATTRIBUTE + TEXT + CLOSE_TAG1, TAG1));
    }
    
    public void testGoodText() {
        assertEquals(TEXT, XmlHelper.get(OPEN_TAG1 + TEXT + CLOSE_TAG1, TAG1));
    }
    
    public void testGoodText2() {
        XmlMatch match = XmlHelper.get(OPEN_TAG1 + TEXT + CLOSE_TAG1 + OPEN_TAG1 + TEXT2 + CLOSE_TAG1, TAG1,
                                       TAG1.length());
        assertEquals(TEXT2, match.getText());
        assertEquals(17, match.getPos());
    }
    
    public void testBadText() {
        assertNull(XmlHelper.get(OPEN_TAG1 + TEXT + CLOSE_TAG2, TAG1));
    }
    
    public void testGetBoolean() {
        assertEquals(Boolean.TRUE,
                     XmlHelper.convert(Converters.booleanConverter, OPEN_TAG1 + Boolean.TRUE + CLOSE_TAG1, TAG1));
    }
    
    public void testGetNumber() {
        assertEquals(NUMBER, XmlHelper.convert(Converters.integerConverter, OPEN_TAG1 + NUMBER + CLOSE_TAG1, TAG1));
    }
}
