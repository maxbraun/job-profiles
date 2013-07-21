package org.jenkinsci.plugins.jobprofiles;


import net.oneandone.sushi.fs.World;

import java.io.IOException;
import java.net.URISyntaxException;
public class ScmSVN implements Scm {

    final String scm;

    public ScmSVN(final String scm) {
        this.scm = scm;
    }


    public String getPom() {
        World world;

        world = new World();

        try {
            return world.node("svn:" + scm).findOne("pom.xml").toString();
        } catch (IOException e) {
            throw new JobProfileException(e.getMessage(), e.getCause());
        } catch (URISyntaxException e) {
            throw new JobProfileException("URI malformed", e.getCause());
        }
    }
}
