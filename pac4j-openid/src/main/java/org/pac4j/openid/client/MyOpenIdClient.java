/*
  Copyright 2012 - 2013 Jerome Leleu

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
package org.pac4j.openid.client;

import org.openid4java.message.AuthSuccess;
import org.openid4java.message.MessageException;
import org.openid4java.message.ax.AxMessage;
import org.openid4java.message.ax.FetchRequest;
import org.openid4java.message.ax.FetchResponse;
import org.pac4j.core.client.BaseClient;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.util.CommonHelper;
import org.pac4j.openid.credentials.OpenIdCredentials;
import org.pac4j.openid.profile.OpenIdAttributesDefinitions;
import org.pac4j.openid.profile.myopenid.MyOpenIdProfile;

/**
 * This class is the OpenID client to authenticate users in myopenid.com.
 * <p />
 * The user used for the redirection to myopenid.com must be defined in the request parameter named {@link #DEFAULT_USER_PARAMETER_NAME} (by
 * default) or the name given through the {@link #setUserParameterName(String)} method.
 * <p />
 * It returns a {@link org.pac4j.openid.profile.myopenid.MyOpenIdProfile}.
 * 
 * @see org.pac4j.openid.profile.myopenid.MyOpenIdProfile
 * @author Jerome Leleu
 * @since 1.4.0
 */
public class MyOpenIdClient extends BaseOpenIdClient<MyOpenIdProfile> {
    
    public static final String DEFAULT_USER_PARAMETER_NAME = "openIdUser";
    
    private String userParameterName = DEFAULT_USER_PARAMETER_NAME;
    
    @Override
    protected void internalInit() {
        super.internalInit();
        CommonHelper.assertNotBlank("userParameterName", this.userParameterName);
    }
    
    @Override
    protected BaseClient<OpenIdCredentials, MyOpenIdProfile> newClient() {
        final MyOpenIdClient newClient = new MyOpenIdClient();
        newClient.setUserParameterName(this.userParameterName);
        return newClient;
    }
    
    @Override
    protected String getUser(final WebContext context) {
        final String user = context.getRequestParameter(getUserParameterName());
        logger.debug("user : {}", user);
        return user;
    }
    
    @Override
    protected FetchRequest getFetchRequest() throws MessageException {
        final FetchRequest fetchRequest = FetchRequest.createFetchRequest();
        fetchRequest.addAttribute("fullname", "http://schema.openid.net/namePerson", true);
        fetchRequest.addAttribute("email", "http://schema.openid.net/contact/email", true);
        logger.debug("fetchRequest: {}", fetchRequest);
        return fetchRequest;
    }
    
    @Override
    protected MyOpenIdProfile createProfile(final AuthSuccess authSuccess) throws MessageException {
        final MyOpenIdProfile profile = new MyOpenIdProfile();
        
        if (authSuccess.hasExtension(AxMessage.OPENID_NS_AX)) {
            final FetchResponse fetchResp = (FetchResponse) authSuccess.getExtension(AxMessage.OPENID_NS_AX);
            for (final String name : OpenIdAttributesDefinitions.myOpenIdDefinition.getAllAttributes()) {
                profile.addAttribute(name, fetchResp.getAttributeValue(name));
            }
        }
        return profile;
    }
    
    public void setUserParameterName(final String userParameterName) {
        this.userParameterName = userParameterName;
    }
    
    public String getUserParameterName() {
        return this.userParameterName;
    }
    
    @Override
    public String toString() {
        return CommonHelper.toString(this.getClass(), "callbackUrl", this.callbackUrl, "userParameterName",
                                     this.userParameterName, "name", getName());
    }
    
    @Override
    protected boolean isDirectRedirection() {
        return false;
    }
}
