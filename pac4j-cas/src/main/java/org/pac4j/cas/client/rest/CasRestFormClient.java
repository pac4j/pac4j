package org.pac4j.cas.client.rest;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.pac4j.cas.config.CasConfiguration;
import org.pac4j.cas.credentials.authenticator.CasRestAuthenticator;
import org.pac4j.core.credentials.extractor.FormExtractor;
import org.pac4j.core.util.Pac4jConstants;

import static org.pac4j.core.util.CommonHelper.assertNotBlank;
import static org.pac4j.core.util.CommonHelper.assertNotNull;

/**
 * Direct client which receives credentials as form parameters and validates them via the CAS REST API.
 *
 * @author Misagh Moayyed
 * @since 1.8.0
 */
@Getter
@Setter
@ToString(callSuper = true)
public class CasRestFormClient extends AbstractCasRestClient {

    private String usernameParameter = Pac4jConstants.USERNAME;

    private String passwordParameter = Pac4jConstants.PASSWORD;

    /**
     * <p>Constructor for CasRestFormClient.</p>
     */
    public CasRestFormClient() {}

    /**
     * <p>Constructor for CasRestFormClient.</p>
     *
     * @param configuration a {@link CasConfiguration} object
     * @param usernameParameter a {@link String} object
     * @param passwordParameter a {@link String} object
     */
    public CasRestFormClient(final CasConfiguration configuration, final String usernameParameter, final String passwordParameter) {
        this.configuration = configuration;
        this.usernameParameter = usernameParameter;
        this.passwordParameter = passwordParameter;
    }

    /** {@inheritDoc} */
    @Override
    protected void internalInit(final boolean forceReinit) {
        assertNotBlank("usernameParameter", this.usernameParameter);
        assertNotBlank("passwordParameter", this.passwordParameter);
        assertNotNull("configuration", this.configuration);

        setCredentialsExtractorIfUndefined(new FormExtractor(this.usernameParameter, this.passwordParameter));
        setAuthenticatorIfUndefined(new CasRestAuthenticator(this.configuration));
    }
}
