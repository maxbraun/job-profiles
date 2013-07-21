package org.jenkinsci.plugins.jobprofiles;


public class JobProfileException extends RuntimeException {
    public JobProfileException() {
        super();
    }

    public JobProfileException(String message) {
        super(message);
    }

    public JobProfileException(String message, Throwable cause) {
        super(message, cause);
    }

    public JobProfileException(Throwable cause) {
        super(cause);
    }

}
