package org.pac4j.kerberos.credentials.authenticator;

import java.util.HashSet;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosPrincipal;

import org.ietf.jgss.GSSContext;


/**
 * 
 * Result of ticket validation
 * 
 * @author Garry Boyce
 * @since 1.9.1
 */
public class KerberosTicketValidation {

	private final String username;
	private final byte[] responseToken;
	private final GSSContext gssContext;
	private final String servicePrincipal;

	public KerberosTicketValidation(String username, String servicePrincipal, byte[] responseToken, GSSContext gssContext) {
		this.username = username;
		this.servicePrincipal = servicePrincipal;
		this.responseToken = responseToken;
		this.gssContext = gssContext;
	}

	public String username() {
		return username;
	}

	public byte[] responseToken() {
		return responseToken;
	}

	public GSSContext getGssContext() {
		return gssContext;
	}

	public Subject subject() {
		final Set<KerberosPrincipal> princs = new HashSet<KerberosPrincipal>();
		princs.add(new KerberosPrincipal(servicePrincipal));
		return new Subject(false, princs, new HashSet<Object>(), new HashSet<Object>());
	}

}