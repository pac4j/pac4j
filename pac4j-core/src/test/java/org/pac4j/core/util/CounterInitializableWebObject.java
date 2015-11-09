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
package org.pac4j.core.util;

import org.pac4j.core.context.WebContext;

/**
 * This class is a counter as {@link InitializableWebObject} for tests purpose.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class CounterInitializableWebObject extends InitializableWebObject {
    
    private int counter = 0;
    
    @Override
    protected void internalInit(final WebContext context) {
        this.counter++;
    }
    
    public int getCounter() {
        return this.counter;
    }
}
