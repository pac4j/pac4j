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
package org.pac4j.gae.credentials;

import org.pac4j.core.credentials.Credentials;

import com.google.appengine.api.users.User;
import org.pac4j.core.util.CommonHelper;

/**
 * Credential for Google App Engine.
 *
 * @author Patrice de Saint Steban
 * @since 1.6.0
 */
public class GaeUserCredentials extends Credentials {

	private static final long serialVersionUID = -135519596194113906L;
	
	private User user;

	public GaeUserCredentials() {
		setClientName("GaeUserClient");
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public User getUser() {
		return user;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		final GaeUserCredentials that = (GaeUserCredentials) o;

		return !(user != null ? !user.equals(that.user) : that.user != null);

	}

	@Override
	public int hashCode() {
		return user != null ? user.hashCode() : 0;
	}

	@Override
	public String toString() {
		return CommonHelper.toString(this.getClass(), "user", this.user);
	}
}
