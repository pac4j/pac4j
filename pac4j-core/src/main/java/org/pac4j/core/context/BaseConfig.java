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
package org.pac4j.core.context;

import java.util.regex.Pattern;

/**
 * Will be removed. Use {@link org.pac4j.core.config.Config} and {@link org.pac4j.core.config.ConfigSingleton} instead.
 * 
 * @author Jerome Leleu
 * @since 1.6.0
 * @deprecated
 */
@Deprecated
public class BaseConfig {

    private final static String DEFAULT_URL = "/";

    // just relative urls
    private final static String DEFAULT_LOGOUT_URL_PATTERN = "/.*";

    private static String defaultSuccessUrl = DEFAULT_URL;

    private static String defaultLogoutUrl = DEFAULT_URL;

    private static Pattern logoutUrlPattern = Pattern.compile(DEFAULT_LOGOUT_URL_PATTERN);

    private static String errorPage401 = "authentication required";

    private static String errorPage403 = "forbidden";

    public static String getDefaultSuccessUrl() {
        return defaultSuccessUrl;
    }

    public static void setDefaultSuccessUrl(final String defaultSuccessUrl) {
        BaseConfig.defaultSuccessUrl = defaultSuccessUrl;
    }

    public static String getDefaultLogoutUrl() {
        return defaultLogoutUrl;
    }

    public static void setDefaultLogoutUrl(final String defaultLogoutUrl) {
        BaseConfig.defaultLogoutUrl = defaultLogoutUrl;
    }

    public static String getErrorPage401() {
        return errorPage401;
    }

    public static void setErrorPage401(final String errorPage401) {
        BaseConfig.errorPage401 = errorPage401;
    }

    public static String getErrorPage403() {
        return errorPage403;
    }

    public static void setErrorPage403(final String errorPage403) {
        BaseConfig.errorPage403 = errorPage403;
    }

    public static Pattern getLogoutUrlPattern() {
        return logoutUrlPattern;
    }

    public static void setLogoutUrlPattern(String logoutUrlPattern) {
        BaseConfig.logoutUrlPattern = Pattern.compile(logoutUrlPattern);
    }

}
