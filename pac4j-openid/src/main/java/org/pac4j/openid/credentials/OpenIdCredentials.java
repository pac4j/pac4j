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
package org.pac4j.openid.credentials;

import org.openid4java.discovery.DiscoveryInformation;
import org.openid4java.message.ParameterList;
import org.pac4j.core.credentials.Credentials;
import org.pac4j.core.util.CommonHelper;

/**
 * This class represents an OpenID credentials with the discovery information, the list of parameters returned by the provider and the
 * client type.
 * 
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class OpenIdCredentials extends Credentials {
    
    private static final long serialVersionUID = -5934736541999523245L;
    
    private final ParameterList parameterList;
    
    private final DiscoveryInformation discoveryInformation;
    
    public OpenIdCredentials(final DiscoveryInformation discoveryInformation, final ParameterList parameterList,
                             final String clientName) {
        this.discoveryInformation = discoveryInformation;
        this.parameterList = parameterList;
        setClientName(clientName);
        
    }
    
    public DiscoveryInformation getDiscoveryInformation() {
        return this.discoveryInformation;
    }
    
    public ParameterList getParameterList() {
        return this.parameterList;
    }
    
    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "discoveryInformation", this.discoveryInformation,
                                     "parameterList", this.parameterList, "clientName", getClientName());
    }
}
