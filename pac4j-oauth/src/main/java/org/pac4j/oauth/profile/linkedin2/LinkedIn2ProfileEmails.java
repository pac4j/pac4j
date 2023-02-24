package org.pac4j.oauth.profile.linkedin2;

import java.io.Serializable;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <p>LinkedIn2ProfileEmails class.</p>
 *
 * @author Vassilis Virvilis
 * @since 3.8.0
 */
public class LinkedIn2ProfileEmails implements Serializable {
    private static final long serialVersionUID = 100L;

    public static class Email implements Serializable {
        private static final long serialVersionUID = 1L;

        public static class HandleTilde implements Serializable {
            private static final long serialVersionUID = 1L;

            private String emailAddress;

            public String getEmailAddress() {
                return emailAddress;
            }

            public void setEmailAddress(String emailAddress) {
                this.emailAddress = emailAddress;
            }

            @Override
            public String toString() {
                return String.format("{emailAddress: %s}", emailAddress);
            }
        }

        private String handle;
        @JsonProperty("handle~")
        private HandleTilde handleTilde;

        public String getHandle() {
            return handle;
        }

        public void setHandle(String handle) {
            this.handle = handle;
        }

        public HandleTilde getHandleTilde() {
            return handleTilde;
        }

        public void setHandleTilde(HandleTilde handleTilde) {
            this.handleTilde = handleTilde;
        }

        @Override
        public String toString() {
            return String.format("{handle: %s, handle~: %s}", handle, handleTilde);
        }
    }

    private Email[] elements;

    /**
     * <p>Getter for the field <code>elements</code>.</p>
     *
     * @return an array of {@link org.pac4j.oauth.profile.linkedin2.LinkedIn2ProfileEmails.Email} objects
     */
    public Email[] getElements() {
        return LinkedIn2ProfilePicture.deepCopy(elements);
    }

    /**
     * <p>Setter for the field <code>elements</code>.</p>
     *
     * @param elements an array of {@link org.pac4j.oauth.profile.linkedin2.LinkedIn2ProfileEmails.Email} objects
     */
    public void setElements(Email[] elements) {
        this.elements = LinkedIn2ProfilePicture.deepCopy(elements);
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return String.format("{elements: %s}", Arrays.asList(elements));
    }
}
