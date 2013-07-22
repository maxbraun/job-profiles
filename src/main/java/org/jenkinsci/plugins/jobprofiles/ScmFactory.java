package org.jenkinsci.plugins.jobprofiles;


import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScmFactory {

    private Scm scm;

    public ScmFactory(String scm) {
        final String gitPattern = "^(.*\\.git)[/]?$";

        if (scm.matches(gitPattern)) {
            this.scm = new ScmGit(scm);
            return;
        }
        this.scm = new ScmSVN("svn:" + scm);
    }

    public Scm get() {
        return scm;
    }
}
