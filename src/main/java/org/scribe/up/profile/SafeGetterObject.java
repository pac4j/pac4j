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
package org.scribe.up.profile;

/**
 * This class is an abstract one with safe getters for primitives.
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public abstract class SafeGetterObject {
    
    /**
     * Return the primitive value or false if null.
     * 
     * @param b
     * @return the primitive value or false if null
     */
    protected boolean getSafeBoolean(Boolean b) {
        return b != null ? b : false;
    }
    
    /**
     * Return the primitive value or 0 if null.
     * 
     * @param i
     * @return the primitive value or 0 if null
     */
    protected int getSafeInt(Integer i) {
        return i != null ? i : 0;
    }
    
    /**
     * Return the primitive value or 0 if null.
     * 
     * @param l
     * @return the primitive value or 0 if null
     */
    protected long getSafeLong(Long l) {
        return l != null ? l : 0;
    }
}
