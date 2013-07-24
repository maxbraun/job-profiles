package org.jenkinsci.plugins.jobprofiles;


import net.oneandone.sushi.fs.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScmSVN extends Scm {

    private final String scm;
    private final World world;

    public ScmSVN(final String scm, World world) {
        this.scm = scm;
        this.world = world;
    }


    public String getPom() {


        try {
            return world.node(scm).findOne("pom.xml").toString();
        } catch (IOException e) {
            throw new JobProfileException(e.getMessage(), e.getCause());
        } catch (URISyntaxException e) {
            throw new JobProfileException("URI malformed", e.getCause());
        }
    }

    public Map<String, String> getProfile(String name) {
        Map<String, String> profiles;
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

    public List<Node> find(String seachString) {
        try {
            return world.node(scm).find(seachString);
        } catch (URISyntaxException e) {
            return null;
        } catch (NodeInstantiationException e) {
            return null;
        } catch (IOException e) {
            return null;
        }

    }

    public Node findOne(String seachString) {
        try {
            return world.node(scm).findOne(seachString);
        } catch (URISyntaxException e) {
            return null;
        } catch (NodeInstantiationException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }
}
