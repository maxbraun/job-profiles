package org.jenkinsci.plugins.jobprofiles;


import net.oneandone.sushi.fs.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class ScmSVN implements Scm {

    private final String scm;

    public ScmSVN(final String scm) {
        this.scm = scm;
    }


    public String getPom() {
        World world;

        world = new World();

        try {
            return world.node(scm).findOne("pom.xml").toString();
        } catch (IOException e) {
            throw new JobProfileException(e.getMessage(), e.getCause());
        } catch (URISyntaxException e) {
            throw new JobProfileException("URI malformed", e.getCause());
        }
    }

    public Map<String, String> getProfile(String name) {
        World world;
        Map<String, String> profiles;

        world = new World();
        profiles = new HashMap<String, String>();
        try {
            for (Node file : world.node(scm).join(name).list()) {
                profiles.put(file.getName(), file.toString());
            }
            return profiles;
        } catch (ListException e) {
            throw new JobProfileException(e.getMessage(), e.getCause());
        } catch (DirectoryNotFoundException e) {
            throw new JobProfileException(e.getMessage(), e.getCause());
        } catch (URISyntaxException e) {
            throw new JobProfileException(e.getMessage(), e.getCause());
        } catch (NodeInstantiationException e) {
            throw new JobProfileException(e.getMessage(), e.getCause());
        }
    }
}
