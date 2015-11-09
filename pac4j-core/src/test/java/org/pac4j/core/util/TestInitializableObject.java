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

import junit.framework.TestCase;

/**
 * This class tests the {@link InitializableWebObject} class.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public final class TestInitializableObject extends TestCase {
    
    public void testInit() {
        CounterInitializableWebObject counterInitializableObject = new CounterInitializableWebObject();
        assertEquals(0, counterInitializableObject.getCounter());
        counterInitializableObject.init(null);
        assertEquals(1, counterInitializableObject.getCounter());
        counterInitializableObject.init(null);
        assertEquals(1, counterInitializableObject.getCounter());
    }
    
    public void testReinit() {
        CounterInitializableWebObject counterInitializableObject = new CounterInitializableWebObject();
        assertEquals(0, counterInitializableObject.getCounter());
        counterInitializableObject.reinit(null);
        assertEquals(1, counterInitializableObject.getCounter());
        counterInitializableObject.reinit(null);
        assertEquals(2, counterInitializableObject.getCounter());
    }
}
