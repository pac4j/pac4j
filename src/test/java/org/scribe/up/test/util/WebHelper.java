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
package org.scribe.up.test.util;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import com.gargoylesoftware.htmlunit.WebClient;

/**
 * This class is an helper to get a basic web client and to extract parameters from an url.
 * 
 * @author Jérôme Leleu
 * @since 1.0.0
 */
public class WebHelper {
    
    public static WebClient newClient() {
        WebClient webClient = new WebClient();
        webClient.setRedirectEnabled(true);
        webClient.setCssEnabled(false);
        webClient.setJavaScriptEnabled(false);
        return webClient;
    }
    
    public static Map<String, String[]> extractParametersFromUrl(String url) {
        Map<String, String[]> parameters = new HashMap<String, String[]>();
        int pos = url.indexOf("?");
        if (pos > 0) {
            url = url.substring(pos + 1);
        }
        StringTokenizer st = new StringTokenizer(url, "&");
        while (st.hasMoreTokens()) {
            String keyValue = st.nextToken();
            String[] parts = keyValue.split("=");
            String[] values = new String[1];
            values[0] = parts[1];
            parameters.put(parts[0], values);
        }
        return parameters;
    }
}
