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
package org.scribe.up.test.profile.facebook;

import junit.framework.TestCase;

import org.scribe.up.profile.facebook.FacebookPicture;

/**
 * This class tests the {@link org.scribe.up.profile.facebook.FacebookPicture} class.
 * 
 * @author Jerome Leleu
 * @since 1.2.0
 */
public final class TestFacebookPicture extends TestCase {
    
    private static final String URL = "http://url";
    
    private static final boolean IS_SILHOUETTE = true;
    
    private static final String GOOD_JSON = "{\"url\": \"" + URL + "\", \"is_silhouette\": " + IS_SILHOUETTE + " }";
    
    private static final String BAD_JSON = "{ }";
    
    public void testNull() {
        final FacebookPicture facebookPicture = new FacebookPicture();
        facebookPicture.buildFrom(null);
        assertNull(facebookPicture.getUrl());
        assertNull(facebookPicture.getIsSilhouette());
    }
    
    public void testBadJson() {
        final FacebookPicture facebookPicture = new FacebookPicture();
        facebookPicture.buildFrom(BAD_JSON);
        assertNull(facebookPicture.getUrl());
        assertNull(facebookPicture.getIsSilhouette());
    }
    
    public void testGoodJson() {
        final FacebookPicture facebookPicture = new FacebookPicture();
        facebookPicture.buildFrom(GOOD_JSON);
        assertEquals(URL, facebookPicture.getUrl());
        assertEquals(IS_SILHOUETTE, facebookPicture.getIsSilhouette().booleanValue());
    }
}
