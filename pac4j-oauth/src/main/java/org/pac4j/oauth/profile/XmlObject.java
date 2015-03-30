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

import org.pac4j.core.profile.RawDataObject;

/**
 * This class is an object which can be built from XML.
 * 
 * @author Jerome Leleu
 * @since 1.4.1
 */
public abstract class XmlObject extends RawDataObject {
    
    private static final long serialVersionUID = 7281757045791685668L;
    
    /**
     * Build an object from XML.
     * 
     * @param xml xml
     */
    public final void buildFrom(final String xml) {
        if (keepRawData && isRootObject()) {
            this.data = xml;
        }
        buildFromXml(xml);
    }
    
    /**
     * Build an object from a XML text.
     * 
     * @param xml xml
     */
    protected abstract void buildFromXml(String xml);
}
