/*
  Copyright 2012 Jérôme Leleu

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
 * This interface is the contract for an attribute converter.
 * 
 * @author Jérôme Leleu
 * @since 1.0.0
 */
public interface AttributeConverter<T> {
    
    /**
     * Convert an attribute to a specific type T.
     * 
     * @param attribute
     * @return
     */
    public T convert(Object attribute);
}
