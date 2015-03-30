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
package org.pac4j.oauth.profile.paypal;

import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.profile.JsonHelper;
import org.pac4j.oauth.profile.JsonObject;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * This class represents a PayPal address.
 * 
 * @author Jerome Leleu
 * @since 1.4.2
 */
public class PayPalAddress extends JsonObject {
    
    private static final long serialVersionUID = -6856575643675582895L;
    
    private String streetAddress;
    
    private String locality;
    
    private String postalCode;
    
    private String country;
    
    @Override
    protected void buildFromJson(final JsonNode json) {
        this.streetAddress = (String) JsonHelper.convert(Converters.stringConverter, json, "street_address");
        this.locality = (String) JsonHelper.convert(Converters.stringConverter, json, "locality");
        this.postalCode = (String) JsonHelper.convert(Converters.stringConverter, json, "postal_code");
        this.country = (String) JsonHelper.convert(Converters.stringConverter, json, "country");
    }
    
    public String getStreetAddress() {
        return this.streetAddress;
    }
    
    public String getLocality() {
        return this.locality;
    }
    
    public String getPostalCode() {
        return this.postalCode;
    }
    
    public String getCountry() {
        return this.country;
    }
}
