/*
  Copyright 2012 - 2014 Michael Remond

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
package org.pac4j.core.client;

/**
 * Indicates the action when the {@link Client} requires a redirection to achieve user authentication. Valid redirection
 * type are :
 * <ul>
 * <li>REDIRECT (HTTP 302)</li>
 * <li>SUCCESS (HTTP 200)</li>
 * </ul>
 * 
 * @author Michael Remond
 * @since 1.5.0
 */
public class RedirectAction {

    public enum RedirectType {
        REDIRECT, SUCCESS
    }

    private RedirectType type;

    private String location;

    private String content;

    private RedirectAction() {

    }

    public static RedirectAction redirect(final String location) {
        RedirectAction action = new RedirectAction();
        action.type = RedirectType.REDIRECT;
        action.location = location;
        return action;
    }

    public static RedirectAction success(final String content) {
        RedirectAction action = new RedirectAction();
        action.type = RedirectType.SUCCESS;
        action.content = content;
        return action;
    }

    public RedirectType getType() {
        return this.type;
    }

    public String getLocation() {
        return this.location;
    }

    public String getContent() {
        return this.content;
    }
}
