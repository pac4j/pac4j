/*
  Copyright 2012 - 2013 Jerome Leleu

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
package org.pac4j.oauth.util;

import junit.framework.TestCase;

/**
 * Test {@link XmlHelper}.
 * 
 * @author Jerome Leleu
 * @since 1.4.1
 */
public final class TestXmlHelper extends TestCase {
    
    private static final String TEXT = "text";
    
    private static final String TAG1 = "tag1";
    
    private static final String OPEN_TAG1 = "<" + TAG1 + ">";
    
    private static final String CLOSE_TAG1 = "</" + TAG1 + ">";
    
    private static final String CLOSE_TAG2 = "</tag2>";
    
    public void testGoodText() {
        assertEquals(TEXT, XmlHelper.get(OPEN_TAG1 + TEXT + CLOSE_TAG1, TAG1));
    }
    
    public void testBadText() {
        assertNull(XmlHelper.get(OPEN_TAG1 + TEXT + CLOSE_TAG2, TAG1));
    }
}
