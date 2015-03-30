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

/**
 * This class represents a XML text found at a position;
 * 
 * @author Jerome Leleu
 * @since 1.4.1
 */
public class XmlMatch {
    
    private final String text;
    
    private final int pos;
    
    public XmlMatch(final String text, final int pos) {
        this.text = text;
        this.pos = pos;
    }
    
    public String getText() {
        return this.text;
    }
    
    public int getPos() {
        return this.pos;
    }
}
