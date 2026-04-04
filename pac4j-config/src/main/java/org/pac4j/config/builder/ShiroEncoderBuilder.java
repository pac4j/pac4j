package org.pac4j.config.builder;

import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.crypto.hash.DefaultHashService;
import org.apache.shiro.lang.util.ByteSource;
import org.pac4j.core.credentials.password.PasswordEncoder;
import org.pac4j.core.credentials.password.ShiroPasswordEncoder;

import java.util.HashMap;
import java.util.Map;

/**
 * Builder for Shiro (DefaultHashService and) DefaultPasswordService as password encoder.
 *
 * @author Jerome Leleu
 * @since 2.1.0
 */
public class ShiroEncoderBuilder extends AbstractBuilder {

    /**
     * <p>Constructor for ShiroEncoderBuilder.</p>
     *
     * @param properties a {@link Map} object
     */
    public ShiroEncoderBuilder(final Map<String, String> properties) {
        super(properties);
    }

    /**
     * <p>tryCreatePasswordEncoder.</p>
     *
     * @param encoders a {@link Map} object
     */
    public void tryCreatePasswordEncoder(final Map<String, PasswordEncoder> encoders) {
        for (var i = 0; i <= MAX_NUM_ENCODERS; i++) {
            val exists = getProperty(SHIRO_ENCODER, i);
            val hasProperty =  containsProperty(SHIRO_ENCODER_GENERATE_PUBLIC_SALT, i)
                || containsProperty(SHIRO_ENCODER_HASH_ALGORITHM_NAME, i)
                || containsProperty(SHIRO_ENCODER_HASH_ITERATIONS, i) || containsProperty(SHIRO_ENCODER_PRIVATE_SALT, i);
            if (StringUtils.isNotBlank(exists) || hasProperty) {

                val passwordService = new DefaultPasswordService();

                if (hasProperty) {
                    val hashService = new DefaultHashService();

                    val parameters = new HashMap<String, Object>();

                    if (containsProperty(SHIRO_ENCODER_GENERATE_PUBLIC_SALT, i)) {
                        parameters.put("generatePublicSalt", getPropertyAsBoolean(SHIRO_ENCODER_GENERATE_PUBLIC_SALT, i));
                    } else {
                        // deprecated
                        parameters.put("generatePublicSalt", true);
                    }
                    if (containsProperty(SHIRO_ENCODER_HASH_ALGORITHM_NAME, i)) {
                        hashService.setDefaultAlgorithmName(getProperty(SHIRO_ENCODER_HASH_ALGORITHM_NAME, i));
                    } else {
                        // deprecated
                        hashService.setDefaultAlgorithmName("SHA-256");
                    }
                    if (containsProperty(SHIRO_ENCODER_HASH_ITERATIONS, i)) {
                        parameters.put("hashIterations", getPropertyAsInteger(SHIRO_ENCODER_HASH_ITERATIONS, i));
                    } else {
                        // deprecated
                        parameters.put("hashIterations", 500000);
                    }
                    if (containsProperty(SHIRO_ENCODER_PRIVATE_SALT, i)) {
                        parameters.put("privateSalt", ByteSource.Util.bytes(getProperty(SHIRO_ENCODER_PRIVATE_SALT, i)));
                    }

                    hashService.setParameters(parameters);

                    passwordService.setHashService(hashService);
                }

                encoders.put(concat(SHIRO_ENCODER, i), new ShiroPasswordEncoder(passwordService));
            }
        }
    }
}
