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
package org.pac4j.test.cas.client;

import org.pac4j.cas.client.CasClient;
import org.pac4j.cas.client.CasClient.CasProtocol;

/**
 * This class tests the {@link CasClient} class when using CAS 2.0 protocol.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class CasClientCasProtocol20ProxyIT extends CasClientIT {
    
    @Override
    protected CasProtocol getCasProtocol() {
        return CasProtocol.CAS20_PROXY;
    }
}
