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

    private ParameterList parameterList;

    private DiscoveryInformation discoveryInformation;

    public OpenIdCredentials(final DiscoveryInformation discoveryInformation, final ParameterList parameterList) {
        this.discoveryInformation = discoveryInformation;
        this.parameterList = parameterList;
    }

    public DiscoveryInformation getDiscoveryInformation() {
        return this.discoveryInformation;
    }

    public ParameterList getParameterList() {
        return this.parameterList;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final OpenIdCredentials that = (OpenIdCredentials) o;

        if (parameterList != null ? !parameterList.equals(that.parameterList) : that.parameterList != null)
            return false;
        return !(discoveryInformation != null ? !discoveryInformation.equals(that.discoveryInformation)
            : that.discoveryInformation != null);

    }

    @Override
    public int hashCode() {
        int result = parameterList != null ? parameterList.hashCode() : 0;
        result = 31 * result + (discoveryInformation != null ? discoveryInformation.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return CommonHelper.toNiceString(this.getClass(), "discoveryInformation", this.discoveryInformation,
                                     "parameterList", this.parameterList);
    }
}
