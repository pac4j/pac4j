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
package org.pac4j.core.credentials;

import java.io.Serializable;

/**
 * This class represents the base credentials. It is always associated to a client name.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public abstract class Credentials implements Serializable {
    
    private static final long serialVersionUID = 4864923514027378583L;
    
    private String clientName;
    
    public String getClientName() {
        return this.clientName;
    }
    
    public void setClientName(final String clientName) {
        this.clientName = clientName;
    }
}
