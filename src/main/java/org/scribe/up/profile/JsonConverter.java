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
 * This class is a converter from String to an object buildable from json (the input string must be a json text).
 * 
 * @author Jerome Leleu
 * @since 1.1.0
 */
public final class JsonConverter<T extends FromJsonBuildableObject> implements AttributeConverter<T> {
    
    public T convert(String attribute) {
        return null;
    }
}
