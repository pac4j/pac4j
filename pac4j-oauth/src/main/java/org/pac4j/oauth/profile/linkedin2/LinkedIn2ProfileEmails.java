package org.pac4j.oauth.profile.linkedin2;

import java.io.Serializable;
import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
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

            public void setEmailAddress(final String emailAddress) {
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

        public void setHandle(final String handle) {
            this.handle = handle;
        }

        public HandleTilde getHandleTilde() {
            return handleTilde;
        }

        public void setHandleTilde(final HandleTilde handleTilde) {
            this.handleTilde = handleTilde;
        }

        @Override
        public String toString() {
            return String.format("{handle: %s, handle~: %s}", handle, handleTilde);
        }
    }

    private Email[] elements;

    public Email[] getElements() {
        return LinkedIn2ProfilePicture.deepCopy(elements);
    }

    public void setElements(final Email[] elements) {
        this.elements = LinkedIn2ProfilePicture.deepCopy(elements);
    }

    @Override
    public String toString() {
        return String.format("{elements: %s}", Arrays.asList(elements));
    }
}
