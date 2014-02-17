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
package org.pac4j.core.profile;

import java.io.Serializable;

/**
 * This class is an object which can build from some raw data input (JSON, XML...)
 * 
 * @author Jerome Leleu
 * @since 1.3.0
 */
public abstract class RawDataObject implements Serializable {
    
    private static final long serialVersionUID = -2364715855308858348L;
    
    protected transient static boolean keepRawData = false;
    
    protected String data = "";
    
    static void setKeepRawData(final boolean keepRawData) {
        RawDataObject.keepRawData = keepRawData;
    }
    
    protected boolean isRootObject() {
        return true;
    }
    
    @Override
    public String toString() {
        return this.data;
    }
}
