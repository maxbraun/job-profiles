package org.jenkinsci.plugins.jobprofiles;


public class ContextFactory {

    private final Scm scm;

    public ContextFactory(Scm scm) {
        this.scm = scm;
    }

    public Context get() {
        return new ContextMaven(scm);
    }
}
