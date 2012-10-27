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
package org.scribe.up.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.scribe.utils.OAuthEncoder;

/**
 * This class is a providers definition which can be used to handle :<br />
 * - the redirection of the user to the OAuth provider<br />
 * - the callback redirection of the user to the callback url after successfull OAuth authentication.
 * <p />
 * The {@link #init()} method must be called to initialize the callback urls from a base url and a specific parameter to define the OAuth
 * provider targeted.
 * <p />
 * The {@link #findProvider(Map)} method must be called to find the right provider according to the specific parameter on the callback url.
 * 
 * @author Jerome Leleu
 * @since 1.3.0
 */
public final class ProvidersDefinition {
    
    private final static String PARAMETER_NAME = "oauth_provider_type";
    
    private List<OAuthProvider> providers;
    
    private String baseUrl;
    
    public ProvidersDefinition() {
    }
    
    public ProvidersDefinition(final OAuthProvider provider) {
        this.providers = new ArrayList<OAuthProvider>();
        this.providers.add(provider);
        BaseOAuthProvider baseOAuthProvider = (BaseOAuthProvider) provider;
        this.baseUrl = baseOAuthProvider.getCallbackUrl();
    }
    
    /**
     * Initialize this providers definition by computing callback urls.
     */
    public void init() {
        if (StringUtils.isBlank(this.baseUrl)) {
            throw new IllegalArgumentException("baseUrl cannot be blank");
        }
        if (this.providers == null) {
            throw new IllegalArgumentException("providers cannot be null");
        }
        for (final OAuthProvider provider : this.providers) {
            final BaseOAuthProvider baseProvider = (BaseOAuthProvider) provider;
            // calculate new callback url by adding the OAuth provider type to the base url
            baseProvider.setCallbackUrl(this.addParameter(this.baseUrl, PARAMETER_NAME, provider.getType()));
        }
    }
    
    /**
     * Return the right provider according to the specific parameter in the callback url.
     * 
     * @param parameters
     * @return the right provider
     */
    public OAuthProvider findProvider(final Map<String, String[]> parameters) {
        String[] values = parameters.get(PARAMETER_NAME);
        if (values != null && values.length == 1) {
            return findProvider(values[0]);
        }
        return null;
    }
    
    /**
     * Return the right provider according to the specific type.
     * 
     * @param type
     * @return the right provider
     */
    public OAuthProvider findProvider(final String type) {
        for (final OAuthProvider provider : this.providers) {
            if (StringUtils.equals(provider.getType(), type)) {
                return provider;
            }
        }
        return null;
    }
    
    /**
     * Add a new parameter to an url.
     * 
     * @param url
     * @param name
     * @param value
     * @return the new url with the parameter appended
     */
    private String addParameter(final String url, final String name, final String value) {
        final StringBuilder sb = new StringBuilder();
        sb.append(url);
        if (url.indexOf("?") >= 0) {
            sb.append("&");
        } else {
            sb.append("?");
        }
        sb.append(name);
        sb.append("=");
        sb.append(OAuthEncoder.encode(value));
        return sb.toString();
    }
    
    public void setBaseUrl(final String baseUrl) {
        this.baseUrl = baseUrl;
    }
    
    public void setProviders(final List<OAuthProvider> providers) {
        this.providers = providers;
    }
}
