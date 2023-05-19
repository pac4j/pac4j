package org.pac4j.config.builder;

import lombok.val;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.crypto.hash.DefaultHashService;
import org.apache.shiro.util.ByteSource;
import org.pac4j.core.credentials.password.PasswordEncoder;
import org.pac4j.core.credentials.password.ShiroPasswordEncoder;

import java.util.Map;

import static org.pac4j.core.util.CommonHelper.isNotBlank;

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
            if (isNotBlank(exists) || hasProperty) {

                val passwordService = new DefaultPasswordService();

                if (hasProperty) {
                    val hashService = new DefaultHashService();

                    if (containsProperty(SHIRO_ENCODER_GENERATE_PUBLIC_SALT, i)) {
                        hashService.setGeneratePublicSalt(getPropertyAsBoolean(SHIRO_ENCODER_GENERATE_PUBLIC_SALT, i));
                    }
                    if (containsProperty(SHIRO_ENCODER_HASH_ALGORITHM_NAME, i)) {
                        hashService.setHashAlgorithmName(getProperty(SHIRO_ENCODER_HASH_ALGORITHM_NAME, i));
                    }
                    if (containsProperty(SHIRO_ENCODER_HASH_ITERATIONS, i)) {
                        hashService.setHashIterations(getPropertyAsInteger(SHIRO_ENCODER_HASH_ITERATIONS, i));
                    }
                    if (containsProperty(SHIRO_ENCODER_PRIVATE_SALT, i)) {
                        hashService.setPrivateSalt(ByteSource.Util.bytes(getProperty(SHIRO_ENCODER_PRIVATE_SALT, i)));
                    }

                    passwordService.setHashService(hashService);
                }

                encoders.put(concat(SHIRO_ENCODER, i), new ShiroPasswordEncoder(passwordService));
            }
        }
    }
}
